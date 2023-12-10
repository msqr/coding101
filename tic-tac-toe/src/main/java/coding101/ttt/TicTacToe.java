package coding101.ttt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Console based 2-player Tic Tac Toe game.
 */
public class TicTacToe {

    /** Our board size. */
    private final int size;

    /**
     * The current game board.
     *
     * The board is implemented as a 2D array.
     * Each array element represents a "row" in the game, and there are {@code size} rows.
     * Each row array element represents a specific "column" in the game, and there are {@code size} columns.
     *
     * Visualize the 2D array like this:
     *
     * ┌─────────────────┐
     * │    ┌───────────┐│
     * │ 0: │ 0 │ 1 │ 2 ││
     * │    └───────────┘│
     * ├─────────────────┤
     * │    ┌───────────┐│
     * │ 1: │ 0 │ 1 │ 2 ││
     * │    └───────────┘│
     * ├─────────────────┤
     * │    ┌───────────┐│
     * │ 2: │ 0 │ 1 │ 2 ││
     * │    └───────────┘│
     * └─────────────────┘
     *
     * A {@code null} row element value represents an unused square, that can be "occupied" by
     * a {@code Status} value (that is, X or O).
     */
    private final Status[][] board;

    /** The next player move. */
    private Status next;

    /**
     * Constructor.
     * @param size the board size; use 3 for a "classic" board
     */
    public TicTacToe(int size) {
        this.size = size;
        board = new Status[size][];
        for (int i = 0; i < size; i++) {
            board[i] = new Status[size];
        }
        next = Status.X;
    }

    /**
     * Start the game.
     * @param in the input source, for example {@code new BufferedReader(new InputStreamReader(System.in)}
     * @param out the output destination, for example {@code new BufferedWriter(new OutputStreamWriter(System.out))}
     * @throws IOException if any I/O error occurs
     */
    public void go(BufferedReader in, BufferedWriter out) throws IOException {
        out.write("\nEnter moves like A1 (top-left) or B2 (center).\n\n");
        Utils.printBoard(board, out);
        while (true) {
            // prompt for next move
            var coord = Utils.promptNextMove(next, in, out);

            if (isMoveValid(coord)) {
                // update board with entered move
                board[coord.y()][coord.x()] = next;

                // print board
                out.write('\n');
                Utils.printBoard(board, out);

                if (isWon(coord)) {
                    out.write('\n');
                    out.write("Player %s wins!\n".formatted(next));
                    out.flush();
                    return;
                } else if (isDraw()) {
                    out.write('\n');
                    out.write("It's a draw!\n");
                    out.flush();
                    return;
                } else {
                    // switch to next player
                    next = (next == Status.X ? Status.O : Status.X);
                }
            } else {
                out.write("Invalid move! Try again.");
            }
        }
    }

    /**
     * Verify a coordinate can be filled, by validating it is within the board bounds and the given coordinate is not already occupied.
     * @param coord the coordinate to check
     * @return true if the given coordinate can be moved on
     */
    private boolean isMoveValid(Coordinate coord) {
        return true;
    }

    /**
     * Test if the game has been won.
     * @param lastMove the last move made
     * @return true if the game has been won
     */
    private boolean isWon(Coordinate lastMove) {
        return false;
    }

    /**
     * Test if the game is a draw (tie/stalemate).
     * @return true if the game is a draw
     */
    private boolean isDraw() {
        return false;
    }

    public static void main(String[] args) {
        var game = new TicTacToe(3);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out))) {
            game.go(in, out);
        } catch (java.io.IOException e) {
            System.err.printf("I/O exception: %s", e.toString());
        }
    }
}
