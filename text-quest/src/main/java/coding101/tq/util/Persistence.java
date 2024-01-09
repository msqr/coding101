package coding101.tq.util;

import coding101.tq.domain.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * Game state persistence.
 */
public class Persistence {

    private final ObjectMapper mapper;

    /**
     * Constructor.
     *
     * @param mapper the mapper to use for persistence to JSON files
     */
    public Persistence(ObjectMapper mapper) {
        super();
        this.mapper = Objects.requireNonNull(mapper);
    }

    /**
     * Save a player.
     *
     * @param player the player to save
     * @param path   the path to save the player to
     * @throws IOException if any IO error occurs
     */
    public void savePlayer(Player player, Path path) throws IOException {
        // write to temp file, then move, to prevent broken data on failed save
        Path tmp = Files.createTempFile("save-", ".tqsave");
        try (OutputStream out = Files.newOutputStream(tmp)) {
            savePlayer(player, out);
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    /**
     * Save a player.
     *
     * @param player the player to save
     * @param out    the destination to save to
     * @throws IOException if any IO error occurs
     */
    public void savePlayer(Player player, OutputStream out) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(out, player);
    }

    /**
     * Load a player.
     *
     * @param path the path to load from
     * @return the player
     * @throws IOException if any IO error occurs
     */
    public Player loadPlayer(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            return loadPlayer(in);
        }
    }

    /**
     * Load a player.
     *
     * @param in the stream to load from
     * @return the player
     * @throws IOException if any IO error occurs
     */
    public Player loadPlayer(InputStream in) throws IOException {
        return mapper.readValue(in, Player.class);
    }
}
