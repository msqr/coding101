# Goal 4: opening chests

As outlined in the [previous goal](./README-G3.md), when a player opens a chest they are meant to be
randomly granted a reward (an increase of coins) or damaged (a decrease of health). For this goal
you will implement this logic.

The [TextQuest](./src/main/java/coding101/tq/TextQuest.java) class has a method `void interactWithChest()`
that is called when the player interacts with a chest. The main bit of logic within this method looks
like this:

```java
if (player.interacted(game.map(), x, y)) {
    final GameConfiguration config = player.config();
    int coinsFound = 0;
    int damageTaken = 0;

    // TODO: open chest and deal with outcome: damage vs coins; decide first if
    // the chest provides coins or deducts health. Then decide either how many
    // coins to reward with, or health to deduct from, the player, updating the
    // coinsFound or damageTaken variables appropriately.

    if (coinsFound > 0) {
        message = MessageFormat.format(bundle.getString("chest.coinsAcquired"), coinsFound);
        player.addCoins(coinsFound);
        ui.info().draw();
    } else if (damageTaken > 0) {
        message = MessageFormat.format(bundle.getString("chest.damageTaken"), damageTaken);
        player.deductHealth(damageTaken);
        ui.health().draw();
    } else {
        message = bundle.getString("chest.empty");
    }
} else {
    // show message that chest has already been opened
    message = bundle.getString("chest.alreadyOpened");
}
```

The `player.interacted(game.map(), x, y)` method "marks" the terrain at (x,y) as "interated with"
and returns `true` if it had **not** been interacted with before. That allows the game to keep track
of which chests have been opened already, and to display an "already opened" message if the player
re-opens a chest.

The `TODO` comment is where you should implement the logic required when opening a chest for the
first time. There are two local variables your code should update: `coinsFound` and `damageTaken`.
These are initialized to 0 for you at the start:

```java
int coinsFound = 0;
int damageTaken = 0;
```

The local variable `config` holds a reference to a [GameConfiguration](./src/main/java/coding101/tq/GameConfiguration.java)
object, on which there are 3 `int` properties relevant to this logic:

| Config Property | Description |
|:----------------|:------------|
| `chestRewardFactor` | an integer percentage (1-100) that a chest provides a reward, versus a penalty |
| `chestCoinMaximum` | the maximum number of coins a chest can provide |
| `chestHealthDamageMaximum` | the maximum health that a chest can damage a player |

Implement the following logic:

 1. **Randomly decide** if the chest will reward or damage the player, using the `chestRewardFactor`
    property to weigh the outcome. For example, if `chestRewardFactor` is `60` it means there should
    be a 60% chance that the chest rewards the player, and a 40% chance it damages the player.
 2. **If rewarding the player**, grant the player a **random** number of coins between 0 and
    `chestCoinMaximum` (inclusive) by updating the `coinsFound` variable.
 3. **If damaging the player**, damage the player by a **random** number of health between 0 and
    `chestHealthDamageMaximum` (inclusive) by updating the `damageTaken` variable.

