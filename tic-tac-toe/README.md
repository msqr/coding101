# Tic Tac Toe

This project is an incomplete console-based Tic Tac Toe game. The game consists of a square
grid _board_ where each space on the board holds an "X" or "O" move _status_. Here is an
example completed game board, where `X` has won via 3 `X`'s in a row, diagonally:

```
 O │ O │ X 
───┼───┼───
   │ X │   
───┼───┼───
 X │ X │ O 
```

# Assumptions

This project assumes you have a basic understanding of these programming concepts already:

 * **variables**
 * **arrays**
 * **[classes and instances](../hello-world/README.md#classes-vs-instances)**
 * **public** vs **private** modifiers
 * class **methods and properties**

# Running the game

The game is designed to be run from a terminal console. To build and run the game, execute the
following in a terminal from within the same directory as this README file:

```sh
# build the game
../gradlew build

# run the game
java -jar build/libs/tic-tac-toe-all.jar
```

You should see something like this:

```
$ ../gradlew build

BUILD SUCCESSFUL in 2s

$ java -jar build/libs/tic-tac-toe-all.jar

Enter moves like A1 (top-left) or B2 (center).

    A   B   C 

1     │   │   
   ───┼───┼───
2     │   │   
   ───┼───┼───
3     │   │   

Player X move: 
```

To enter a move, type the desired coordinate, such as `A1` followed by <kbd>⏎ Return</kbd> (or
<kbd>⏎ Enter</kbd>).

# Code structure

A skeleton of the game already exists for you to start with. Your [goal](#goal-1-complete-game)
will be to complete some missing pieces. This section describes the basic structure of the code
to help you get started.

## Status enum

A player move is modeled as the `coding101.ttt.Status` Java `enum`. An `enum` is an _enumeration_
(list) of a **fixed-set** of possible values (the list cannot be changed at runtime). As the
`Status` enum is used to model a player move, it has `X` and `O` values:

```java
enum Status {
    X,
    O;
}
```

Enums are handy, for example, when modeling _something that can be one of X, Y, or Z_. Imagine a
light switch object, which has a `status` property that can be either `ON` or `OFF`. That `status`
is a good candidate for an `enum`, something like this:

```java
// an example of an enum modelling the state of a light switch
enum LightSwitchStatus {
    ON,
    OFF;
}
```

Traditionally, enum values are written as `ALL_UPPERCASE_WITH_UNDERSCORES`, a style referred
to as [screaming snake case](https://en.wikipedia.org/wiki/Snake_case). In Java enum values
are not required to follow that convention, but it is still very common.

In Java code you can refer to the enumeration values like class properties, with a `.` delimiter,
like:

```java
var move = Status.X; // move holds "X"
```

In Java you can also use `==` and `!=` to compare for equality and difference. For
example here is a method that returns what the "next" move should be after a given
"last" move:

```java
Status nextMove(Status lastMove) {
    if (lastMove == Status.X) {
        return Status.O;
    } else {
        return Status.X;
    }
}
```

## Game board

The game board is modeled as a 2D array of `Status` values, saved on the `board` property of the
`TicTacToe` class. This representation closely mimics a real-world tic tac toe board; imagine each
box in this diagram is an array element. The "outer" boxes form "rows" on the board, and is the
**first** array dimension, each element holding an "inner" array that form the "columns" of that
row, that is the **second** array dimension:

```
       0   1   2
┌─────────────────┐
│    ┌───────────┐│
│ 0: │   │   │   ││
│    └───────────┘│
├─────────────────┤
│    ┌───────────┐│
│ 1: │   │   │   ││
│    └───────────┘│
├─────────────────┤
│    ┌───────────┐│
│ 2: │   │   │   ││
│    └───────────┘│
└─────────────────┘
```

> :point_up: **Note** how in Java the first element of an array is indexed by `0`, not `1`!

This model has an _origin_ at the top-left: where the (0,0) coordinate sits. The reason for
modelling it that way was based on the way we know the board will be drawn to the console: starting
at the top-left.

## TicTacToe class

The main entry point to the game is the `coding101.ttt.TicTacToe` class. This is the class you will
need to modify to [complete the game](#goal-1-complete-game).

### Board property

As outlined in the [Game board](#game-board) section, the board is modeled as a 2D array of `Status`
values, like this:

```java
Status[][] board;
```

In Java you define an array of something by adding `[]` after the type. For additional dimensions
you add additional `[]` pairs. Thus `Status[][]` represents a 2D array of `Status` values because
there are 2 pairs of `[]` defined.

To access individual spaces on the board, you use the _array index_ operator `[i]` **twice**, once
for each dimension of the array. The `i` value is the array element, or **index**, that you want to 
access. The **top-left** coordinate (0,0) is thus accessed like this:

```java
var status = board[0][0]; // status for top-left coordinate (0,0)
```

Because game board is modelled with **rows** as the first dimension, that means you **specify the row
index first, then the column**. This is a bit like reversing the order of a traditional (x,y) style
coordinate system where `x` is the horizontal axis and `y` is the vertical axis. Thus if you imagine
the board space represented by "(3rd across,2nd down)" you would access that like:

```java
var status = board[1][2]; // status for 2nd row down, 3rd column across
```

#### Thought exercise

Here is a picture of a finished game where player `X` has won:

```
 O │ O │ X 
───┼───┼───
   │ O │   
───┼───┼───
 X │ X │ X 
```

How would you access the player's 3 coordinates that earned them the win? Fill in the necessary array
indicies in this template, going from **left to right** with the winning coordinates as the `move1`,
`move2`, and `move3` variables here:

```java
var move1 = board[   ][   ]; // bottom-left space

var move2 = board[   ][   ]; // bottom-middle space

var move3 = board[   ][   ]; // bottom-right space
```

### Board size constant

The `TicTacToe` class also provides a `size` property that represents the number of rows and columns
on the board. This will be `3` to model a traditional tic tac toe game.

```java
/** Our board size. */
int size = 3;
```

# Goal 1: complete game

There are 3 methods in the `coding101.ttt.TicTacToe` class that need to be implemented to complete
the game. You should **only make changes in the `TicTacToe` class**. Refer to the `Coordinate` and
`Status` classes for reference.

## 1.1 Validate player move

The `boolean isMoveValid(Coordinate coord)` method needs to validate a move entered by a player. It
is passed a `coding101.ttt.Coordinate` representing the player's desired move.

```java
/**
 * Verify a coordinate can be filled, by validating it is within the board bounds
 * and the given coordinate is not already occupied.
 *
 * @param coord the coordinate to check
 * @return true if the given coordinate can be moved on
 */
private boolean isMoveValid(Coordinate coord) {
    return true;
}
```

`Coordinate` is a _record_, which is a simplified class object type in Java. 

```java
/**
 * A human-numbered grid coorinate like A1.
 *
 * @param col the column, starting from A
 * @param row the row, starting from 1
 */
public record Coordinate(char col, int row) {

    /**
     * Get the 0-based column index.
     * @return the 0-based column index
     */
    public int x() {
        return Character.toUpperCase(col) - 'A';
    }

    /**
     * Get the 0-based row index.
     * @return the 0-based row index
     */
    public int y() {
        return row - 1;
    }
}
```

A `Coordinate` provides `char col()` and `int row()` methods to return the board position the player
provided, along with helper `int x()` and `int y()` methods that return 0-based index equivalents of
`col()` and `row()`. Here are some examples of what those methods return, for a given player input:

| Player Input | `col()` | `row()` | `x()` | `y()` |
|:-------------|:--------|:--------|:------|:------|
| `A1`         | `A`     | `1`     | `0`   | `0`   |
| `B3`         | `B`     | `3`     | `1`   | `2`   |
| `C2`         | `C`     | `2`     | `2`   | `1`   |   

### 1.1 Task

Implement `isMoveValid()` such that `true` is returned only if **all** of the following conditions
are met:

 * the given coordinate is within the bounds of the board size; for example `A4` is not valid for a
   board size of `3` because it is out of bounds
 * the given coordinate has not already been used in the current game

## 1.2 Check if a player has won

The `boolean isWon(Coordinate lastMove)` method needs to check if a player has won the game. The
method is provided a `coding101.ttt.Coordinate` representing the last player's move. The game
board will have been already updated with that move before this method is called.

### 1.2 Task

Implement `isWon(Coordinate lastMove)` such that `true` is returned only if **any** of the following
conditions are met, assuming [`size`](#board-size-constant) is `3`:

 * there are 3 status values for the same player in  a column
 * there are 3 status values for the same player in  a row
 * there are 3 status values for the same player in  a top-left to bottom-right diagonal
 * there are 3 status values for the same player in  a bottom-left to top-right diagonal

```java
/**
 * Test if the game has been won.
 * @param lastMove the last move made
 * @return true if the game has been won
 */
private boolean isWon(Coordinate lastMove) {
    return false;
}
```

## 1.3 Check if the game is a draw (tie)

The `boolean isDraw()` method needs to check if the game is over due to a draw, or tie.
The game board will have been already updated with the last player move before this method
is called.

### 1.3 Task

Implement `boolean isDraw()` such that `true` is returned only if **all** the following
conditions are met:

 * there are no possible ways for either player to win

```java
/**
 * Test if the game is a draw (tie/stalemate).
 * @return true if the game is a draw
 */
private boolean isDraw() {
    return false;
}
```

# Goal 2: dynamic board size

Change the `TicTacToe` `main()` method to accept a **size** argument that is an integer. This can be
accomplished like this:

```java
public static void main(String[] args) {
    // accept an integer size for the game board, defaulting to 3 if none provided
    var size = (args.length > 0 ? Integer.parseInt(args[0]) : 3);
    if (size < 2 || size > 26) {
        System.err.println("Invalid board size, please enter a size between 2 and 26.");
        System.exit(1);
    }
    var game = new TicTacToe(size);
```

## 2.1 Task

Make sure the game functions correctly with the dynamic board size, that is, the `isMoveValid()`,
`isWon()`, and `isDraw()` methods you wrote in Task 1 work for any size between 2 and 26.
