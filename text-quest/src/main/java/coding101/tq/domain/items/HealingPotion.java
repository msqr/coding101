/**
 *
 */
package coding101.tq.domain.items;

import coding101.tq.domain.Player;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A potion that restores health.
 *
 * A potion can be used only once, and after using it will be automatically
 * removed from the player's inventory.
 */
@JsonTypeName("HealingPotion")
public class HealingPotion extends BaseInventoryItem {

    private final int amount;

    /**
     * Constructor.
     *
     * @param name      the weapon name
     * @param minimumXp the minimum experience points required to use
     * @param price     the price to purchase, in coins
     * @param amount    the amount of health the potion restores, or a negative
     *                  number to restore to full health
     */
    @JsonCreator
    public HealingPotion(
            @JsonProperty("name") String name,
            @JsonProperty("minimumXp") int minimumXp,
            @JsonProperty("price") int price,
            @JsonProperty("amount") int amount) {
        super(ItemType.Potion, name, minimumXp, price);
        this.amount = amount;
    }

    /**
     * Get the amount of health the potion restores
     *
     * @return the amount of health the potion restores; a negative number indicates
     *         that full health is restored
     */
    public int getAmount() {
        return amount;
    }

    @Override
    public int strength() {
        return amount;
    }

    @Override
    public boolean apply(Player player) {
        if (getRemainingUses() < 1) {
            return false;
        }
        if (amount < 0) {
            // negative amount means "restore full health"
            player.setHealth(player.getMaxHealth());
        } else {
            // otherwise restore the potion amount, capped at the player's maximum health
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + amount));
        }
        // potion can be used only once
        setRemainingUses(0);

        // remove from player inventory
        player.getItems().removeItem(this);
        return true;
    }

    @Override
    public boolean stash(Player player) {
        return false;
    }
}
