package io.tunabytes.bytecode;

import io.tunabytes.bytecode.editor.*;
import io.tunabytes.bytecode.introspect.MixinClassVisitor;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.classloader.TunaClassDefiner;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;
import java.util.Map.Entry;

/**
 * A class for applying changes from mixins to actual classes.
 *
 * @see #init(boolean)
 * @see #init()
 */
public final class MixinsBootstrap {

    private MixinsBootstrap() { }

    /**
     * Initializes and applies mixins, and throws an exception on each loaded class.
     */
    public static void init() {
        init(false);
    }

    /**
     * Initializes and applies mixins
     *
     * @param ignoreLoadedClasses Whether should we ignore any class that has been alreade loaded.
     *                            If false, an {@link IllegalStateException} will be thrown if
     *                            a class appears to be loaded.
     */
    @SneakyThrows public static void init(boolean ignoreLoadedClasses) {
        init(ignoreLoadedClasses, Collections.emptyList());
    }

    /**
     * Initializes and applies mixins
     *
     * @param ignoreLoadedClasses Whether should we ignore any class that has been alreade loaded.
     *                            If false, an {@link IllegalStateException} will be thrown if
     *                            a class appears to be loaded.
     * @param searchClassLoaders  A list of additional classloaders to search classes for.
     */
    public static void init(boolean ignoreLoadedClasses, Collection<ClassLoader> searchClassLoaders) {
        Set<ClassLoader> classLoaders = new LinkedHashSet<>();
        classLoaders.add(Thread.currentThread().getContextClassLoader());
        classLoaders.addAll(searchClassLoaders);
        List<MixinsEditor> editors = new ArrayList<>();
        editors.add(new DefinalizeEditor());
        editors.add(new OverwriteEditor());
        editors.add(new AccessorEditor());
        editors.add(new InjectionEditor());
        editors.add(new MethodMergerEditor());
        MixinsConfig config = new MixinsConfig();
        Map<String, TargetedMixin> writers = new HashMap<>();
        for (MixinEntry mixinEntry : config.getMixinEntries()) {
            ClassReader reader = mixinEntry.mixinReader();
            MixinClassVisitor visitor = new MixinClassVisitor();
            reader.accept(visitor, ClassReader.SKIP_FRAMES);
            MixinInfo info = visitor.getInfo();
            ClassReader targetReader = mixinEntry.targetReader(classLoaders);

            ClassNode targetNode;
            TargetedMixin writerEntry = writers.get(mixinEntry.getTargetClass());
            if (writerEntry == null) {
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                targetNode = new ClassNode();
                targetReader.accept(targetNode, ClassReader.SKIP_FRAMES);
                writers.put(mixinEntry.getTargetClass(), new TargetedMixin(writer, mixinEntry.getClassLoader(), targetNode));
            } else {
                targetNode = writerEntry.node;
            }
            for (MixinsEditor editor : editors) {
                editor.edit(targetNode, info);
            }
        }
        for (Entry<String, TargetedMixin> writerEntry : writers.entrySet()) {
            TargetedMixin mixin = writerEntry.getValue();
            mixin.node.accept(mixin.writer);
            try {
                TunaClassDefiner.defineClass(
                        writerEntry.getKey(),
                        mixin.writer.toByteArray(),
                        config.getNeighbor(writerEntry.getKey()),
                        mixin.classLoader,
                        null
                );
            } catch (Throwable throwable) {
                if (!ignoreLoadedClasses) {
                    if (throwable.getClass() == LinkageError.class || Objects.equals(throwable.getCause().getClass(), LinkageError.class)) {
                        throw new IllegalStateException("Class " + writerEntry.getKey() + " has already been loaded.");
                    }
                    throw new IllegalStateException("Unable to load mixin modifications for class " + writerEntry.getKey(), throwable);
                }
            }
        }
    }

    @AllArgsConstructor
    private static class TargetedMixin {

        private final ClassWriter writer;
        private final ClassLoader classLoader;
        private final ClassNode node;
    }

}
