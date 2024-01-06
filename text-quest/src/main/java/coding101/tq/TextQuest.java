package coding101.tq;

import static coding101.tq.domain.ColorScheme.color;

import coding101.tq.domain.ColorScheme;
import coding101.tq.domain.Player;
import coding101.tq.domain.Settings;
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

/**
 * Console based text adventure game.
 */
public class TextQuest {

    private static int INFO_PANE_WIDTH = 20;

    private final Screen screen;
    private final Settings settings;
    private final Player player;
    private final ObjectMapper mapper;
    private final TextGraphics graphics;
    private final TerminalSize screenSize;
    private final ResourceBundle bundle;

    /**
     * Constructor.
     *
     * @param screen   the screen to render to
     * @param settings the game settings
     * @param player   the player
     * @param mapper   the JSON mapper
     * @throws IllegalArgumentException if any argument is {@literal null}
     */
    public TextQuest(Screen screen, Settings settings, Player player, ObjectMapper mapper) {
        super();
        this.screen = Objects.requireNonNull(screen);
        this.settings = Objects.requireNonNull(settings);
        this.player = Objects.requireNonNull(player);
        this.mapper = Objects.requireNonNull(mapper);
        this.graphics = screen.newTextGraphics();
        this.screenSize = screen.getTerminalSize();
        bundle = ResourceBundle.getBundle("coding101.tq.TextQuest");
    }

    public void start() throws IOException {
        // clear screen
        graphics.setForegroundColor(color(settings.colors().uiBorder(), ANSI.WHITE));
        graphics.setBackgroundColor(color(settings.colors().uiBorderBg(), ANSI.BLACK));
        graphics.fill(' ');

        drawChrome();
        drawHealth();
        screen.refresh();
        while (true) {
            KeyStroke keyStroke = screen.readInput();
            KeyType keyType = keyStroke != null ? keyStroke.getKeyType() : null;
            if (keyType == KeyType.Escape || keyType == KeyType.EOF) {
                return;
            }
            Thread.yield();
        }
    }

    private final int infoPaneLeft() {
        return screenSize.getColumns() - INFO_PANE_WIDTH - 2;
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

    private void drawChrome() throws IOException {
        graphics.setForegroundColor(color(settings.colors().uiBorder(), ANSI.WHITE));
        graphics.setBackgroundColor(color(settings.colors().uiBorderBg(), ANSI.BLACK));

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
        graphics.setCharacter(infoPaneLeft() - 1, screenSize.getRows() - 3, Symbols.DOUBLE_LINE_T_UP);
        graphics.drawLine(
                infoPaneLeft() - 1, 1, infoPaneLeft() - 1, screenSize.getRows() - 4, Symbols.DOUBLE_LINE_VERTICAL);

        // info title
        graphics.setCharacter(infoPaneLeft() - 1, 2, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
        graphics.setCharacter(screenSize.getColumns() - 1, 2, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
        graphics.drawLine(infoPaneLeft(), 2, screenSize.getColumns() - 2, 2, Symbols.SINGLE_LINE_HORIZONTAL);

        graphics.setForegroundColor(color(settings.colors().uiText(), ANSI.WHITE_BRIGHT));
        graphics.setBackgroundColor(color(settings.colors().uiTextBg(), ANSI.BLACK));

        String inventory = bundle.getString("inventory");
        graphics.putString(infoPaneLeft() + (INFO_PANE_WIDTH - inventory.length()) / 2, 1, inventory);
    }

    /** The amount of health each display heart represents. */
    public static final int PLAYER_HEALTH_HEART_VALUE = 10;

    private void drawHealth() {
        int health = player.getHealth();
        int partial = health % PLAYER_HEALTH_HEART_VALUE;
        int full = (health - partial) / PLAYER_HEALTH_HEART_VALUE;

        graphics.setForegroundColor(color(settings.colors().health(), ANSI.RED));
        graphics.setBackgroundColor(color(settings.colors().healthBg(), ANSI.BLACK));

        // draw all full hearts
        for (int row = statusPaneLeft(), max = statusPaneLeft() + full; row < max; row++) {
            graphics.setCharacter(row, statusPaneTop(), Symbols.HEART);
        }

        // if a partial heart, draw using a different color
        if (partial > 0) {
            graphics.setForegroundColor(color(settings.colors().healthPartial(), ANSI.RED_BRIGHT));
            graphics.setCharacter(statusPaneLeft() + full, statusPaneTop(), Symbols.HEART);
        }

        // draw blanks to "erase" any lost hearts
        for (int row = statusPaneLeft() + full + (partial > 0 ? 1 : 0),
                        max = statusPaneLeft() + Player.MAX_HEALTH / PLAYER_HEALTH_HEART_VALUE;
                row < max;
                row++) {
            graphics.setCharacter(row, statusPaneTop(), ' ');
        }
    }

    public static void main(String[] args) {
        try (Terminal terminal = new DefaultTerminalFactory().createTerminal()) {
            TerminalSize screenSize = terminal.getTerminalSize();
            if (screenSize.getColumns() < 80 || screenSize.getRows() < 24) {
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

            // create game settings
            Settings settings = new Settings(colors);

            // create player
            // TODO: load saved player
            Player player = new Player();

            // create text screen on top of our terminal
            Screen screen = new TerminalScreen(terminal);
            try {
                screen.startScreen();
                screen.setCursorPosition(null);
                TextQuest tq = new TextQuest(screen, settings, player, mapper);
                tq.start();
            } finally {
                screen.stopScreen();
            }
        } catch (IOException e) {
            System.err.println("I/O error with terminal (%s), bye!".formatted(e.getMessage()));
        }
    }
}
