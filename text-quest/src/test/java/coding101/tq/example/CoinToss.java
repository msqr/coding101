package coding101.tq.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Demonstration of a random coin toss style game.
 */
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
                    System.out.println("Oops, it was " + (rand < 0.5 ? "tails" : "heads") + ". Try again!");
                }
                System.out.println("");
            }
        }
    }
}
