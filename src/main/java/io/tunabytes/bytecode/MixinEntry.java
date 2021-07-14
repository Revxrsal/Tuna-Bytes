package io.tunabytes.bytecode;

import org.objectweb.asm.ClassReader;

import java.io.IOException;

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
        try {
            return new ClassReader(mixinClass);
        } catch (IOException e) {
            throw new ReadClassException(mixinClass, e);
        }
    }

    public ClassReader targetReader() {
        try {
            return new ClassReader(targetClass);
        } catch (IOException e) {
            throw new ReadClassException(targetClass, e);
        }
    }

}
