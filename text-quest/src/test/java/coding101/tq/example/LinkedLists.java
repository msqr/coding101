package coding101.tq.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Demonstration of a linked list.
 */
public class LinkedLists {

    /**
     * A linked list of arbitrary objects.
     *
     * @param <T> the element item type
     */
    public static class LinkedList<T> {

        /**
         * An element in a linked list.
         *
         * @param <T> the item type
         */
        public static class LinkedListElement<T> {

            private final T item;
            private LinkedListElement<T> next;

            /**
             * Constructor.
             *
             * @param item the item
             */
            public LinkedListElement(T item) {
                super();
                this.item = item;
            }

            /**
             * Get the item.
             *
             * @return
             */
            public T item() {
                return item;
            }

            /**
             * Get the next element in the list.
             *
             * @return the next element in the list
             */
            public LinkedListElement<T> next() {
                return next;
            }
        }

        // the head element, or start, of the list
        private LinkedListElement<T> head;

        /**
         * Constructor.
         */
        public LinkedList() {
            super();
        }

        /**
         * Get the first item in the list.
         *
         * @return the first item, or {@code null} if the list is empty
         */
        public T firstItem() {
            return (head != null ? head.item : null);
        }

        /**
         * Add an item to the end of the list.
         *
         * @param item the item to add
         */
        public void add(T item) {
            // TODO: add item to the end of the list
        }

        /**
         * Iterate over all items in the list, invoking a callback function for each
         * item.
         *
         * @param fn the callback function to call for every item in the list
         */
        public void forEach(Consumer<T> fn) {
            // TODO: iterate over all items in the list and invoke fn.accept() on each
        }

        /**
         * Get the number of elements in the list.
         *
         * @return the count of elements in the list
         */
        public int size() {
            int size = 0;

            // TODO: count and return the number of elements in the list

            return size;
        }

        /**
         * Insert an item into the list, after another item.
         *
         * @param item  the item to insert
         * @param after the item to insert after; if not found then the item will become
         *              the new end of the list
         */
        public void insertAfter(T item, T after) {
            // TODO insert item behind after, or the end of the list if not found
        }

        /**
         * Insert an item into the list, before another item.
         *
         * @param item   the item to insert
         * @param before the item to insert before; if not found then the item will
         *               become the new start of the list
         */
        public void insertBefore(T item, T before) {
            // TODO insert item in front of before, or the start of the list if not found
        }

        /**
         * Remove an item from the list.
         *
         * @param item the item to remove
         * @return {@code true} if the item was found in the list and removed,
         *         {@code false} otherwise
         */
        public boolean remove(T item) {
            boolean removed = false;

            // TODO: find and remove the given item from the list

            return removed;
        }

        /**
         * Reverse the list.
         */
        public void reverse() {
            // TODO: reverse the list
        }
    }

    /**
     * Interactive linked list demonstration.
     *
     * @param args the command line arguments (none supported)
     * @throws IOException if any IO error occurs
     */
    public static void main(String[] args) throws IOException {
        LinkedList<String> list = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            AtomicInteger count = new AtomicInteger();
            while (true) {
                count.set(0);
                System.out.print("Word list has %d items: [".formatted(list.size()));
                list.forEach(item -> System.out.println("\n  % 3d. %s".formatted(count.incrementAndGet(), item)));
                System.out.print(
                        "]\n\nAdd or remove item? Type 'a' or 'r' followed by a space then the word to add/remove: ");
                String[] input = reader.readLine().split("\\s+", 2);
                if (input.length < 1) {
                    continue;
                }
                if ("b".equalsIgnoreCase(input[0])) {
                    list.reverse();
                    System.out.println("The list is reversed.\n");
                    continue;
                }
                if (input.length < 2) {
                    continue;
                }
                if ("a".equalsIgnoreCase(input[0])) {
                    list.add(input[1]);
                    System.out.println("Item [%s] was added.".formatted(input[1]));
                } else if ("r".equalsIgnoreCase(input[0])) {
                    boolean removed = list.remove(input[1]);
                    if (removed) {
                        System.out.println("Item [%s] was removed.".formatted(input[1]));
                    } else {
                        System.out.println("Item [%s] was not found!".formatted(input[1]));
                    }
                }
                System.out.println("");
            }
        }
    }
}
