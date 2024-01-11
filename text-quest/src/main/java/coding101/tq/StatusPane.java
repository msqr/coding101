package coding101.tq;

import static coding101.tq.domain.ColorPalette.color;
import static java.util.Objects.requireNonNull;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor.ANSI;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The UI status pane.
 */
public class StatusPane implements Pane {

    private final Game game;
    private final int rightOffset;
    private final int height;
    private final Timer timer;
    private String message;
    private TimerTask statusTask;

    /**
     * Constructor.
     *
     * @param game        the game
     * @param rightOffset the width to offset the right from the screen dimensions
     * @param height      the height
     * @param timer       a timer for status background tasks
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public StatusPane(Game game, int rightOffset, int height, Timer timer) {
        super();
        this.game = requireNonNull(game);
        this.rightOffset = rightOffset;
        this.height = height;
        this.timer = requireNonNull(timer);
    }

    @Override
    public int top() {
        return game.screen().getTerminalSize().getRows() - height - 1;
    }

    @Override
    public int left() {
        return 1;
    }

    @Override
    public int bottom() {
        return game.screen().getTerminalSize().getRows() - 1;
    }

    @Override
    public int right() {
        return game.screen().getTerminalSize().getColumns() - rightOffset;
    }

    @Override
    public int height() {
        return height;
    }

    /**
     * Get the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void draw() {
        draw(message);
    }

    private void draw(String message) {
        final int paneTop = top();
        final int paneLeft = left();
        final int paneWidth = width();
        game.textGraphics()
                .setForegroundColor(color(game.settings().colors().foreground().uiText(), ANSI.BLACK));
        game.textGraphics()
                .setBackgroundColor(color(game.settings().colors().background().uiText(), ANSI.WHITE_BRIGHT));

        final String msg = (message != null ? message.substring(0, Math.min(message.length(), width())) : null);
        final TerminalPosition topLeft = new TerminalPosition(paneLeft, paneTop);
        game.textGraphics().fillRectangle(topLeft, new TerminalSize(paneWidth, 1), ' ');
        if (msg != null && !msg.isBlank()) {
            game.textGraphics().putString(topLeft, msg);
        }
    }

    /**
     * Draw a message, optionally clearing it after a timeout.
     *
     * @param message        the message to display
     * @param clearAfterSecs if greater than {@code 0} then clear the message after
     *                       this many seconds
     */
    public void drawMessage(String message, int clearAfterSecs) {
        final TimerTask statusTask = this.statusTask;
        if (statusTask != null) {
            statusTask.cancel();
        }
        setMessage(message);
        if (message == null) {
            draw();
            return;
        }
        int start = 0;
        while (true) {
            int end = Math.min(start + width(), message.length());
            if (end < message.length()) {
                while (end > start && message.charAt(end - 1) != ' ') {
                    end--;
                }
                if (end == start) {
                    // no space found... oh well
                    end = Math.min(start + width(), message.length());
                }
            }
            String msg = message.substring(start, end);
            if (end < message.length()) {
                msg = message.substring(start, end - 1);
                msg += Symbols.TRIANGLE_DOWN_POINTING_MEDIUM_BLACK;
            }
            draw(msg);
            start = end;
            if (end >= message.length()) {
                break;
            }
            try {
                game.screen().refresh();
                game.readYesNo();
            } catch (IOException e) {
                throw new RuntimeException("Error drawing message: %s".formatted(e.getMessage()), e);
            }
        }
        if (clearAfterSecs > 0) {
            TimerTask tt = new TimerTask() {

                @Override
                public void run() {
                    setMessage(null);
                    draw();
                    try {
                        game.screen().refresh();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            };
            timer.schedule(tt, clearAfterSecs * 1000L);
            this.statusTask = tt;
        }
    }
}
