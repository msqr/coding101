package coding101.tq.util;

import coding101.tq.domain.Coordinate;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.TreeMap;

/**
 * JSON handling for the {@link Coordinate} class.
 */
public class CoordinateJson {

    /** A default key serializer instance. */
    public static final JsonSerializer<Coordinate> KEY_SERIALIZER = new CoordinateKeySerializer();

    /** A default value serializer instance. */
    public static final JsonSerializer<Coordinate> VALUE_SERIALIZER = new CoordinateValueSerializer();

    /** A default key deserializer instance. */
    public static final KeyDeserializer KEY_DESERIALIZER = new CoordinateKeyDeserializer();

    /** A default value deserializer instance. */
    public static final JsonDeserializer<Coordinate> VALUE_DESERIALIZER = new CoordinateValueDeserializer();

    /** A type reference for a sorted map of {@link Coordinate} keys and values. */
    public static final TypeReference<TreeMap<Coordinate, Coordinate>> COORDINATE_SORTED_MAP_TYPE =
            new CoordinateSortedMapTypeReference();

    private static final class CoordinateSortedMapTypeReference extends TypeReference<TreeMap<Coordinate, Coordinate>> {

        public CoordinateSortedMapTypeReference() {
            super();
        }
    }

    /**
     * Get a JSON module for handling Coordinate values.
     *
     * @return the module
     */
    public static SimpleModule createCoordinateModule() {
        SimpleModule module = new SimpleModule("Coordinates");

        module.addSerializer(Coordinate.class, CoordinateJson.VALUE_SERIALIZER);
        module.addDeserializer(Coordinate.class, CoordinateJson.VALUE_DESERIALIZER);
        module.addKeySerializer(Coordinate.class, CoordinateJson.KEY_SERIALIZER);
        module.addKeyDeserializer(Coordinate.class, CoordinateJson.KEY_DESERIALIZER);

        return module;
    }

    /**
     * Serialize a {@link Coordinate} as a string {@code x,y} field name value.
     */
    public static final class CoordinateKeySerializer extends StdSerializer<Coordinate> {

        private static final long serialVersionUID = 4602411097819717905L;

        /**
         * Constructor.
         */
        public CoordinateKeySerializer() {
            super(Coordinate.class);
        }

        @Override
        public void serialize(Coordinate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeFieldName(value.toKey());
        }
    }

    /**
     * Serialize a {@link Coordinate} as a string {@code x,y} string value.
     */
    public static final class CoordinateValueSerializer extends StdSerializer<Coordinate> {

        private static final long serialVersionUID = -7179648173055902431L;

        /**
         * Constructor.
         */
        public CoordinateValueSerializer() {
            super(Coordinate.class);
        }

        @Override
        public void serialize(Coordinate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value.toKey());
            }
        }
    }

    /**
     * Deserialize a {@link Coordinate} from a string {@code x,y} field name value.
     */
    public static final class CoordinateKeyDeserializer extends KeyDeserializer {

        /**
         * Constructor.
         */
        public CoordinateKeyDeserializer() {
            super();
        }

        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
            return Coordinate.forKey(key);
        }
    }

    /**
     * Deserialize a {@link Coordinate} from a string {@code x,y} field name value.
     */
    public static final class CoordinateValueDeserializer extends StdDeserializer<Coordinate> {

        private static final long serialVersionUID = 7988008912965006670L;

        /**
         * Constructor.
         */
        public CoordinateValueDeserializer() {
            super(Coordinate.class);
        }

        @Override
        public Coordinate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            String s = p.getText();
            return Coordinate.forKey(s);
        }
    }
}
