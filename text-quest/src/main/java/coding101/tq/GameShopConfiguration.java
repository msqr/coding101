package coding101.tq;

/**
 * The game shop configuration.
 *
 * @param purchaseRateDiscount a percentage (0-1) discount to apply when
 *                             offering to purchase an item; the discount is
 *                             applied to the original sale price
 */
public record GameShopConfiguration(double purchaseRateDiscount) {

    /** The default game shop configuration. */
    public static final GameShopConfiguration DEFAULTS = new GameShopConfiguration(0.8);
}
