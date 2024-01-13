package coding101.tq.domain;

/**
 * Game settings.
 *
 * @param colors the game color scheme
 * @param items the game items (all possible items)
 */
public record Settings(ColorScheme colors, PlayerItems items) {}
