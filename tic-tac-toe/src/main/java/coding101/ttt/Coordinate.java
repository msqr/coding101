package coding101.ttt;

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
