package coding101.tq;

import coding101.tq.domain.Player;
import coding101.tq.domain.Settings;
import coding101.tq.domain.TerrainMap;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

/**
 * API for the game overall.
 */
public interface Game {

    /**
     * Get the screen.
     *
     * @return the screen
     */
    Screen screen();

    /**
     * Get the text graphics for the game screen.
     *
     * @return the text graphics
     */
    TextGraphics textGraphics();

    /**
     * Get the game settings.
     *
     * @return the settings
     */
    Settings settings();

    /**
     * Get the current player.
     *
     * @return the player
     */
    Player player();

    /**
     * Get the active map.
     *
     * @return the active map
     */
    TerrainMap map();
}
