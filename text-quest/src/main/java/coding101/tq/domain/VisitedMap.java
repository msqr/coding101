package coding101.tq.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import java.util.BitSet;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A map of "visited" terrain.
 */
public class VisitedMap {

    private SortedMap<Integer, BitSet> visitedRows = new TreeMap<>();

    /**
     * Constructor.
     */
    public VisitedMap() {
        super();
    }

    /**
     * Get the visited data.
     *
     * @return the visited data
     */
    @JsonGetter(value = "visited")
    public SortedMap<Integer, BitSet> visited() {
        return visitedRows;
    }

    /**
     * Mark a specific map coordinate as visited.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return {@code true} if the coordinate was not visited before
     */
    public boolean visit(int x, int y) {
        BitSet row = visitedRows.computeIfAbsent(y, BitSet::new);
        boolean result = row.get(x);
        row.set(x);
        return !result;
    }

    /**
     * Test if a specific map coordinate has been visited before.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return {@code true} if the coordinate has been visited before
     */
    public boolean hasVisited(int x, int y) {
        BitSet row = visitedRows.get(y);
        return (row != null ? row.get(x) : false);
    }

    /**
     * Test if a specific map coordinate has been visited "near by" before.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return {@code true} if the coordinate has been visited "near by" before
     */
    public boolean hasVisitedNear(int x, int y) {
        for (int row = Math.max(0, y - 1), maxRow = y + 1; row <= maxRow; row++) {
            BitSet visitedRow = visitedRows.get(row);
            if (visitedRow == null) {
                continue;
            }
            for (int col = Math.max(0, x - 1), maxCol = x + 1; col <= maxCol; col++) {
                if (visitedRow.get(col)) {
                    return true;
                }
            }
        }
        return false;
    }
}
