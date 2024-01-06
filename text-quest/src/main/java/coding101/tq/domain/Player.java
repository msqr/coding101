package coding101.tq.domain;

/**
 * A player.
 */
public class Player {

    /** The default (starting) health value. */
    public static final int DEFAULT_HEALTH = 30;

    /** The maximum health value. */
    public static final int MAX_HEALTH = 100;

    private int health = DEFAULT_HEALTH;

    /**
     * Constructor.
     */
    public Player() {
        super();
    }

    /**
     * Get the health.
     *
     * @return the health
     */
    public int getHealth() {
        return health;
    }

    /**
     * Set the health.
     *
     * @param health the health to set
     */
    public void setHealth(int health) {
        if (health > MAX_HEALTH) {
            health = MAX_HEALTH;
        } else if (health < 0) {
            health = 0;
        }
        this.health = health;
    }
}
