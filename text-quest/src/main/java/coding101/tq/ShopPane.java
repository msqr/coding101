package coding101.tq;

import static java.util.Objects.requireNonNull;

import coding101.tq.domain.Shop;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import java.util.Objects;

/**
 * A shop pane.
 *
 * This is a transient pane that takes over the map pane when interacting with a
 * shop.
 */
public class ShopPane implements Pane {

    private final Game game;
    private final int rightOffset;
    private final int bottomOffset;
    private final Shop shop;

    /**
     * Constructor.
     *
     * @param shop the shop
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public ShopPane(Game game, int rightOffset, int bottomOffset, Shop shop) {
        super();
        this.game = requireNonNull(game);
        this.rightOffset = rightOffset;
        this.bottomOffset = bottomOffset;
        this.shop = Objects.requireNonNull(shop);
    }

    /**
     * Get the shop.
     *
     * @return the shop
     */
    public Shop getShop() {
        return shop;
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
        return game.screen().getTerminalSize().getRows() - bottomOffset;
    }

    @Override
    public int right() {
        return game.screen().getTerminalSize().getColumns() - rightOffset;
    }

    @Override
    public void draw() {
        // clear screen
        game.textGraphics()
                .fillRectangle(new TerminalPosition(top(), left()), new TerminalSize(width(), height()), ' ');
    }
}
