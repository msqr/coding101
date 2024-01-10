# Text Quest

This project is an incomplete console-based adventure game. 

# Game Overview

The game is a classic text-based top-down adventure game in the style of the original Ultima or
Legend of Zelda series. You can explore the world and will encounter towns you can enter for
trading, caves you can explore for treasure, and enemies that can attack you and reduce your health.
The game interface looks like this:

<img alt="Text Quest overview" src="docs/tq-overview@2x.png" width="459">

The screen is split into 4 _panes_. The large pane on the top-left is the map view that shows where
you are. The **Inventory** pane on the top-right shows what items you have. The row of **♥**
characters in the bottom-right pane represents your current state of health (you lose the game if
you lose all your health). The bottom-left pane is the **message** area that will display
information throughout the game.

## Keyboard actions

The following keys are used to interact with the game:

| Key | Description |
|:----|:------------|
| <kbd>←</kbd> | Move left |
| <kbd>→</kbd> | Move right |
| <kbd>↑</kbd> | Move up |
| <kbd>↓</kbd> | Move down |
| <kbd>Space</kbd> | Interact (enter, open, and so on) |
| <kbd>⏎ Enter</kbd> | Accept |
| <kbd>n</kbd> | Reject |
| <kbd>s</kbd> | Save game |
| <kbd>Esc</kbd> | Quit game |

## Terrain symbols

The map is composed of the following symbols:

| Symbol | Description |
|:-------|:------------|
| `O`    | cave |
| `%`    | chest |
| `@`    | player |
| `^`    | forest |
| `.`    | grass |
| `m`    | hill |
| `=`    | lava |
| `"`    | lava rock |
| `A`    | mountain |
| `,`    | sand |
| `&`    | ship |
| `*`    | town |
| `~`    | water |

# Assumptions

This project assumes you have completed the [Tic Tac Toe](../tic-tac-toe/) challenge.

# Running the game

The game is designed to be run from a terminal console. To build and run the game, execute the
following in a terminal from within the same directory as this README file:

```sh
# build the game (and apply Spotless formatting)
../gradlew spotlessApply build

# run the game
java -jar build/libs/text-quest-all.jar

# all-in-one
../gradlew spotlessApply build && java -jar build/libs/text-quest-all.jar
```

You should see something like this:

```
# build
$ ../gradlew spotlessApply build

BUILD SUCCESSFUL in 2s

# run
$ java -jar build/libs/text-quest-all.jar
```

## Command line arguments

The game supports several command line arguments. You can pass `-h` or `--help` for all available
options:

```
usage: <options>
 _____            _    _____                    _
|_   _|          | |  |  _  |                  | |
  | |  ___ __  __| |_ | | | | _   _   ___  ___ | |_
  | | / _ \\ \/ /| __|| | | || | | | / _ \/ __|| __|
  | ||  __/ >  < | |_ \ \/' /| |_| ||  __/\__ \| |_
  \_/ \___|/_/\_\ \__| \_/\_\ \__,_| \___||___/ \__|

 -c,--coins <arg>        starting number of coins
 -d,--map-dir <arg>      the main map directory path
 -f,--save-file <arg>    the save file path to use
 -h,--help               show usage information
 -L,--colors-dir <arg>   the colors directory path
 -l,--colors <arg>       the colors name to load
 -m,--map <arg>          the main map name to load
```

# Goal 1: fix movement

At the moment the player can move across any terrain. A player should not be able to move onto
**Mountain** or **Water** terrain, however. A player should be able to move onto a **Ship**, and
then board that ship, and then move to any **Water** terrain. To fix this method, complete the
`canMoveTo(map, x, y)` method in the [Player](./src/main/java/coding101/tq/domain/Player.java)
class. Currently the method looks like this:

```java
/**
 * Test if a player can move to a given coordinate.
 *
 * @param map the map to test
 * @param x   the x coordinate
 * @param y   the y coordinate
 * @return {@literal true} if the player is allowed to move to the coordinate
 */
public boolean canMoveTo(TerrainMap map, int x, int y) {
    // get terrain at the desired position so we can validate it is OK to move
    final TerrainType newTerrain = map.terrainAt(x, y);

    // test for on board a ship
    if (onboard()) {
        // on a ship! can only travel to another water
        return (newTerrain == TerrainType.Water || newTerrain == TerrainType.Ship) 
            && !vehicleLocatedAt(map, x, y);
    }
    // TODO: finish validation that player can move to specified coordinate
    return true;
}
```

> :point_up: **Note** the `// TODO` comment, which is where you should complete the implementation.
> The `if (onboard()){}` block before that handles the logic for movement when on board a ship.

> :point_down: **Continue reading** the next sections to learn about ships and the `TerrainMap`,
> `TerrainType`, and `Player` classes you see in this method.

## About the map and terrain

A map in the game is modeled by the
[`TerrainMap`](./src/main/java/coding101/tq/domain/TerrainMap.java) class, which has a `terrain`
field that is a 2D array of [`TerrainType`](./src/main/java/coding101/tq/domain/TerrainType.java)
enum values. The **first** array dimension represents **rows** of map data and the **second**
dimension represents **columns**. For example, imagine a 3x3 map:

```
~~^
~.m
,.A
```

We can visualise that as a 2D array with rows and columns, like this:

```
       0   1   2
┌─────────────────┐
│    ┌───────────┐│
│ 0: │ ~ │ ~ │ ^ ││
│    └───────────┘│
├─────────────────┤
│    ┌───────────┐│
│ 1: │ ~ │ . │ m ││
│    └───────────┘│
├─────────────────┤
│    ┌───────────┐│
│ 2: │ , │ . │ A ││
│    └───────────┘│
└─────────────────┘
```

> :question: Using (x,y) coordinate notation where `x` is a column and `y` is a row, what coodinate
> is Mountain (`A`)? What coordinate is Forrest (`^`)?

The `TerrainType` enumeration models all possible terrain types, and also defines the
text character used by that type:

```java
public enum TerrainType {

    Cave(TerrainType.CAVE),

    Chest(TerrainType.CHEST),

    Empty(TerrainType.EMPTY),

    Forest(TerrainType.FOREST),

    Grass(TerrainType.GRASS),

    // ... and so on 
    ;

    // here begins the text character constants for each terrain type:

    public static final char CAVE = 'O';
    public static final char CHEST = '%';
    public static final char EMPTY = ' ';
    public static final char FOREST = '^';
    public static final char GRASS = '.';

    // ... and so on
}
```
You can think of the `char` type in Java as representing a single letter, or _character_. Thus the
line:

```java
public static final char CAVE = 'O';
```

defines a constant named `CAVE` that is equal to the letter `O`.

### Finding the type of terrain at a map coordinate

The `TerrainMap` class will be populated with the map data read from a game map file. You may 
have noticed the `canMoveTo(map, x, y)` method above called the `terrainAt(x, y)` method on
the passed-in `map` object. That method will return the `TerrainType` at the given (x,y)
coordinate in the map, so that is the method to use if you want to know what the terrain is
at a given coordinate. That method looks like this:

```java
/**
 * Get the terrain type at a specific coordinate.
 *
 * @param x the x coordinate
 * @param y the y coordinate
 * @return the terrain type, or {@link TerrainType#Empty} if {@code x} or
 *         {@code y} are out of bounds
 */
public final TerrainType terrainAt(int x, int y) {
    if (x >= width || y >= height || x < 0 || y < 0) {
        return TerrainType.Empty;
    }
    return terrain[y][x];
}
```

This code should look very familiar to you if you completed the [Tic Tac Toe](../tic-tac-toe/) project
and you recall that the `terrain` field is a 2D array of enum values, just like how the `board` field
in the [`TicTacToe`](../tic-tac-toe/src/main/java/coding101/ttt/TicTacToe.java) class was a 2D array
of enum values.

> :question: Why is `terrain[y][x]` used and not `terrain[x][y]`? Does it matter? Refer 
> [back](#about-the-map-and-terrain) for details.

## About ships

Ships are a special type of terrain in that they can by **boarded** and then moved by the player to
any adjacent **Water** terrain. To board a ship, move onto a ship and type <kbd>Space</kbd> to
board. Once boarded, the arrow keys move the player **along with the ship**. The `TerrainMap` data
**does not change** to reflect the ship's updated location, however! Instead the updated ship
locations are stored on the Player.

> :bulb: Maintaining ship locations on `Player`, instead of modifying `TerrainMap`, makes sense if
> you think of a ship postion as just another player game state attribute, like health and coins.

> :question: If we did modify `TerrainMap` to keep track of ship locations, what impact would that
> have on saving the game, and then reloading the game?

Since the **map data does not change** as ships move around, calling `map.terrainAt(x,y)` will only
return `Ship` at each ship's **starting coordinate on the map**. To really know if a map location is
a ship you must use the `vehicleLocatedAt(map, x, y)` method in the
[Player](./src/main/java/coding101/tq/domain/Player.java) class. That method will return `true` only
on coordinates that hold a ship, even after the ship has moved.

Take a look back at that `canMoveTo()` method that deals with ship movement:

```java
    if (onboard()) {
        // on a ship! can only travel to another water
        return (newTerrain == TerrainType.Water || newTerrain == TerrainType.Ship) 
            && !vehicleLocatedAt(map, x, y);
    }
```

Notice the `&& !vehicleLocatedAt(map, x, y)` logic clause that is included. You could write
the entire logic statement in plain language like:

> While on board a ship, the player **CAN** move to (x, y) **IF** the original map terrain **IS**
> Water **OR** Ship, **AND** a ship is not currently located there.

> :question: Why is the `|| newTerrain == TerrainType.Ship` clause included in this logic,
> when logically a ship can only move to Water terrain?

## About the player

The player state is modeled by the [`Player`](./src/main/java/coding101/tq/domain/Player.java) class.
This class has a lot of fields and methods already, but we can focus on just a couple of things at
this point.

### Player position

The player's current position is stored on `x` and `y` `int` fields of the `Player` class:

```java
public class Player {

    private int x;
    private int y;

}
```

# Goal 2: player health and death by lava

A player has a "health" status that is modeled as an integer on the [`Player`](./src/main/java/coding101/tq/domain/Player.java) class:

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

The `GameConfiguration` class defines several "knobs" that can be tweaked via command line arguments.
There are a few health-related properties:

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
 * @param map the map
 * @param x   the x coordinate
 * @param y   the y coordinate
 * @return {@code true} if the coordinate was not visited before
 */
public boolean visited(TerrainMap map, int x, int y) {
    assert map != null;
    // TODO: walking on lava should decrease player's health

    // update the visited state of this coordinate by setting to a non-null value;
    // the actual type used does not matter, we merely chose to use Town
    TerrainMap visited = visitedMaps.computeIfAbsent(map.getName(),
            name -> nullMap(name, map.width(), map.height()));
    boolean result = visited.modifyAt(x, y, TerrainType.Town);
    return result;
}
```

Implement the `TODO` shown here, by decreasing the player's health by the `lavaHealthDamage` 
configuration value **if the player has visited `Lava` at the given coordinate**.

> :point_up: You do not have to worry about the final lines of code in this method, that updates the
> `visitedMaps` data to keep track of what coordinates the player has visited. However, if you can
> explain what that code does in plain language, you earn super bonus points!
