package coding101.tq.domain;

import coding101.tq.util.TerrainMapBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A player.
 */
public class Player {

    /** The default (starting) health value. */
    public static final int DEFAULT_HEALTH = 30;

    /** The maximum possible health value. */
    public static final int MAX_POSSIBLE_HEALTH = 100;

    private final PlayerItems items = new PlayerItems();
    private int health = DEFAULT_HEALTH;
    private int maxHealth = DEFAULT_HEALTH;
    private String activeMapName;
    private int x;
    private int y;
    private boolean onboard;
    private int coins;
    private Map<String, TerrainMap> visitedMaps = new HashMap<>(2);
    private Map<String, Set<Coordinate>> interactions = new HashMap<>(16);

    // a mapping of dynamic vehicles (ships) for each map
    // the nested map keys represent the coordinate the ship starts at on the map
    // and the associated value is the ships current position
    private Map<String, Map<Coordinate, Coordinate>> vehicles = new HashMap<>(8);

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
     * @param activeMapName the active map name to set
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
        if (onboard) {
            // update vehicle coordinate to match
            Map<Coordinate, Coordinate> mapVehicles = vehicles.get(map.getName());
            Coordinate vehicleOrigCoord = null;
            for (Entry<Coordinate, Coordinate> e : mapVehicles.entrySet()) {
                if (e.getValue().x() == this.x && e.getValue().y() == this.y) {
                    vehicleOrigCoord = e.getKey();
                    break;
                }
            }
            if (vehicleOrigCoord != null) {
                mapVehicles.put(vehicleOrigCoord, new Coordinate(x, y));
            }
        }
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
        if (health > maxHealth) {
            health = maxHealth;
        } else if (health < 0) {
            health = 0;
        }
        this.health = health;
    }

    /**
     * Get the maximum health the player can have.
     *
     * @return the maximum health
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Set the maximum health the player can have.
     *
     * @param maxHealth the max health to set
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * Get the "on board" status.
     *
     * A player can be on board a ship, for example.
     *
     * @return {@code true} if the player is on board a vehicle
     */
    public boolean isOnboard() {
        return onboard;
    }

    /**
     * Set the "on board" status.
     *
     * @param onboard {@code true} if the player is on board a vehicle
     */
    public void setOnboard(boolean onboard) {
        this.onboard = onboard;

        final Coordinate coord = new Coordinate(x, y);

        Map<Coordinate, Coordinate> mapVehicles = vehicles.get(activeMapName);

        if (onboard) {
            // is this coordinate already in vehicles? If so, nothing else to do
            if (mapVehicles != null && mapVehicles.containsValue(coord)) {
                return;
            }

            // need to add vehicle
            if (mapVehicles == null) {
                mapVehicles = new TreeMap<>();
                vehicles.put(activeMapName, mapVehicles);
            }
            mapVehicles.put(coord, coord);
        }
    }

    /**
     * Get the number of coins the player owns.
     *
     * @return the coins the coins
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Set the number of coins the player owns.
     *
     * @param coins the coins to set
     */
    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    /**
     * Add a number of coins to the player.
     *
     * @param coins the coins to add
     */
    public void addCoins(int coins) {
        setCoins(getCoins() + coins);
    }

    /**
     * Remove a number of coins from the player.
     *
     * @param coins the coins to remove
     */
    public void deductCoins(int coins) {
        setCoins(getCoins() - coins);
    }

    /**
     * Get the player items.
     *
     * @return the items
     */
    public PlayerItems getItems() {
        return items;
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
     * Test if a specific map coordinate has been visited "near by" before.
     *
     * @param map the map
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @return {@code true} if the coordinate has been visited before
     */
    public boolean hasVisitedNear(TerrainMap map, int x, int y) {
        TerrainMap visited = visitedMaps.get(map.getName());
        for (int row = Math.max(0, y - 1), maxRow = Math.min(visited.height() - 1, y + 1); row <= maxRow; row++) {
            for (int col = Math.max(0, x - 1), maxCol = Math.min(visited.width() - 1, x + 1); col <= maxCol; col++) {
                if (visited.terrainAt(col, row) == TerrainType.Town) {
                    return true;
                }
            }
        }
        return false;
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
     * @param visitedMaps the visited maps to set
     */
    public void setVisitedMaps(Map<String, TerrainMap> visitedMaps) {
        if (visitedMaps == null) {
            visitedMaps = new HashMap<>(2);
        }
        this.visitedMaps = visitedMaps;
    }

    /**
     * Mark a specific map coordinate as interacted with.
     *
     * @param map the map
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @return {@code true} if the coordinate was not interacted with before
     */
    public boolean interacted(TerrainMap map, int x, int y) {
        assert map != null;
        // use TreeMap here just for convenience of keeping coordinates sorted for
        // persistence
        Set<Coordinate> mapInteractions = interactions.computeIfAbsent(map.getName(), k -> new TreeSet<>());
        return mapInteractions.add(new Coordinate(x, y));
    }

    /**
     * Test if a specific map coordinate has been interacted with before.
     *
     * @param map the map
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @return {@code true} if the coordinate has been interacted with before
     */
    public boolean hasInteracted(TerrainMap map, int x, int y) {
        Set<Coordinate> mapInteractions = interactions.get(map.getName());
        return (mapInteractions != null ? mapInteractions.contains(new Coordinate(x, y)) : false);
    }

    /**
     * Get the interactions data.
     *
     * This is a mapping of {@link TerrainMap} names to associated coordinates at
     * which the player has "interacted" already, for example by opening a chest.
     *
     * @return the interactions, never {@literal null}
     */
    public Map<String, Set<Coordinate>> getInteractions() {
        return interactions;
    }

    /**
     * Set the interactions data.
     *
     * @param interactions the interactions to set
     */
    public void setInteractions(Map<String, Set<Coordinate>> interactions) {
        this.interactions = interactions;
    }

    /**
     * Test if a (possibly moved) vehicle is located at the given coordinates.
     *
     * @param map the map to test
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @return {@literal true} if a ship is located at the given point
     */
    public boolean vehicleLocatedAt(TerrainMap map, int x, int y) {
        Map<Coordinate, Coordinate> mapVehicles = vehicles.get(map.getName());
        if (mapVehicles != null) {
            // search current vehicle locations for coordinate match
            for (Coordinate coord : mapVehicles.values()) {
                if (coord.x() == x && coord.y() == y) {
                    return true;
                }
            }
        } else {
            return map.terrainAt(x, y) == TerrainType.Ship;
        }
        return false;
    }

    /**
     * Test if a player can move to a given coordinate.
     *
     * @param activeMap the map to test
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @return {@literal true} if the player is allowed to move to the coordinate
     */
    public boolean canMoveTo(TerrainMap activeMap, int x, int y) {
        final int currX = this.x;
        final int currY = this.y;

        // get terrain at the current position so we tell if they are on a ship
        final TerrainType currTerrain = activeMap.terrainAt(currX, currY);

        // get terrain at the desired position so we can validate it is OK to move
        final TerrainType newTerrain = activeMap.terrainAt(x, y);

        // test for on board a ship
        if (onboard && (currTerrain == TerrainType.Ship || currTerrain == TerrainType.Water)) {
            // on a ship! can only travel to another water
            return newTerrain == TerrainType.Water;
        }
        // TODO: finish validation that player can move to specified coordinate
        return true;
    }
}
