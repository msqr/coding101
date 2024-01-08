package coding101.tq.domain.items;

/**
 * A type of item.
 */
public enum ItemType {

    /** An item that adds defensive strength. */
    Armor(true),

    /** An item that adds offensive strength. */
    Weapon(true),

    /** An item that performs some magic. */
    Potion(false),

    /** Something else. */
    Other(false);

    private final boolean singleton;

    private ItemType(boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * Test if the item type can have multiple instances equipped or used at once.
     *
     * @return {@code true} if multiple items of the same type can be equipped at
     *         the same time
     */
    public boolean canEquipMultiple() {
        return !singleton;
    }
}
