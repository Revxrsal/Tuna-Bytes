package io.tunabytes.bytecode;

import io.tunabytes.bytecode.editor.*;
import io.tunabytes.bytecode.introspect.MixinClassVisitor;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.classloader.DefineClassHelper;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A class for applying changes from mixins to actual classes.
 *
 * @see #init(boolean)
 * @see #init()
 */
public final class MixinsBootstrap {

    private MixinsBootstrap() {}

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
        List<MixinsEditor> editors = new ArrayList<>();
        editors.add(new DefinalizeEditor());
        editors.add(new OverwriteEditor());
        editors.add(new AccessorEditor());
        editors.add(new InjectionEditor());
        editors.add(new MethodMergerEditor());
        MixinsConfig config = new MixinsConfig();
        Map<String, Entry<ClassWriter, ClassNode>> writers = new HashMap<>();
        for (MixinEntry mixinEntry : config.getMixinEntries()) {
            ClassReader reader = mixinEntry.mixinReader();
            MixinClassVisitor visitor = new MixinClassVisitor();
            reader.accept(visitor, ClassReader.SKIP_FRAMES);
            MixinInfo info = visitor.getInfo();
            ClassReader targetReader = mixinEntry.targetReader();

            ClassNode targetNode;
            Entry<ClassWriter, ClassNode> writerEntry = writers.get(mixinEntry.getTargetClass());
            if (writerEntry == null) {
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                targetNode = new ClassNode();
                targetReader.accept(targetNode, ClassReader.SKIP_FRAMES);
                writers.put(mixinEntry.getTargetClass(), new SimpleEntry<>(writer, targetNode));
            } else {
                targetNode = writerEntry.getValue();
            }
            for (MixinsEditor editor : editors) {
                editor.edit(targetNode, info);
            }
        }
        for (Entry<String, Entry<ClassWriter, ClassNode>> writerEntry : writers.entrySet()) {
            Entry<ClassWriter, ClassNode> be = writerEntry.getValue();
            be.getValue().accept(be.getKey());
            try {
                try {
                    DefineClassHelper.toClass(writerEntry.getKey(),
                            null, Thread.currentThread().getContextClassLoader(), null, be.getKey().toByteArray());
                } catch (Throwable throwable) {
                    Class<?> neighbor = config.getNeighbor(writerEntry.getKey());
                    DefineClassHelper.toClass(neighbor, be.getKey().toByteArray());
                }
            } catch (Throwable throwable) {
                if (!ignoreLoadedClasses)
                    throw new IllegalStateException("Unable to load mixin modifications for class " + writerEntry.getKey() + ". Has the class been loaded?", throwable);
            }
        }
    }

}
