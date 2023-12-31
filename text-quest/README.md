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
$ ../gradlew spotlessApply build

BUILD SUCCESSFUL in 2s

$ java -jar build/libs/text-quest-all.jar
```

# Goal 1: fix movement

At the moment the player can move across any terrain. A player should not be able to move onto
**Mountain** or **Water** terrain, however. To fix this, complete the `canMoveTo(map, x, y)` method
in the [Player](./src/main/java/coding101/tq/domain/Player.java) class. Currently the method looks
like this:

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
    // grab the player's current position and save to local constants
    final int currX = this.x;
    final int currY = this.y;

    // get terrain at the current position so we tell if they are on a ship
    final TerrainType currTerrain = map.terrainAt(currX, currY);

    // get terrain at the desired position so we can validate it is OK to move
    final TerrainType newTerrain = map.terrainAt(x, y);

    // test for on board a ship
    if (onboard && (currTerrain == TerrainType.Ship || currTerrain == TerrainType.Water)) {
        // on a ship! can only travel to another water
        return newTerrain == TerrainType.Water;
    }
    // TODO: finish validation that player can move to specified coordinate
    return true;
}
```

> :point_up: **Note** the `// TODO` comment, which is where you should complete the implementation.
> You **do not** need to worry too much about understanding the `if (onboard...){}` block before
> that, just know that that handles the logic for movement when on board a ship.

> :point_down: **Continue reading** the next sections to learn about the `TerrainMap`,
> `TerrainType`, and `Player` classes you see in this method.

## About the map and terrain

A map in the game is modeled by the
[`TerrainMap`](./src/main/java/coding101/tq/domain/TerrainMap.java) class, which has a `terrain`
field that is a 2D arrayof [`TerrainType`](./src/main/java/coding101/tq/domain/TerrainType.java)
enum values. The `TerrainType` enumeration models all possible terrain types, and also defines the
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

When the `canMoveTo(map, x, y)` method is called, the player's `x` and `y` values represent the
coordinate the player is **currently at**. That is why the `canMoveTo(map, x, y)` method grabs
those and stores them in local constants:

```java
public boolean canMoveTo(TerrainMap map, int x, int y) {
    // grab the player's current position and save to local constants
    final int currX = this.x;
    final int currY = this.y;  
```

> :point_up: Notice how `this.` is used to access the `x` and `y` fields of the player. That is
> necessary here because the method defines `x` and `y` argument variables that represent the
> **desired** position to **move to**, not the player's current position! That means while inside
> this method, `x` and `y` refer to the argument values (the desired position); `this.x` and
> `this.y` refer to the player's field values (the current position). Essentially the
> argument variable names **override** or **mask** the class field names.
