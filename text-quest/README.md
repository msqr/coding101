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
| `$`    | shop |
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

 -c,--coins <arg>          starting number of coins
 -C,--chest-coins <arg>    maximum number of coins a chest can provide
 -d,--map-dir <arg>        the main map directory path
 -f,--save-file <arg>      the save file path to use
 -h,--help                 show usage information
 -K,--colors-dir <arg>     the colors directory path
 -k,--colors <arg>         the colors name to load
 -l,--chest-luck <arg>     a percentage from 1-100 that a chest will
                           reward rather than penalise
 -m,--map <arg>            the main map name to load
 -P,--chest-damage <arg>   the maximum amount of health a chest can damage
                           the player
 -r,--reveal-map           make the map completely visible
```

# Key game code concepts

This section outlines some key code concepts that the game uses, and you will need to understand
to complete all the coding goals of this challenge.

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

### About terrain

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

The `TerrainMap` class will be populated with the map data read from a game map file. It provides
the `terrainAt(x, y)` method, that returns the `TerrainType` at the given (x,y) coordinate in the
map, so that is the method to use if you want to know what the terrain is at a given coordinate.
That method looks like this:

```java
public class TerrainMap {

    // the 2D array of map data, as TerrainType values
    private final TerrainType[][] terrain;

    // the width and height of the map data, saved here for convenience
    private final int width;
    private final int height;

    /**
     * Get the terrain type at a specific coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the terrain type, or {@link TerrainType#Empty} if {@code x} or
     *         {@code y} are out of bounds
     */
    public TerrainType terrainAt(int x, int y) {
        if (x >= width || y >= height || x < 0 || y < 0) {
            return TerrainType.Empty;
        }
        return terrain[y][x];
    }

}
```

This code should look very familiar to you if you completed the [Tic Tac Toe](../tic-tac-toe/) project
and you recall that the `terrain` field is a 2D array of enum values, just like how the `board` field
in the [`TicTacToe`](../tic-tac-toe/src/main/java/coding101/ttt/TicTacToe.java) class was a 2D array
of enum values.

> :question: Why is `terrain[y][x]` used and not `terrain[x][y]`? Does it matter? Refer 
> [back](#about-the-map-and-terrain) for details.

> :question: The `terrainAt(x,y)` method refers to `width`, `height`, and `terrain` variables.
> Where are those defined, and why can the method use them at all?

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

Take a look at the `canMoveTo()` method that deals with ship movement:

```java
public class Player {

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
            return (newTerrain == TerrainType.Water || newTerrain == TerrainType.Ship) 
                    && !vehicleLocatedAt(map, x, y);
        }
        // TODO: finish validation that player can move to specified coordinate
        return true;
    }
```

Notice the `&& !vehicleLocatedAt(map, x, y)` logic clause that is included. You could write
the entire logic statement in plain language like:

> While on board a ship, the player **CAN** move to (x, y) **IF** the original map terrain **IS**
> Water **OR** Ship, **AND** a ship is not currently located there.

> :question: Why is the `|| newTerrain == TerrainType.Ship` clause included in this logic,
> when logically a ship can only move to Water terrain?

### About `return`

Notice how there are 2 `return` statements in this method. A method can define any number of
`return` statements, and they do not have to be on the final lines of the method. For methods like
this one that are defined to return a value (a `boolean` here) then the `return` statement accepts
an _expression_ and returns the **result of evaluating that expression** to the caller. The first
`return` statement evaluates this **logic expression**:

```java
(newTerrain == TerrainType.Water || newTerrain == TerrainType.Ship) 
    && !vehicleLocatedAt(map, x, y)
```

A **logic expression** is anything that results in a `boolean` outcome. Logic expressions
are used in `if` statements, for example.

Some people prefer methods have only at most 1 `return` statement. The `canMoveTo()` method
could be re-written in this style, for example:

```java
public boolean canMoveTo(TerrainMap map, int x, int y) {
    // get terrain at the desired position so we can validate it is OK to move
    final TerrainType newTerrain = map.terrainAt(x, y);

    // variable to store final retult, initialized to true
    boolean result = true;

    // test for on board a ship
    if (onboard()) {
        // on a ship! can only travel to another water
        result = (newTerrain == TerrainType.Water || newTerrain == TerrainType.Ship) 
                && !vehicleLocatedAt(map, x, y);
    } else {
        // TODO: finish validation that player can move to specified coordinate,
        //       changing the result variable as appropriate
    }

    // return our result
    return result;
}
```

Both of these method implementaions logically do the same thing, and to the caller it
does not matter which style was used as it only cares about the final result.

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

## Class fields, method arguments, variable scope, and `this`

Knowing that `Player` has `x` and `y` **fields** defined, take a look again at that `canMoveTo(map, x, y)`
method, along with `getX()` and `getY()` methods known as **getter methods** or just **getters** in Java:

```java
public class Player {

    // imagine a player is located at (10,11) by initializing x,y to that here

    private int x = 10;
    private int y = 11;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean canMoveTo(TerrainMap map, int x, int y) {
        // get terrain at the desired position so we can validate it is OK to move
        TerrainType newTerrain = map.terrainAt(x, y);

        // ...
        return true;
    }
}
```

The `canMoveTo()` method defines `x` and `y` **arguments**. The method arguments are called **local
variables** because they are only available (local) to the method body. Imagine we have some code
that calls this method, passing in X and Y values of 2 and 3:

```java
boolean result = player.canMoveTo(map, 2, 3);
```

Inside the `canMoveTo(map, 2, 3)` invocation, what values do `x` and `y` have at this line:

```java
TerrainType newTerrain = map.terrainAt(x, y);
```

> :question: Do `x` and `y` have the values (10, 11) or (2, 3)?

If you answered (2,3) you are correct. Why is that, when there are two valid `x` variables and two
valid `y` variables available? 

Variables that are "available" to a particular line of code are said to be **in scope**. The
**fields** of a class are automatically **in scope** to every method of that class. That is why the
following method can refer to `x` -- it refers to the class field `x`:

```java
public class Player {

    private int x = 10;

    public int getX() {
        // x refers to the Player class field x above, which has the value 10
        return x; 
    }
```

In Java, **local variables** override, or **mask** class variables of the same name. That is
why the `map.terrainAt(x, y)` line above refers to (2, 3) and not (10, 11).

What if you wanted to refer to masked field variable? In Java there is a special keyword
`this` that is automatically available to every class method, and it always refers to 
the current **class instance**. In the above `getX()` method, Java has actually magically
added `this.` to the code, so behind the scenes the code looks like this:

```java
public int getX() {
    return this.x; 
}
```

Java is merely helping you, the coder, do something coders are really good at: being lazy. It would
be very tedious having to type `this.x` and `this.y` and `this.whatever` all the time, so Java lets
you omit the `this.` and it will automagically figure out what **in scope** variable you are
referring to.

> :bulb: You might like to refresh you memory on [classes vs
> instances](../hello-world/README.md#classes-vs-instances). In this example, `Player` is the class,
> and an instance of `Player` would be created like `Player player = new Player();`, so the `player`
> **variable** refers to a `Player` **class instance**.

Now back to our immediate problem: what if you wanted to refer to a masked variable? Imagine
a method `moveTo(int x, int y)` that is meant to update a player's (x,y) field values:

```java
public class Player {

    private int x = 10;
    private int y = 11;

    public int getX() {
        return x;
    }

    public int moveTo(int x, int y) {
        x = x;
        y = y;
    }
}
```

Then imagine calling this method:

```java
player.moveTo(2, 3);
```

> :question: What would `getX()` return now, after calling `moveTo(2, 3)` like this? Something is
> not quite right. What does `x = x` do here? Well, what does `x` refer to here? What `x` is **in
> scope** in this method?

We need to use an explicit `this.x` and `this.y` in this method to refer to both the **local**
variables and the **class fields** in the same method:

```java
public int moveTo(int x, int y) {
    this.x = x;
    this.y = y;
}
```

## About blocks and scope

In Java, a **block** is a chunk of code between curly braces `{}`. The clever observer
(that's you!) will have noticed that **methods** are defined with `{}`, which then means
each method is also a **block**.

> :bulb: Where else have you noticed blocks? How about `if` statements? How about `for` or `while`
> loops? They all use `{}`... and yes, they all define blocks. What about classes? They are defined
> like `class Foo {}`... is that also a block? Yes indeed!

So blocks are everywhere in Java. Blocks also define variable **scopes**... and as blocks
can be **nested** within each other, that means scopes are nested within each other as well.
Let us look at another bit of example code:

```java
public class Player {
    // START CLASS BLOCK (1)

    private int health;

     public int getHealth() {
        // START METHOD BLOCK 2a
        return health;
        // START METHOD BLOCK 2a
    }

   public void takeDamage(int amount) {
        // START METHOD BLOCK (2b)
        health -= amount;

        if (health < 0) {
            // START IF BLOCK (3a)
            int overkill = 0 - health;
            System.out.println("You died, by " +overkill + " health!");
            // END IF BLOCK (3a)
        } else {
            // START ELSE BLOCK (3b)
            System.out.println("Ouch! Luckily you still have " +health + " health.");
            // END ELSE BLOCK (3b)
        }

        // END METHOD BLOCK (2b)
    }

    // END CLASS BLOCK (1)
} 
```

The various blocks are annoated with `// START` and `// END` comments. You can think of
the variables in scope like a **combination** of variables from all blocks, sort of like this:

| Block | Variables in scope |
|:------|:----------|
| 1     | `health` |
| 2a    | `health` |
| 2b    | `health`, `amount` |
| 3a    | `health`, `amount`, `overkill` |
| 3b    | `health`, `amount` |

Notice how `overkill` is not available in block 3b (or any other block). That variable
is only **in scope** in the block it is defined in: 3a.

### Comparing Java scope to JavaScript

Different programming languages treat variable scope differently. Both languages define blocks with
`{}` characters, and a **function** in JavaScript is like a **method** in Java. In JavaScript
variables can be defined with `var` or `let`, and they have different scope meanings: `var` defines
the variable in **function scope** and **let** uses **block scope**. Take this example:

```js
class Player {
    // START CLASS BLOCK (1)

    health = 10;

    getHealth() {
        // START FUNCTION BLOCK 2a
        return health;
        // START FUNCTION BLOCK 2a
    }

   takeDamage(amount) {
        // START FUNCTION BLOCK (2b)
        this.health -= amount;

        console.log(`Overkill START 2a: ${overkill}`);

        if (this.health < 0) {
            // START IF BLOCK (3a)
            var overkill = 0 - this.health;
            console.log(`You died, by ${overkill} health!`);
            // END IF BLOCK (3a)
        } else {
            // START ELSE BLOCK (3b)
            console.log(`Ouch! Luckily you still have ${this.health} health.`);
            // END ELSE BLOCK (3b)
        }

        console.log(`Overkill END 2a: ${overkill}`);

        // END FUNCTION BLOCK (2b)
    }

    // END CLASS BLOCK (1)
}
```

Look what gets logged to the console if you call `takeDamage(5)`:

```
Overkill START 2a: undefined
Ouch! Luckily you still have 5 health.
Overkill END 2a: undefined
```

Now look what happens when you call `takeDamage(20)`:

```
Overkill START 2a: undefined
You died, by 10 health!
Overkill END 2a: 10
```

Spot the difference on the `OVERKILL END 2a:` output? How is `overkill` in scope for block 2b, when
it was defined in block 3a? This is a feature of JavaScript's **function scope** for variables
defined with `var`: the scope is **hoisted** to the closest-defined function scope, which is block
2b.

If we instead defined `overkill` using `let`, then JavaScript uses **block scope** in the same
manner as Java, and then the code will produce a `ReferenceError: overkill is not defined`
exception.

> :bulb: The `var` function scope catches many JavaScript developers by surprise, especially
> when coming from other languages like Java, and is why `let` is often recommended over `var`
> in JavaScript these days (`let` did not exist in JavaScript origionally).

# Goals

Here are the goals of this coding challenge:

 1. [Death by lava](./README-G1.md) (walk on lava)
 2. [Hitting a wall](./README-G2.md) (fix movement)
 3. [Randomness](./README-G3.md) (oh but I digress)
 4. [Risky business](./README-G4.md) (opening chests)
