package io.tunabytes.bytecode;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

final class MixinEntry {

    private final String mixinClass;
    private final String targetClass;
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public MixinEntry(String mixinClass, String targetClass) {
        this.mixinClass = mixinClass;
        this.targetClass = targetClass;
    }

    public String getMixinClass() {
        return mixinClass;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public ClassReader mixinReader() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(mixinClass.replace('.', '/') + ".class")) {
            if (stream != null) {
                return new ClassReader(stream);
            }
            throw new IllegalStateException("Class not found: " + mixinClass + ". Make sure you specify any additional classloaders in MixinsBootstrap.init(...)!");
        } catch (IOException e) {
            throw new ReadClassException(mixinClass, e);
        }
    }

    public ClassReader targetReader(Set<ClassLoader> classLoaders) {
        for (ClassLoader loader : classLoaders) {
            try (InputStream stream = loader.getResourceAsStream(targetClass.replace('.', '/') + ".class")) {
                if (stream != null) {
                    classLoader = loader;
                    return new ClassReader(stream);
                }
            } catch (IOException e) {
                throw new ReadClassException(targetClass, e);
            }
        }
        throw new IllegalStateException("Class not found: " + targetClass + ". Make sure you specify any additional classloaders in MixinsBootstrap.init(...)!");
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
