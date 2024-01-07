package coding101.tq;

import static java.util.Objects.requireNonNull;

/**
 * The game UI.
 */
public class GameUI {

    private final MapPane map;
    private final InfoPane info;
    private final StatusPane status;
    private final HealthPane health;

    /**
     * Constructor.
     *
     * @param map    the map pane
     * @param status the status pane
     * @param info   the info pane
     * @param health the health pane
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public GameUI(MapPane map, InfoPane info, StatusPane status, HealthPane health) {
        super();
        this.map = requireNonNull(map);
        this.info = requireNonNull(info);
        this.status = requireNonNull(status);
        this.health = requireNonNull(health);
    }

    /**
     * Get the map pane.
     *
     * @return the map
     */
    public MapPane map() {
        return map;
    }

    /**
     * Get the status pane.
     *
     * @return the status
     */
    public StatusPane status() {
        return status;
    }

    /**
     * Get the info pane.
     *
     * @return the info
     */
    public InfoPane info() {
        return info;
    }

    /**
     * Get the health pane.
     *
     * @return the health
     */
    public HealthPane health() {
        return health;
    }
}
