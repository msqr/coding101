package coding101.tq.domain.items;

import coding101.tq.domain.Player;
import coding101.tq.domain.TerrainType;

/**
 * An item a player can possess.
 */
public interface InventoryItem {

    /**
     * Get the name of the item.
     *
     * @return the name of the item
     */
    String name();

    /**
     * Get the item type.
     *
     * @return the type of item
     */
    ItemType type();

    /**
     * Test if the item can be equipped (activated) on the player, such as a piece
     * of armor or a weapon.
     *
     * @return {@code true} if the item can be equipped
     */
    default boolean canEquip() {
        return false;
    }

    /**
     * Test if an item is equipped (activated) on the player.
     *
     * @return {@literal true} if the item is currently equipped
     */
    default boolean isEquipped() {
        return false;
    }

    /**
     * Get a defensive score offset.
     *
     * @return a positive number for more defense, negative for less defense, or 0
     *         for no change
     */
    default int getDefenseOffset() {
        return 0;
    }

    /**
     * Get an offense score offset.
     *
     * @return a positive number for more offense, negative for less offense, or 0
     *         for no change
     */
    default int getOffenseOffset() {
        return 0;
    }

    /**
     * Get the number of uses left.
     *
     * @return a positive number for the remaining uses or a negative number for
     *         unlimited
     */
    default int getRemainingUses() {
        return -1;
    }

    /**
     * Equip or use this item on the player.
     *
     * @param player the player to apply the item to
     * @return {@literal true} if the item was equipped or used
     */
    boolean apply(Player player);

    /**
     * Test if this item provides immunity to a given terrain type.
     *
     * For example a "lava boot" item might provide immunity to lava.
     *
     * @param terrain the terrain to test
     * @return
     */
    default boolean providesImmunityFrom(TerrainType terrain) {
        return false;
    }

    /**
     * Apply travel wear and tear to the item.
     *
     * Some items might be used or degrade in some way when traveling.
     *
     * @param terrain the terrain traveled over
     * @return {@literal true} if the item has been completely degraded or used as a
     *         result of the travel
     */
    default boolean travelOn(TerrainType terrain) {
        return false;
    }
}
