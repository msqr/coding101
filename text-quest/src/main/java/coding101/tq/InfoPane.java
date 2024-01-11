package coding101.tq;

import static coding101.tq.domain.ColorPalette.color;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import coding101.tq.domain.PlayerItems;
import coding101.tq.domain.items.InventoryItem;
import coding101.tq.domain.items.ItemType;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TextColor.ANSI;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * The UI info pane.
 */
public class InfoPane implements Pane {

    private final NumberFormat fmt = NumberFormat.getNumberInstance();

    private final Game game;
    private final int width;
    private final int bottomOffset;

    private int scrollOffset = 0; // used to "scroll" display

    /**
     * Constructor.
     *
     * @param game         the game
     * @param rightOffset  the width
     * @param bottomOffset the height to offset the bottom from the screen
     *                     dimensions
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public InfoPane(Game game, int width, int bottomOffset) {
        super();
        this.game = requireNonNull(game);
        this.width = width;
        this.bottomOffset = bottomOffset;
    }

    @Override
    public int top() {
        return 3;
    }

    @Override
    public int left() {
        return game.screen().getTerminalSize().getColumns() - width - 1;
    }

    @Override
    public int bottom() {
        return game.screen().getTerminalSize().getRows() - bottomOffset;
    }

    @Override
    public int right() {
        return game.screen().getTerminalSize().getColumns() - 2;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public void draw() {
        drawCoins();
        drawItems();
    }

    private void drawItemCount(String label, int count, int col, int row) {
        drawItem(label, fmt.format(count), col, row);
    }

    private void drawItem(String label, String value, int col, int row) {
        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().uiText(), ANSI.BLACK));

        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiText(), ANSI.WHITE_BRIGHT));
        game.textGraphics().putString(col, row, label);

        int coinDisplayCol = col + width - value.length();
        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiBorder(), ANSI.WHITE));
        for (int i = col + label.length(); i < coinDisplayCol; i++) {
            game.textGraphics().setCharacter(i, row, '.');
        }

        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiText(), ANSI.WHITE_BRIGHT));
        game.textGraphics().putString(coinDisplayCol, row, value);
    }

    /**
     * Update the coins display.
     */
    public void drawCoins() {
        if (scrollOffset > 0) {
            // coins scrolled away
            return;
        }
        final int top = top();
        final int left = left();
        final int coins = game.player().getCoins();

        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().uiText(), ANSI.BLACK));

        String label = game.bundle().getString("coins.label");
        drawItemCount(label, coins, left, top);
    }

    /**
     * Update the inventory items display.
     */
    public void drawItems() {
        final int top = top() + (scrollOffset < 1 ? 1 : 0);
        final int bottom = bottom();
        final int left = left();
        final int right = right();

        final PlayerItems items = game.player().getItems();
        final Collection<InventoryItem> allItems = items.getItems();
        final int displayStart = Math.max(0, scrollOffset - 1);
        final int displayEnd = Math.min(displayStart + height(), allItems.size());

        int currItemIndex = 0;
        int displayRow = top;

        // show equipped items by type
        Map<ItemType, List<InventoryItem>> equippedItemsByType = allItems.stream()
                .filter(InventoryItem::isEquipped)
                .collect(groupingBy(InventoryItem::type, () -> new EnumMap<>(ItemType.class), toList()));
        for (List<InventoryItem> typeItems : equippedItemsByType.values()) {
            for (InventoryItem item : typeItems) {
                if (currItemIndex < displayStart) {
                    currItemIndex++;
                    continue;
                }
                if (currItemIndex >= displayEnd) {
                    break;
                }
                String label = itemDisplayName(item);
                String value = itemDisplayValue(item);
                drawItem(label, value, left, displayRow++);
                currItemIndex++;
            }
        }

        // draw horizontal rule before non-equipped items
        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().uiBorder(), ANSI.BLACK));
        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiBorder(), ANSI.WHITE));

        game.textGraphics().setCharacter(left - 1, displayRow, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
        game.textGraphics().drawLine(left, displayRow, right, displayRow, Symbols.SINGLE_LINE_HORIZONTAL);
        game.textGraphics().setCharacter(right + 1, displayRow, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
        displayRow++;

        // group non-equipped items by type
        Map<ItemType, List<InventoryItem>> itemsByType = allItems.stream()
                .filter(item -> !item.isEquipped())
                .collect(groupingBy(InventoryItem::type, () -> new EnumMap<>(ItemType.class), toList()));
        int itemNum = 0;
        for (List<InventoryItem> typeItems : itemsByType.values()) {
            for (InventoryItem item : typeItems) {
                if (currItemIndex < displayStart) {
                    currItemIndex++;
                    continue;
                }
                if (currItemIndex >= displayEnd) {
                    break;
                }
                String label = itemDisplayName(item);
                label = "% 2d %s".formatted(++itemNum, label);
                String value = itemDisplayValue(item);
                drawItem(label, value, left, displayRow++);
                currItemIndex++;
            }
        }

        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiText(), ANSI.WHITE_BRIGHT));
        while ((++displayRow) <= bottom) {
            for (int i = left; i <= right; i++) {
                game.textGraphics().setCharacter(i, displayRow, ' ');
            }
        }
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
}
