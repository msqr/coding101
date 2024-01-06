package coding101.tq.domain;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.TextColor.RGB;

/**
 * Color scheme.
 */
public final record ColorScheme(
        String uiText,
        String uiTextBg,
        String uiBorder,
        String uiBorderBg,
        String health,
        String healthBg,
        String healthPartial) {

    /**
     * Parse a string color reference.
     *
     * @param ref     the color reference; can be either a hex-encoded RGB color in
     *                the form {@literal #rrggbb} or any {@link ANSI} name
     * @param default the default color to use if {@code ref} is null or empty
     * @return the parsed color
     * @throws IllegalArgumentException if the color reference is not supported
     */
    public static TextColor color(String ref, TextColor defaultColor) {
        if (ref == null || ref.isBlank()) {
            return defaultColor;
        }
        if (ref.startsWith("#")) {
            // treat as RGB hex
            if (ref.length() != 7) {
                throw new IllegalArgumentException(
                        "RGB hex color [%s] invalid (must be in form #rrggbb.".formatted(ref));
            }
            return new RGB(
                    Integer.parseInt(ref.substring(1, 3), 16),
                    Integer.parseInt(ref.substring(3, 5), 16),
                    Integer.parseInt(ref.substring(5, 7), 16));
        }
        for (ANSI a : ANSI.values()) {
            if (ref.compareToIgnoreCase(a.name()) == 0) {
                return a;
            }
        }
        throw new IllegalArgumentException("Unsupported color [%s].".formatted(ref));
    }
}
