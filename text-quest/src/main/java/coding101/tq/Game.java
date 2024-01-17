package coding101.tq;

import coding101.tq.domain.Player;
import coding101.tq.domain.PlayerItems;
import coding101.tq.domain.Settings;
import coding101.tq.domain.TerrainMap;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * API for the game overall.
 */
public interface Game {

    /**
     * Get the game resource bundle.
     *
     * @return the resource bundle
     */
    ResourceBundle bundle();

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
     * Get all possible game items.
     *
     * @return the game items
     */
    default PlayerItems items() {
        return settings().items();
    }

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

    /**
     * Read a yes/no style response from the player.
     *
     * @return {@litera true} if the player confirmed in the affirmative
     * @throws IOException if an IO error occurs
     */
    boolean readYesNo() throws IOException;

    /**
     * Read a character response from the player.
     *
     * @return the character
     * @throws IOException if an IO error occurs
     */
    char readCharacter() throws IOException;

    /**
     * Read an integer response from the player.
     *
     * This method will read number characters until the Enter key is pressed. If
     * Enter is pressed before typing any number character, {@code null} is
     * returned.
     *
     * @param x the column to display the typed numbers
     * @param y the row to display the typed numbers
     * @return the integer, or {@code null} if no number entered
     * @throws IOException if an IO error occurs
     */
    Integer readInteger(int x, int y) throws IOException;
}
