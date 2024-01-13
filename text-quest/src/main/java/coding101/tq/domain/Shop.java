/**
 *
 */
package coding101.tq.domain;

import static java.util.Objects.requireNonNull;

import coding101.tq.domain.items.InventoryItem;
import java.util.Collections;
import java.util.List;

/**
 * A shop helper class, to facilitate the buying and selling of items.
 */
public class Shop {

    private final PlayerItems gameItems;
    private final Player player;
    private final double purchaseRateDiscount;

    private final List<InventoryItem> itemsForSale;

    /**
     * Constructor.
     *
     * @param gameItems            all possible items
     * @param player               the player
     * @param purchaseRateDiscount a percentage (0-1) discount to apply when
     *                             offering to purchase an item; the discount is
     *                             applied to the original sale price
     * @param sellItemsMaximum     the maximum number of items the shop can sell
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public Shop(PlayerItems gameItems, Player player, double purchaseRateDiscount, int sellItemsMaximum) {
        super();
        this.gameItems = requireNonNull(gameItems);
        this.player = requireNonNull(player);
        this.purchaseRateDiscount = purchaseRateDiscount;
        this.itemsForSale = generateItemsForSale(gameItems, player, sellItemsMaximum);
    }

    private static List<InventoryItem> generateItemsForSale(PlayerItems gameItems, Player player, int maxItems) {
        // TODO: generate a list of at most maxItems items to offer for sale to the
        // player. Only items whose minimumXp is less than, or equal to, the player's xp
        // should be offered for sale. The offered item selection should be WEIGHTED
        // such that the higher an item's minimumXp is, the LESS LIKELY that item will
        // be offered.

        return Collections.emptyList();
    }

    /**
     * Get the items for sale.
     *
     * @return the items for sale, never {@code null}
     */
    public List<InventoryItem> itemsForSale() {
        return itemsForSale;
    }
}
