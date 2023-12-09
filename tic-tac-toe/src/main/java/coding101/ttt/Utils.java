package coding101.ttt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Utility functions.
 */
public final class Utils {

    /** Regular expression to extract a letter,number coordinate. */
    private static final Pattern COORDINATE_REGEX = Pattern.compile("([a-zA-Z])(?:\\s*,?\\s*)(\\d+)");

    private Utils() {
        // not available
    }

    /**
     * Prompt for the next move, returning a coordinate.
     * @param next the next move (for display in the prompt)
     * @param in the input source
     * @param out the output destination
     * @return the coordinate
     * @throws IOException if any I/O error occurs
     */
    public static Coordinate promptNextMove(Status next, BufferedReader in, BufferedWriter out) throws IOException {
        while (true) {
            out.write("\nPlayer %s move: ".formatted(next));
            out.flush();
            var line = in.readLine();
            var match = COORDINATE_REGEX.matcher(line);
            if (match.find()) {
                var col = match.group(1).charAt(0);
                var row = Integer.parseInt(match.group(2));
                return new Coordinate(col, row);
            }
        }
    }

    /**
     * Render a game board.
     *
     * @param board the board to render; each array element is a row in the game board
     * @param out the output to write to
     * @throws IOException if any I/O error occurs
     */
    public static void printBoard(Status[][] board, BufferedWriter out) throws IOException {
        final var height = board.length;
        for (var y = 0; y < height; y++) {
            final var width = board[y].length;
            if (y > 0) {
                // draw a row delimiter
                for (var x = 0; x < width; x++) {
                    if (x > 0) {
                        out.write("┼");
                    }
                    out.write("───");
                }

                out.append('\n');
            }
            for (var x = 0; x < width; x++) {
                if (x > 0) {
                    out.write("│");
                }
                var status = board[y][x];
                var display = " ";
                if (status != null) {
                    display = status.toString();
                }
                out.append(' ').append(display).append(' ');
            }
            out.append('\n');
        }
        out.flush();
    }
}
