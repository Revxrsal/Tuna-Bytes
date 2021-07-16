package io.tunabytes.classloader;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

final class Java11 implements ClassDefiner {

    private final Method defineClass = getDefineClassMethod();

    private Method getDefineClassMethod() {
        try {
            return SecurityActions.getDeclaredMethod(ClassLoader.class, "defineClass",
                    new Class[]{
                            String.class, byte[].class, int.class, int.class, ProtectionDomain.class
                    });
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("cannot initialize", e);
        }
    }

    @Override
    public Class<?> defineClass(String name, byte[] b, int off, int len, Class<?> neighbor,
                                ClassLoader loader, ProtectionDomain protectionDomain)
            throws ClassFormatError {
        if (neighbor != null)
            return toClass(neighbor, b);
        else {
            // Lookup#defineClass() is not available.  So fallback to invoking defineClass on
            // ClassLoader, which causes a warning message.

            try {
                SecurityActions.setAccessible(defineClass, true);
                return (Class<?>) defineClass.invoke(loader, new Object[]{
                        name, b, off, len, protectionDomain
                });
            } catch (Throwable e) {
                sneakyThrow(e);
                return null;
            }
        }
    }

    private static RuntimeException sneakyThrow(Throwable t) {
        if (t == null) throw new NullPointerException("t");
        return sneakyThrow0(t);
    }

    private static <T extends Throwable> T sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }

    /**
     * Loads a class file by {@code java.lang.invoke.MethodHandles.Lookup}.
     * It is obtained by using {@code neighbor}.
     *
     * @param neighbor a class belonging to the same package that the loaded
     *                 class belogns to.
     * @param bcode    the bytecode.
     * @since 3.24
     */
    public static Class<?> toClass(Class<?> neighbor, byte[] bcode) {
        try {
            TunaClassDefiner.class.getModule().addReads(neighbor.getModule());
            Lookup lookup = MethodHandles.lookup();
            Lookup prvlookup = MethodHandles.privateLookupIn(neighbor, lookup);
            return prvlookup.defineClass(bcode);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage() + ": " + neighbor.getName()
                    + " has no permission to define the class");
        }
    }

    @Override public boolean requiresNeighbor() {
        return true;
    }
}
