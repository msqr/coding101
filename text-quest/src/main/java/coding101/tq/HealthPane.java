package coding101.tq;

import static coding101.tq.domain.ColorPalette.color;
import static java.util.Objects.requireNonNull;

import coding101.tq.domain.Player;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TextColor.ANSI;

/**
 * The UI health pane.
 */
public class HealthPane implements Pane {

    private final Game game;
    private final int width;
    private final int height;

    /**
     * Constructor.
     *
     * @param game        the game
     * @param rightOffset the width
     * @param height      the height
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public HealthPane(Game game, int width, int height) {
        super();
        this.game = requireNonNull(game);
        this.width = width;
        this.height = height;
    }

    @Override
    public int top() {
        return game.screen().getTerminalSize().getRows() - height - 1;
    }

    @Override
    public int left() {
        return game.screen().getTerminalSize().getColumns() - width - 1;
    }

    @Override
    public int bottom() {
        return game.screen().getTerminalSize().getRows() - 1;
    }

    @Override
    public int right() {
        return game.screen().getTerminalSize().getColumns() - 1;
    }

    /** The amount of health each display heart represents. */
    public static final int PLAYER_HEALTH_HEART_VALUE = 5;

    @Override
    public void draw() {
        int health = game.player().getHealth();
        int partial = health % PLAYER_HEALTH_HEART_VALUE;
        int full = (health - partial) / PLAYER_HEALTH_HEART_VALUE;
        int y = top();
        int startX = left();

        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().health(), ANSI.RED));
        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().health(), ANSI.BLACK));

        // draw all full hearts
        for (int row = startX, max = startX + full; row < max; row++) {
            game.textGraphics().setCharacter(row, y, Symbols.HEART);
        }

        // if a partial heart, draw using a different color
        if (partial > 0) {
            game.textGraphics()
                    .setForegroundColor(
                            color(game.settings().colors().foreground().healthPartial(), ANSI.RED_BRIGHT));
            game.textGraphics().setCharacter(startX + full, y, Symbols.HEART);
        }

        // draw blanks to "erase" any lost hearts
        for (int row = startX + full + (partial > 0 ? 1 : 0),
                        max = startX + Player.MAX_HEALTH / PLAYER_HEALTH_HEART_VALUE;
                row < max;
                row++) {
            game.textGraphics().setCharacter(row, y, ' ');
        }
    }
}
