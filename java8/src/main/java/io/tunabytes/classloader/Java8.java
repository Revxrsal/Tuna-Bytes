package io.tunabytes.classloader;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;

final class Java8 implements ClassDefiner {

    private final Method defineClass = getDefineClassMethod();
    private final SecurityActions stack = SecurityActions.stack;

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

    private static RuntimeException sneakyThrow(Throwable t) {
        if (t == null) throw new NullPointerException("t");
        return sneakyThrow0(t);
    }

    private static <T extends Throwable> T sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }

}
