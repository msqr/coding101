package coding101.tq.domain;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A color palette.
 */
public record ColorPalette(
        String uiText,
        String uiBorder,
        String health,
        String healthPartial,
        String player,
        String cave,
        String chest,
        String forest,
        String grass,
        String hill,
        String lava,
        String lavaRock,
        String mountain,
        String sand,
        String ship,
        String town,
        String wall,
        String water) {

    /**
     * A Vim-compatible terminal color mapping of lower-case names to associated
     * {@link TextColor} objects.
     */
    public static final Map<String, TextColor> COLOR_NAMES;

    static {
        Map<String, TextColor> m = new HashMap<>(24);
        m.put("black", ANSI.BLACK);
        m.put("darkblue", ANSI.BLUE);
        m.put("darkgreen", ANSI.GREEN);
        m.put("darkcyan", ANSI.CYAN);
        m.put("darkred", ANSI.RED);
        m.put("darkmagenta", ANSI.MAGENTA);
        m.put("brown", ANSI.YELLOW);
        m.put("darkyellow", ANSI.YELLOW);
        m.put("lightgray", ANSI.WHITE);
        m.put("lightgrey", ANSI.WHITE);
        m.put("gray", ANSI.WHITE);
        m.put("grey", ANSI.WHITE);
        m.put("darkgray", ANSI.BLACK_BRIGHT);
        m.put("darkgrey", ANSI.BLACK_BRIGHT);
        m.put("blue", ANSI.BLUE_BRIGHT);
        m.put("lightblue", ANSI.BLUE_BRIGHT);
        m.put("green", ANSI.GREEN_BRIGHT);
        m.put("lightgreen", ANSI.GREEN_BRIGHT);
        m.put("cyan", ANSI.CYAN_BRIGHT);
        m.put("lightcyan", ANSI.CYAN_BRIGHT);
        m.put("red", ANSI.RED_BRIGHT);
        m.put("lightred", ANSI.RED_BRIGHT);
        m.put("magenta", ANSI.MAGENTA_BRIGHT);
        m.put("lightmagenta", ANSI.MAGENTA_BRIGHT);
        m.put("yellow", ANSI.YELLOW_BRIGHT);
        m.put("lightyellow", ANSI.YELLOW_BRIGHT);
        m.put("white", ANSI.WHITE_BRIGHT);
        COLOR_NAMES = Collections.unmodifiableMap(m);
    }

    /**
     * Parse a string color reference.
     *
     * @param ref     the color reference; can be either a hex-encoded RGB color in
     *                the form {@literal #hh} (256 color index) or
     *                {@literal #rrggbb} or any {@link ANSI} name or any name
     *                supported by the {@link #COLOR_NAMES} mapping
     * @param default the default color to use if {@code ref} is null or empty
     * @return the parsed color
     * @throws IllegalArgumentException if the color reference is not supported
     */
    public static TextColor color(String ref, TextColor defaultColor) {
        if (ref == null || ref.isBlank()) {
            return defaultColor;
        }
        String lcRef = ref.toLowerCase();
        TextColor result = COLOR_NAMES.get(lcRef);
        if (result != null) {
            return result;
        }
        return TextColor.Factory.fromString(ref);
    }

    /**
     * Get a color for a terrain.
     *
     * @param type         the terrain type
     * @param defaultColor the default color to use
     * @return the color
     */
    public TextColor terrain(TerrainType type, TextColor defaultColor) {
        if (type == null) {
            return defaultColor;
        }
        return switch (type) {
            case Cave -> color(cave(), defaultColor);
            case Chest -> color(chest(), defaultColor);
            case Empty -> defaultColor;
            case Forest -> color(forest(), defaultColor);
            case Grass -> color(grass(), defaultColor);
            case Hill -> color(hill(), defaultColor);
            case Lava -> color(lava(), defaultColor);
            case LavaRock -> color(lavaRock(), defaultColor);
            case Mountain -> color(mountain(), defaultColor);
            case Sand -> color(sand(), defaultColor);
            case Ship -> color(ship(), defaultColor);
            case Town -> color(town(), defaultColor);
            case WallCorner, WallHorizontal, WallVertical -> color(wall(), defaultColor);
            case Water -> color(water(), defaultColor);
        };
    }
}
