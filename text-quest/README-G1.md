# Goal 1: player health and death by lava

A player has a "health" status that is modeled as an integer on the
[`Player`](./src/main/java/coding101/tq/domain/Player.java) class:

```java
public class Player {

    private GameConfiguration config;
    private int health;
    private int maxHealth;

    /**
     * Constructor.
     *
     * @param config the game configuration
     */
    public Player(GameConfiguration config) {
        super();
        this.config = Objects.requireNonNull(config);
        this.health = config.initialHealth();
        this.maxHealth = config.initialMaxHealth();
    }

}
```

The `health` field is used to keep track of the player's current health. The `maxHealth` field is
used to keep track of the maximum health the player can currently have. As a player's experience
grows in the game, the `maxHealth` can be increased, so the player's health _capacity_ grows
stronger and the player can take more damage as they fight more powerful enemies.

The `GameConfiguration` class defines several "knobs" that can be tweaked via command line
arguments. There are a few health-related properties:

```java
public record GameConfiguration(
        int initialCoins,
        int initialHealth,
        int initialMaxHealth,
        int maxPossibleHealth,
        int lavaHealthDamage) {
}
```

The `initialHealth` property defines the player's starting `health` (this defaults to 30), and the
`initialMaxHealth` property the player's starting `maxHealth` (this defaults to 30). The
`maxPossibleHealth` defines  defines the maximum possible health value that can be achieved in the
game (this defaults to 100).

You can see that the `Player` constructor initializes the `health` and `maxHealth` from the 
configuration:

```java
this.health = config.initialHealth();
this.maxHealth = config.initialMaxHealth();
```

## Death by lava

A player can walk on lava, but their health should **decrease** as a result. The `visited(map, x,
y)` method on the [`Player`](./src/main/java/coding101/tq/domain/Player.java) class is called each
time a player moves to a new coordinate:

```java
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
```

Implement the `TODO` shown here, by decreasing the player's health by the `lavaHealthDamage` 
configuration value **if the player has visited `Lava` at the given coordinate**.

> :point_up: You do not have to worry about the final lines of code in this method, that updates the
> `visitedMaps` data to keep track of what coordinates the player has visited. However, if you can
> explain what that code does in plain language, you earn super bonus points!
