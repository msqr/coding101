package coding101.tq.domain.items;

import static java.util.Objects.requireNonNull;

/**
 * An abstract {@link InventoryItem}.
 */
public abstract class BaseInventoryItem implements InventoryItem {

    private final ItemType type;
    private final String name;
    private int remainingUses;

    /**
     * Constructor.
     *
     * @param name the item name
     * @param type the item type
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public BaseInventoryItem(ItemType type, String name) {
        super();
        this.type = requireNonNull(type);
        this.name = requireNonNull(name);
        this.remainingUses = -1;
    }

    @Override
    public ItemType type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int getRemainingUses() {
        return remainingUses;
    }

    /**
     * Set the remaining uses.
     *
     * @param remaining uses to set
     */
    public void setRemainingUses(int remainingUses) {
        this.remainingUses = remainingUses;
    }
}
