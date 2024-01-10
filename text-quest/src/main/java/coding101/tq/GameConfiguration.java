package coding101.tq;

/**
 * Game configurable options.
 */
public record GameConfiguration(
        int initialCoins, int initialHealth, int initialMaxHealth, int maxPossibleHealth, int lavaHealthDamage) {

    /** The default game configuration. */
    public static final GameConfiguration DEFAULTS = new GameConfiguration(20, 30, 30, 100, 5);

    /**
     * Get a new configuration with a specific number of initial coins.
     *
     * @param coins the initial coins to configure
     * @return the new configuration
     */
    public GameConfiguration withInitialCoins(int coins) {
        return new GameConfiguration(coins, initialHealth, initialMaxHealth, maxPossibleHealth, lavaHealthDamage);
    }
}
