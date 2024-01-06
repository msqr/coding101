package coding101.tq.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A map of terrain, as a 2D array of rows.
 */
public class TerrainMap {

    private final String name;
    private final TerrainType[][] terrain;
    private final int width;
    private final int height;

    /**
     * Constructor.
     *
     * @param name    the map name
     * @param terrain the terrain
     * @throws IllegalArgumentException if any argument is {@litearl null}
     */
    public TerrainMap(String name, TerrainType[][] terrain) {
        super();
        this.name = Objects.requireNonNull(name);
        this.terrain = Objects.requireNonNull(terrain);
        if (terrain.length < 1 || terrain[0] == null || terrain[0].length < 1) {
            throw new IllegalArgumentException("Invalid terrain array: must have at least 1 non-empty element.");
        }
        this.width = terrain[0].length;
        this.height = terrain.length;
    }

    /**
     * Get the map name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the terrain width.
     *
     * @return the width
     */
    public final int width() {
        return width;
    }

    /**
     * Get the terrain height.
     *
     * @return the height.
     */
    public final int height() {
        return height;
    }

    /**
     * Get the terrain type at a specific coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the terrain type, or {@link TerrainType#Empty} if {@code x} or
     *         {@code y} are out of bounds
     */
    public final TerrainType terrainAt(int x, int y) {
        if (x >= width || y >= height) {
            return TerrainType.Empty;
        }
        return terrain[y][x];
    }

    /**
     * Modify the terrain type at a specific coordinate.
     *
     * @param x    the x coordinate
     * @param y    the y coordinate
     * @param type the type to set
     * @returns {@code true} if the terrain was changed
     */
    public final boolean modifyAt(int x, int y, TerrainType type) {
        if (x >= width || y >= height) {
            return false;
        }
        if (terrain[y][x] != type) {
            terrain[y][x] = type;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "TerrainMap{width=" + width + ", height=" + height + "}";
    }

    /**
     * Consume a terrain coordinate.
     */
    @FunctionalInterface
    public static interface TerrainConsumer {

        /**
         * Process a single terrain coordinate.
         *
         * @param x    the x coordinate
         * @param y    the y coordinate
         * @param type the terrain type
         */
        void accept(int x, int y, TerrainType type);
    }

    /**
     * Walk a quadrant of the terrain.
     *
     * This method will walk all coordinates in a given quadrant, that is defined by
     * the top-left (x,y) origin and a width and height. The width and height will
     * be capped to the overall terrain width and height.
     *
     * @param x      the x origin
     * @param y      the y origin
     * @param width  the quadrant width
     * @param height the quadrant height
     * @param out    the destination
     */
    public void walk(int x, int y, int width, int height, TerrainConsumer out) {
        int maxCol = Math.min(this.width, x + width);
        int maxRow = Math.min(this.height, y + height);
        for (int row = y; row < maxRow; row++) {
            for (int col = x; col < maxCol; col++) {
                out.accept(col, row, terrain[row][col]);
            }
        }
    }

    /**
     * Render the complete map to a string.
     *
     * @return the map rendered as as string
     * @throws RuntimeException if there is any problem rendering
     */
    public String render() {
        return render(0, 0, width, height);
    }

    /**
     * Render a quadrant as a string.
     *
     * @param x      the x origin
     * @param y      the y origin
     * @param width  the quadrant width
     * @param height the quadrant height
     * @return the quadrant rendered as a string
     * @throws RuntimeException if there is any problem rendering
     */
    public String render(int x, int y, int width, int height) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(width * height)) {
            walk(x, y, width, height, new TerrainPrinter(out));
            return out.toString(StandardCharsets.US_ASCII);
        } catch (IOException e) {
            throw new RuntimeException("Error rendering quadrant: %s".formatted(e.getMessage()), e);
        }
    }

    /**
     * A {@link TerrainConsumer} that prints the terrain to an output stream.
     *
     * Rows are delimited with a newline character.
     */
    public static final class TerrainPrinter implements TerrainConsumer {

        private final OutputStream out;
        int lastRow = -1;

        /**
         * Constructor.
         *
         * @param out the output stream
         * @throws IllegalArgumentException if any argument is {@literal null}
         */
        public TerrainPrinter(OutputStream out) {
            super();
            this.out = Objects.requireNonNull(out);
        }

        @Override
        public void accept(int x, int y, TerrainType type) {
            try {
                if (lastRow < 0) {
                    lastRow = y;
                } else if (y > lastRow) {
                    out.write('\n');
                    lastRow = y;
                }
                out.write(type != null ? type.getKey() : TerrainType.EMPTY);
            } catch (IOException e) {
                throw new RuntimeException("Error writing to output stream: %s".formatted(e.getMessage()), e);
            }
        }
    }
}
