package coding101.tq.domain;

/**
 * Enumeration of terrain types.
 */
public enum TerrainType {
    Empty(TerrainType.EMPTY),

    Mountain(TerrainType.MOUNTAIN),

    Forest(TerrainType.FOREST),

    Grass(TerrainType.GRASS),

    Water(TerrainType.WATER),

    Lava(TerrainType.LAVA),

    Cave(TerrainType.CAVE),

    Town(TerrainType.TOWN),
    ;

    public static final char EMPTY = ' ';
    public static final char MOUNTAIN = 'A';
    public static final char FOREST = '^';
    public static final char GRASS = '.';
    public static final char WATER = '~';
    public static final char LAVA = '=';
    public static final char CAVE = 'O';
    public static final char TOWN = '*';

    private final char key;

    private TerrainType(char key) {
        this.key = key;
    }

    /**
     * Get the key.
     *
     * @return the key
     */
    public char getKey() {
        return key;
    }

    /**
     * Get an enum value for a key.
     *
     * Any unsupported type will be mapped to {@code Empty}.
     *
     * @param key the key to get the enum value for
     * @return the enum value
     */
    public static final TerrainType forKey(char key) {
        return switch (key) {
            case MOUNTAIN -> Mountain;
            case FOREST -> Forest;
            case GRASS -> Grass;
            case WATER -> Water;
            case LAVA -> Lava;
            case CAVE -> Cave;
            case TOWN -> Town;
            default -> Empty;
        };
    }
}
