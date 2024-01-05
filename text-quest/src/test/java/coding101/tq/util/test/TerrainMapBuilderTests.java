package coding101.tq.util.test;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import coding101.tq.domain.TerrainMap;
import coding101.tq.util.TerrainMapBuilder;

/**
 * Test cases for the {@link TerrainMapBuilder} class.
 */
public class TerrainMapBuilderTests {

	@Test
	public void parseClassPathResources() {
		// WHEN
		TerrainMap tm = TerrainMapBuilder.parseResources("coding101/tq/util/test/map01").build();
		String result = tm.render();

		// THEN
		String expectedMap = """
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
				""".trim();

		then(result).as("Parsed quadrant tiles into complete map").isEqualTo(expectedMap);
	}

	@Test
	public void parseClassPathResources_withQuadrantHole() {
		// WHEN
		TerrainMap tm = TerrainMapBuilder.parseResources("coding101/tq/util/test/map02").build();
		String result = tm.render();

		// THEN
		String expectedMap = """
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
				""".trim();

		then(result).as("Parsed quadrant tiles with missing tiles into complete map").isEqualTo(expectedMap);
	}

}
