package coding101.tq;

import static coding101.tq.domain.ColorPalette.color;
import static java.util.Objects.requireNonNull;

import coding101.tq.domain.Shop;
import coding101.tq.domain.items.InventoryItem;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor.ANSI;
import java.text.MessageFormat;
import java.util.List;
import java.util.MissingResourceException;
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

        List<InventoryItem> itemsForSale = shop.itemsForSale();

        final int top = top();
        final int left = left();
        final int width = width();

        if (itemsForSale.isEmpty()) {
            drawWrappedString(game.bundle().getString("shop.nothingForSale"), top, left, width);
            return;
        }

        int y = drawWrappedString(
                MessageFormat.format(game.bundle().getString("shop.itemsForSale.intro"), itemsForSale.size()),
                top,
                left,
                width);
        int i = 0;
        for (InventoryItem item : itemsForSale) {
            y++;
            String itemName = "%d. %s (%s) %s"
                    .formatted(
                            ++i, itemDisplayName(item), item.type().toString().toLowerCase(), itemDisplayValue(item));
            String itemCost = String.valueOf(item.price());
            drawItem(itemName, itemCost, left, y, width);
        }
    }

    private void drawItem(String label, String value, int col, int row, int width) {
        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().uiText(), ANSI.BLACK));

        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiText(), ANSI.WHITE_BRIGHT));
        game.textGraphics().putString(col, row, label);

        int valueDisplayCol = col + width - value.length();
        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiBorder(), ANSI.WHITE));
        for (int i = col + label.length(); i < valueDisplayCol; i++) {
            game.textGraphics().setCharacter(i, row, '.');
        }

        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiText(), ANSI.WHITE_BRIGHT));
        game.textGraphics().putString(valueDisplayCol, row, value);
    }

    private String itemDisplayName(InventoryItem item) {
        String label;
        try {
            label = game.bundle().getString("item." + item.name());
        } catch (MissingResourceException e) {
            label = item.name();
        }
        return label;
    }

    private String itemDisplayValue(InventoryItem item) {
        String value =
                switch (item.type()) {
                    case Armor -> "%+d DEF".formatted(item.strength());
                    case Weapon -> "%+d ATK".formatted(item.strength());
                    case Potion -> {
                        if (item.strength() < 0) {
                            yield "+MAX HLT";
                        }
                        yield "%+d HLT".formatted(item.strength());
                    }
                    default -> "";
                };
        return value;
    }

    private int drawWrappedString(String message, int top, int left, int maxWidth) {
        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiText(), ANSI.BLACK));
        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().uiText(), ANSI.WHITE_BRIGHT));

        int start = 0;
        int y = top;

        while (true) {
            int end = Math.min(start + maxWidth, message.length());
            if (end < message.length()) {
                while (end > start && message.charAt(end - 1) != ' ') {
                    end--;
                }
                if (end == start) {
                    // no space found... oh well
                    end = Math.min(start + maxWidth, message.length());
                }
            }
            String msg = message.substring(start, end);
            msg = msg.substring(0, Math.min(msg.length(), maxWidth));
            game.textGraphics().putString(left, y, msg);

            start = end;
            y++;
            if (end >= message.length()) {
                break;
            }
        }

        return y;
    }
}
