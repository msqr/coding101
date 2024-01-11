# Goal 2: fix movement

At the moment the player can move across any terrain. A player should not be able to move onto
**Mountain**, **Water**, **WallHorizontal**, **WallVertical**, or **WallCorner** terrain, however. A
player should be able to move onto a **Ship**, and then board that ship, and then move to any
**Water** terrain. To fix this method, complete the `canMoveTo(map, x, y)` method in the
[Player](./src/main/java/coding101/tq/domain/Player.java) class. Currently the method looks like
this:

```java
/**
 * Test if a player can move to a given coordinate on a given map.
 *
 * @param map the map to test
 * @param x   the x coordinate to test
 * @param y   the y coordinate to test
 * @return {@literal true} if the player is allowed to move to the (x,y) coordinate on {@code map}
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
