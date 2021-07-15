[![](https://jitpack.io/v/ReflxctionDev/tuna-bytes.svg)](https://jitpack.io/#ReflxctionDev/tuna-bytes)

# Tuna Bytes
![A tuna byte :)](https://i.imgur.com/15VLkMI.jpg)
Tuna-bytes is an all-purpose powerful class and bytecode manipulation mixins for Java, which is intended at those with minimal understanding of the Java bytecode structure.

## Features
- Full support for the notorious Java 8+ versions
- Requires zero knowledge of the Java bytecode.
- Requires zero overhead to get started. Just add tuna-bytes as a dependency and as an annotation processor, and tuna-bytes will handle the rest.
- Does not require any additional Java execution arguments, like what Java agents do.

## Usage

Note: Due to the many changes done to classloading semantics, classloading code has been abstracted across Java 8+ versions. Therefore, the value of `{artifact}` in the examples below should be determined by the Java version you're going to run on. When unsure, you can include all versions and exclude the dependencies from them accordingly.

| Your Java version | Tuna-Bytes artifact |
|-------------------|--------------------|
| Java 8            | java8              |
| Java 9 / Java 10  | java9              |
| Java 11+          | java11             |

**build.gradle**:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    implementation 'com.github.ReflxctionDev.tuna-bytes:{artifact}:1.0'
    annotationProcessor 'com.github.ReflxctionDev.tuna-bytes:{artifact}:1.0'
}
```

**pom.xml**
```xml
<repositories>
    <repository>
	<id>jitpack.io</id>
	<url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.ReflxctionDev.tuna-bytes</groupId>
    <artifactId>{artifact}</artifactId>
    <version>1.0</version>
</dependency>
```

```xml
<pluginManagement>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.6.1</version>
            <configuration>
                <annotationProcessorPaths>
                    <annotationProcessorPath>
                        <groupId>com.github.ReflxctionDev.tuna-bytes</groupId>
                        <artifactId>{artifact}</artifactId>
                        <version>1.0/version>
                    </annotationProcessorPath>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</pluginManagement>
```

## Examples
Let's say we have our `Point` class
```java
public class Point {  
  
  private final int x, y;  
  
  public Point(int x, int y) {  
    this.x = x;  
    this.y = y;  
  } 
  
  public void print() {
    System.out.println("X: " + x);  
    System.out.println("Y: " + y);  
  }  
}
```

### Accessor
Accessors are a handy type of mixins that allow to access otherwise inaccessible fields or methods.
```java
import io.tunabytes.Accessor;
import io.tunabytes.Mixin;

@Mixin(Point.class)
public interface PointAccessor {

    @Accessor int getX();

    @Accessor int getY();

    @Accessor callPrivateMethod();

}
```

```java
import io.tunabytes.bytecode.MixinsBootstrap;

public final class PointTest {

    public static void main(String[] args) {
        MixinsBootstrap.init();
        Point point = new Point(55, 94);
        int x = ((PointAccessor) point).getX();
        int z = ((PointAccessor) point).getY();
        ((PointAccessor) point).callPrivateMethod();
        ..
    }
}
```

### Overwrite
We would like to overwrite the functionality of `print()` to print the x and y in a different format.
```java
import io.tunabytes.FieldMirror;
import io.tunabytes.Mixin;
import io.tunabytes.Overwrite;

@Mixin(Point.class)
public class PointMixin {

    @FieldMirror
    private int x; // the actual x field
    
    @FieldMirror
    private int y; // the actual y field

    @Overwrite
    public void print() {
        System.out.println("I'm a funny point at " + x + " and " + (y - 10) + " :D");
    }
}
```

```java
import io.tunabytes.bytecode.MixinsBootstrap;

public final class PointTest {

    public static void main(String[] args) {
        MixinsBootstrap.init();
        Point point = new Point(10, 50);
        point.print();
    }

}
```

Output:
```
I'm a funny point at 10 and 40 :D
```

### Inject
Sometimes overwriting a method is an overkill, when we would like to only add a small line of code. This is where `@Inject` comes into play!

Injection can happen at 5 places:
- **Beginning of a method**: Just before anything happens in the method
- **Very end of the method**: At the very end of the method (note: the end! not before each `return` statement)
- **Before each `return` statement**: Before the method returns any result
- **Before a line**: In case of knowing the line number from the source code, we can inject before that line
- **After a line**: In case of knowing the line number from the source code, we can inject after that line

Let's say we have a fluffy `Animal` class:
```java
public class Animal {

    public String getSound(String animal) { // method is private
        switch (animal) {
            case "Wolf":
                return "Bark";
            case "Cat":
                return "Meow";
            case "Cow":
                return "MOO";
            default:
                return "*makes human sound*";
        }
    }
}
```

Injection is done using the `@Inject` annotation:
```java
import io.tunabytes.FieldMirror;
import io.tunabytes.Inject;
import io.tunabytes.Inject.At;
import io.tunabytes.Mixin;

@Mixin(Animal.class)
public class AnimalMixin {

    @Inject(method = "getSound", at = At.BEFORE_EACH_RETURN)
    public void injectGetSound() {
        System.out.println("I think I heard something");
    }
}
```

```java
import io.tunabytes.bytecode.MixinsBootstrap;

public final class AnimalTest {

    public static void main(String[] args) {
        MixinsBootstrap.init();
        Animal animal = new Animal();
        System.out.println(animal.getSound("Cat"));
        System.out.println(animal.getSound("human thing"));
    }
}
```

Output:
```
I think I heard something
Meow
I think I heard something
*makes human noise*
```

# Drawbacks
Just like any other bytecode manipulation library, **manipulating a class after is has been loaded is not possible** without things like instrumentation, agents or such. Tuna-bytes assumes that any class it is about to modify has not been loaded, and will otherwise throw an exception. To suppress, use `MixinsBootstrap.init(true)` 