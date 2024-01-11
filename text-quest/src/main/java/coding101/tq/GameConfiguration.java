package coding101.tq;

/**
 * Game configurable options.
 *
 * @param initialCoins             the number of coins a player should start
 *                                 with
 * @param initialHealth            the amount of health a player should start
 *                                 with
 * @param initialMaxHealth         the maximum amount of health a player should
 *                                 start with
 * @param maxPossibleHealth        the maximum possible amount of health a
 *                                 player can achieve
 * @param lavaHealthDamage         the amount of health to deduct from a player
 *                                 when they move over lava terrain
 * @param chestCoinMaximum         the maximum number of coins a chest can
 *                                 provide
 * @param chestRewardFactor        an integer percentage (1-100) that a chest
 *                                 provides a reward, versus a penalty
 * @param chestHealthDamageMaximum the maximum health that a chest can damage a
 *                                 player
 */
public record GameConfiguration(
        int initialCoins,
        int initialHealth,
        int initialMaxHealth,
        int maxPossibleHealth,
        int lavaHealthDamage,
        int chestCoinsMaximum,
        int chestRewardFactor,
        int chestHealthDamageMaximum) {

    /** The default game configuration. */
    public static final GameConfiguration DEFAULTS = new GameConfiguration(20, 30, 30, 100, 5, 100, 50, 5);

    /**
     * Get a new configuration with a specific number of initial coins.
     *
     * @param coins the initial coins to configure
     * @return the new configuration
     */
    public GameConfiguration withInitialCoins(int coins) {
        return new GameConfiguration(
                coins,
                initialHealth,
                initialMaxHealth,
                maxPossibleHealth,
                lavaHealthDamage,
                chestCoinsMaximum,
                chestRewardFactor,
                chestHealthDamageMaximum);
    }

    /**
     * Get a new configuration with a specific chest coins maximum.
     *
     * @param chestCoinsMaximum the maximum number of coins to configure
     * @return the new configuration
     */
    public GameConfiguration withChestCoinsMaximum(int chestCoinsMaximum) {
        return new GameConfiguration(
                initialCoins,
                initialHealth,
                initialMaxHealth,
                maxPossibleHealth,
                lavaHealthDamage,
                chestCoinsMaximum,
                chestRewardFactor,
                chestHealthDamageMaximum);
    }

    /**
     * Get a new configuration with a specific chest reward factor.
     *
     * @param chestRewardFactor an integer percentage (1-100) that a chest provides
     *                          a reward, versus a penalty
     * @return the new configuration
     */
    public GameConfiguration withChestRewardFactor(int chestRewardFactor) {
        return new GameConfiguration(
                initialCoins,
                initialHealth,
                initialMaxHealth,
                maxPossibleHealth,
                lavaHealthDamage,
                chestCoinsMaximum,
                chestRewardFactor,
                chestHealthDamageMaximum);
    }

    /**
     * Get a new configuration with a specific chest maximum health damage.
     *
     * @param chestHealthDamageMaximum the maximum health that a chest can damage a
     *                                 player
     * @return the new configuration
     */
    public GameConfiguration withChestHeathDamageMaximum(int chestHealthDamageMaximum) {
        return new GameConfiguration(
                initialCoins,
                initialHealth,
                initialMaxHealth,
                maxPossibleHealth,
                lavaHealthDamage,
                chestCoinsMaximum,
                chestRewardFactor,
                chestHealthDamageMaximum);
    }
}
