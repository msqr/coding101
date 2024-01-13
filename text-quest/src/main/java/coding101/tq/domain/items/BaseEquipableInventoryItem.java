package coding101.tq.domain.items;

import coding101.tq.domain.Player;

/**
 * An abstract {@link InventoryItem} that can be eqipped.
 */
public abstract class BaseEquipableInventoryItem extends BaseInventoryItem {

    private boolean equipped;

    /**
     * Constructor.
     *
     * @param type      the item type
     * @param name      the item name
     * @param minimumXp the minimum experience points required to use
     * @param price     the price to purchase, in coins
     */
    public BaseEquipableInventoryItem(ItemType type, String name, int minimumXp, int price) {
        super(type, name, minimumXp, price);
    }

    @Override
    public boolean canEquip() {
        return true;
    }

    @Override
    public boolean isEquipped() {
        return equipped;
    }

    /**
     * Set the eqipped state.
     *
     * @param equipped the equipped state
     */
    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    @Override
    public final boolean apply(Player player) {
        boolean result = !equipped;
        equipped = true;
        equip(player);
        return result;
    }

    /**
     * Equip the item on a player.
     *
     * @param player the player to equip the item on
     */
    protected void equip(Player player) {
        // extending classes can override
    }

    @Override
    public final boolean stash(Player player) {
        boolean result = equipped;
        equipped = false;
        unEquip(player);
        return result;
    }

    /**
     * Un-equip the item on a player.
     *
     * @param player the player to un-equip the item from
     */
    protected void unEquip(Player player) {
        // extending classes can override
    }
}
