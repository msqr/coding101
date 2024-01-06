# Text Quest

This project is an incomplete console-based adventure game. 

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
