# Hello, World!

See the source in [HelloWorld.java](src/main/java/coding101/helloworld/HelloWorld.java).

Run like this:

```sh
# first compile the Java code into an executable Jar file
../gradlew build

# now run the Jar
java -jar build/libs/hello-world-all.jar

# also run with some arguments
java -jar build/libs/hello-world-all.jar a b c

```

> :fire: **Note** you need to run `build` only after making changes to a source file.

# Classes vs. Instances

Learn the difference between a _class_ and a _class instance_. See the source in
[ClassVsInstance.java](src/main/java/coding101/helloworld/ClassVsInstance.java).

```sh
# first compile the Java code into an executable Jar file
../gradlew build

# now run the Jar
java -cp build/libs/hello-world-all.jar coding101.helloworld.ClassVsInstance

# also run with some arguments
java -cp build/libs/hello-world-all.jar coding101.helloworld.ClassVsInstance a b c
```

# Imports and Logging

Learn about importing "outside" classes by using a logging framework to log messages
instead of using `System.out`. See the source in
[ImportsAndLogging.java](src/main/java/coding101/helloworld/ImportsAndLogging.java).

```sh
# first compile the Java code into an executable Jar file
../gradlew build

# now run the Jar
java -cp build/libs/hello-world-all.jar coding101.helloworld.ImportsAndLogging

# also run with some arguments
java -cp build/libs/hello-world-all.jar coding101.helloworld.ImportsAndLogging a b c
```
