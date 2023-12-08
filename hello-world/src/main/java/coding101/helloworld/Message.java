package coding101.helloworld;

/**
 * A message object.
 */
public class Message {

    private final String message;

    /**
     * Constructor.
     * @param message the message to use
     */
    public Message(String message) {
        this.message = message;
    }

    /**
     * Get the message value.
     * @return the message value
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Get the class-level message.
     * @return the class-level message
     */
    public static String getClassMessage() {
        // as a `static` method, we do NOT have access to the **instance** field `message`!
        return "This is the class message.";
    }
}
