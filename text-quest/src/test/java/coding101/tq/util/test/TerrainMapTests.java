package coding101.tq.util.test;

import static org.assertj.core.api.BDDAssertions.then;

import coding101.tq.domain.TerrainMap;
import coding101.tq.domain.TerrainType;
import coding101.tq.util.TerrainMapBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link TerrainMap} class.
 */
public class TerrainMapTests {

    private static TerrainMap map01;

    @BeforeAll
    public static void setupClass() {
        map01 = TerrainMapBuilder.parseResources("coding101/tq/util/test/map01").build("");
    }

    @Test
    public void renderSlice() {
        // WHEN
        String result = map01.render(4, 1, 3, 3);

        // THEN
        String expectedMap = """
				.==
				A^^
				A..
				""".trim();

        then(result).as("Rendered slice of map").isEqualTo(expectedMap);
    }

    @Test
    public void terrainAt() {
        // WHEN
        TerrainType result = map01.terrainAt(2, 7);

        // THEN
        then(result).as("Terrain returned for valid coordinate").isEqualTo(TerrainType.Town);
    }

    @Test
    public void terrainAt_xOverflow() {
        // WHEN
        TerrainType result = map01.terrainAt(99, 7);

        // THEN
        then(result).as("Empty terrain returned for invalid coordinate").isEqualTo(TerrainType.Empty);
    }

    @Test
    public void terrainAt_yOverflow() {
        // WHEN
        TerrainType result = map01.terrainAt(2, 99);

        // THEN
        then(result).as("Empty terrain returned for invalid coordinate").isEqualTo(TerrainType.Empty);
    }
}
