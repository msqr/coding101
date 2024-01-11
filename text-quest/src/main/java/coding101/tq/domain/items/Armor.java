package coding101.tq.domain.items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * An armor item that boosts a player's defense.
 */
@JsonTypeName("Armor")
public class Armor extends BaseEquipableInventoryItem {

    private final int amount;

    /**
     * Constructor.
     *
     * @param name   the armor name
     * @param amount the amount of defense the armor adds
     */
    @JsonCreator
    public Armor(@JsonProperty("name") String name, @JsonProperty("defenseOffset") int amount) {
        super(ItemType.Armor, name);
        this.amount = amount;
    }

    @Override
    public int getDefenseOffset() {
        return amount;
    }
}
