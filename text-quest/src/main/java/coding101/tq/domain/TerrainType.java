package coding101.tq.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of terrain types.
 */
public enum TerrainType {
    Cave(TerrainType.CAVE),

    Chest(TerrainType.CHEST),

    Empty(TerrainType.EMPTY),

    Forest(TerrainType.FOREST),

    Grass(TerrainType.GRASS),

    Lava(TerrainType.LAVA),

    Mountain(TerrainType.MOUNTAIN),

    Sand(TerrainType.SAND),

    Ship(TerrainType.SHIP),

    Town(TerrainType.TOWN),

    WallHorizontal(TerrainType.WALL_HORIZONTAL),

    WallVertical(TerrainType.WALL_VERTICAL),

    WallCorner(TerrainType.WALL_CORNER),

    Water(TerrainType.WATER),
    ;

    public static final char CAVE = 'O';
    public static final char CHEST = '%';
    public static final char EMPTY = ' ';
    public static final char FOREST = '^';
    public static final char GRASS = '.';
    public static final char LAVA = '=';
    public static final char MOUNTAIN = 'A';
    public static final char SAND = ',';
    public static final char SHIP = '&';
    public static final char TOWN = '*';
    public static final char WATER = '~';

    public static final char WALL_VERTICAL = '|';
    public static final char WALL_HORIZONTAL = '-';
    public static final char WALL_CORNER = '+';

    private final char key;

    private TerrainType(char key) {
        this.key = key;
    }

    /**
     * Get the key.
     *
     * @return the key
     */
    @JsonValue
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
    @JsonCreator
    public static final TerrainType forKey(char key) {
        return switch (key) {
            case CAVE -> Cave;
            case CHEST -> Chest;
            case FOREST -> Forest;
            case GRASS -> Grass;
            case LAVA -> Lava;
            case MOUNTAIN -> Mountain;
            case SAND -> Sand;
            case SHIP -> Ship;
            case TOWN -> Town;
            case WALL_CORNER -> WallCorner;
            case WALL_HORIZONTAL -> WallHorizontal;
            case WALL_VERTICAL -> WallVertical;
            case WATER -> Water;
            default -> Empty;
        };
    }
}
