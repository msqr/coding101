package coding101.tq.util.test;

import static org.assertj.core.api.BDDAssertions.from;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

import coding101.tq.util.BitSetJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link BitSetJson} class.
 */
public class BitSetJsonTests {

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(BitSetJson.createBitSetModule());
        this.mapper = mapper;
    }

    public static final class TestObj {
        private BitSet bits;

        public BitSet getBits() {
            return bits;
        }

        public void setBits(BitSet bits) {
            this.bits = bits;
        }
    }

    @Test
    public void serialize() throws IOException {
        // GIVEN
        BitSet bits = new BitSet();
        bits.set(3);
        bits.set(1);

        // WHEN
        String json = mapper.writeValueAsString(bits);

        // THEN
        then(json).as("JSON string generated").isEqualTo("[10]");
    }

    @Test
    public void serialize_field() throws IOException {
        // GIVEN
        BitSet bits = new BitSet();
        bits.set(3);
        bits.set(1);
        TestObj obj = new TestObj();
        obj.setBits(bits);

        // WHEN
        String json = mapper.writeValueAsString(obj);

        // THEN
        then(json).as("JSON string generated").isEqualTo("{\"bits\":[10]}");
    }

    @Test
    public void deserialize() throws IOException {
        // GIVEN
        String json = "[10]";

        // WHEN
        BitSet bits = mapper.readValue(json, BitSet.class);

        // THEN
        // @formatter:off
        then(bits)
                .as("JSON string parsed")
                .isNotNull()
                .as("2 bits set")
                .returns(2, from(BitSet::cardinality))
                .as("Bit 3 set")
                .returns(true, from(b -> b.get(3)))
                .as("Bit 1 set")
                .returns(true, from(b -> b.get(1)));
        // @formatter:on
    }

    @Test
    public void deserialize_field() throws IOException {
        // GIVEN
        String json = "{\"bits\":[10]}";

        // WHEN
        TestObj obj = mapper.readValue(json, TestObj.class);

        // THEN
        // @formatter:off
        then(obj)
                .as("JSON object parsed")
                .isNotNull()
                .extracting(TestObj::getBits, type(BitSet.class))
                .as("2 bits set")
                .returns(2, from(BitSet::cardinality))
                .as("Bit 3 set")
                .returns(true, from(b -> b.get(3)))
                .as("Bit 1 set")
                .returns(true, from(b -> b.get(1)));
        // @formatter:on
    }

    @Test
    public void deserialize_list() throws IOException {
        // GIVEN
        String json = "[[10],[10,1]]";

        // WHEN
        List<BitSet> list = mapper.readValue(json, BitSetJson.BITSET_LIST_TYPE);

        // THEN
        BitSet expected1 = new BitSet();
        expected1.set(3);
        expected1.set(1);

        BitSet expected2 = (BitSet) expected1.clone();
        expected2.set(64);

        // @formatter:off
        then(list).as("JSON list parsed").isNotNull().as("Items parsed").contains(expected1, expected2);
        // @formatter:on
    }

    public static final class TestListObj {
        private List<BitSet> bits;

        public List<BitSet> getBits() {
            return bits;
        }

        public void setBits(List<BitSet> bits) {
            this.bits = bits;
        }
    }

    @Test
    public void deserialize_listField() throws IOException {
        // GIVEN
        String json = "{\"bits\":[[10],[10,1]]}";

        // WHEN
        TestListObj obj = mapper.readValue(json, TestListObj.class);

        // THEN
        BitSet expected1 = new BitSet();
        expected1.set(3);
        expected1.set(1);

        BitSet expected2 = (BitSet) expected1.clone();
        expected2.set(64);

        // @formatter:off
        then(obj)
                .as("JSON object parsed")
                .isNotNull()
                .extracting(TestListObj::getBits, list(BitSet.class))
                .as("2 bits set")
                .as("Items parsed")
                .contains(expected1, expected2);
        // @formatter:on
    }
}
