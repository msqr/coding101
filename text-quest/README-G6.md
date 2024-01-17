# Goal 6: Java collections

In the [previous](./README-G5.md) challenge, you learned about **linked lists** and why they can be
a useful alternative to arrays. The broader concept of a list of items is so common in programming
that languages like Java actually provide various list classes for you to use, including
`java.util.LinkedList` that implements a linked list very much like the one you just implemented.

> :bulb: Why did I make you write a linked list class then, when Java already provides one? Fair
> question. The reason is because implementing a linked list is a great introduction to many common
> programming techniques, such as modeling **data structures**, **iteration**, and general
> **computer problem solving**. The skills you learned will help you over and over as you write more
> code.

## About interfaces

I mentioned the `java.util.LinkedList` class earlier, which is a linked-list _implementation_ of the
_concept of a list_. Java actually provides a way to formally define _concepts_ like "a list" by way
of **interfaces**. An **interface** in Java is like a **definition** of the publicly visible methods
of some concept, **without any implementation** of that concept.

Contrast this idea to the idea of a **class**, which provides an **implementation** of a concept,
from which you can sometimes _infer_ its concept. The `LinkedList` class you wrote implements the
concept of a list, with methods like `add()` and `remove()` to manipulate the list, but there is no
formal definition of what a "list" is, and thus what methods a "list" should offer in general.

Here is where an **interface** comes in: we can **formally define** what a "list" **should offer**
without actually writing any implementation of that definition. Here is how we might define our
"list" concept as an interface:

```java
/**
 * Definition of an ordered list of items.
 * 
 * @param <T> the type of values the list contains, for example String, Integer, ...
 */
public interface List<T> {

    /**
     * Remove all items from the list.
     */
    public void clear();    

    /**
     * Add an item to the end of the list.
     *
     * @param item the item to add
     */
    public void add(T item);

    /**
     * Get the number of elements in the list.
     *
     * @return the count of elements in the list
     */
    public int size();

    /**
     * Insert an item into the list, after another item.
     *
     * @param item  the item to insert
     * @param after the item to insert after; if not found then the item will become
     *              the new end of the list
     */
    public void insertAfter(T item, T after);

    /**
     * Insert an item into the list, before another item.
     *
     * @param item   the item to insert
     * @param before the item to insert before; if not found then the item will
     *               become the new start of the list
     */
    public void insertBefore(T item, T before);

    /**
     * Remove an item from the list.
     *
     * @param item the item to remove
     * @return {@code true} if the item was found in the list and removed,
     *         {@code false} otherwise
     */
    public boolean remove(T item);
}
```

Notice how the methods are _defined_ but have no `{ }` blocks of implementation. All the `interface`
has doen is formally define what we should be able to do with a "list".

In Java a **class** can choose to **implement** any number of **interfaces** and must provide an
**implementation** of all the methods defined in those interfaces. The `LinkedList` class in the
previous goal was defined like this:

```java
public class LinkedList<T> { }
```

We can make that class **implement** our new `List<T>` interface like this:

```java
public class LinkedList<T> implements List<T> { }
```

Now, how would this be useful in practise, why bother with this `List<T>` interface when you could just use 
the `LinkedList<T>` class directly? Imagine you write a helper method, like this:

```java
public class ListUtilities {

    /**
     * Reverse a linked list in place and return the new start.
     * 
     * @param list the list to reverse
     * @return the new start of the list
     */
    public String reverseListAndReturnFirst(LinkedList<String> list) {
        list.reverse();
        return list.firstItem();
    }

}
```

Later on you realise an array-based list, `ArrayList`, would perform better for what you need in
**some places**, and that class has the same public methods as `LinkedList` but stores the values in
an array instead of a linked list. You sill want to use `LinkedList` in some places, however, and
you need to be able to call `reverseListAndReturnFirst()` on both kinds of lists. You could then
write another method to do that:

```java
public class ListUtilities {

    /**
     * Reverse a linked list in place and return the new start.
     * 
     * @param list the list to reverse
     * @return the new start of the list
     */
    public String reverseListAndReturnFirst(LinkedList<String> list) {
        list.reverse();
        return list.firstItem();
    }

    /**
     * Reverse an array list in place and return the new start.
     * 
     * @param list the list to reverse
     * @return the new start of the list
     */
    public String reverseListAndReturnFirst(ArrayList<String> list) {
        list.reverse();
        return list.firstItem();
    }
}
```

> :question: What do you notice about these two methods?

One thing that stands out is that the method implementations are **identical**! When you
see two blocks of identical code, that should raise at least one of your eyebrows and 
make you think if there might be a way to **avoid the duplicate code**.

Here is where a `List` interface could help. If we re-write our `LinkedList` and `ArrayList`
classes to **both implement the `List` interface** we can use write just one `reverseListAndReturnFirst()`
method, like this:

```java
// make LinkedList implement List
public class LinkedList<T> implements List<T> { }

// make ArrayList implement List
public class ArrayList<T> implements List<T> { }

// and now our helper needs just one method to handle BOTH classes:
public class ListUtilities {

    /**
     * Reverse a list in place and return the new start.
     * 
     * @param list the list to reverse
     * @return the new start of the list
     */
    public String reverseListAndReturnFirst(List<String> list) {
        list.reverse();
        return list.firstItem();
    }

}
```

That is much better! Duplicate code goes against that laziness principle central to programming:
writing two methods is just more work than writing one.

### Multiple interfaces

I mentioned that, in Java, a class can implement any number of interfaces. What does that look like?
Imagine the following interfaces that model vehicles:

```java
public interface Automobile {

    /**
     * Get the number of wheels the vehicle has.
     * 
     * @return the number of wheels
     */
    public int getWheelCount();
}

public interface Ship {

    /**
     * Get the number of propellers the ship has.
     * 
     * @return the number of wheels
     */
    public int getPropellerCount();

}

public interface Plane {

    /**
     * Get the number of propellers the plane has.
     * 
     * @return the number of wheels
     */
    public int getPropellerCount();

}
```

Let us imagine some basic classes that implement the `Automobile` interface:

```java
public class Car implements Automobile {

    public int getWheelCount() {
        return 4;
    }

}

public class BigRig implements Automobile {

    public int getWheelCount() {
        return 18;
    }
}
```

OK, we have a `Car` and a `BigRig` now, both of which are autombiles. What if we need an **amphibious vehicle**?
That is both an `Automobile` **and** a `Ship`, is it not? Indeed:

```java
public class AmphibiousCar implements Automobile, Ship {

    public int getWheelCount() {
        return 4;
    }

    public int getPropellerCount() {
        return 1;
    }

}
```

What if we need a **sea plane**? Ok:

```java
public class SeaPlane implements Ship, Plane {

    public int getPropellerCount() {
        return 1;
    }

}
```

> :bulb: Wait a minute, `Ship` and `Plane` both define the same `getPropellerCount()` method! It
> turns out that is fine in Java: all interface methods implemented by a class are sort of merged
> together and they can overlap. If the methods need to be treated differently, you might model them
> as such, like `getWaterPropellerCount()` in `Ship` and `getAirPropellerCount()` in `Plane`.

### Interface inheritence

In Java an interface can **extend** other interfaces, and by doing so **acquires** all the methods
defined in those extended interfaces. If we think about our `Automobile`, `Ship`, and `Plane` interfaces,
they are all types of _vehicles_. What if we wanted to have a `boolean canFly();` method on all
three? We could just add that method to all three, but that should raise at least one eyebrow,
with all that code duplication. Instead we can add a new `Vehicle` **parent interface** and make 
`Automobile`, `Ship`, and `Plane` **child interfaces** that **extend** `Vehicle`:

```java
public interface Vehicle {

    /**
     * Test if this vehicle can fly.
     * 
     * @return {@code true} if the vehicle can fly
     */
    public boolean canFly();

}
public interface Automobile extends Vehicle {

    /**
     * Get the number of wheels the vehicle has.
     * 
     * @return the number of wheels
     */
    public int getWheelCount();
}

public interface Ship extends Vehicle {

    /**
     * Get the number of propellers the ship has.
     * 
     * @return the number of wheels
     */
    public int getPropellerCount();

}

public interface Plane extends Vehicle {

    /**
     * Get the number of propellers the plane has.
     * 
     * @return the number of wheels
     */
    public int getPropellerCount();

}
```

> :question: What methods are defined on `Automobile` now?

How might this `Vehicle` interface end up being used in a program? Perhaps you have a shop that
sells any type of vehicle. The shop's inventory might be modeled as a list of `Vehicle`:

```java
public class VehicleShop {

    // the list of vehicles available for sale
    private LinkedList<Vehicle> inventory;

}
```

## List and Set collections

We have seen over and over how the concept of a list of items is very common and very useful
in programming (using both arrays and dynamic list classes like `LinkedList`). Another term
we can use to describe a list is **collection**, as in a list is a _collection_ of items in
a specific order. There are two other extremely useful **collection data structures** commonly
used in programming: the **set** and the **map**. Let us take a closer look at what Java
provides in this area.

### Lists

In Java the concept of a list is defined in the `java.util.List` interface. If you look
at `java.util.List` you find that it actually extends `java.util.SequencedCollection`
which then extends `java.util.Collection` which then extends `java.util.Iterable`. Oh my,
that deserves a diagram:

```
Iterable<T>
|
+-- Collection<T>
    |
    +-- SequencedCollection<T>
        |
        +-- List<T>
```

This **hierarchy** of interfaces goes from **least-specific** at the top to **most-specific** at the
bottom. Starting from `List` you could say that a `List` **is a** `SequencedCollection`, which **is
a** `Collection`, which **is an** `Iterable`. Or you could say that a `List` **is also** a
`SequencedCollection`, `Collection`, and `Iterable`.

> :bulb: You do not need to think too much about all these interfaces yet, just focus on `List` and
> we will see how the parent interfaces fit it a bit later.

#### List implementations

There are two main `List` implementations in Java: `java.util.ArrayList` and `java.util.LinkedList`.
They are implemented using arrays and linked lists, respectively. You can mostly use the two 
implementations interchangeably. For general use the `ArrayList` usually performs better than
`LinkedList`.

> :question: Given all we have discussed about array-backed lists versus linked lists, can you think
> of different list **usage scenarios** where `LinkedList` would be a better-performing choice than
> `ArrayList`, and vice-versa? By _usage scenario_ I mean _how the list would be **primarily** used
> in your program_.

### Sets

The idea of a **set** is that of a list that holds **only unique values** and does **not** keep
the values in any particular order. A list might contain duplicate values, but a set can not.
Recall our `List` interface, which had this method:

```java
public interface List<T> {

    /**
     * Add an item to the list.
     *
     * @param item the value to add
     */
    public void add(T item);

}
```

We could have the same method on a `Set` interface:

```java
public interface Set<T> {

    /**
     * Add a value to the set.
     * 
     * @param item the value to add
     */
    public void add(T item);

}
```

Now imagine we have both a `List` and a `Set` instance and we do the following:

```java
List<Integer> list = newList();
Set<Integer> set = newSet();

int[] numbers = new int[] {1, 2, 1, 3, 2, 4};

// add all numbers to both list and set
for (int i = 0; i < numbers.length; i++) {
    list.add(numbers[i]);
    set.add(numbers[i]);
}
```

> :question: After this code executes, what values does `list` contain, and what values does `set`
> contain?

So when is a set more useful than a list? Well, a set is great when you only need to know if a
value **exists or not** within your collection of items. In the previous code example, what if
you only wanted to know if the collection contains a `1` or not, and you do not care _where_
the `1` is in relation to other values, nor do you care if `1` happend to be added more than
once. You really just want to know **do you contain `1` or not**? That is exactly the type
of question a `Set` is good at answering:

```java
public interface Set<T> {

    /**
     * Test if a value exists in the set
     * 
     * @param item the value to test
     * @return {@code true} if item exists in the set
     */
    public boolean contains(T item);

}
```

> :question: Can you think of any other scenarios a `Set` would be more useful than a `List`?

In Java, the `java.util.Set` interface models a set. If we update our interface hierarchy
diagram from earlier, `Set` fits in like this:

```
Iterable<T>
|
+-- Collection<T>
    |
    +-- SequencedCollection<T>
    |   |
    |   +-- List<T>
    |
    +-- Set<T>
```

So a `Set<T>` is a `Collection<T>` and `Iterable<T>`, but **not** a `SequencedCollection<T>`.

#### Set implementations

There are three main `Set` implementations in Java: `java.util.HashSet`, `java.util.LinkedHashSet`,
and `java.util.TreeSet`. The `HashSet` is a good all-around set implementation that does not
maintain any order of the values added to it, so when you iterate over the values in the set you
have no way of knowing what order they will be returned. The `LinkedHashSet` maintains **insertion
order** of the values, so when you iterate over the values they will be returned in the order they
were added, oldest to newest. The `TreeSet` maintains **sorted order** of the values, so when you
iterate over the values they will be returned in a sorted order, be that alphabetic (for strings) or
numeric (for numbers).

> :bulb: Unless you rely on an ordering of the values in a set for some reason, the `HashSet` is
> your best option and will perform better than `LinkedHashSet` or `TreeSet`.

### Collection iteration

In the `LinkedList` class in the previous challenge, we had an iteration method `forEach(fn)`:

```java
/**
 * Iterate over all items in the list, invoking the {@code accept(item)}
 * method on the provided callback function for each item.
 * 
 * @param fn the callback function to call {@code accept(item)} for every
 *               item in the list
 */
public void forEach(Consumer<T> fn);
```

The Java `java.util.Iterable<T>` interface that both `List` and `Set` extend provides a similar
mechanism to support iteration:

```java
public interface Iterable<T> {

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    Iterator<T> iterator();
}
```

OK, that `iterator()` method just returns an `java.util.Iterator<T>` object. Let us have a look
at that:

```java
public interface Iterator<T> {
    
    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    boolean hasNext();

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    T next();

}
```

> :question: Take a look at the `Iterable` and `Iterator` interfaces. Can you think of how
> you could use those on a `List` object to loop over all items in the list?

Here is one way you can use the `Iterable` interface on a `List` to loop over all values in the
list:

```java
List<String> list = newList("a", "b", "c"); // newList() contains "a", "b", and "c"
for (    Iterator<String> itr = list.iterator();  // get Iterator from the List
         itr.hasNext();                           // loop until hasNext() returns false
         ) {                                      // nothing to change here
    String val = itr.next();                      // call next() for the next list litem
    System.out.println("Item is %s".formatted(val));
} 
```

#### Short-cut `Iterable` for loop

This technique of iterating over Java collections is so common that Java actually provides
a short-hand way of doing it, using a **modified `Iterable` for loop** that looks like this:

```java
List<String> list = newList("a", "b", "c"); // newList() contains "a", "b", and "c"
for (String val : list) {                   // loop over all values in the list
    System.out.println("Item is %s".formatted(val));
} 
```

Notice the `:` character between the local variable `val` and the `list` variable. The only
requirement for this style of loop is that `list` implement the `Iterable` interface (which we know
`List` extends). This syntax translates to "loop over each item in `list`, setting `val` to each
item per loop iteration". Behind the scenes the Java compiler actually translates your code into
code resembling the `for (Iterator<String> = itr.iterator(); itr.hasNext(); ) { var val = itr.next(); }` syntax shown
previously.

> :question: We talked about how to iterate a `List`; how do you think you can iterate a `Set`?

## Maps

A **map** concept is like a **dictionary** of **unique keys** with associated **values**. In some
programming languages, maps are actually called dictionaries, or sometimes keyed arrays.

> :bulb: Imagine how you use a real (word) dictionary: you look up a word (the key) and get its
> associated meaning (the value).

In JavaScript objects behave a lot like maps, where the object properties are the keys and their
associated values the map values:

```js
{
    name:   "Player 1",
    health: 50,
    xp :    1020
}
```

Here we have a JavaScript object that is like a map with _name_, _health_, and _xp_ **keys**. The
value for key `name` is `Player 1`, and key `xp` is `1020`.

In Java, the `java.util.Map<K, V>` interface models a map, where `K` is the **type of key** used and
`V` the **type of value**. For example a map of strings to associated integers would be a
`Map<String, Integer>`. The gist of the `Map` interface looks like this:

```java
public interface Map<K, V> {
    // Query Operations

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    int size();

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    boolean isEmpty();

    /**
     * Returns {@code true} if this map contains a mapping for the specified
     * key.
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified
     *         key
     */
    boolean containsKey(Object key);

    /**
     * Returns {@code true} if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the
     *         specified value
     */
    boolean containsValue(Object value);

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     */
    V get(Object key);

    // Modification Operations

    /**
     * Associates the specified value with the specified key in this map.
     * 
     * If the map previously contained a mapping for the key, the old value
     * is replaced by the specified value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key},
     *         if the implementation supports {@code null} values.)
     */
    V put(K key, V value);

    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     */
    V remove(Object key);
}
```

The `get(key)`, `put(key, value)`, and `remove(key)` methods allow you to get the value for a key,
add a key and associated value, and remove a key (along with its associated value).

It is important to reiterate that the set of keys in a map **is always unique**. If you call
`put(key, value)` with the same `key` but a different `value`, the map will **replace** the first
value with the second.

> :question: Thinking about how the collection of keys in a map is unique, does that remind you of
> any other data structure?

Another method in `Map` returns a `Set<K>` of all the keys defined in the map:

```java
public interface Map<K, V> {

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.
     *
     * @return a set view of the keys contained in this map
     */
    Set<K> keySet();
   
}
```

That opens up the possibility of **iterating** over all keys in the `Map`, since
you can iterate over the values in the `Set` returned by `keySet()`:

```java
Map<String, Integer> enemyPoints = newMap("zombie", 10, "werewolf", 15, "vampire", 25);
Set<String> enemies = enemyPoints.keySet();
for (String enemy : enemies) {
    Integer points = enemyPoints.get(enemy);
    System.out.println("Enemy %s is worth %d points.".formatted(enemy, points));
}
```

> :question: This program prints out one line per map key. Exactly what does this program print out?
> Do you know the exact order of those lines?

What we just did, iterate over the keys of a map, calling `map.get(key)` for each
key of the iteration, is a very common technique. Java provides another method
to help with this pattern:

```java
public interface Map<K, V> {
    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation, or through the
     * {@code setValue} operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Set.remove}, {@code removeAll}, {@code retainAll} and
     * {@code clear} operations.  It does not support the
     * {@code add} or {@code addAll} operations.
     *
     * @return a set view of the mappings contained in this map
     */
    Set<Entry<K, V>> entrySet();

}
```

The `entrySet()` method is returning a `Set`, much like `keySet()` did, but
the **set type** is different, it is a `Entry<K, V>` type. What is that?
The gist of if the `Entry` interface looks like this:

```java
public interface Entry<K, V> {

    /**
     * Returns the key corresponding to this entry.
     *
     * @return the key corresponding to this entry
     */
    K getKey();

    /**
     * Returns the value corresponding to this entry.  If the mapping
     * has been removed from the backing map (by the iterator's
     * {@code remove} operation), the results of this call are undefined.
     *
     * @return the value corresponding to this entry
     */
    V getValue();
}
```

OK, so `Entry` is something with `getKey()` and `getValue()` methods. Can you
see where this is going? An `Entry` represents one key/value pair in a `Map`!
So a `Set<Entry<K, V>>` is a set of key/value pairs, unique to key key in the
map. Here is how we could re-write our previous example of iterating over a
`Map`:

```java
Map<String, Integer> enemyPoints = newMap("zombie", 10, "werewolf", 15, "vampire", 25);
for (Entry<String, Ingeger> entry : enemyPoints.entrySet()) {
    String enemy = entry.getKey();
    Integer points = entry.getValue();
    System.out.println("Enemy %s is worth %d points.".formatted(enemy, points));
}
```

### Map implementation: `HashMap`

The default `java.util.Map` implementation in Java is the `java.util.HashMap` class. This class
models a map using a **hash bucket** approach. At its most basic, a hash bucket is an **array of
lists**. Each element of the array is called a **bucket** and represents a **subset (range) of all
possible key values** in the map, and the list associated with each element holds all the
**key/value pairs**, or **entries** that fall into that bucket's range.

If you imagine a regular dictionary (book) again, the **pages** of the dictionary are like elements
in the array: a **bucket**. Each page has a **letter range** at the top that shows you the possible
range of word in that bucket: the **range**.

Now magine a dictionary of fruit names with associated costs (instead of a definition). That is a
`Map<String, Integer>` style map. Imagine this dictionary has just 2 pages, one for fruits starting
with a letter in the range **a - m** and another with a letter in the range **n - z**. After we add
several entries to the map it would look like this:

```
    buckets                       entries
+------------+    
|            |     +---------------+-------------+-------------+
| range: a-m |     | key:   apple  | key: mango  | key: cherry |
| values:  ------->| value: 5      | value: 15   | value: 20   |
|            |     +---------------+-------------+-------------+
+------------+     
|            |     +---------------+-------------+
| range: n-z |     | key:   pear   | key: peach  |
| values:  ------->| value: 3      | value: 8    |
|            |     +---------------+-------------+
+------------+
```

> :question: What are the logical steps it would take, in plain language, to add an entry `(key:
> nectarine, value: 8)` to this structure? What steps would it take to update the `cherry` entry's
> value to `22`?

#### Finding the right bucket

So far our bucket elements have a `range` that identifies a range of letters to hold in that bucket.
That means for every operation that depends on a map key, in order to identify which bucket that key
is associated with we need to:

 1. extract the first character out of the key
 2. find the index of the bucket whose range contains that character
 
 What if instead of just 2 buckets, we decided we wanted one bucket for every letter in the
 alphabet, so 26 buckets? Let us visualize that, and include the bucket array indexes:

 ```
       buckets   
    +------------+
  0 | range: a-a |
    | values: -------> ...
    +------------+
  1 | range: b-b |
    | values: -------> ...
    +------------+
    ...
    +------------+
 24 | range: y-y |
    | values: -------> ...
    +------------+
 25 | range: z-z |
    | values: -------> ...
    +------------+
 ```

Now step #2 in our "find the right bucket" process changes ever so slightly, because the
"range" in each bucket element is actually not a range at all, it is just a single
character. The process can be changed to:

 1. extract the first character out of the key
 2. find the index of the bucket whose "range" **equals** that character

Now there is another interesting thing about computers that comes into play here: **a "character" is
actually just a number**, that is **displayed** to you as a letter with the help of a **font**.
Let us imagine that our computer alphabet are actually numbers, starting at `0`, like this:

```
 a  b        y  z
+--+--+     +--+--+
|0 |1 | ... |24|25|
+--+--+     +--+--+
```

So, if `a == 0` and `b == 1` and so on, does that help our "find the right bucket"
process at all, knowning that the buckets are stored in an **array**?

It sure does! Once we have a letter, we can just treat that as an **array index**
and we know exactly what bucket element we need. In Java, however, the character
`a` is not equal to `0` so we can subtract whatever `a` is from the letter to
shift the result so that `a` is treated as `0`:

```java
public class HashMap {

    /**
     * Get the bucket array index to use for a given key.
     * 
     * @param key the key
     * @return the bucket array index to use
     */
    public int bucketIndexForKey(String key) {
        // 1. extract first character from key
		char letter = key.charAt(0);
        
        // 2. treat letter as number, and shift so that a == 0
		int bucketIndex = letter - 'a';

        return bucketIndex;
    }

}
```

#### Handling ∞ possible keys

OK, this is feeling pretty good now: our "find the right bucket" process is fast and efficient. But
it has several limitations, such as:

 1. Does not handle capital letters
 2. Does not handle words starting with numbers, or any other character outside a - z
 3. Does not work for other key types, like `Integer`

Hmm. What can we do to fix these? For example if we look just at handling capital letters, maybe we
just convert the key's first character to lower-case (Java provides the
`Character.toLowerCase(character)` for that). That does not solve #2, though, nor would it work for
#3 when we are not working with string keys in the first place.

If we take a step back and try to think about what these limitations have in common, one that that
comes to mind is that they all require finding a way to **translate** a possibly **infinite number
of keys** into a **fixed number of buckets**. We could go back to our original range-based bucket
search approach, but after seeing how nice it was being able to quickly translate a key into a
bucket index, it would be nice to be able to keep using this approach.

This problem, of translating an infinite number of values into a fixed subset of values, is known as
**hashing** in computing. Put another way, you take an arbitrary value and compute a **hash value**
for it, or **hash code**. We can rename our `bucketIndexForKey()` method accordingly:

```java
public int hashCode(String key) {
    // 1. extract first character from key
    char letter = key.charAt(0);
    
    // 2. treat letter as number, and shift so that a == 0
    int bucketIndex = letter - 'a';

    return bucketIndex;
}
```

#### Division to the rescue

Now let me take you back to your math class from years ago, when you studied basic whole number
division with **remainders**. Do you remember that? For example **5 ÷ 3** is **1 remainder 2**.

> :question: What is the **possible range** of values the **remainder** can be when dividing any
> number by **3**?

We can quickly write out a small table of dividing by 3 to help give us a clue:

| Dividend | Divisor | Result | Remainder |
|:---------|:--------|:-------|:----------|
| 0        | 3       | 0 | 0 |
| 1        | 3       | 0 | 1 |
| 2        | 3       | 0 | 2 |
| 3        | 3       | 1 | 0 |
| 4        | 3       | 1 | 1 |
| 5        | 3       | 1 | 2 |
| 6        | 3       | 2 | 0 |
| 7        | 3       | 2 | 1 |
| 8        | 3       | 2 | 2 |

Do you see it? When dividing by 3, the remainder can be **one of 3 values**, between 0 - 2. What if
the divisor is, say, 4?

| Dividend | Divisor | Result | Remainder |
|:---------|:--------|:-------|:----------|
| 0        | 4       | 0 | 0 |
| 1        | 4       | 0 | 1 |
| 2        | 4       | 0 | 2 |
| 3        | 4       | 0 | 3 |
| 4        | 4       | 1 | 0 |
| 5        | 4       | 1 | 1 |
| 6        | 4       | 1 | 2 |
| 7        | 4       | 1 | 3 |
| 8        | 4       | 2 | 0 |

When dividing by 4, the remainder can be **one of 4 values**, always between 0 - 3. Do you see a
pattern here that might help with our hashing problem? 

> :bulb: In our example tables, the Dividend is the **infinite list of keys** and the Divisor is the
> **fixed number of buckets** and the Remainder is the **hash code** for the Dividend!

Java provides the **modulo** operator `%` that returns the remainder from whole-number division:

```java
int whole     = 7 / 3; // whole == 2
int remainder = 7 % 3; // remainder == 1
```

Using the `%` operator we can update our `hashCode()` method to handle **any string key**:

```java
public class HashMap {

    private List<Entry>[] buckets; // initialized somewhere

    /**
     * Get the bucket array index to use for a given key.
     * 
     * @param key the key
     * @return the bucket array index to use
     */
    public int hashCode(String key) {
        // 1. extract first character from key
        char letter = key.charAt(0);
        
        // 2. translate the letter into a bucket element index
        //    as the remainder after dividing the letter by
        //    the number of buckets
        return letter % buckets.length;
    }

}
```

> :question: What if our keys were `Integer` values? Complete the following method implementation:

```java
public class HashMap {

    private List<Entry>[] buckets; // initialized somewhere

    public int hashCode(int key) {
        int result;
        // TODO: comptue the hash code for key and assign to result
        return result;
    }
}
```

> :bulb: Now you know why the `java.util.HashMap` class is named what it is: because under the hood
> it uses **hashing** of key values to assign entries to a fixed number of array elements, or _hash
> buckets_.

> :question: Did you also remember the `java.util.HashSet` class? Can you think of why that class
> is named what it is?


## Other useful collections

Java provides many other useful collections. Here is an incomplete list of some popular ones that I
will leave to you to explore more fully:

| Collection | Description |
|:-----------|:------------|
| `java.util.ArrayList` | A **list** using arrays for storage. |
| `java.util.LinkedList` | A **list** using a linked list for storage. |
| `java.util.HashMap` | A **map** using a hash-bucket array of linked-lists for storage. |
| `java.util.LinkedHashMap` | A **map** using a hash-bucket array of linekd-lists _and_ a separate linked-list to iterate in insertion order. |
| `java.util.TreeMap` | A **map** using a [red-black](https://en.wikipedia.org/wiki/Red%E2%80%93black_tree) tree structure for storage to iterate in natural sort order. |
| `java.util.HashSet` | A **set** using a `HashMap` for stoarge, the keys of the map being the values of the set. |
| `java.util.LinkedHashSet` | A **set** using a `LinkedHashMap` for stoarge, to iterate in insertion order. |
| `java.util.TreeSet` | A **set** using a `TreeMap` for storage, to iterate in natural sort order. |
