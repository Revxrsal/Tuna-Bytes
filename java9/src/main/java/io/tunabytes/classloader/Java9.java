package io.tunabytes.classloader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.List;

final class Java9 implements ClassDefiner {

    static final class ReferencedUnsafe {

        private final SecurityActions.TheUnsafe sunMiscUnsafeTheUnsafe;
        private final MethodHandle defineClass;

        ReferencedUnsafe(SecurityActions.TheUnsafe usf, MethodHandle meth) {
            sunMiscUnsafeTheUnsafe = usf;
            defineClass = meth;
        }

        Class<?> defineClass(String name, byte[] b, int off, int len,
                             ClassLoader loader, ProtectionDomain protectionDomain)
                throws ClassFormatError {
            try {
                return (Class<?>) defineClass.invokeWithArguments(
                        sunMiscUnsafeTheUnsafe.theUnsafe,
                        name, b, off, len, loader, protectionDomain);
            } catch (Throwable e) {
                throw sneakyThrow(e);
            }
        }
    }

    private static <T extends Throwable> T sneakyThrow(Throwable t) throws T {
        throw (T)t;
    }

    private final ReferencedUnsafe sunMiscUnsafe = getReferencedUnsafe();

    public Java9() {
        Class<?> stackWalkerClass = null;
        try {
            stackWalkerClass = Class.forName("java.lang.StackWalker");
        } catch (ClassNotFoundException e) {
            // Skip initialization when the class doesn't exist i.e. we are on JDK < 9
        }
        if (stackWalkerClass != null) {
            try {
                Class<?> optionClass = Class.forName("java.lang.StackWalker$Option");
            } catch (Throwable e) {
                throw new RuntimeException("cannot initialize", e);
            }
        }
    }

    private ReferencedUnsafe getReferencedUnsafe() {
        try {
            SecurityActions.TheUnsafe usf = SecurityActions.getSunMiscUnsafeAnonymously();
            List<Method> defineClassMethod = usf.methods.get("defineClass");
            // On Java 11+ the defineClass method does not exist anymore
            if (null == defineClassMethod)
                return null;
            MethodHandle meth = MethodHandles.lookup().unreflect(defineClassMethod.get(0));
            return new ReferencedUnsafe(usf, meth);
        } catch (Throwable e) {
            throw new RuntimeException("cannot initialize", e);
        }
    }

    @Override
    public Class<?> defineClass(String name, byte[] b, int off, int len, Class<?> neighbor,
                                ClassLoader loader, ProtectionDomain protectionDomain)
            throws ClassFormatError {
        return sunMiscUnsafe.defineClass(name, b, off, len, loader,
                protectionDomain);
    }
}
