package coding101.tq.util.test;

import static org.assertj.core.api.BDDAssertions.then;

import coding101.tq.domain.TerrainMap;
import coding101.tq.util.TerrainMapBuilder;
import coding101.tq.util.TerrainMapBuilder.Tile;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link TerrainMapBuilder} class.
 */
public class TerrainMapBuilderTests {

    @Test
    public void parseClassPathResources() {
        // WHEN
        TerrainMap tm =
                TerrainMapBuilder.parseResources("coding101/tq/util/test/map01").build("");
        String result = tm.render();

        // THEN
        String expectedMap =
                """
				AAAAA~~~~~
				~~~~.=====
				^^^AA^^^^^
				^^AAA.....
				===~AAAAAA
				AAOAA    .
				===~......
				^^*AA    A
				~~~~~=====
				=====AAAAA
				"""
                        .trim();

        then(result).as("Parsed quadrant tiles into complete map").isEqualTo(expectedMap);
    }

    @Test
    public void parseClassPathResources_withQuadrantHole() {
        // WHEN
        TerrainMap tm =
                TerrainMapBuilder.parseResources("coding101/tq/util/test/map02").build("");
        String result = tm.render();

        // THEN
        String expectedMap =
                """
				AAAAA~~~~~
				~~~~.=====
				^^^AA^^^^^
				^^AAA.....
				===~AAAAAA
				         .
				     .....
				         A
				     =====
				     AAAAA
				"""
                        .trim();

        then(result)
                .as("Parsed quadrant tiles with missing tiles into complete map")
                .isEqualTo(expectedMap);
    }

    @Test
    public void parseTileMetadata() throws IOException {
        // GIVEN
        try (InputStream in = getClass().getResourceAsStream("test-map-with-metadata.tqmap")) {

            // WHEN
            Tile tile = TerrainMapBuilder.parseTileResource("0,0.tqmap", in);

            // THEN
            Map<String, String> expected = new HashMap<String, String>(4);
            expected.put("foo", "bar");
            expected.put("bim", "bam");
            expected.put("loud", "ONE");
            expected.put("dash-ok", "1");
            then(tile.getMetadata()).as("Metadata parsed").containsExactlyInAnyOrderEntriesOf(expected);
        }
    }

    @Test
    public void parseMergedMetadata() {
        // WHEN
        TerrainMap tm =
                TerrainMapBuilder.parseResources("coding101/tq/util/test/map02").build("");

        // THEN
        Map<String, String> expected = new HashMap<String, String>(4);
        expected.put("start", "3,3");
        expected.put("a", "A");
        expected.put("b", "B");
        expected.put("c", "C");

        then(tm.metadata()).as("Metadata parsed and merged").containsExactlyInAnyOrderEntriesOf(expected);
    }
}
