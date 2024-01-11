package coding101.tq.domain;

import coding101.tq.domain.items.InventoryItem;
import java.util.ArrayList;
import java.util.List;

/**
 * The player item list.
 */
public class PlayerItems {

    private List<InventoryItem> items = new ArrayList<>(5);

    /**
     * Constructor.
     */
    public PlayerItems() {
        super();
    }

    /**
     * Get the collection of all inventory items.
     *
     * @return the items
     */
    public List<InventoryItem> getItems() {
        return items;
    }

    /**
     * Set the collection of all inventory items.
     *
     * @return the items
     */
    public void setItems(List<InventoryItem> items) {
        this.items = items != null ? items : new ArrayList<>(4);
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
     * @param item   the item to equip
     * @param player the player to eqip the item on
     */
    public void apply(InventoryItem item, Player player) {
        // TODO: equip or use the item
        // TODO: might have to un-equip an item of same type first
    }

    /**
     * Unequip an item, but keep it in the inventory.
     *
     * @param item   the item to unequip
     * @param player the player
     */
    public void stash(InventoryItem item, Player player) {
        // TODO: unequip the item
    }
}
