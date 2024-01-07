package coding101.tq;

import static coding101.tq.domain.ColorPalette.color;

import coding101.tq.domain.ColorScheme;
import coding101.tq.domain.Player;
import coding101.tq.domain.Settings;
import coding101.tq.domain.TerrainMap;
import coding101.tq.domain.TerrainType;
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
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

/**
 * Console based text adventure game.
 */
public class TextQuest {

    private static int INFO_PANE_WIDTH = 20;

    private final Screen screen;
    private final Settings settings;
    private final TerrainMap mainMap;
    private final Player player;
    private final ObjectMapper mapper;
    private final TextGraphics graphics;
    private final ResourceBundle bundle;
    private TerrainMap activeMap;
    private TerminalSize screenSize;

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
        this.activeMap = mainMap;
        this.screenSize = screen.getTerminalSize();
        this.bundle = ResourceBundle.getBundle("coding101.tq.TextQuest");
    }

    public void run() throws IOException {
        setupScreen();
        while (true) {
            KeyStroke keyStroke = screen.readInput();
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
                    movePlayer(newX, newY);
                    screen.refresh();
                }
                continue;
            }

            if (keyType == KeyType.Character && keyStroke.getCharacter().charValue() == ' ') {
                // check terrain for possible enter/exit
                TerrainType t = activeMap.terrainAt(player.getX(), player.getY());
                switch (t) {
                    case Cave -> iteractWithCave();
                    default -> {
                        // nothing to do
                    }
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
        drawHealth();

        drawMapForPoint(activeMap, player.getX(), player.getY());

        drawPlayer();

        screen.refresh();
    }

    private final int infoPaneLeft() {
        return screenSize.getColumns() - INFO_PANE_WIDTH - 1;
    }

    private final int infoPaneTop() {
        return 3;
    }

    private final int statusPaneLeft() {
        return 1;
    }

    private final int statusPaneTop() {
        return screenSize.getRows() - 2;
    }

    private final int mapPaneTop() {
        return 1;
    }

    private final int mapPaneLeft() {
        return 1;
    }

    private final int mapPaneRight() {
        return infoPaneLeft() - 2;
    }

    private final int mapPaneBottom() {
        return statusPaneTop() - 2;
    }

    private final int mapPaneWidth() {
        return mapPaneRight() - mapPaneLeft() + 1;
    }

    private final int mapPaneHeight() {
        return mapPaneBottom() - mapPaneTop() + 1;
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
        graphics.setCharacter(0, statusPaneTop() - 1, Symbols.DOUBLE_LINE_T_RIGHT);
        graphics.setCharacter(screenSize.getColumns() - 1, statusPaneTop() - 1, Symbols.DOUBLE_LINE_T_LEFT);
        graphics.drawLine(
                1,
                statusPaneTop() - 1,
                screenSize.getColumns() - 2,
                statusPaneTop() - 1,
                Symbols.DOUBLE_LINE_HORIZONTAL);

        // right info pane
        graphics.setCharacter(infoPaneLeft() - 1, 0, Symbols.DOUBLE_LINE_T_DOWN);
        graphics.drawLine(
                infoPaneLeft() - 1, 1, infoPaneLeft() - 1, screenSize.getRows() - 1, Symbols.DOUBLE_LINE_VERTICAL);
        graphics.setCharacter(infoPaneLeft() - 1, screenSize.getRows() - 3, Symbols.DOUBLE_LINE_CROSS);
        graphics.setCharacter(infoPaneLeft() - 1, screenSize.getRows() - 1, Symbols.DOUBLE_LINE_T_UP);

        // info title
        graphics.setCharacter(infoPaneLeft() - 1, 2, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
        graphics.setCharacter(screenSize.getColumns() - 1, 2, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
        graphics.drawLine(infoPaneLeft(), 2, screenSize.getColumns() - 2, 2, Symbols.SINGLE_LINE_HORIZONTAL);

        graphics.setForegroundColor(color(settings.colors().foreground().uiText(), ANSI.WHITE_BRIGHT));
        graphics.setBackgroundColor(color(settings.colors().background().uiText(), ANSI.BLACK));

        String inventory = bundle.getString("inventory");
        graphics.putString(infoPaneLeft() + (INFO_PANE_WIDTH - inventory.length()) / 2, 1, inventory);
    }

    /** The amount of health each display heart represents. */
    public static final int PLAYER_HEALTH_HEART_VALUE = 5;

    private void drawHealth() {
        int health = player.getHealth();
        int partial = health % PLAYER_HEALTH_HEART_VALUE;
        int full = (health - partial) / PLAYER_HEALTH_HEART_VALUE;
        int y = statusPaneTop();
        int startX = infoPaneLeft();

        graphics.setForegroundColor(color(settings.colors().foreground().health(), ANSI.RED));
        graphics.setBackgroundColor(color(settings.colors().background().health(), ANSI.BLACK));

        // draw all full hearts
        for (int row = startX, max = startX + full; row < max; row++) {
            graphics.setCharacter(row, y, Symbols.HEART);
        }

        // if a partial heart, draw using a different color
        if (partial > 0) {
            graphics.setForegroundColor(color(settings.colors().foreground().healthPartial(), ANSI.RED_BRIGHT));
            graphics.setCharacter(startX + full, y, Symbols.HEART);
        }

        // draw blanks to "erase" any lost hearts
        for (int row = startX + full + (partial > 0 ? 1 : 0),
                        max = startX + Player.MAX_HEALTH / PLAYER_HEALTH_HEART_VALUE;
                row < max;
                row++) {
            graphics.setCharacter(row, y, ' ');
        }
    }

    private void drawMapForPoint(TerrainMap map, int x, int y) {
        final int paneWidth = mapPaneWidth();
        final int paneHeight = mapPaneHeight();
        final int paneTop = mapPaneTop();
        final int paneLeft = mapPaneLeft();
        final int startX = (x / paneWidth) * paneWidth;
        final int startY = (y / paneHeight) * paneHeight;
        map.walk(startX, startY, paneWidth, paneHeight, (col, row, t) -> {
            drawTerrain(col - startX + paneLeft, row - startY + paneTop, t);
            graphics.setForegroundColor(settings.colors().foreground().terrain(t, ANSI.WHITE_BRIGHT));
            graphics.setBackgroundColor(settings.colors().background().terrain(t, ANSI.BLACK));
            char c = t != null ? t.getKey() : TerrainType.EMPTY;
            if (c == TerrainType.WALL_CORNER || c == TerrainType.WALL_HORIZONTAL || c == TerrainType.WALL_VERTICAL) {
                c = Symbols.BLOCK_SOLID;
            }
            graphics.setCharacter(col - startX + paneLeft, row - startY + paneTop, c);
        });
    }

    private void drawTerrain(int screenCol, int screenRow, TerrainType t) {
        graphics.setForegroundColor(settings.colors().foreground().terrain(t, ANSI.WHITE_BRIGHT));
        graphics.setBackgroundColor(settings.colors().background().terrain(t, ANSI.BLACK));
        char c = t != null ? t.getKey() : TerrainType.EMPTY;
        if (c == TerrainType.WALL_CORNER || c == TerrainType.WALL_HORIZONTAL || c == TerrainType.WALL_VERTICAL) {
            c = Symbols.BLOCK_SOLID;
        }
        graphics.setCharacter(screenCol, screenRow, c);
    }

    private void drawPlayer() {
        final int paneWidth = mapPaneWidth();
        final int paneHeight = mapPaneHeight();
        final int paneTop = mapPaneTop();
        final int paneLeft = mapPaneLeft();
        final int startX = (player.getX() / paneWidth) * paneWidth;
        final int startY = (player.getY() / paneHeight) * paneHeight;
        graphics.setForegroundColor(color(settings.colors().foreground().player(), ANSI.WHITE_BRIGHT));
        graphics.setBackgroundColor(color(settings.colors().background().player(), ANSI.MAGENTA_BRIGHT));
        graphics.setCharacter(player.getX() - startX + paneLeft, player.getY() - startY + paneTop, '@');
    }

    private void movePlayer(int newX, int newY) {
        // draw old position terrain
        final int paneWidth = mapPaneWidth();
        final int paneHeight = mapPaneHeight();
        final int paneTop = mapPaneTop();
        final int paneLeft = mapPaneLeft();
        final int startX = (player.getX() / paneWidth) * paneWidth;
        final int startY = (player.getY() / paneHeight) * paneHeight;

        final int newStartX = (newX / paneWidth) * paneWidth;
        final int newStartY = (newY / paneHeight) * paneHeight;
        if (newStartX != startX || newStartY != startY) {
            // redraw entire map
            drawMapForPoint(activeMap, newX, newY);
        } else {
            TerrainType t = activeMap.terrainAt(player.getX(), player.getY());
            drawTerrain(player.getX() - startX + paneLeft, player.getY() - startY + paneTop, t);
        }
        player.moveTo(activeMap, newX, newY);
        drawPlayer();
    }

    private void iteractWithCave() throws IOException {
        // if the active map is the main map, we want to enter a cave, otherwise we want
        // to exit back to the main map
        final int x = player.getX();
        final int y = player.getY();
        if (activeMap == mainMap) {
            // enter cave
            String mapName = "%04d,%04d".formatted(x, y);
            TerrainMap caveMap = TerrainMapBuilder.parseResources(
                            "META-INF/tqmaps/%s/%s".formatted(mainMap.getName(), mapName))
                    .build(mapName);
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
        drawMapForPoint(activeMap, player.getX(), player.getY());
        drawPlayer();
        screen.refresh();
    }

    public static void main(String[] args) {
        try (Terminal terminal = new DefaultTerminalFactory().createTerminal()) {
            TerminalSize screenSize = terminal.getTerminalSize();
            if (screenSize.getColumns() < 30 || screenSize.getRows() < 18) {
                System.err.println("Terminal must be at least 80x24.");
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
            // TODO: load saved player
            Player player = new Player();
            player.moveTo(mainMap, mainMap.startingCoordinate());

            // create text screen on top of our terminal
            Screen screen = new TerminalScreen(terminal);
            try {
                screen.startScreen();
                screen.setCursorPosition(null);
                TextQuest tq = new TextQuest(screen, settings, mainMap, player, mapper);
                tq.run();
            } finally {
                screen.stopScreen();
            }
        } catch (IOException e) {
            System.err.println("I/O error with terminal (%s), bye!".formatted(e.getMessage()));
        }
    }
}
