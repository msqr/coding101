package coding101.tq;

import static java.util.Objects.requireNonNull;

/**
 * The UI info pane.
 */
public class InfoPane implements Pane {

    private final Game game;
    private final int width;
    private final int bottomOffset;

    /**
     * Constructor.
     *
     * @param game         the game
     * @param rightOffset  the width
     * @param bottomOffset the height to offset the bottom from the screen
     *                     dimensions
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public InfoPane(Game game, int width, int bottomOffset) {
        super();
        this.game = requireNonNull(game);
        this.width = width;
        this.bottomOffset = bottomOffset;
    }

    @Override
    public int top() {
        return 3;
    }

    @Override
    public int left() {
        return game.screen().getTerminalSize().getColumns() - width - 1;
    }

    @Override
    public int bottom() {
        return game.screen().getTerminalSize().getRows() - bottomOffset;
    }

    @Override
    public int right() {
        return game.screen().getTerminalSize().getColumns() - 1;
    }

    @Override
    public void draw() {
        // TODO Auto-generated method stub

    }
}
