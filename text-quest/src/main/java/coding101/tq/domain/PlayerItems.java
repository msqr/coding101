package coding101.tq.domain;

import coding101.tq.domain.items.InventoryItem;
import java.util.Collection;
import java.util.Collections;

/**
 * The player item list.
 */
public class PlayerItems {

    // TODO: implement field(s) to track the collection of items
    // TODO: should there be a limit to the number of items?
    // TODO: should there be a limit to the number of equipped items?

    /**
     * Constructor.
     */
    public PlayerItems() {
        super();
    }

    /**
     * Get a collection of all inventory items.
     *
     * @return
     */
    public Collection<InventoryItem> allItems() {
        // TODO: implement
        return Collections.emptyList();
    }

    /**
     * Add an item to the inventory.
     *
     * @param item the item to add
     */
    public void addItem(InventoryItem item) {
        // TODO: implement
    }

    /**
     * Remove an item from the inventory.
     *
     * @param item the item to remove
     */
    public void removeItem(InventoryItem item) {
        // TODO: implement
    }

    /**
     * Get the total defensive offset of all equipped items.
     *
     * @return the defensive offset total
     */
    public int equippedDefensiveOffsetTotal() {
        // TODO: return the total defensive offset
        return 0;
    }

    /**
     * Get the total offensive offset of all equipped items.
     *
     * @return the offensive offset total
     */
    public int equippedOffensiveOffsetTotal() {
        // TODO: return the total offensive offset
        return 0;
    }

    /**
     * Test if an equipped item provides immunity to a given terrain.
     *
     * @param terrain the terrain to test
     * @return {@code true} if some equipped item provides immunity to the given
     *         terrain
     */
    public boolean immuneTo(TerrainType terrain) {
        // TODO: return true if some equipped inventory item provides immunity
        return false;
    }

    /**
     * Equip or use an item.
     *
     * If the item can be equipped and the type is a singleton, then any currently
     * equipped item of the same type will be un-equipped first.
     *
     * @param item the item to equip
     */
    public void apply(InventoryItem item, Player player) {
        // TODO: equip or use the item
        // TODO: might have to un-equip an item of same type first
    }
}
