package coding101.tq.domain.items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A weapon item that boosts a player's offense.
 */
@JsonTypeName("Weapon")
public class Weapon extends BaseEquipableInventoryItem {

    private final int amount;

    /**
     * Constructor.
     *
     * @param name   the weapon name
     * @param amount the amount of offense the weapon adds
     */
    @JsonCreator
    public Weapon(@JsonProperty("name") String name, @JsonProperty("offenseOffset") int amount) {
        super(ItemType.Weapon, name);
        this.amount = amount;
    }

    @Override
    public int getOffenseOffset() {
        return amount;
    }
}
