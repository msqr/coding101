# Goal 3: random digression

When a player opens a chest, they are meant to be randomly granted a reward (an increase of coins)
or damaged (a decrease of health). For this goal, you will learn about random numbers in Java,
so you an ready for the next goal of implementing the chest opening logic.

## About randomness

Most programming languages provide random number utilities that can return some sort of random
value to you when asked.

Computers are excellent at being precise, but not so excellent at being random. Mostly when you ask
a computer for a random number, they give you an approximate, or _pseudo_, random value instead. It
turns out that generating a truely random value is very hard, and can be computationally expensive,
so languages like Java provide different ways of generating random numbers, each of varying
**quality**. A low-quality method might execute quickly, but return predictable values over time. A
high-quality method might execute slowly, but return unpredictable values over time. The choice of
which style to use comes down to how important the quality of the randomness is versus the speed at
which the randomness can be obtained.

## Fast & cheap randomness

In Java the `Math.random()` method is a **low-quality** and fast method for generating random
**double** values. It returns a pseudorandom number in the range **>= 0.0 and  <1.0**. It turns out
having a random value beween 0 and 1 is quite useful, as you can think of this as being a **random
fraction**.

> :bulb: In Java the `float` and `double` types represent **floating-point number** values. A
> _floating point_ number is a number with a decimal point, like `1.23`, as opposed to the `int` and
> `long` types that can only hold whole integer values like `1`, `2`, and `3`. The difference
> between `float` and `double` is the possible range of values the type can hold. The `float` value
> is smaller and less precise than `double`. Similarly `int` has a smaller range of possible values
> it can hold versus `long`.

Imagine playing a coin toss game, where you toss a coin into the air and try to guess which side
will land facing up: **heads** or **tails**. You have a 50% chance of guessing the outcome, because
there are just two possible outcomes, so 100% / 2 == 50%. If you wanted to write the code for a coin
toss game, you could simulate the coin toss with `Math.random()` and treat the result as "tails"
when the number is between 0.0-0.5 (the first 50% range in 0-1) and "heads" when the number is
between 0.5-1.0 (the second 50% range in 0-1).

The [CoinToss](./src/test/java/coding101/tq/example/CoinToss.java) class does just this:

```java
public class CoinToss {
    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("Heads or tails? (h or t): ");
                String guess = reader.readLine();
                boolean guessTails = false;
                if ("t".equalsIgnoreCase(guess)) {
                    guessTails = true;
                } else if (!"h".equalsIgnoreCase(guess)) {
                    System.out.println("Enter 'h' for heads or 't' for tails.");
                    continue;
                }

                // rand is a number between 0 and 1
                double rand = Math.random();
                System.out.println("Random number is: " + rand);

                // treat 0 - 0.5 as tails, and 0.5 - 1.0 as heads
                if (rand < 0.5 && guessTails) {
                    System.out.println("Correct, it was tails (<0.5)!");
                } else if (rand >= 0.5 && !guessTails) {
                    System.out.println("Correct, it was heads (>= 0.5)!");
                } else {
                    System.out.println("Oops, it was " 
                        + (rand < 0.5 ? "tails" : "heads")
                        + ". Try again!");
                }
                System.out.println("");
            }
        }
    }
}
```

If you run this program, you'll be able to type <kbd>t</kbd> or <kbd>h</kbd>, followed by <kbd>⏎
Enter</kbd>, to guess the outcome, and then a random number `rand` is generated and the result
treated as **tails** if `rand` is less than 0.5, or **heads** of greater than or equal to 0.5. An
example output looks like this:

```
Heads or tails? (h or t): h
Random number is: 0.8935480104877707
Correct, it was heads (>= 0.5)!

Heads or tails? (h or t): h
Random number is: 0.9479736626322087
Correct, it was heads (>= 0.5)!

Heads or tails? (h or t): h
Random number is: 0.07050406231376127
Oops, it was tails. Try again!
```

> :question: The code tests for `rand < 0.5` and then `rand >= 0.5`. Why less-than in the first
> case, and then greater-than-or-equal in the second?

## Random range

In the game, the [`GameConfiguration`](./src/main/java/coding101/tq/GameConfiguration.java) class
defines a `chestCoinMaximum` variable, that can be changed by passing a `--chest-coins <arg>`
argument when running the game. This variable defines the **maximum number of coins a chest can
provide when opened**. The idea here being that, when the player opens a chest, it could give the
player a random number of coins, between 0 to `chestCoinMaximum`.

That's all find and dandy: we have `Math.random()` that can return random values, between 0 and...
oh wait! It returns values between 0 and 1, not 0 and `chestCoinMaximum`, which could be set to
**anything** when the game runs! What can we do?

> :bulb: Think for a moment here, and let me remind you about our earlier discussion of this 0 - 1
> random stuff being like a **random fraction**. Any ideas? From your math lessons, perhaps?

If you are reading this, I assume you quickly figured the solution out, either yourself or by
summoning your math teacher for assistance. Either way, bravo! You figured out you could
**multiply** the random number by the `chestCoinMaximum` value, which will always then result in a
number between 0 and `chestCoinMaximum`. The result would be a decimal, but we can round the value
to a nice integer to get whole integers between 0 and `chestCoinMaximum`. For example:

| Random Double | Chest Coin Maximum | Scary Math | Result | Rounded |
|:--------------|:-------------------|:-----------|:-------|:--------|
| 0.0           | 10                 | `0.0 × 10` | 0.0 | 0 |
| 0.2           | 10                 | `0.2 × 10` | 2.0 | 2 |
| 0.8           | 10                 | `0.8 × 10` | 8.0 | 8 |
| 0.99999999    | 10                 | `0.99999999 × 10` | 9.99999999 | 10 |

In Java the `Math.random()` method can round a `double` value into a `long` value. When you only
want an `int` you can **cast** a `long` into an `int` by adding `(int)` in front of the value you
want to affect, like this:

```java
int n = (int)Math.round(1.23); // cast the long returned by round() to int
// n == 1 here
```

## Exercise: random range

The [`DynamicRandomRange`](./src/test/java/coding101/tq/example/DynamicRandomRange.java) class is a
partially implemented program that accepts a single integer number argument and then should print
out 10 random numbers between 0 and that argument.

```java
public class DynamicRandomRange {

    public static int randomIntFromZeroTo(int max) {
        // TODO: implement this method by generating a random integer between 0 and max
        return 0;
    }

    public static void main(String[] args) throws IOException {
        int max = Integer.parseInt(args[0]);
        for (int i = 0; i < 10; i++) {
            int rand = randomIntFromZeroTo(max);
            System.out.println(rand);
        }
    }
}
```

Implement the `randomIntFromZeroTo(max)` method so it returns an `int` value between 0 and `max`.
Run the program several times to check if it does return values in the given range.
