package io.tunabytes.classloader;

import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.List;

public final class TunaClassDefiner {

    /**
     * The major version number of class files
     * for JDK 1.8.
     */
    public static final int JAVA_8 = 52;

    /**
     * The major version number of class files
     * for JDK 1.9.
     */
    public static final int JAVA_9 = 53;

    /**
     * The major version number of class files
     * for JDK 10.
     */
    public static final int JAVA_10 = 54;

    /**
     * The major version number of class files
     * for JDK 11.
     */
    public static final int JAVA_11 = 55;

    /**
     * The least supported version respecting the
     * current one
     */
    public static final int MAJOR_VERSION;

    static {
        int ver = JAVA_8;
        try {
            Class.forName("java.lang.Module");
            ver = JAVA_9;
            List.class.getMethod("copyOf", Collection.class);
            ver = JAVA_10;
            Class.forName("java.util.Optional").getMethod("isEmpty");
            ver = JAVA_11;
        } catch (Throwable ignored) {}
        MAJOR_VERSION = ver;
    }

    // Java 11+ removed sun.misc.Unsafe.defineClass, so we fallback to invoking defineClass on
    // ClassLoader until we have an implementation that uses MethodHandles.Lookup.defineClass
    private static final ClassDefiner classDefiner = MAJOR_VERSION > JAVA_10
            ? createDefiner("11")
            : MAJOR_VERSION >= JAVA_9 ? createDefiner("9")
            : createDefiner("8");

    private static ClassDefiner createDefiner(String name) {
        try {
            return Class.forName("io.tunabytes.classloader.Java" + name).asSubclass(ClassDefiner.class).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to create ClassDefiner for Java " + name, e);
        }
    }

    public static Class<?> defineClass(String name,
                                       byte[] b,
                                       Class<?> neighbor,
                                       ClassLoader loader,
                                       ProtectionDomain protectionDomain) throws ClassFormatError {
        return classDefiner.defineClass(name, b, 0, b.length, neighbor, loader, protectionDomain);
    }
}
