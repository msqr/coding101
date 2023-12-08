package coding101.helloworld;

public class ClassVsInstance {

    public static void main(String[] args) {
        if (args.length < 1) {
            // no argument(s) provided, so create a hard-coded default one
            args = new String[] {"Hello, world."};
        }

        // Use a for loop to iterate over each argument one by one
        for (int i = 0; i < args.length; i++) {
            // Create an *instance* of the `Message` *class* using `new`. The `new` keyword invokes a special
            // class method called a **constructor**. The implied return value of a constructor is an
            // **instance** of that class.
            //
            // Constructors are like methods; they can accept arguments. Here we pass a String argument: the
            // current command-line argument passed to this `main()` method.
            var msg = new Message(args[i]);

            if (i > 0) {
                System.out.println(); // add an empty line between loops
            }

            // Print out the *instance* message by calling `getMessage()` on our `msg` variable instance.
            // Notice how the `msg` instance changes on each loop iteration, and thus each different
            // argument is printed even though there is just one `Message` *class*.
            System.out.println("msg.getMessage()          : %s".formatted(msg.getMessage()));

            // Print out the *class* message by calling `getClassMessage()` on the `Message` class itself.
            // Notice how this value is the same on each loop iteration: there is only one `Message` class
            // object and they all share the same class message value.
            System.out.println("Message.getClassMessage() : %s".formatted(Message.getClassMessage()));
        }
    }
}
