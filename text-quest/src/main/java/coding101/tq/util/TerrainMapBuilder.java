package coding101.tq.util;

import static java.nio.charset.StandardCharsets.US_ASCII;

import coding101.tq.domain.TerrainMap;
import coding101.tq.domain.TerrainType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to help build up a {@link TerrainMap} from map tiles.
 */
public class TerrainMapBuilder {

    /**
     * A terrain map "tile" which is a subset of the overall terrain, positioned at
     * x and y coordinates.
     */
    public static final class Tile implements Comparable<Tile> {
        private final int x;
        private final int y;
        private final TerrainType[][] terrain;
        private final Map<String, String> metadata;

        /**
         * Constructor.
         *
         * @param x       the tile horizontal coordinate
         * @param y       the tile vertical coordinate
         * @param terrain the terrain data
         * @throws IllegalArgumentException if any argument is {@literal null}
         */
        public Tile(int x, int y, TerrainType[][] terrain, Map<String, String> metadata) {
            super();
            this.x = x;
            this.y = y;
            this.terrain = Objects.requireNonNull(terrain);
            this.metadata = Objects.requireNonNull(metadata);
        }

        /**
         * Get the X coordinate.
         *
         * @return the x coordinate
         */
        public int getX() {
            return x;
        }

        /**
         * Get the Y coordinate.
         *
         * @return the y coordinate
         */
        public int getY() {
            return y;
        }

        /**
         * Get the number of columns.
         *
         * @return the number of columns.
         */
        public int getWidth() {
            return terrain[0].length;
        }

        /**
         * Get the number of rows.
         *
         * @return the number of rows
         */
        public int getHeight() {
            return terrain.length;
        }

        /**
         * Get the terrain data.
         *
         * @return the terrain data
         */
        public TerrainType[][] getTerrain() {
            return terrain;
        }

        /**
         * Get the metadata.
         *
         * @return the metadata
         */
        public Map<String, String> getMetadata() {
            return metadata;
        }

        @Override
        public int compareTo(Tile o) {
            int result = Integer.compare(y, o.y);
            if (result != 0) {
                return result;
            }
            return Integer.compare(x, o.x);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Objects.hash(x, y);
            return result;
        }

        /**
         * Compare for equality.
         *
         * This method compares the {@code x} and {@code y} coordinates for equality.
         *
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Tile other = (Tile) obj;
            return x == other.x && y == other.y;
        }
    }

    /**
     * The tile resource file name pattern.
     *
     * This pattern expects the name to follow the pattern <code>X,Y.tqmap</code>,
     * where {@code X} and {@code Y} are integer numbers.
     */
    public static final Pattern RESOURCE_NAME_REGEX = Pattern.compile(".*(\\d+),(\\d+).tqmap");

    private SortedSet<Tile> tiles = new TreeSet<>();

    /**
     * Constructor.
     */
    public TerrainMapBuilder() {
        super();
    }

    /**
     * Get the number of tiles that have been loaded.
     *
     * @return the number of loaded tiles
     */
    public int getSize() {
        return tiles.size();
    }

    /**
     * Add a tile.
     *
     * @param tile the tile to add
     * @return this instance
     */
    public TerrainMapBuilder addTile(Tile tile) {
        tiles.add(tile);
        return this;
    }

    /**
     * Build a {@link TerrainMap} from the loaded tiles.
     *
     * @param name the name
     * @return the map
     * @throws IllegalArgumentException if any argument is {@literal null} or no
     *                                  tiles have been loaded
     */
    public TerrainMap build(String name) {
        int rows = 0;
        int cols = 0;
        int tileWidth = 0;
        int tileHeight = 0;

        // calculate dimensions
        for (Tile t : tiles) {
            // make sure all tiles are of the same width/height
            if (tileWidth < 1) {
                tileWidth = t.getWidth();
                tileHeight = t.getHeight();
            } else if (t.getWidth() != tileWidth || t.getHeight() != tileHeight) {
                throw new IllegalArgumentException(
                        "Inconsistent tile width: expected (%d,%d) but got (%d,%d) for tile (%d,%d)"
                                .formatted(tileWidth, tileHeight, t.getWidth(), t.getHeight(), t.x, t.y));
            }
            int maxRow = t.y * tileHeight + tileHeight;
            int maxCol = t.x * tileWidth + tileWidth;
            if (maxRow > rows) {
                rows = maxRow;
            }
            if (maxCol > cols) {
                cols = maxCol;
            }
        }

        TerrainType[][] terrain = new TerrainType[rows][];
        Map<String, String> metadata = new LinkedHashMap<>(4);
        for (Tile t : tiles) {
            metadata.putAll(t.getMetadata());
            for (int row = 0, len = t.getHeight(); row < len; row++) {
                int destRow = t.y * tileHeight + row;
                if (terrain[destRow] == null) {
                    terrain[destRow] = new TerrainType[cols];
                }
                System.arraycopy(
                        t.terrain[row],
                        0,
                        terrain[destRow],
                        t.x * tileWidth,
                        Math.min(t.terrain[row].length, tileWidth));
            }
        }

        return new TerrainMap(name, terrain, metadata);
    }

    /**
     * Parse all tile resources in a directory.
     *
     * @param directoryName the directory to scan and parse all tile resources from
     * @return the builder
     * @throws IllegalArgumentException if the resource cannot be parsed
     */
    public static TerrainMapBuilder parseResources(String directoryName) {
        TerrainMapBuilder b = new TerrainMapBuilder();
        try (ScanResult scanResult = new ClassGraph().acceptPaths(directoryName).scan()) {
            scanResult.getResourcesMatchingPattern(RESOURCE_NAME_REGEX).forEach((Resource res) -> {
                try (InputStream in = res.open()) {
                    b.addTile(parseTileResource(res.getPath(), in));
                } catch (IOException e) {
                    throw new IllegalArgumentException(
                            "Error parsing resource [%s]: %s".formatted(res.getPath(), e.getMessage()), e);
                }
            });
        }
        if (b.getSize() < 1) {
            // try loading file paths
            try (DirectoryStream<Path> s = Files.newDirectoryStream(Path.of(directoryName), (Path p) -> {
                Matcher m = RESOURCE_NAME_REGEX.matcher(p.getFileName().toString());
                return m.find();
            })) {
                s.forEach(p -> {
                    try (InputStream in = Files.newInputStream(p)) {
                        b.addTile(parseTileResource(p.toString(), in));
                    } catch (IOException e) {
                        throw new IllegalArgumentException(
                                "Error parsing resource [%s]: %s".formatted(p, e.getMessage()), e);
                    }
                });
            } catch (NoSuchFileException e) {
                throw new IllegalArgumentException("Map directory [%s] not found!".formatted(directoryName));
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        "Error loading tile files from directory [%s]: %s".formatted(directoryName, e.getMessage()), e);
            }
        }
        if (b.getSize() < 1) {
            throw new IllegalArgumentException(
                    "Map directory [%s] does not contain any map tile files!".formatted(directoryName));
        }
        return b;
    }

    /**
     * Parse {@code US_ASCII} encoded {@link TerrainType} resource.
     *
     * @param resource the resource to parse; must have a file name that matches
     *                 {@link #RESOURCE_NAME_REGEX}
     * @return the parsed tile
     * @throws IllegalArgumentException if the resource cannot be parsed
     */
    public static Tile parseTileResource(String resource) {
        // first try classpath resource
        try {
            try (InputStream in = TerrainMapBuilder.class.getClassLoader().getResourceAsStream(resource)) {
                return parseTileResource(resource, in);
            } catch (NullPointerException e) {
                // resource not found; try as file path
                try (InputStream in2 = Files.newInputStream(Paths.get(resource))) {
                    return parseTileResource(resource, in2);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Error reading resource [%s]: %s".formatted(resource, e.getMessage(), e));
        }
    }

    /**
     * Parse {@code US_ASCII} encoded {@link TerrainType} resource.
     *
     * @param resource the resource name
     * @param in       the data input stream
     * @return the parsed tile
     * @throws IllegalArgumentException if the resource cannot be parsed
     */
    public static Tile parseTileResource(String resource, InputStream in) {
        Path path = Path.of(resource);
        String fileName = path.getFileName().toString();
        Matcher matcher = RESOURCE_NAME_REGEX.matcher(fileName);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Resource must match 'X,Y.tqmap' pattern.");
        }

        TerrainType[][] data = null;
        Map<String, String> metadata = new LinkedHashMap<>(4);

        // first try classpath resource
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, US_ASCII))) {
            data = parseTerrainData(r, metadata);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Error reading resource [%s]: %s".formatted(resource, e.getMessage(), e));
        }

        return new Tile(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), data, metadata);
    }

    /**
     * A comment metadata line with a key/value pair in the form {@code key: value}.
     */
    public static final Pattern METADATA_REGEX =
            Pattern.compile("#-\\s*([a-zA-Z0-9_-]+)\s*:\s*(.*)", Pattern.CASE_INSENSITIVE);

    /**
     * Parse {@code US_ASCII} encoded terrain data.
     *
     * Blank lines or those staring with {@literal #} are ignored.
     *
     * @param r        the resource to parse
     * @param metadata the metadata map to populate with any extracted metadata
     * @return the terrain data
     * @throws IOException if any I/O error occurs
     */
    public static TerrainType[][] parseTerrainData(BufferedReader r, Map<String, String> metadata) throws IOException {
        List<TerrainType[]> rows = new ArrayList<>(64);
        String line = null;
        while ((line = r.readLine()) != null) {
            if (line.isBlank()) {
                // skip blank line
                continue;
            } else if (line.charAt(0) == '#') {
                if (line.length() > 1 && line.charAt(1) == '-') {
                    // metadata line
                    Matcher m = METADATA_REGEX.matcher(line);
                    if (m.matches()) {
                        String key = m.group(1).toLowerCase();
                        String val = m.group(2).trim();
                        metadata.put(key, val);
                    }
                }
                continue;
            }
            // since we assume the data is US_ASCII, the string length is our row length
            TerrainType[] row = new TerrainType[line.length()];
            for (int i = 0, len = row.length; i < len; i++) {
                row[i] = TerrainType.forKey(line.charAt(i));
            }
            rows.add(row);
        }
        return rows.toArray(TerrainType[][]::new);
    }

    /**
     * Construct a new {@link TerrainMap} of all {@literal null} values.
     *
     * @param name   the map name
     * @param width  the map width
     * @param height the map height
     * @return the new map instance
     * @throws IllegalArgumentException if any argument is null
     */
    public static TerrainMap nullMap(String name, int width, int height) {
        TerrainType[][] data = new TerrainType[height][];
        for (int row = 0; row < height; row++) {
            data[row] = new TerrainType[width];
        }
        return new TerrainMap(name, data, Collections.emptyMap());
    }
}
