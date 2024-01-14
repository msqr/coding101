package coding101.tq.example;

import static org.assertj.core.api.BDDAssertions.from;
import static org.assertj.core.api.BDDAssertions.then;

import coding101.tq.example.LinkedLists.LinkedList;
import coding101.tq.example.LinkedLists.LinkedList.LinkedListElement;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link LinkedList} class.
 */
public class LinkedListTests {

    private static final class TestList extends LinkedList<String> {}

    @Test
    public void add_first() {
        // GIVEN
        var list = new TestList();

        // WHEN
        final String item = "a";
        list.add(item);

        // THEN
        then(list.head).as("head is added item").returns(item, from(LinkedListElement::item));
    }

    private void thenListContains(TestList list, String... items) {
        var element = list.head;
        for (int i = 0; i < items.length; i++) {
            then(element).as("item %d".formatted(i)).returns(items[i], from(LinkedListElement::item));
            element = element.next();
        }
        then(element).as("tail element has no next").isNull();
    }

    @Test
    public void add_second() {
        // GIVEN
        var list = new TestList();

        // WHEN
        final String[] items = new String[] {"a", "b"};
        for (String item : items) {
            list.add(item);
        }

        // THEN
        thenListContains(list, items);
    }

    @Test
    public void add_third() {
        // GIVEN
        var list = new TestList();

        // WHEN
        final String[] items = new String[] {"a", "b", "c"};
        for (String item : items) {
            list.add(item);
        }

        // THEN
        thenListContains(list, items);
    }

    private TestList threeItemList() {
        final String[] items = new String[] {"a", "b", "c"};
        var list = new TestList();
        for (String item : items) {
            list.add(item);
        }
        return list;
    }

    @Test
    public void remove_head() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        boolean removed = list.remove("a");

        // THEN
        then(removed).as("head item removed").isTrue();
        thenListContains(list, "b", "c");
    }

    @Test
    public void remove_middle() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        boolean removed = list.remove("b");

        // THEN
        then(removed).as("middle item removed").isTrue();
        thenListContains(list, "a", "c");
    }

    @Test
    public void remove_tail() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        boolean removed = list.remove("c");

        // THEN
        then(removed).as("tail item removed").isTrue();
        thenListContains(list, "a", "b");
    }

    @Test
    public void remove_last() {
        // GIVEN
        var list = new TestList();
        list.add("a");

        // WHEN
        boolean removed = list.remove("a");

        // THEN
        then(removed).as("last item removed").isTrue();
        then(list.head).as("list is now empty").isNull();
    }

    @Test
    public void remove_notFound() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        boolean removed = list.remove("d");

        // THEN
        then(removed).as("no item removed").isFalse();
        thenListContains(list, "a", "b", "c");
    }

    @Test
    public void forEach_empty() {
        // GIVEN
        var list = new TestList();

        // WHEN
        var consumed = new ArrayList<>();
        list.forEach(item -> consumed.add(item));

        // THEN
        then(consumed).as("no items iterated").hasSize(0);
    }

    @Test
    public void forEach_singleton() {
        // GIVEN
        var list = new TestList();
        list.add("a");

        // WHEN
        var consumed = new ArrayList<>();
        list.forEach(item -> consumed.add(item));

        // THEN
        then(consumed).as("one item iterated").containsExactly("a");
    }

    @Test
    public void forEach_multiple() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        var consumed = new ArrayList<>();
        list.forEach(item -> consumed.add(item));

        // THEN
        then(consumed).as("all items iterated").containsExactly("a", "b", "c");
    }

    @Test
    public void size_empty() {
        // GIVEN
        var list = new TestList();

        // WHEN
        var size = list.size();

        // THEN
        then(size).as("empty list size").isEqualTo(0);
    }

    @Test
    public void size_singleton() {
        // GIVEN
        var list = new TestList();
        list.add("a");

        // WHEN
        var size = list.size();

        // THEN
        then(size).as("singleton list size").isEqualTo(1);
    }

    @Test
    public void size_multiple() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        var size = list.size();

        // THEN
        then(size).as("list size is number of items").isEqualTo(3);
    }

    @Test
    public void insertAfter_head() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        list.insertAfter("d", "a");

        // THEN
        thenListContains(list, "a", "d", "b", "c");
    }

    @Test
    public void insertAfter_middle() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        list.insertAfter("d", "b");

        // THEN
        thenListContains(list, "a", "b", "d", "c");
    }

    @Test
    public void insertAfter_tail() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        list.insertAfter("d", "c");

        // THEN
        thenListContains(list, "a", "b", "c", "d");
    }

    @Test
    public void insertAfter_missing() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        list.insertAfter("d", "NO");

        // THEN
        thenListContains(list, "a", "b", "c", "d");
    }

    @Test
    public void insertBefore_head() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        list.insertBefore("d", "a");

        // THEN
        thenListContains(list, "d", "a", "b", "c");
    }

    @Test
    public void insertBefore_middle() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        list.insertBefore("d", "b");

        // THEN
        thenListContains(list, "a", "d", "b", "c");
    }

    @Test
    public void insertBefore_tail() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        list.insertBefore("d", "c");

        // THEN
        thenListContains(list, "a", "b", "d", "c");
    }

    @Test
    public void insertBefore_missing() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        list.insertBefore("d", "NO");

        // THEN
        thenListContains(list, "d", "a", "b", "c");
    }

    @Test
    public void reverse_empty() {
        // GIVEN
        var list = new TestList();

        // WHEN
        list.reverse();

        // THEN
        then(list.head).as("empty list").isNull();
    }

    @Test
    public void reverse_singleton() {
        // GIVEN
        var list = new TestList();
        list.add("a");

        // WHEN
        list.reverse();

        // THEN
        thenListContains(list, "a");
    }

    @Test
    public void reverse_double() {
        // GIVEN
        var list = new TestList();
        list.add("a");
        list.add("b");

        // WHEN
        list.reverse();

        // THEN
        thenListContains(list, "b", "a");
    }

    @Test
    public void reverse_multiple() {
        // GIVEN
        var list = threeItemList();

        // WHEN
        list.reverse();

        // THEN
        thenListContains(list, "c", "b", "a");
    }
}
