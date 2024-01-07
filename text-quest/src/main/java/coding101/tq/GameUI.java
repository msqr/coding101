package coding101.tq;

import static coding101.tq.domain.ColorPalette.color;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TextColor.ANSI;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;

/**
 * The game UI.
 */
public class GameUI implements Pane {

    private final Game game;
    private final MapPane map;
    private final InfoPane info;
    private final StatusPane status;
    private final HealthPane health;

    /**
     * Constructor.
     *
     * @param game         the game
     * @param timer        a task timer
     * @param infoWidth    the info pane width
     * @param statusHeight the status pane height
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public GameUI(final Game game, final Timer timer, final int infoWidth, final int statusHeight) {
        super();
        this.game = Objects.requireNonNull(game);
        this.map = new MapPane(game, infoWidth + 3, statusHeight + 3);
        this.info = new InfoPane(game, infoWidth, statusHeight + 3);
        this.status = new StatusPane(game, infoWidth + 3, statusHeight, timer);
        this.health = new HealthPane(game, infoWidth, statusHeight);
    }

    /**
     * Get the map pane.
     *
     * @return the map
     */
    public MapPane map() {
        return map;
    }

    /**
     * Get the status pane.
     *
     * @return the status
     */
    public StatusPane status() {
        return status;
    }

    /**
     * Get the info pane.
     *
     * @return the info
     */
    public InfoPane info() {
        return info;
    }

    /**
     * Get the health pane.
     *
     * @return the health
     */
    public HealthPane health() {
        return health;
    }

    @Override
    public int top() {
        return 1;
    }

    @Override
    public int left() {
        return 1;
    }

    @Override
    public int bottom() {
        return game.screen().getTerminalSize().getRows() - 1;
    }

    @Override
    public int right() {
        return game.screen().getTerminalSize().getColumns() - 1;
    }

    @Override
    public void draw() {
        // clear screen
        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiBorder(), ANSI.WHITE));
        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().uiBorder(), ANSI.BLACK));
        game.textGraphics().fill(' ');

        drawChrome();
        map().draw();
        info().draw();
        status().draw();
        health().draw();

        try {
            game.screen().refresh();
        } catch (IOException e) {
            throw new RuntimeException("Error refreshing screen: %s".formatted(e.getMessage()), e);
        }
    }

    /**
     * Draw a single pane and refresh the screen.
     *
     * @param pane the pane to draw
     */
    public void draw(Pane pane) {
        pane.draw();
        try {
            game.screen().refresh();
        } catch (IOException e) {
            throw new RuntimeException("Error refreshing screen: %s".formatted(e.getMessage()), e);
        }
    }

    /**
     * Draw the game "chrome" (borders).
     *
     * @throws IOException
     */
    public void drawChrome() {
        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiBorder(), ANSI.WHITE));
        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().uiBorder(), ANSI.BLACK));

        // top
        game.textGraphics()
                .drawLine(1, 0, game.screen().getTerminalSize().getColumns() - 2, 0, Symbols.DOUBLE_LINE_HORIZONTAL);

        // bottom
        game.textGraphics()
                .drawLine(
                        1,
                        game.screen().getTerminalSize().getRows() - 1,
                        game.screen().getTerminalSize().getColumns() - 2,
                        game.screen().getTerminalSize().getRows() - 1,
                        Symbols.DOUBLE_LINE_HORIZONTAL);

        // left
        game.textGraphics()
                .drawLine(0, 1, 0, game.screen().getTerminalSize().getRows() - 1, Symbols.DOUBLE_LINE_VERTICAL);

        // right
        game.textGraphics()
                .drawLine(
                        game.screen().getTerminalSize().getColumns() - 1,
                        1,
                        game.screen().getTerminalSize().getColumns() - 1,
                        game.screen().getTerminalSize().getRows() - 2,
                        Symbols.DOUBLE_LINE_VERTICAL);

        // corners
        game.textGraphics().setCharacter(0, 0, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
        game.textGraphics()
                .setCharacter(
                        game.screen().getTerminalSize().getColumns() - 1, 0, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
        game.textGraphics()
                .setCharacter(0, game.screen().getTerminalSize().getRows() - 1, Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
        game.textGraphics()
                .setCharacter(
                        game.screen().getTerminalSize().getColumns() - 1,
                        game.screen().getTerminalSize().getRows() - 1,
                        Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);

        // bottom status pane
        final int statusTop = status().top();
        game.textGraphics().setCharacter(0, statusTop - 1, Symbols.DOUBLE_LINE_T_RIGHT);
        game.textGraphics()
                .setCharacter(
                        game.screen().getTerminalSize().getColumns() - 1, statusTop - 1, Symbols.DOUBLE_LINE_T_LEFT);
        game.textGraphics()
                .drawLine(
                        1,
                        statusTop - 1,
                        game.screen().getTerminalSize().getColumns() - 2,
                        statusTop - 1,
                        Symbols.DOUBLE_LINE_HORIZONTAL);

        // right info pane
        final int infoLeft = info().left();
        game.textGraphics().setCharacter(infoLeft - 1, 0, Symbols.DOUBLE_LINE_T_DOWN);
        game.textGraphics()
                .drawLine(
                        infoLeft - 1,
                        1,
                        infoLeft - 1,
                        game.screen().getTerminalSize().getRows() - 1,
                        Symbols.DOUBLE_LINE_VERTICAL);
        game.textGraphics()
                .setCharacter(infoLeft - 1, game.screen().getTerminalSize().getRows() - 3, Symbols.DOUBLE_LINE_CROSS);
        game.textGraphics()
                .setCharacter(infoLeft - 1, game.screen().getTerminalSize().getRows() - 1, Symbols.DOUBLE_LINE_T_UP);

        // info title
        game.textGraphics().setCharacter(infoLeft - 1, 2, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
        game.textGraphics()
                .setCharacter(game.screen().getTerminalSize().getColumns() - 1, 2, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
        game.textGraphics()
                .drawLine(
                        infoLeft,
                        2,
                        game.screen().getTerminalSize().getColumns() - 2,
                        2,
                        Symbols.SINGLE_LINE_HORIZONTAL);

        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiText(), ANSI.WHITE_BRIGHT));
        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().uiText(), ANSI.BLACK));

        String inventory = game.bundle().getString("inventory");
        game.textGraphics().putString(infoLeft + (info.width() - inventory.length()) / 2, 1, inventory);
    }
}
