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

## List, Set, and Map collections

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

So a `Set` is a `Collection<T>` and `Iterable<T>`, but **not** a `SequencedCollection<T>`.

### Maps
