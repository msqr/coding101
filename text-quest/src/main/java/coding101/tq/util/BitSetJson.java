package coding101.tq.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * JSON handling for the {@link BitSet} class.
 */
public class BitSetJson {

    /** A default value serializer instance. */
    public static final JsonSerializer<BitSet> VALUE_SERIALIZER = new BitSetValueSerializer();

    /** A default value deserializer instance. */
    public static final JsonDeserializer<BitSet> VALUE_DESERIALIZER = new BitSetValueDeserializer();

    /** A type reference for a list of {@link BitSet} values. */
    public static final TypeReference<List<BitSet>> BITSET_LIST_TYPE = new BitSetListTypeReference();

    private static final class BitSetListTypeReference extends TypeReference<List<BitSet>> {

        public BitSetListTypeReference() {
            super();
        }
    }

    /**
     * Get a JSON module for handling BitSet values.
     *
     * @return the module
     */
    public static SimpleModule createBitSetModule() {
        SimpleModule module = new SimpleModule("BitSets");

        module.addSerializer(BitSet.class, BitSetJson.VALUE_SERIALIZER);
        module.addDeserializer(BitSet.class, BitSetJson.VALUE_DESERIALIZER);

        return module;
    }

    /**
     * Serialize a {@link BitSet} as a string {@code x,y} string value.
     */
    public static final class BitSetValueSerializer extends StdSerializer<BitSet> {

        private static final long serialVersionUID = -6510311759744722960L;

        /**
         * Constructor.
         */
        public BitSetValueSerializer() {
            super(BitSet.class);
        }

        @Override
        public void serialize(BitSet value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            long[] data = value != null ? value.toLongArray() : null;
            if (data == null) {
                gen.writeNull();
            } else {
                gen.writeArray(data, 0, data.length);
            }
        }
    }

    /**
     * Deserialize a {@link BitSet} from a string {@code x,y} field name value.
     */
    public static final class BitSetValueDeserializer extends StdDeserializer<BitSet> {

        private static final long serialVersionUID = 4341691897258912108L;

        /**
         * Constructor.
         */
        public BitSetValueDeserializer() {
            super(BitSet.class);
        }

        @Override
        public BitSet deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {

            if (p.currentToken() == JsonToken.START_ARRAY) {
                List<Long> data = new ArrayList<>(32);
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                        data.add(p.getLongValue());
                    }
                }
                return BitSet.valueOf(data.stream().mapToLong(Long::longValue).toArray());
            }
            return ctxt.reportInputMismatch(this, "Expected an array.");
        }
    }
}
