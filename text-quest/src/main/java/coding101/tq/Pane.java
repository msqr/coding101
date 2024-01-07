package coding101.tq;

/**
 * A rectangular portion of the main game screen, excluding any borders.
 */
public interface Pane {

    /**
     * Get the top position.
     *
     * @return the top row
     */
    int top();

    /**
     * Get the left position.
     *
     * @return the left column
     */
    int left();

    /**
     * Get the bottom position.
     *
     * @return the bottom row
     */
    int bottom();

    /**
     * Get the right position.
     *
     * @return the right column
     */
    int right();

    /**
     * Get the width.
     *
     * @return the width
     */
    default int width() {
        return right() - left() + 1;
    }

    /**
     * Get the height.
     *
     * @return the height
     */
    default int height() {
        return bottom() - top() + 1;
    }

    /**
     * Draw the pane.
     */
    void draw();
}
