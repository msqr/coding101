package coding101.tq;

import static coding101.tq.util.CommandLineGameConfiguration.printErrorAndExit;

import coding101.tq.domain.ColorScheme;
import coding101.tq.domain.Player;
import coding101.tq.domain.Settings;
import coding101.tq.domain.TerrainMap;
import coding101.tq.domain.TerrainType;
import coding101.tq.util.BitSetJson;
import coding101.tq.util.CommandLineGameConfiguration;
import coding101.tq.util.CoordinateJson;
import coding101.tq.util.Persistence;
import coding101.tq.util.PlayerItemsJson;
import coding101.tq.util.TerrainMapBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.regex.Matcher;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Console based text adventure game.
 */
public class TextQuest {

    private static int INFO_PANE_WIDTH = 20;
    private static int STATUS_PANE_HEIGHT = 1;

    private static int MESSAGE_CLEAR_DELAY = 2;

    private static final int SHIP_COST = 100;

    private static char INTERACT_KEY = ' ';
    private static char SAVE_KEY = 's';

    private final Screen screen;
    private final Settings settings;
    private final TerrainMap mainMap;
    private final Player player;
    private final ObjectMapper mapper;
    private final TextGraphics graphics;
    private final ResourceBundle bundle;
    private final Timer timer;
    private final GameImpl game;
    private final GameUI ui;
    private TerrainMap activeMap;
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
        this.bundle = ResourceBundle.getBundle(getClass().getName());
        this.timer = new Timer("TQ Tasks", true);
        this.game = new GameImpl();
        this.ui = new GameUI(this.game, this.timer, INFO_PANE_WIDTH, STATUS_PANE_HEIGHT);

        if (player.getActiveMapName().equals(mainMap.getName())) {
            this.activeMap = mainMap;
        } else {
            this.activeMap = loadChildMap(player.getActiveMapName());
        }
    }

    private class GameImpl implements Game {

        @Override
        public ResourceBundle bundle() {
            return bundle;
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

        @Override
        public boolean readYesNo() throws IOException {
            KeyStroke keyStroke = screen.readInput();
            KeyType keyType = keyStroke != null ? keyStroke.getKeyType() : null;
            if (keyType == KeyType.Enter) {
                return true;
            }
            return false;
        }
    }

    private void setSavePath(Path path) {
        this.savePath = Objects.requireNonNull(path);
    }

    /**
     * Start the main game loop.
     *
     * This method will process key inputs and re-draw the game, until the game is
     * quit via the Escape key, Ctl-C, and so on.
     *
     * @throws IOException if an IO error occurs
     */
    public void run() throws IOException {
        ui.draw();
        while (true) {
            KeyStroke keyStroke = screen.readInput();

            // check for death
            if (player.getHealth() < 1) {
                return;
            }

            KeyType keyType = keyStroke != null ? keyStroke.getKeyType() : null;
            if (keyType == KeyType.Escape || keyType == KeyType.EOF) {
                return;
            }

            TerminalSize newSize = screen.doResizeIfNecessary();
            if (newSize != null) {
                ui.draw();
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
            if ((newX != player.getX() || newY != player.getY()) && player.canMoveTo(activeMap, newX, newY)) {
                // move player
                if (newX >= 0 && newY >= 0 && newX < activeMap.width() && newY < activeMap.height()) {
                    ui.map().movePlayer(newX, newY);

                    // redraw health in case that changed
                    ui.health().draw();

                    screen.refresh();

                    if (player.isDead()) {
                        death(0, bundle.getString("killed.terrain"));
                    }
                }
                continue;
            }

            if (keyType == KeyType.Character) {
                final char key = Character.toLowerCase(keyStroke.getCharacter().charValue());
                if (key == INTERACT_KEY) {
                    // check terrain for possible enter/exit
                    TerrainType t = activeMap.terrainAt(player.getX(), player.getY());
                    switch (t) {
                        case Cave -> interactWithCave();
                        case Chest -> interactWithChest();
                        case Ship, Water -> interactWithShip();
                        case Town -> interactWithTown();
                        default -> {
                            // nothing to do
                        }
                    }
                } else if (key == SAVE_KEY) {
                    // save game
                    saveGame();
                }
            }
        }
    }

    private void interactWithCave() {
        // if the active map is the main map, we want to enter a cave, otherwise we want
        // to exit back to the main map
        final int x = player.getX();
        final int y = player.getY();
        if (activeMap == mainMap) {
            // enter cave
            String mapName = "%04d,%04d".formatted(x, y);
            TerrainMap caveMap = loadChildMap(mapName);
            activeMap = caveMap;
            player.moveTo(caveMap, caveMap.startingCoordinate());
        } else {
            // exit cave, to the coordinate that is the map name
            Matcher m = TerrainMap.COORDINATE_REGEX.matcher(activeMap.getName());
            if (m.find()) {
                player.moveTo(mainMap, Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                activeMap = mainMap;
            }
        }
        ui.draw(ui.map());
    }

    private void interactWithTown() {
        // treat town the same as a cave... jump to new map
        interactWithCave();
    }

    private void interactWithChest() throws IOException {
        final int x = player.getX();
        final int y = player.getY();
        String message = null; // message to show the outcome of interacting with the chest
        if (player.interacted(game.map(), x, y)) {
            final GameConfiguration config = player.config();
            int coinsFound = 0;
            int damageTaken = 0;

            // TODO: open chest and deal with outcome: damage vs coins; decide first if
            // the chest provides coins or deducts health. Then decide either how many
            // coins to reward with, or health to deduct from, the player, updating the
            // coinsFound or damageTaken variables appropriately.

            if (coinsFound > 0) {
                message = MessageFormat.format(bundle.getString("chest.coinsAcquired"), coinsFound);
                player.addCoins(coinsFound);
                ui.info().draw();
            } else if (damageTaken > 0) {
                message = MessageFormat.format(bundle.getString("chest.damageTaken"), damageTaken);
                player.deductHealth(damageTaken);
                ui.health().draw();
            } else {
                message = bundle.getString("chest.empty");
            }
        } else {
            // show message that chest has already been opened
            message = bundle.getString("chest.alreadyOpened");
        }
        if (message != null) {
            ui.status().drawMessage(message, MESSAGE_CLEAR_DELAY);
        }

        // update coins display
        ui.info().drawCoins();

        screen.refresh();

        // check for death!
        if (player.getHealth() < 1) {
            int delay = message != null ? MESSAGE_CLEAR_DELAY : 0;
            death(delay, bundle.getString("killed.chest"));
        }
    }

    private void death(int delay, String message) throws IOException {
        if (delay > 0) {
            try {
                Thread.sleep(delay * 1000L);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        ui.status().drawMessage(message, -1);
        screen.refresh();
    }

    private void interactWithShip() throws IOException {
        String message = null; // message to show the outcome of interacting with the chest
        int clearDelay = MESSAGE_CLEAR_DELAY;
        if (player.onboard()) {
            // disembark!
            ui.status().drawMessage(bundle.getString("ship.askDisembark"), -1);
            screen.refresh();
            if (game.readYesNo()) {
                player.disembark();
                ui.status().drawMessage(bundle.getString("ship.disembarked"), MESSAGE_CLEAR_DELAY);
            } else {
                ui.status().drawMessage(null, -1);
            }
            screen.refresh();
        } else {
            if (player.getCoins() < SHIP_COST) {
                message = bundle.getString("ship.canNotAfford");
                clearDelay = MESSAGE_CLEAR_DELAY * 3;
            } else {
                // ask if player wants to hire the ship
                message = MessageFormat.format(bundle.getString("ship.askHire"), SHIP_COST);
                clearDelay = -1;
            }
            ui.status().drawMessage(message, clearDelay);
            screen.refresh();
            if (clearDelay < 0) {
                if (game.readYesNo()) {
                    message = bundle.getString("ship.hired");
                    player.board();
                    player.deductCoins(SHIP_COST);

                    // update coins display
                    ui.info().drawCoins();
                } else {
                    message = bundle.getString("ship.hireDeclined");
                }
                ui.status().drawMessage(message, MESSAGE_CLEAR_DELAY);
                screen.refresh();
            }
        }
    }

    private TerrainMap loadChildMap(String mapName) {
        return TerrainMapBuilder.parseResources("META-INF/tqmaps/%s/%s".formatted(mainMap.getName(), mapName))
                .build(mapName);
    }

    private void saveGame() throws IOException {
        try {
            new Persistence(mapper).savePlayer(player, savePath);
            ui.status().drawMessage(bundle.getString("game.save.ok"), MESSAGE_CLEAR_DELAY);
            // TODO: write success message
        } catch (IOException e) {
            ui.status()
                    .drawMessage(
                            MessageFormat.format(bundle.getString("game.save.error"), e.getLocalizedMessage()),
                            MESSAGE_CLEAR_DELAY);
        }
        screen.refresh();
    }

    private static void printHelp(Options options) {
        HelpFormatter help = new HelpFormatter();
        StringBuilder banner = new StringBuilder();
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(TextQuest.class.getResourceAsStream("banner.txt"), StandardCharsets.UTF_8))) {
            String line = null;
            while ((line = r.readLine()) != null) {
                banner.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            // ignore and continue
        }
        help.printHelp("<options>", banner.toString(), options, null);
    }

    private static CommandLine commandLine(String[] args) {
        Options opts = CommandLineGameConfiguration.cliOptions();
        try {
            return DefaultParser.builder()
                    .setStripLeadingAndTrailingQuotes(true)
                    .build()
                    .parse(opts, args);
        } catch (ParseException e) {
            System.err.println("Error parsing arguments: %s".formatted(e.getMessage()));
            printHelp(opts);
            System.exit(1);
        }
        return null;
    }

    public static void main(String[] args) {
        // parse arguments
        CommandLine cl = commandLine(args);

        if (cl.hasOption(CommandLineGameConfiguration.OPT_HELP)) {
            printHelp(CommandLineGameConfiguration.cliOptions());
            System.exit(0);
        }

        // create JSON mapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.registerModule(CoordinateJson.createCoordinateModule());
        mapper.registerModule(BitSetJson.createBitSetModule());
        mapper.registerSubtypes(PlayerItemsJson.itemSubTypes());

        // load main map
        TerrainMap mainMap = CommandLineGameConfiguration.map(cl);

        // create game settings
        ColorScheme colors = CommandLineGameConfiguration.colors(cl, mapper);
        Settings settings = new Settings(colors);

        // create game configuration
        GameConfiguration config = CommandLineGameConfiguration.parseConfiguration(cl);

        // create player
        Player player = null;
        Path save = Paths.get("game.tqsave");
        if (cl.hasOption(CommandLineGameConfiguration.OPT_SAVE_PATH)) {
            save = Paths.get(cl.getOptionValue(CommandLineGameConfiguration.OPT_SAVE_PATH));
        }
        if (Files.isReadable(save)) {
            try {
                player = new Persistence(mapper).loadPlayer(save);
                player.configure(config);
            } catch (IOException e) {
                printErrorAndExit("I/O error loading saved game file (%s): %s".formatted(save, e.getMessage()));
            }
        } else {
            player = new Player(config);
            player.moveTo(mainMap, mainMap.startingCoordinate());
        }

        // free CommandLine
        cl = null;

        try (Terminal terminal = new DefaultTerminalFactory().createTerminal()) {
            TerminalSize screenSize = terminal.getTerminalSize();
            if (screenSize.getColumns() < 30 || screenSize.getRows() < 10) {
                printErrorAndExit("Terminal must be at least 30x10.");
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
            printErrorAndExit("I/O error with terminal (%s), bye!".formatted(e.getMessage()));
        }
    }
}
