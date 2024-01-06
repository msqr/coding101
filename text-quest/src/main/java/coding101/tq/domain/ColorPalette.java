package coding101.tq.domain;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;

/**
 * A color palette.
 */
public record ColorPalette(
        String uiText,
        String uiBorder,
        String health,
        String healthPartial,
        String cave,
        String forest,
        String grass,
        String lava,
        String mountain,
        String town,
        String water) {

    /**
     * Parse a string color reference.
     *
     * @param ref     the color reference; can be either a hex-encoded RGB color in
     *                the form {@literal #hh} (256 color index) or
     *                {@literal #rrggbb} or any {@link ANSI} name
     * @param default the default color to use if {@code ref} is null or empty
     * @return the parsed color
     * @throws IllegalArgumentException if the color reference is not supported
     */
    public static TextColor color(String ref, TextColor defaultColor) {
        if (ref == null || ref.isBlank()) {
            return defaultColor;
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
            case Empty -> defaultColor;
            case Forest -> color(forest(), defaultColor);
            case Grass -> color(grass(), defaultColor);
            case Lava -> color(lava(), defaultColor);
            case Mountain -> color(mountain(), defaultColor);
            case Town -> color(town(), defaultColor);
            case Water -> color(water(), defaultColor);
        };
    }
}
