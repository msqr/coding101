package coding101.tq;

/**
 * Game experience points configuration.
 *
 * @param initialXp the player's starting experience points; defaults to 0
 * @param exploreXp the experience points earned by moving to unexplored
 *                  terrain; defaults to 1
 * @param chestXp   the experience points earned by opening chest; defaults to 5
 */
public record GameXpConfiguration(int iniialXp, int exploreXp, int chestXp) {

    /** The default game experience points configuration. */
    public static final GameXpConfiguration DEFAULTS = new GameXpConfiguration(0, 1, 5);
}
