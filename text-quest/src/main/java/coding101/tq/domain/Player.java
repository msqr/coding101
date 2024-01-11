package coding101.tq.domain;

import coding101.tq.GameConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A player.
 */
public class Player {

    private GameConfiguration config;
    private int health;
    private int maxHealth;
    private String activeMapName;
    private int x;
    private int y;
    private Coordinate onboard; // the map coordinate of the boarded vehicle
    private int coins;
    private final PlayerItems items = new PlayerItems();
    private Map<String, VisitedMap> visitedMaps = new HashMap<>(2);
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
     * Constructor.
     *
     * @param config the game configuration
     */
    public Player(GameConfiguration config) {
        super();
        this.config = Objects.requireNonNull(config);
        this.coins = config.initialCoins();
        this.health = config.initialHealth();
        this.maxHealth = config.initialMaxHealth();
    }

    /**
     * Set the game configuration.
     *
     * @param config the configuration to set
     */
    public void configure(GameConfiguration config) {
        this.config = config;
    }

    /**
     * Get the game configuration.
     *
     * @return the game configuration
     */
    public GameConfiguration config() {
        return this.config;
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
     * This method will update the player's {@code x} and {@code y} coordinate
     * values to those given, set the {@code activeMapName} to the name of the given
     * {@code map}, and then call the {@link #visited(TerrainMap, int, int)} method
     * to mark the coordinate as "visited".
     *
     * If the {@code onboard} property is {@code true} then the {@code vehicles}
     * data will be updated to track vehicle movement.
     *
     * @param map the map to move to
     * @param x   the X coordinate
     * @param y   the Y coordinate
     * @return {@code true} if visiting the coordinate for the first time
     * @see #visited(TerrainMap, int, int)
     */
    public boolean moveTo(TerrainMap map, int x, int y) {
        if (onboard != null) {
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
        if (health < 0) {
            health = 0;
        }
        this.health = health;
    }

    /**
     * Test if the player is dead.
     *
     * @return {@code true} if the player's health has reached 0
     */
    public boolean isDead() {
        return health < 1;
    }

    /**
     * Add a number of health to the player.
     *
     * @param health the health to add
     */
    public void addHealth(int health) {
        int newHealth = this.health + health;
        setHealth(Math.min(maxHealth, newHealth));
    }

    /**
     * Remove a number of health from the player.
     *
     * @param health the health to remove
     */
    public void deductHealth(int health) {
        setHealth(this.health - health);
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
    public boolean onboard() {
        return onboard != null;
    }

    /**
     * Get the origin coordinate of the vehicle currently on board.
     *
     * @return the origin coordinate of the boarded vehicle, or {@code null} if not
     *         on board a vehicle
     */
    public Coordinate getOnboard() {
        return onboard;
    }

    /**
     * Set the origin coordinate of the vehicle currently on board.
     *
     * @param coord the origin coordinate of the boarded vehicle, or {@code null} if
     *              not on board a vehicle
     */
    public void setOnboard(Coordinate coord) {
        this.onboard = coord;
    }

    /**
     * Board the vehicle at the player's current location.
     */
    public void board() {
        final Coordinate coord = new Coordinate(x, y);

        // have to consult vehicles data for moved ship locations
        Map<Coordinate, Coordinate> mapVehicles = vehicles.get(activeMapName);

        // find the ship "origin": its original position encoded on the map
        Coordinate shipOrigin = null;
        if (mapVehicles != null) {
            for (Entry<Coordinate, Coordinate> e : mapVehicles.entrySet()) {
                Coordinate shipCoord = e.getValue();
                if (shipCoord.equals(coord)) {
                    // found the ship's current location
                    shipOrigin = e.getKey();
                    break;
                }
            }
        }
        if (shipOrigin == null) {
            // origin must be current location
            shipOrigin = coord;
        }

        this.onboard = shipOrigin;

        if (mapVehicles == null) {
            mapVehicles = new TreeMap<>();
            vehicles.put(activeMapName, mapVehicles);
        }
        mapVehicles.put(shipOrigin, coord);
    }

    /**
     * Disembark the currently boarded vehicle.
     */
    public void disembark() {
        onboard = null;
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
        setCoins(this.coins + coins);
    }

    /**
     * Remove a number of coins from the player.
     *
     * @param coins the coins to remove
     */
    public void deductCoins(int coins) {
        setCoins(this.coins - coins);
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
     * This method is automatically called by the
     * {@link #moveTo(TerrainMap, int, int)} method. It can perform any player logic
     * that occurs as a consequence of visiting the given coordinate, for example
     * deducting health when visiting a "dangerous" terrain type like lava.
     *
     * This method maintains the {@code visitedMaps} data by calling
     * {@link VisitedMap#visit(int, int)} with the given x,y coordinates.
     *
     * @param map the map
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @return {@code true} if the coordinate was not visited before
     * @see #moveTo(TerrainMap, int, int)
     */
    public boolean visited(TerrainMap map, int x, int y) {
        assert map != null;
        // TODO: walking on lava should decrease player's health

        // update the visited state of this coordinate
        VisitedMap visited = visitedMaps.computeIfAbsent(map.getName(), name -> new VisitedMap());
        boolean result = visited.visit(x, y);
        return result;
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
        VisitedMap visited = visitedMaps.get(map.getName());
        return (visited != null && visited.hasVisited(x, y));
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
        VisitedMap visited = visitedMaps.get(map.getName());
        return (visited != null && visited.hasVisitedNear(x, y));
    }

    /**
     * Get the visited map data.
     *
     * Each {@link VisitedMap} represents terrain visited by the player.
     *
     * @return the visited maps, never {@literal null}
     */
    public Map<String, VisitedMap> getVisitedMaps() {
        return visitedMaps;
    }

    /**
     * Set the visited map data.
     *
     * @param visitedMaps the visited maps to set
     */
    public void setVisitedMaps(Map<String, VisitedMap> visitedMaps) {
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
        // use TreeSet here just for convenience of keeping sorted for persistence
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
     * Get the vehicle location data.
     *
     * This is a mapping of {@link TerrainMap} names an associated mapping of
     * vehicle origin coordinates to associated current coordinates, to track the
     * location of vehicles as they are moved.
     *
     * @return the vehicle location data
     */
    public Map<String, Map<Coordinate, Coordinate>> getVehicles() {
        return vehicles;
    }

    /**
     * Set the vehicle location data.
     *
     * @param vehicles the vehicle location data to set
     */
    public void setVehicles(Map<String, Map<Coordinate, Coordinate>> vehicles) {
        this.vehicles = vehicles;
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
        boolean shipMoved = false;
        if (mapVehicles != null) {
            // search current vehicle locations for coordinate match
            for (Entry<Coordinate, Coordinate> e : mapVehicles.entrySet()) {
                Coordinate coord = e.getValue();
                if (coord.x() == x && coord.y() == y) {
                    return true;
                }
                coord = e.getKey();
                if (coord.x() == x && coord.y() == y) {
                    shipMoved = true;
                }
            }
        }
        return map.terrainAt(x, y) == TerrainType.Ship && !shipMoved;
    }

    /**
     * Test if a player can move to a given coordinate on a given map.
     *
     * @param map the map to test
     * @param x   the x coordinate to test
     * @param y   the y coordinate to test
     * @return {@literal true} if the player is allowed to move to the (x,y)
     *         coordinate on {@code map}
     */
    public boolean canMoveTo(TerrainMap map, int x, int y) {
        // get terrain at the desired position so we can validate it is OK to move
        final TerrainType newTerrain = map.terrainAt(x, y);

        // test for on board a ship
        if (onboard()) {
            // on a ship! can only travel to another water
            return (newTerrain == TerrainType.Water || newTerrain == TerrainType.Ship) && !vehicleLocatedAt(map, x, y);
        }
        // TODO: finish validation that player can move to specified coordinate
        return true;
    }
}
