# Role-based authorization in Ktor
This repository contains a demo application showing a possible way to implement role-based authorization of routes in Ktor.

More information on its design can be found in this [accompanying blog post](https://www.ximedes.com/role-based-authorization-in-ktor/).

## Running from IntelliJ IDEA
Run the `main` method on line 16 in `Application.kt`

## Running from maven
Compile and package the JAR file and run it:
```
mvn package
cd target
java -jar ktor-authorization-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

In either case, the application should be accessible at http://localhost:8080

When asked to log in, any username will do. The password is always `secret`.

