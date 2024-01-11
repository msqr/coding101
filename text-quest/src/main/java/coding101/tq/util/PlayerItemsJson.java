package coding101.tq.util;

import coding101.tq.domain.PlayerItems;
import coding101.tq.domain.items.Armor;
import coding101.tq.domain.items.HealingPotion;
import coding101.tq.domain.items.InventoryItem;
import coding101.tq.domain.items.Weapon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Json support for {@link PlayerItems}.
 */
public class PlayerItemsJson {

    /**
     * Get a collection of all {@link InventoryItem} types.
     *
     * @return the inventory types
     */
    public static Collection<Class<?>> itemSubTypes() {
        List<Class<?>> result = new ArrayList<>(3);
        result.add(Armor.class);
        result.add(HealingPotion.class);
        result.add(Weapon.class);
        return result;
    }
}
