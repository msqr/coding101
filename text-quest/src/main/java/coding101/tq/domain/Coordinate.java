package coding101.tq.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An X,Y coordinate.
 */
public record Coordinate(int x, int y) implements Comparable<Coordinate> {

    /** A string-encoding pattern for a coordinate, as {@code "x,y"}. */
    public static final Pattern KEY_PATTERN = Pattern.compile("(\\d+),(\\d+)");

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

    /**
     * Get a "key" encoding for this coordinate, as {@code "x,y"}.
     *
     * @return the key encoding
     */
    public String toKey() {
        return String.valueOf(x) + ',' + String.valueOf(y);
    }

    /**
     * Parse a "key" encoding for this coordinate.
     *
     * @param key the key to parse, as {@code "x,y"}
     * @return the parsed key, or {@code null} if {@code key} is {@code null}
     * @throws IllegalArgumentException if {@code key} is not a valid format
     */
    public static Coordinate forKey(String key) {
        if (key == null) {
            return null;
        }
        Matcher m = KEY_PATTERN.matcher(key);
        if (!m.find()) {
            throw new IllegalArgumentException("Invalid coordinate key value [%s]".formatted(key));
        }
        return new Coordinate(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)));
    }
}
