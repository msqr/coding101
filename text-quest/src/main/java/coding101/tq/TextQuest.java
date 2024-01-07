package coding101.tq;

import static coding101.tq.domain.ColorPalette.color;

import coding101.tq.domain.ColorScheme;
import coding101.tq.domain.Player;
import coding101.tq.domain.Settings;
import coding101.tq.domain.TerrainMap;
import coding101.tq.domain.TerrainType;
import coding101.tq.util.Persistence;
import coding101.tq.util.TerrainMapBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.regex.Matcher;

/**
 * Console based text adventure game.
 */
public class TextQuest implements Game {

    private static int INFO_PANE_WIDTH = 20;
    private static int STATUS_PANE_HEIGHT = 1;

    private final Screen screen;
    private final Settings settings;
    private final TerrainMap mainMap;
    private final Player player;
    private final ObjectMapper mapper;
    private final TextGraphics graphics;
    private final ResourceBundle bundle;
    private final Timer timer;
    private final GameUI ui;
    private TerrainMap activeMap;
    private TerminalSize screenSize;
    private Path savePath;

    /**
     * Constructor.
     *
     * @param screen   the screen to render to
     * @param settings the game settings
     * @param mainMap  the main map
     * @param player   the player
     * @param mapper   the JSON mapper
     * @throws IllegalArgumentException if any argument is {@literal null}
     */
    public TextQuest(Screen screen, Settings settings, TerrainMap mainMap, Player player, ObjectMapper mapper) {
        super();
        this.screen = Objects.requireNonNull(screen);
        this.settings = Objects.requireNonNull(settings);
        this.mainMap = Objects.requireNonNull(mainMap);
        this.player = Objects.requireNonNull(player);
        this.mapper = Objects.requireNonNull(mapper);
        this.graphics = screen.newTextGraphics();
        this.screenSize = screen.getTerminalSize();
        this.bundle = ResourceBundle.getBundle(getClass().getName());
        this.timer = new Timer("TQ Tasks", true);
        this.ui = new GameUI(
                new MapPane(this, INFO_PANE_WIDTH + 3, STATUS_PANE_HEIGHT + 3),
                new InfoPane(this, INFO_PANE_WIDTH, STATUS_PANE_HEIGHT + 3),
                new StatusPane(this, INFO_PANE_WIDTH + 3, STATUS_PANE_HEIGHT, timer),
                new HealthPane(this, INFO_PANE_WIDTH, STATUS_PANE_HEIGHT));

        if (player.getActiveMapName().equals(mainMap.getName())) {
            this.activeMap = mainMap;
        } else {
            this.activeMap = loadChildMap(player.getActiveMapName());
        }
    }

    @Override
    public Screen screen() {
        return screen;
    }

    @Override
    public TextGraphics textGraphics() {
        return graphics;
    }

    @Override
    public Settings settings() {
        return settings;
    }

    @Override
    public Player player() {
        return player;
    }

    @Override
    public TerrainMap map() {
        return activeMap;
    }

    private void setSavePath(Path path) {
        this.savePath = Objects.requireNonNull(path);
    }

    public void run() throws IOException {
        setupScreen();
        while (true) {
            KeyStroke keyStroke = screen.pollInput();
            KeyType keyType = keyStroke != null ? keyStroke.getKeyType() : null;
            if (keyType == KeyType.Escape || keyType == KeyType.EOF) {
                return;
            }

            TerminalSize newSize = screen.doResizeIfNecessary();
            if (newSize != null) {
                screenSize = newSize;
                setupScreen();
            }

            // handle player movement via arrow keys
            int newX = player.getX();
            int newY = player.getY();
            if (keyType == KeyType.ArrowLeft) {
                newX -= 1;
            } else if (keyType == KeyType.ArrowRight) {
                newX += 1;
            } else if (keyType == KeyType.ArrowUp) {
                newY -= 1;
            } else if (keyType == KeyType.ArrowDown) {
                newY += 1;
            }
            if (newX != player.getX() || newY != player.getY() && activeMap.canPlayerMoveTo(newX, newY)) {
                // move player
                if (newX >= 0 && newY >= 0) {
                    ui.map().movePlayer(newX, newY);
                    screen.refresh();
                }
                continue;
            }

            if (keyType == KeyType.Character) {
                final char key = Character.toLowerCase(keyStroke.getCharacter().charValue());
                if (key == ' ') {
                    // check terrain for possible enter/exit
                    TerrainType t = activeMap.terrainAt(player.getX(), player.getY());
                    switch (t) {
                        case Cave -> iteractWithCave();
                        default -> {
                            // nothing to do
                        }
                    }
                } else if (key == 's') {
                    // save game
                    saveGame();
                }
            }
        }
    }

    public void setupScreen() throws IOException {
        // clear screen
        graphics.setForegroundColor(color(settings.colors().foreground().uiBorder(), ANSI.WHITE));
        graphics.setBackgroundColor(color(settings.colors().background().uiBorder(), ANSI.BLACK));
        graphics.fill(' ');

        drawChrome();
        ui.health().draw();
        ui.map().draw();

        screen.refresh();
    }

    private void drawChrome() throws IOException {
        graphics.setForegroundColor(color(settings.colors().foreground().uiBorder(), ANSI.WHITE));
        graphics.setBackgroundColor(color(settings.colors().background().uiBorder(), ANSI.BLACK));

        // top
        graphics.drawLine(1, 0, screenSize.getColumns() - 2, 0, Symbols.DOUBLE_LINE_HORIZONTAL);

        // bottom
        graphics.drawLine(
                1,
                screenSize.getRows() - 1,
                screenSize.getColumns() - 2,
                screenSize.getRows() - 1,
                Symbols.DOUBLE_LINE_HORIZONTAL);

        // left
        graphics.drawLine(0, 1, 0, screenSize.getRows() - 1, Symbols.DOUBLE_LINE_VERTICAL);

        // right
        graphics.drawLine(
                screenSize.getColumns() - 1,
                1,
                screenSize.getColumns() - 1,
                screenSize.getRows() - 2,
                Symbols.DOUBLE_LINE_VERTICAL);

        // corners
        graphics.setCharacter(0, 0, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
        graphics.setCharacter(screenSize.getColumns() - 1, 0, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
        graphics.setCharacter(0, screenSize.getRows() - 1, Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
        graphics.setCharacter(
                screenSize.getColumns() - 1, screenSize.getRows() - 1, Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);

        // bottom status pane
        final int statusTop = ui.status().top();
        graphics.setCharacter(0, statusTop - 1, Symbols.DOUBLE_LINE_T_RIGHT);
        graphics.setCharacter(screenSize.getColumns() - 1, statusTop - 1, Symbols.DOUBLE_LINE_T_LEFT);
        graphics.drawLine(1, statusTop - 1, screenSize.getColumns() - 2, statusTop - 1, Symbols.DOUBLE_LINE_HORIZONTAL);

        // right info pane
        final int infoLeft = ui.info().left();
        graphics.setCharacter(infoLeft - 1, 0, Symbols.DOUBLE_LINE_T_DOWN);
        graphics.drawLine(infoLeft - 1, 1, infoLeft - 1, screenSize.getRows() - 1, Symbols.DOUBLE_LINE_VERTICAL);
        graphics.setCharacter(infoLeft - 1, screenSize.getRows() - 3, Symbols.DOUBLE_LINE_CROSS);
        graphics.setCharacter(infoLeft - 1, screenSize.getRows() - 1, Symbols.DOUBLE_LINE_T_UP);

        // info title
        graphics.setCharacter(infoLeft - 1, 2, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
        graphics.setCharacter(screenSize.getColumns() - 1, 2, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
        graphics.drawLine(infoLeft, 2, screenSize.getColumns() - 2, 2, Symbols.SINGLE_LINE_HORIZONTAL);

        graphics.setForegroundColor(color(settings.colors().foreground().uiText(), ANSI.WHITE_BRIGHT));
        graphics.setBackgroundColor(color(settings.colors().background().uiText(), ANSI.BLACK));

        String inventory = bundle.getString("inventory");
        graphics.putString(infoLeft + (INFO_PANE_WIDTH - inventory.length()) / 2, 1, inventory);
    }

    private void iteractWithCave() throws IOException {
        // if the active map is the main map, we want to enter a cave, otherwise we want
        // to exit back to the main map
        final int x = player.getX();
        final int y = player.getY();
        if (activeMap == mainMap) {
            // enter cave
            String mapName = "%04d,%04d".formatted(x, y);
            TerrainMap caveMap = loadChildMap(mapName);
            player.moveTo(caveMap, caveMap.startingCoordinate());
            activeMap = caveMap;
        } else {
            // exit cave, to the coordinate that is the map name
            Matcher m = TerrainMap.COORDINATE_REGEX.matcher(activeMap.getName());
            if (m.find()) {
                player.moveTo(mainMap, Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                activeMap = mainMap;
            }
        }
        ui.map().draw();
        screen.refresh();
    }

    private TerrainMap loadChildMap(String mapName) {
        return TerrainMapBuilder.parseResources("META-INF/tqmaps/%s/%s".formatted(mainMap.getName(), mapName))
                .build(mapName);
    }

    private void saveGame() throws IOException {
        try {
            new Persistence(mapper).savePlayer(player, savePath);
            ui.status().drawMessage(bundle.getString("game.save.ok"), 2);
            // TODO: write success message
        } catch (IOException e) {
            ui.status()
                    .drawMessage(MessageFormat.format(bundle.getString("game.save.error"), e.getLocalizedMessage()), 2);
        }
        screen.refresh();
    }

    public static void main(String[] args) {
        try (Terminal terminal = new DefaultTerminalFactory().createTerminal()) {
            TerminalSize screenSize = terminal.getTerminalSize();
            if (screenSize.getColumns() < 30 || screenSize.getRows() < 10) {
                System.err.println("Terminal must be at least 30x10.");
                System.exit(1);
            }

            // create JSON mapper
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            // load color scheme
            String colorScheme = "default"; // TODO support command-line switch
            ColorScheme colors = mapper.readValue(
                    TextQuest.class
                            .getClassLoader()
                            .getResourceAsStream("META-INF/colors/%s.json".formatted(colorScheme)),
                    ColorScheme.class);

            // load map
            String mapName = "main"; // TODO support command-line switch
            TerrainMap mainMap = TerrainMapBuilder.parseResources("META-INF/tqmaps/%s".formatted(mapName))
                    .build(mapName);

            // create game settings
            Settings settings = new Settings(colors);

            // create player
            Player player = null;
            // TODO: allow save path command-line switch
            Path save = Paths.get("game.tqsave");
            if (Files.isReadable(save)) {
                player = new Persistence(mapper).loadPlayer(save);
            } else {
                player = new Player();
                player.moveTo(mainMap, mainMap.startingCoordinate());
            }

            // create text screen on top of our terminal
            Screen screen = new TerminalScreen(terminal);
            try {
                screen.startScreen();
                screen.setCursorPosition(null);
                TextQuest tq = new TextQuest(screen, settings, mainMap, player, mapper);
                tq.setSavePath(save);
                tq.run();
            } finally {
                screen.stopScreen();
            }
        } catch (IOException e) {
            System.err.println("I/O error with terminal (%s), bye!".formatted(e.getMessage()));
        }
    }
}
