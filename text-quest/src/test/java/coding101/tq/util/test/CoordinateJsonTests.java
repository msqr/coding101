package coding101.tq.util.test;

import static org.assertj.core.api.BDDAssertions.from;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

import coding101.tq.domain.Coordinate;
import coding101.tq.util.CoordinateJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link CoordinateJson} class.
 */
public class CoordinateJsonTests {

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(CoordinateJson.createCoordinateModule());
        this.mapper = mapper;
    }

    public static final class TestObj {
        private Coordinate coord;

        public Coordinate getCoord() {
            return coord;
        }

        public void setCoord(Coordinate coord) {
            this.coord = coord;
        }
    }

    @Test
    public void serialize() throws IOException {
        // GIVEN
        Coordinate coord = new Coordinate(1, 2);

        // WHEN
        String json = mapper.writeValueAsString(coord);

        // THEN
        then(json).as("JSON string generated").isEqualTo("\"1,2\"");
    }

    @Test
    public void serialize_field() throws IOException {
        // GIVEN
        Coordinate coord = new Coordinate(1, 2);
        TestObj obj = new TestObj();
        obj.setCoord(coord);

        // WHEN
        String json = mapper.writeValueAsString(obj);

        // THEN
        then(json).as("JSON string generated").isEqualTo("{\"coord\":\"1,2\"}");
    }

    @Test
    public void serialize_key() throws IOException {
        // GIVEN
        Coordinate coord = new Coordinate(1, 2);
        Map<Coordinate, Boolean> map = new LinkedHashMap<>(2);
        map.put(coord, Boolean.TRUE);

        // WHEN
        String json = mapper.writeValueAsString(map);

        // THEN
        then(json).as("JSON string generated").isEqualTo("{\"1,2\":true}");
    }

    @Test
    public void deserialize() throws IOException {
        // GIVEN
        String json = "\"1,2\"";

        // WHEN
        Coordinate coord = mapper.readValue(json, Coordinate.class);

        // THEN
        // @formatter:off
        then(coord)
                .as("JSON string parsed")
                .isNotNull()
                .as("X parsed")
                .returns(1, from(Coordinate::x))
                .as("X parsed")
                .returns(2, from(Coordinate::y));
        // @formatter:on
    }

    @Test
    public void deserialize_field() throws IOException {
        // GIVEN
        String json = "{\"coord\":\"1,2\"}";

        // WHEN
        TestObj obj = mapper.readValue(json, TestObj.class);

        // THEN
        // @formatter:off
        then(obj)
                .as("JSON object parsed")
                .isNotNull()
                .extracting(TestObj::getCoord, type(Coordinate.class))
                .as("X parsed")
                .returns(1, from(Coordinate::x))
                .as("X parsed")
                .returns(2, from(Coordinate::y));
        // @formatter:on
    }

    private static final TypeReference<LinkedHashMap<Coordinate, Boolean>> COORD_BOOL_MAP_TYPE =
            new CoordinateBooleanMapTypeReference();

    private static final class CoordinateBooleanMapTypeReference
            extends TypeReference<LinkedHashMap<Coordinate, Boolean>> {

        public CoordinateBooleanMapTypeReference() {
            super();
        }
    }

    @Test
    public void deserialize_key() throws IOException {
        // GIVEN
        String json = "{\"1,2\":true}";

        // WHEN
        Map<Coordinate, Boolean> map = mapper.readValue(json, COORD_BOOL_MAP_TYPE);

        // THEN
        // @formatter:off
        then(map).as("JSON map parsed").isNotNull().as("Key parsed").containsEntry(new Coordinate(1, 2), Boolean.TRUE);
        // @formatter:on
    }
}
