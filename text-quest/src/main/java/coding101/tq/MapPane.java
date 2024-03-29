package coding101.tq;

import static coding101.tq.domain.ColorPalette.color;
import static java.util.Objects.requireNonNull;

import coding101.tq.domain.Player;
import coding101.tq.domain.TerrainMap;
import coding101.tq.domain.TerrainType;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.ANSI;

/**
 * The UI map pane.
 */
public final class MapPane implements Pane {

    private final Game game;
    private final int rightOffset;
    private final int bottomOffset;

    /**
     * Constructor.
     *
     * @param game         the game
     * @param rightOffset  the width to offset the right from the screen dimensions
     * @param bottomOffset the height to offset the bottom from the screen
     *                     dimensions
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public MapPane(Game game, int rightOffset, int bottomOffset) {
        super();
        this.game = requireNonNull(game);
        this.rightOffset = rightOffset;
        this.bottomOffset = bottomOffset;
    }

    @Override
    public int top() {
        return 1;
    }

    @Override
    public int left() {
        return 1;
    }

    @Override
    public int bottom() {
        return game.screen().getTerminalSize().getRows() - bottomOffset;
    }

    @Override
    public int right() {
        return game.screen().getTerminalSize().getColumns() - rightOffset;
    }

    @Override
    public void draw() {
        drawMapForPoint(game.map(), game.player().getX(), game.player().getY());
        drawPlayer(game.player());
    }

    /**
     * Specialized routine to move a player and re-draw the map.
     *
     * @param newX the new X position
     * @param newY the new Y position
     * @return {@code true} if visiting the coordinate for the first time
     */
    public boolean movePlayer(int newX, int newY) {
        // draw old position terrain
        final int paneWidth = width();
        final int paneHeight = height();
        final int paneTop = top();
        final int paneLeft = left();
        final int startX = (game.player().getX() / paneWidth) * paneWidth;
        final int startY = (game.player().getY() / paneHeight) * paneHeight;

        final int newStartX = (newX / paneWidth) * paneWidth;
        final int newStartY = (newY / paneHeight) * paneHeight;

        boolean result = game.player().moveTo(game.map(), newX, newY);

        if (newStartX != startX || newStartY != startY) {
            // redraw entire map
            drawMapForPoint(game.map(), newX, newY);
        } else {
            game.map().walkSurrounding(newX, newY, (col, row, t) -> {
                drawTerrain(col, row, col - startX + paneLeft, row - startY + paneTop, t);
            });
        }
        drawPlayer(game.player());
        return result;
    }

    private void drawMapForPoint(TerrainMap map, int x, int y) {
        final int paneWidth = width();
        final int paneHeight = height();
        final int paneTop = top();
        final int paneLeft = left();
        final int startX = (x / paneWidth) * paneWidth;
        final int startY = (y / paneHeight) * paneHeight;
        map.walk(startX, startY, paneWidth, paneHeight, (col, row, t) -> {
            drawTerrain(col, row, col - startX + paneLeft, row - startY + paneTop, t);
        });
    }

    private void drawTerrain(int x, int y, int screenCol, int screenRow, TerrainType t) {
        if (screenCol < left() || screenCol > right() || screenRow < top() || screenRow > bottom()) {
            return;
        }
        // show the terrain if the revealMap config active, or if visited "near"
        final boolean visited =
                game.player().config().revealMap() || game.player().hasVisitedNear(game.map(), x, y);
        char c = t != null ? t.getKey() : TerrainType.EMPTY;
        TextColor bg = game.settings().colors().background().terrain(t, ANSI.BLACK);
        TextColor fg = game.settings().colors().foreground().terrain(t, ANSI.WHITE_BRIGHT);
        if (!visited) {
            bg = ANSI.BLACK;
            c = TerrainType.EMPTY;
        } else if (c == TerrainType.WALL_CORNER || c == TerrainType.WALL_HORIZONTAL || c == TerrainType.WALL_VERTICAL) {
            c = Symbols.BLOCK_SOLID;
        } else if (c == TerrainType.CHEST && game.player().hasInteracted(game.map(), x, y)) {
            // this chest has been opened; draw with a different color
            fg = color(game.settings().colors().foreground().cave(), ANSI.WHITE_BRIGHT);
        } else if (c == TerrainType.SHIP && !game.player().vehicleLocatedAt(game.map(), x, y)) {
            // ship no longer at this spot, draw water instead
            c = TerrainType.WATER;
            bg = game.settings().colors().background().terrain(TerrainType.Water, ANSI.BLACK);
            fg = game.settings().colors().foreground().terrain(TerrainType.Water, ANSI.WHITE_BRIGHT);
        } else if (c == TerrainType.WATER && game.player().vehicleLocatedAt(game.map(), x, y)) {
            // ship is at this spot, draw ship instead
            c = TerrainType.SHIP;
            bg = game.settings().colors().background().terrain(TerrainType.Ship, ANSI.BLACK);
            fg = game.settings().colors().foreground().terrain(TerrainType.Ship, ANSI.WHITE_BRIGHT);
        }
        game.textGraphics().setBackgroundColor(bg);
        game.textGraphics().setForegroundColor(fg);
        game.textGraphics().setCharacter(screenCol, screenRow, c);
    }

    private void drawPlayer(Player player) {
        final int paneWidth = width();
        final int paneHeight = height();
        final int paneTop = top();
        final int paneLeft = left();
        final int startX = (player.getX() / paneWidth) * paneWidth;
        final int startY = (player.getY() / paneHeight) * paneHeight;
        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().player(), ANSI.WHITE_BRIGHT));
        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().player(), ANSI.MAGENTA_BRIGHT));
        game.textGraphics().setCharacter(player.getX() - startX + paneLeft, player.getY() - startY + paneTop, '@');
    }
}
