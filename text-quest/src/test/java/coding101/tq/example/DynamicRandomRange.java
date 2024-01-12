package coding101.tq.example;

import java.io.IOException;

/**
 * Demonstration of a random range calculation.
 */
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
