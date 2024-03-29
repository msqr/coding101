package coding101.tq;

import static coding101.tq.domain.ColorPalette.color;

import coding101.tq.domain.Shop;
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

    private final int mapRightOffset;
    private final int mapBottomOffset;
    private ShopPane shop;

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
        this.mapRightOffset = infoWidth + 3;
        this.mapBottomOffset = statusHeight + 3;
        this.map = new MapPane(game, mapRightOffset, mapBottomOffset);
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

    /**
     * Get the (optional) shop pane.
     *
     * @return the shop
     */
    public ShopPane shop() {
        return shop;
    }

    /**
     * Start a shop UI.
     *
     * @return the shop pane
     */
    public ShopPane startShop(Shop shop) {
        this.shop = new ShopPane(game, mapRightOffset, mapBottomOffset, shop);
        this.shop.draw();
        return this.shop;
    }

    /**
     * End the shop UI.
     */
    public void endShop() {
        if (this.shop != null) {
            this.shop = null;
            map.draw();
        }
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
        if (shop != null) {
            shop().draw();
        } else {
            map().draw();
        }
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
        final int statusBorderRow = status().top() - 1;
        game.textGraphics().setCharacter(0, statusBorderRow, Symbols.DOUBLE_LINE_T_RIGHT);
        game.textGraphics()
                .setCharacter(
                        game.screen().getTerminalSize().getColumns() - 1, statusBorderRow, Symbols.DOUBLE_LINE_T_LEFT);
        game.textGraphics()
                .drawLine(
                        1,
                        statusBorderRow,
                        game.screen().getTerminalSize().getColumns() - 2,
                        statusBorderRow,
                        Symbols.DOUBLE_LINE_HORIZONTAL);

        // right info pane
        final int infoBorderCol = info().left() - 1;
        game.textGraphics().setCharacter(infoBorderCol, 0, Symbols.DOUBLE_LINE_T_DOWN);
        game.textGraphics()
                .drawLine(
                        infoBorderCol,
                        1,
                        infoBorderCol,
                        game.screen().getTerminalSize().getRows() - 1,
                        Symbols.DOUBLE_LINE_VERTICAL);
        game.textGraphics()
                .setCharacter(infoBorderCol, game.screen().getTerminalSize().getRows() - 3, Symbols.DOUBLE_LINE_CROSS);
        game.textGraphics()
                .setCharacter(infoBorderCol, game.screen().getTerminalSize().getRows() - 1, Symbols.DOUBLE_LINE_T_UP);

        // info title
        game.textGraphics().setCharacter(infoBorderCol, 2, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
        game.textGraphics()
                .setCharacter(game.screen().getTerminalSize().getColumns() - 1, 2, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
        game.textGraphics()
                .drawLine(
                        infoBorderCol + 1,
                        2,
                        game.screen().getTerminalSize().getColumns() - 2,
                        2,
                        Symbols.SINGLE_LINE_HORIZONTAL);

        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().uiText(), ANSI.BLACK));
        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiText(), ANSI.WHITE_BRIGHT));

        String inventory = game.bundle().getString("inventory");
        game.textGraphics().putString(infoBorderCol + 1 + (info.width() - inventory.length()) / 2, 1, inventory);
    }
}
