package io.tunabytes.classloader;

import java.security.ProtectionDomain;

/**
 * A simple interface for providing cross-compatibility across Java versions to
 * define classes from their bytecode.
 */
public interface ClassDefiner {

    Class<?> defineClass(String name,
                         byte[] b,
                         int off,
                         int len,
                         Class<?> neighbor,
                         ClassLoader loader,
                         ProtectionDomain protectionDomain) throws ClassFormatError;
}
