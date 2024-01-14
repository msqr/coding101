# Goal 5: Working with lists

We have worked a lot with arrays, and using `for` loops to _iterate_ over array elements. Arrays are
great when you the number of items you want to store in the array does not change, because the
**length** of an array **can not be changed** once you create it. What to do, then, if you need to
deal with a changing number of items?

## First idea: create new arrays

What if you just create a new array when the number of items you need to store in it changes? Why
not, that is not a bad idea. Imagine you start with an array `src` of length `len1`, but need an
array of length `len2`. You could create a **new** array `dest` of length `len2`, and then **copy**
the needed elements of `src` into `dest`. Java even provides a specialized method to copy the
elements of one array into another:

```java
System.arraycopy(src, srcPosition, dest, destPosition, count)
```

You just have to tell it the starting position to start copying from, and the destination position
to start copying to, and the number of elements you want to copy.

### Example 1: grow an array

Let us take an example where we want to "grow" an array of 5 elements into an array of 6 elements,
so we can add a new 6th element to the **end** of the new array. Here is an example of what that
looks like:

```java
// our 5-element starting array
int[] src = new int[] {1, 2, 3, 4, 5};

// a new element we want to set as the 6th element of the new array
int elementToAdd = 6;

// our 6-element destination array
int[] dest = new int[6];

// copy all 5 src elements into dest, such that
// src[0] -> dest[0]
// ...
// src[4] -> dest[4]
System.arraycopy(src, 0, dest, 0, src.length);

// set our new 6th element
dest[5] = elementToAdd;
```

At this point, `dest` has 6 elements and looks like `[1, 2, 3, 4, 5, 6]`. Splendid!
In our prgram we can then start using `dest` in place of `src`.

### Example 2: shrink an array

Now for an example where we want to "shrink" an array of 5 elements into an array of 4
elements, discarding the 5th (last) element. Here is an example of what that looks like:

```java
// our 5-element starting array
int[] src = new int[] {1, 2, 3, 4, 5};

// our 4-element destination array
int[] dest = new int[4];

// copy the first 4 elements of src elements into dest, such that
// src[0] -> dest[0]
// src[1] -> dest[1]
// src[2] -> dest[2]
// src[3] -> dest[3]
System.arraycopy(src, 0, dest, 0, dest.length);
```

At this point, `dest` has 4 elements and looks like `[1, 2, 3, 4]`. Wonderful!

> :question: Why in this example is the last argument passed to `System.arraycopy()`
> set to `dest.length`, while in the previous example it was set to `src.length`?

## Second idea: linked list

What if we wanted to have a list of **ten zillion** items, but for whatever reason it
was not possible to have an array of **ten zillion** length. How could we manage this?
One solution is what is known as a **linked list**. A linked list is a classic computing
data structure, where the elements of the list include a reference, or **link**, to 
the **next** item in the list. You can visualize it like this:

```
+--------+     +--------+     +--------+
| Item 1 |     | Item 2 |     | Item 3 |
| Next ------->| Next ------->| Next   |
+--------+     +--------+     +--------+
```

Notice how Item 3's `Next` links to nothing: that signals the end of the list. These
squares in the diagram are the **list elements** and you can think of these very much
like little boxes that hold the actual item you want along with the directions to 
the next box. In Java a list element class might look like this:

```java
/**
 * An element in a linked list.
 * 
 * @param <T> the item type
 */
public static class LinkedListElement<T> {

    private final T item;              // the element value
    private LinkedListElement<T> next; // the next element

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
```

The funky `<T>` part is called a **generic type** or just **generic**. It is a placeholder for some
actual type. For example imagine you would like a linked list of integers. In that case, you could
create a list element of type `Integer` like this:

```java
var element = new LinkedListElement<Integer>(123);
// element.item() returns 123
```

You could also have a linked list of strings, and then the list element of type `String` would look
like this:

```java
var element = new LinkedListElement<String>("foobar");
// element.item() returns "foobar"
```

### Iterate over a linked list

You are familiar with looping over the elements of an array, often with a `for` loop like:

```java
var array = new int[] {1, 2, 3};
for (   int i = 0;         // start at first element
        i < array.length;  // stop after last element
        i = i + 1) {       // move to next element
    System.out.println("Item %d is: %d".formatted(i, array[i]));
}
```

Looping over an array, or a list, is also called **iterating** or **iteration**. Instead of saying
"loop over the array" you can say **iterate over the array**. So how can you iterate over a linked
list, that does support the array-style index operator `[i]`? We use the `Next` links, starting with
the **first element** of the list, also know as the **head** of the list. Something like this:

```java
var list = new LinkedList<Integer>(1, 2, 3);
int i = 0;
for (    var element = list.head();     // start at first element (head)
         element != null;               // stop after last element
         element = element.next()) {    // move to next element
    System.out.println("Item %d is: %d".formatted(i, element.item());
    i++;
}
```

Compare this `for` loop closely with that of the earlier array loop. The first think you might
notice is that our `i` count variable is not part of the `for` statement at all: it is initialized
before the loop and then incremented within the loop. That is because the `for` statement is looping
over the elements of the list, starting at `list.head()` and stopping when `element.next()` is
`null`.

### Example 1: grow a linked list

If we want to add an item to the end of a linked list, you must **find the last** element
and set its `Next` link to the new element. The list changes to this:

```
+--------+     +--------+     +--------+     +--------+
| Item 1 |     | Item 2 |     | Item 3 |     | Item 4 |
| Next ------->| Next ------->| Next ------->| Next   |
+--------+     +--------+     +--------+     +--------+
```

### Example 2: shrink a linked list

If we want to remove an item from the end of a linked list, you must **find the next-to-last** element
and clear its `Next` link. The list changes to this:

```
+--------+     +--------+     +--------+
| Item 1 |     | Item 2 |     | Item 3 |
| Next ------->| Next   |     | Next   |
+--------+     +--------+     +--------+
```

Notice how **Item 3** is disconnected from the list now: we have effectively removed it!

> :question: How could you remove the last 2 items from the list?

### Example 3: insert in the middle

If we want to insert an item into the middle of the list, for example **between items 2 and 3**,
we need to manipulate a couple of `Next` links. Imagine we start with this:

```
+--------+     +--------+     +--------+
| Item 1 |     | Item 2 |     | Item 3 |
| Next ------->| Next ------->| Next   |
+--------+     +--------+  ↑  +--------+
                       +--------+
                       | Item 4 |
                       | Next   |
                       +--------+
```

> :question: Can you see what needs to happen here?

We need to:

 1. Find Item 2
 2. Move Item 2's `Next` to point to Item 4
 3. Set Item 4's `Next` to point to Item 3

 It looks a bit like this:

```
+--------+     +--------+               +--------+
| Item 1 |     | Item 2 |               | Item 3 |
| Next ------->| Next + |            +->| Next   |
+--------+     +------|-+            |  +--------+
                      |   +--------+ |
                      |   | Item 4 | |
                      +-->| Next ----+
                          +--------+
```

We can re-phrase that process to make it more general, given a `NewItem` item we want to insert into the list:

1. Find `PrevItem`, the item we want to insert the new item **after**
2. Set `NewItem.Next` to `PrevItem.Next`
3. Set `PrevItem.Next` to `NewItem.Next`

> :question: This process sets `NewItem.Next` before `PrevItem.Next`. Could it do this the other way around?
> Why or why not?

In Java this process might look like this:

```java
LinkedListElement<String> newItem = new LinkedListItem("Item 4");
LinkedListElement<String> prevItem = findItem(2);
newItem.next = prevItem.next;
prevItem.next = newItem;
```

## Exercise: linked list


The [`LinkedLists`](./src/test/java/coding101/tq/example/LinkedLists.java) class is a
partially implemented program that allows you to create and manipulate a linked list.
You can add words to the list by typing `a <word>` or remove a word with `r <word>`.
You can reverse the list by typing `b`.

What you need to implement are a series of `TODO` comments in the `LinkedList` class.

```java
public static class LinkedList<T> {

    // the head element, or start, of the list
    private LinkedListElement<T> head;

}
```

Note the `head` field which is a `LinkedListElement<T>` object, the same class as discussed earlier.

### Task 1: add to end of list

Implement the `add(item)` method, which should add `item` to the **end** of the list:

```java
/**
 * Add an item to the end of the list.
 * 
 * @param item the item to add
 */
public void add(T item) {
    // TODO: add item to the end of the list
}
```

### Task 2: iterate over list

```java
/**
 * Iterate over all items in the list, invoking a callback function for each
 * item.
 * 
 * @param fn the callback function to call for every item in the list
 */
public void forEach(Consumer<T> fn) {
    // TODO: iterate over all items in the list and invoke fn.accept() on each
}
```

### Task 3: count number of items in the list

Implement the `size()` method, which should return the number of items in the list.

```java
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
```

### Task 4: insert after an existing item

Implement `insertAfter(item, after)`, which should insert `item` into the list
directly **after** the `after` item, or if `after` is not found then at the
**end** of the list.

```java
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
```

### Task 5: insert before an existing item

Implement `insertBefore(item, before)`, which should insert `item` into the list
directly **before** the `before` item, or if `before` is not found then at the
**start** of the list.

```java
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
```

### Task 6: remove item from list

Implement `remove(item)`, which should remove `item` from the list and return `true` if the item was
found, or `false` otherwise. 

```java
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
```

### Task 7: reverse the list

Implement the `reverse()` method, which should flip the list so that the last item becomes
the first, and the order of all items is reversed.

```java
/**
 * Reverse the list.
 */
public void reverse() {
    // TODO: reverse the list
}
```
