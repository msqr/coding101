// In Java multiple source files within a folder form a "package". Packages provide a way to organise code into
// groups of related functionality. A Java file must declare the package it belongs in with a `package` statement,
// where the package name is like a folder path, using '.' delimiters instead of '/' and must match the actual folder
// hierarchy in the file system, starting from the "source root" of the build, which is the tic-tac-toe/src/main/java
// folder in this repo.
//
// The folder path to this file, starting from the "source root", is `coding101/ttt`, to the package becomes
// `coding101.ttt`.

package coding101.helloworld;

// (Almost) everything in Java is a "class", which is a definition of behaviours (methods) and associated state
// (fields, class variables). For a Java program to run, you need to provide the name of the class to execute, so our
// program starts with a class named TicTacToe to match what the program actually does.
//
// You can only define one top-level class in a Java file, and its name *must* match the name of the file, minus the
// '.java' extension. The case matters as well, and the Java convention for class names is called Pascal case or
// "upper camel case"; see https://en.wikipedia.org/wiki/Letter_case#Camel_case.

public class HelloWorld {

    // When you ask Java to execute a class, it looks for a class method named "main" that accepts a String array
    // argument and returns nothing. Class methods must be declared as "static" and the main method must also be
    // declared as "public" (as opposed to "private" or "protected"). That is a lot of syntax to absorb at once,
    // but just go with the flow at this stage. To recap:
    //
    // `public` -- means any other code can call this method
    // `static` -- means this method is part of the class itself, not specific to an "instance" of the class
    // `void`   -- this is the return type; in Java `void` that means "nothing"
    // `main()` -- the name of the method; arguments are included within `()`
    // `String[] args` -- a method argument, i.e. a variable passed *to* the method. `String` is a built-in type
    //                    for text, and `[]` turns it into an **array**, or list, of strings. `args` is the name
    //                    of the argument variable, that we can use within the method.

    public static void main(String[] args) {
        System.out.println("Hello, world!");
        for (var i = 0; i < args.length; i++) {
            System.out.println("Arg %d: %s".formatted(i, args[i]));
        }
    }
}
