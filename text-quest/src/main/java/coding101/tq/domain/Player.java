package coding101.tq.domain;

import coding101.tq.util.TerrainMapBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 * A player.
 */
public class Player {

    /** The default (starting) health value. */
    public static final int DEFAULT_HEALTH = 30;

    /** The maximum health value. */
    public static final int MAX_HEALTH = 100;

    private int health = DEFAULT_HEALTH;
    private String activeMapName;
    private int x;
    private int y;
    private Map<String, TerrainMap> visitedMaps = new HashMap<>(2);

    /**
     * Constructor.
     */
    public Player() {
        super();
    }

    /**
     * Get the active map name.
     *
     * @return the active map name
     */
    public String getActiveMapName() {
        return activeMapName;
    }

    /**
     * Set the active map name.
     *
     * @param activeMapName the active map nameto set
     */
    public void setActiveMapName(String activeMapName) {
        this.activeMapName = activeMapName;
    }

    /**
     * Update the player coordinate.
     *
     * @param map   the map to move to
     * @param coord the coordinate
     * @return {@code true} if visiting the coordinate for the first time
     */
    public boolean moveTo(TerrainMap map, Coordinate coord) {
        return moveTo(map, coord.x(), coord.y());
    }

    /**
     * Update the player coordinate.
     *
     * @param map the map to move to
     * @param x   the X coordinate
     * @param y   the Y coordinate
     * @return {@code true} if visiting the coordinate for the first time
     */
    public boolean moveTo(TerrainMap map, int x, int y) {
        setX(x);
        setY(y);
        setActiveMapName(map.getName());
        return visited(map, x, y);
    }

    /**
     * Get the current X coordinate on the active map.
     *
     * @return the X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Set the current X coordinate on the active map.
     *
     * @param x the X coordinate to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get the current Y coordinate on the active map.
     *
     * @return the Y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Set the current Y coordinate on the active map.
     *
     * @param y the Y coordinate to set
     */
    public void setY(int y) {
        this.y = y;
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

    /**
     * Mark a specific map coordinate as visited.
     *
     * @param map the map
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @return {@code true} if the coordinate was not visited before
     */
    public boolean visited(TerrainMap map, int x, int y) {
        assert map != null;
        TerrainMap visited = visitedMaps.computeIfAbsent(
                map.getName(), name -> TerrainMapBuilder.nullMap(name, map.width(), map.height()));
        return visited.modifyAt(x, y, TerrainType.Town);
    }

    /**
     * Test if a specific map coordinate has been visited before.
     *
     * @param map the map
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @return {@code true} if the coordinate has been visited before
     */
    public boolean hasVisited(TerrainMap map, int x, int y) {
        TerrainMap visited = visitedMaps.get(map.getName());
        return (visited != null && visited.terrainAt(x, y) == TerrainType.Town);
    }

    /**
     * Get the visited map data.
     *
     * Each {@link TerrainMap} represents terrain visited by the player: all
     * non-null values have been visited.
     *
     * @return the visited maps, never {@literal null}
     */
    public Map<String, TerrainMap> getVisitedMaps() {
        return visitedMaps;
    }

    /**
     * Set the visited map data.
     *
     * @param visitedMaps the visitedMaps to set
     */
    public void setVisitedMaps(Map<String, TerrainMap> visitedMaps) {
        if (visitedMaps == null) {
            visitedMaps = new HashMap<>(2);
        }
        this.visitedMaps = visitedMaps;
    }
}
