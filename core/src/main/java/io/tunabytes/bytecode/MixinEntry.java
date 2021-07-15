package io.tunabytes.bytecode;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

final class MixinEntry {

    private final String mixinClass;
    private final String targetClass;

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
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(mixinClass.replace('.', '/') + ".class");) {
            Objects.requireNonNull(stream, "Class not found: " + mixinClass);
            return new ClassReader(stream);
        } catch (IOException e) {
            throw new ReadClassException(mixinClass, e);
        }
    }

    public ClassReader targetReader() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(targetClass.replace('.', '/') + ".class");) {
            Objects.requireNonNull(stream, "Class not found: " + targetClass);
            return new ClassReader(stream);
        } catch (IOException e) {
            throw new ReadClassException(targetClass, e);
        }
    }

}
