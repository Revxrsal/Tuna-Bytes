[![](https://jitpack.io/v/ReflxctionDev/tuna-bytes.svg)](https://jitpack.io/#ReflxctionDev/tuna-bytes)

# Tuna Bytes
![A tuna byte :)](https://i.imgur.com/15VLkMI.jpg)
Tuna-bytes is an all-purpose powerful class and bytecode manipulation mixins for Java, which is intended at those with minimal understanding of the Java bytecode structure.

## Features
- Full support for the notorious Java 9+ versions, as well as Java 8
- Requires zero knowledge of the Java bytecode.
- Requires zero overhead to get started. Just add tuna-bytes as a dependency and as an annotation processor, and tuna-bytes will handle the rest.
- Does not require any additional Java execution arguments, like what Java agents do.

## Index
1. [Maven setup](wiki/Maven-Setup)
2. [Gradle setup](wiki/Gradle-Setup)
3. [**Example**: Create accessors for inaccessible fields and methods](wiki/Accessors)
4. [**Example**: Overwrite a method](wiki/Overwrite)
5. [**Example**: Inject into a method](wiki/Injecting)
6. [**Example**: Mirroring a field or a method](wiki/Mirroring)

# Drawbacks
Just like any other bytecode manipulation library, **manipulating a class after is has been loaded is not possible** without things like instrumentation, agents or such. Tuna-bytes assumes that any class it is about to modify has not been loaded, and will otherwise throw an exception. To suppress `Class XX has alreade beel loaded` exceptions, use `MixinsBootstrap.init(true)`
