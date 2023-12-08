package coding101.helloworld;

// To more easily reference other classes in other packages, you can add `import` declarations after the `package`
// declaration. Once a class is imported, you can reference that class without its associated package.
//
// Here we import two classes that are used by the slf4j logging framework:

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Now that org.slf4j.Logger is imported, this file can refer to that class as simply `Logger`.

public class ImportsAndLogging {

    // Here we configure a private, class-level (`static`), constant (`final`) org.slf4j.Logger variable `log`.
    // This `Logger` is like `console.log()` in JavaScript, and lets us log messages "somewhere" that are
    // useful for debugging or general runtime info.
    //
    // `private` -- this variable can only be used within this class
    // `static`  -- this is a class variable, not an "instance" variable
    // `final`   -- this variable cannot be changed after initialization, like `const` in JavaScript
    // `Logger`  -- the variable type: the org.slf4j.Logger class
    // `log`     -- the variable name
    private static final Logger log = LoggerFactory.getLogger(ImportsAndLogging.class);

    public static void main(String[] args) {
        // Compare this to HelloWorld.java, which used System.out.println(). The Logger class provides
        // different "levels" of logging: `trace()`, `debug()`, `info()`, `warn()`, and `error()`.
        // The levels can be turned on/off at runtime, and the default level is usually `INFO` which means
        // lower-levels are hidden (`TRACE` and `DEBUG`).
        log.info("Hello, world!");
        for (var i = 0; i < args.length; i++) {
            // Using `debug()` log level here, and notice how template parameters are supported with `{}`
            // placeholders, follwed by the list of values to use. The `{}` placeholders are replaced in
            // the same order as the arguments passed in, here `i` and `args[i]`.
            log.debug("Arg {}: {}}", i, args[i]);
        }
    }
}
