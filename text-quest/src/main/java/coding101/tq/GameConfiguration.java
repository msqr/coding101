package coding101.tq;

/**
 * Game configurable options.
 *
 * @param initialCoins             the number of coins a player should start
 *                                 with; default 20
 * @param initialHealth            the amount of health a player should start
 *                                 with; default 30
 * @param initialMaxHealth         the maximum amount of health a player should
 *                                 start with; default 30
 * @param maxPossibleHealth        the maximum possible amount of health a
 *                                 player can achieve; default 100
 * @param lavaHealthDamage         the amount of health to deduct from a player
 *                                 when they move over lava terrain; default 5
 * @param chestRewardFactor        an integer percentage (1-100) that a chest
 *                                 provides a reward, versus a penalty; default
 *                                 50
 * @param chestCoinMaximum         the maximum number of coins a chest can
 *                                 provide; default 100
 * @param chestHealthDamageMaximum the maximum health that a chest can damage a
 *                                 player; default 5
 * @param xp                       the experience points configuration; defaults
 *                                 to {@link GameXpConfiguration#DEFAULTS}
 * @param revealMap                show the map, regardless if visited; defaults
 *                                 to false
 * @param gui                      use the texture image renderer
 */
public record GameConfiguration(
        int initialCoins,
        int initialHealth,
        int initialMaxHealth,
        int maxPossibleHealth,
        int lavaHealthDamage,
        int chestRewardFactor,
        int chestCoinsMaximum,
        int chestHealthDamageMaximum,
        GameXpConfiguration xp,
        GameShopConfiguration shop,
        boolean revealMap,
        boolean gui) {

    /** The default game configuration. */
    public static final GameConfiguration DEFAULTS = new GameConfiguration(
            20, 30, 30, 100, 5, 100, 50, 5, GameXpConfiguration.DEFAULTS, GameShopConfiguration.DEFAULTS, false, false);

    /**
     * Get a new configuration with a specific number of initial coins.
     *
     * @param initialCoins the initial coins to configure
     * @return the new configuration
     */
    public GameConfiguration withInitialCoins(int initialCoins) {
        return new GameConfiguration(
                initialCoins,
                initialHealth,
                initialMaxHealth,
                maxPossibleHealth,
                lavaHealthDamage,
                chestCoinsMaximum,
                chestRewardFactor,
                chestHealthDamageMaximum,
                xp,
                shop,
                revealMap,
                gui);
    }

    /**
     * Get a new configuration with a specific number of initial experience points.
     *
     * @param xp the initial experience points to configure
     * @return the new configuration
     */
    public GameConfiguration withInitialXp(int xp) {
        return new GameConfiguration(
                initialCoins,
                initialHealth,
                initialMaxHealth,
                maxPossibleHealth,
                lavaHealthDamage,
                chestCoinsMaximum,
                chestRewardFactor,
                chestHealthDamageMaximum,
                new GameXpConfiguration(xp, this.xp.exploreXp(), this.xp.chestXp()),
                shop,
                revealMap,
                gui);
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
                chestHealthDamageMaximum,
                xp,
                shop,
                revealMap,
                gui);
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
                chestHealthDamageMaximum,
                xp,
                shop,
                revealMap,
                gui);
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
                chestHealthDamageMaximum,
                xp,
                shop,
                revealMap,
                gui);
    }

    /**
     * Get a new configuration with a specific reveal map flag.
     *
     * @param revealMap true to reveal the map
     * @return the new configuration
     */
    public GameConfiguration withRevealMap(boolean revealMap) {
        return new GameConfiguration(
                initialCoins,
                initialHealth,
                initialMaxHealth,
                maxPossibleHealth,
                lavaHealthDamage,
                chestCoinsMaximum,
                chestRewardFactor,
                chestHealthDamageMaximum,
                xp,
                shop,
                revealMap,
                gui);
    }

    /**
     * Get a new configuration with a specific GUI flag.
     *
     * @param gui true to use the texture image renderer
     * @return the new configuration
     */
    public GameConfiguration withGui(boolean gui) {
        return new GameConfiguration(
                initialCoins,
                initialHealth,
                initialMaxHealth,
                maxPossibleHealth,
                lavaHealthDamage,
                chestCoinsMaximum,
                chestRewardFactor,
                chestHealthDamageMaximum,
                xp,
                shop,
                revealMap,
                gui);
    }
}
