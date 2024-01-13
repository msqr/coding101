package coding101.tq.util;

import coding101.tq.GameConfiguration;
import coding101.tq.TextQuest;
import coding101.tq.domain.ColorScheme;
import coding101.tq.domain.PlayerItems;
import coding101.tq.domain.TerrainMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Helper to configure a {@link GameConfiguration} instance from command line
 * arguments.
 */
public final class CommandLineGameConfiguration {

    /** The starting coins CLI option. */
    public static final char OPT_COINS = 'c';

    /** The chest coins maximum CLI option. */
    public static final char OPT_CHEST_COINS_MAX = 'C';

    /** The chest reward factor CLI option. */
    public static final char OPT_CHEST_REWARD_FACTOR = 'l';

    /** The chest health damage maximum CLI option. */
    public static final char OPT_CHEST_DAMAGE_MAX = 'P';

    /** The color scheme directory CLI option. */
    public static final char OPT_COLORS_DIR = 'K';

    /** The color scheme name CLI option. */
    public static final char OPT_COLORS_NAME = 'k';

    /** The help CLI option. */
    public static final char OPT_HELP = 'h';

    /** The items directory CLI option. */
    public static final char OPT_ITEMS_DIR = 'I';

    /** The items name CLI option. */
    public static final char OPT_ITEMS_NAME = 'i';

    /** The map root directory path CLI option. */
    public static final char OPT_MAIN_MAP_DIR = 'd';

    /** The main map name CLI option. */
    public static final char OPT_MAIN_MAP_NAME = 'm';

    /** The game save file path CLI option. */
    public static final char OPT_SAVE_PATH = 'f';

    /** The "reveal map" flag CLI option. */
    public static final char OPT_REVEAL_MAP = 'r';

    /** The GUI flag option. */
    public static final char OPT_GUI = 'g';

    /** The experience points option. */
    public static final char OPT_XP = 'x';

    private CommandLineGameConfiguration() {
        // not available
    }

    /**
     * Print an error message to STDERR and exit.
     *
     * @param msg the message to display
     */
    public static void printErrorAndExit(String msg) {
        System.err.println(msg);
        System.err.println("Pass --help for command line argument help.");
        System.exit(1);
    }

    /**
     * Get the command line options.
     *
     * @return the options
     */
    public static Options cliOptions() {
        Options options = new Options();
        options.addOption(Option.builder(String.valueOf(OPT_HELP))
                .longOpt("help")
                .desc("show usage information")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_COINS))
                .longOpt("coins")
                .hasArg()
                .desc("starting number of coins")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_CHEST_COINS_MAX))
                .longOpt("chest-coins")
                .hasArg()
                .desc("maximum number of coins a chest can provide")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_CHEST_REWARD_FACTOR))
                .longOpt("chest-luck")
                .hasArg()
                .desc("a percentage from 1-100 that a chest will reward rather than penalise")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_CHEST_DAMAGE_MAX))
                .longOpt("chest-damage")
                .hasArg()
                .desc("the maximum amount of health a chest can damage the player")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_XP))
                .longOpt("xp")
                .hasArg()
                .desc("starting experience points")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_COLORS_DIR))
                .longOpt("colors-dir")
                .hasArg()
                .desc("the colors directory path")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_COLORS_NAME))
                .longOpt("colors")
                .hasArg()
                .desc("the colors name to load")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_ITEMS_DIR))
                .longOpt("items-dir")
                .hasArg()
                .desc("the items directory path")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_ITEMS_NAME))
                .longOpt("items")
                .hasArg()
                .desc("the items name to load")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_MAIN_MAP_DIR))
                .longOpt("map-dir")
                .hasArg()
                .desc("the main map directory path")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_MAIN_MAP_NAME))
                .longOpt("map")
                .hasArg()
                .desc("the main map name to load")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_SAVE_PATH))
                .longOpt("save-file")
                .hasArg()
                .desc("the save file path to use")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_REVEAL_MAP))
                .longOpt("reveal-map")
                .desc("make the map completely visible")
                .build());
        options.addOption(Option.builder(String.valueOf(OPT_GUI))
                .longOpt("gui")
                .desc("use the image texture GUI renderer")
                .build());
        return options;
    }

    /**
     * Load the main map based on the command line options.
     *
     * @param cl the command line
     * @return the main map
     */
    public static TerrainMap map(CommandLine cl) {
        // load main map
        String mapPath = "META-INF/tqmaps";
        if (cl.hasOption(OPT_MAIN_MAP_DIR)) {
            mapPath = cl.getOptionValue(OPT_MAIN_MAP_DIR);
        }
        String mapName = "main";
        if (cl.hasOption(OPT_MAIN_MAP_NAME)) {
            mapName = cl.getOptionValue(OPT_MAIN_MAP_NAME);
        }
        try {
            return TerrainMapBuilder.parseResources("%s/%s".formatted(mapPath, mapName))
                    .build(mapName);
        } catch (IllegalArgumentException e) {
            printErrorAndExit(e.getMessage());
            return null;
        }
    }

    /**
     * Get the color scheme to use.
     *
     * @param cl     the command line
     * @param mapper the JSON mapper
     * @return the color scheme
     */
    public static ColorScheme colors(CommandLine cl, ObjectMapper mapper) {
        // load color scheme
        String colorSchemeDir = "META-INF/tqcolors";
        if (cl.hasOption(OPT_COLORS_DIR)) {
            colorSchemeDir = cl.getOptionValue(OPT_COLORS_DIR);
        }
        String colorScheme = "main";
        if (cl.hasOption(OPT_COLORS_NAME)) {
            colorScheme = cl.getOptionValue(OPT_COLORS_NAME);
        }
        InputStream in = TextQuest.class
                .getClassLoader()
                .getResourceAsStream("%s/%s.json".formatted(colorSchemeDir, colorScheme));
        try {
            if (in == null) {
                // try as file path
                in = Files.newInputStream(Paths.get(colorSchemeDir, colorScheme));
            }
            return mapper.readValue(in, ColorScheme.class);
        } catch (NoSuchFileException e) {
            printErrorAndExit("Color scheme file %s not found!".formatted(colorScheme));
        } catch (IOException e) {
            printErrorAndExit("Error reading color scheme %s: %s".formatted(colorScheme, e.getMessage()));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return null;
    }

    /**
     * Get the player items to use.
     *
     * @param cl     the command line
     * @param mapper the JSON mapper
     * @return the items
     */
    public static PlayerItems items(CommandLine cl, ObjectMapper mapper) {
        // load color scheme
        String itemsDir = "META-INF/tqitems";
        if (cl.hasOption(OPT_COLORS_DIR)) {
            itemsDir = cl.getOptionValue(OPT_ITEMS_DIR);
        }
        String itemsName = "main";
        if (cl.hasOption(OPT_COLORS_NAME)) {
            itemsName = cl.getOptionValue(OPT_ITEMS_NAME);
        }
        InputStream in =
                TextQuest.class.getClassLoader().getResourceAsStream("%s/%s.json".formatted(itemsDir, itemsName));
        try {
            if (in == null) {
                // try as file path
                in = Files.newInputStream(Paths.get(itemsDir, itemsName));
            }
            return mapper.readValue(in, PlayerItems.class);
        } catch (NoSuchFileException e) {
            printErrorAndExit("Items file %s not found!".formatted(itemsName));
        } catch (IOException e) {
            printErrorAndExit("Error reading items %s: %s".formatted(itemsName, e.getMessage()));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return null;
    }

    /**
     * Parse the command line options into a {@link GameConfiguration}.
     *
     * @param cl the command line
     * @return the configuration
     */
    public static GameConfiguration parseConfiguration(CommandLine cl) {
        // start with defaults
        GameConfiguration config = GameConfiguration.DEFAULTS;

        if (cl.hasOption(OPT_COINS)) {
            try {
                int coins = Integer.parseInt(cl.getOptionValue(OPT_COINS));
                if (coins < 0) {
                    throw new IllegalArgumentException();
                }
                config = config.withInitialCoins(coins);
            } catch (Exception e) {
                printErrorAndExit("The --coins argument must be a number 0 or more.");
            }
        }

        if (cl.hasOption(OPT_CHEST_COINS_MAX)) {
            try {
                int coins = Integer.parseInt(cl.getOptionValue(OPT_CHEST_COINS_MAX));
                if (coins < 0) {
                    throw new IllegalArgumentException();
                }
                config = config.withChestCoinsMaximum(coins);
            } catch (Exception e) {
                printErrorAndExit("The --chest-coins argument must be a number 0 or more.");
            }
        }

        if (cl.hasOption(OPT_CHEST_REWARD_FACTOR)) {
            try {
                int factor = Integer.parseInt(cl.getOptionValue(OPT_CHEST_REWARD_FACTOR));
                if (factor < 1 || factor > 100) {
                    throw new IllegalArgumentException();
                }
                config = config.withChestRewardFactor(factor);
            } catch (Exception e) {
                printErrorAndExit("The --chest-luck argument must be a number between 1 and 100.");
            }
        }

        if (cl.hasOption(OPT_CHEST_DAMAGE_MAX)) {
            try {
                int max = Integer.parseInt(cl.getOptionValue(OPT_CHEST_DAMAGE_MAX));
                if (max < 0) {
                    throw new IllegalArgumentException();
                }
                config = config.withChestHeathDamageMaximum(max);
            } catch (Exception e) {
                printErrorAndExit("The --chest-damage argument must be a number 0 or more.");
            }
        }

        if (cl.hasOption(OPT_XP)) {
            try {
                int xp = Integer.parseInt(cl.getOptionValue(OPT_XP));
                if (xp < 0) {
                    throw new IllegalArgumentException();
                }
                config = config.withInitialXp(xp);
            } catch (Exception e) {
                printErrorAndExit("The --xp argument must be a number 0 or more.");
            }
        }

        if (cl.hasOption(OPT_REVEAL_MAP)) {
            config = config.withRevealMap(true);
        }

        if (cl.hasOption(OPT_GUI)) {
            config = config.withGui(true);
        }

        return config;
    }
}
