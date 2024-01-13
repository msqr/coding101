package coding101.tq.domain.items;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An abstract {@link InventoryItem}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public abstract class BaseInventoryItem implements InventoryItem {

    private final ItemType type;
    private final String name;
    private final int minimumXp;
    private final int price;
    private int remainingUses;

    /**
     * Constructor.
     *
     * @param name      the item name
     * @param type      the item type
     * @param minimumXp the minimum experience points required to use
     * @param price     the price to purchase, in coins
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public BaseInventoryItem(ItemType type, String name, int minimumXp, int price) {
        super();
        this.type = requireNonNull(type);
        this.name = requireNonNull(name);
        this.minimumXp = minimumXp;
        this.price = price;
        this.remainingUses = -1;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[type=" + type + ", name=" + name + "]";
    }

    @Override
    public InventoryItem clone() {
        try {
            return (InventoryItem) super.clone();
        } catch (CloneNotSupportedException e) {
            // should not happen
            throw new IllegalStateException(e);
        }
    }

    @JsonProperty("type")
    @Override
    public ItemType type() {
        return type;
    }

    @JsonProperty("name")
    @Override
    public String name() {
        return name;
    }

    @JsonProperty("minimumXp")
    @Override
    public int minimumXp() {
        return this.minimumXp;
    }

    @JsonProperty("price")
    @Override
    public int price() {
        return this.price;
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
