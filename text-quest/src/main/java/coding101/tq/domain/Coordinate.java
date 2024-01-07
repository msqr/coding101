package coding101.tq.domain;

/**
 * An X,Y coordinate.
 */
public record Coordinate(int x, int y) implements Comparable<Coordinate> {

    @Override
    public int compareTo(Coordinate o) {
        if (o == null) {
            return 1;
        }
        int result = Integer.compare(x, o.x);
        if (result != 0) {
            return result;
        }
        return Integer.compare(y, o.y);
    }
}
