package io.tunabytes.bytecode.editor;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import io.tunabytes.bytecode.introspect.MixinField;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;

/**
 * A mixins editor for processing {@link io.tunabytes.Overwrite} methods.
 */
public class OverwriteEditor implements MixinsEditor {

     @Override public void edit(ClassNode classNode, MixinInfo info) {
        for (MixinField field : info.getFields()) {
            if (field.isMirror()) continue;
            classNode.fields.add(field.getNode());
        }
        for (MixinMethod method : info.getMethods()) {
            if (method.isInject()) continue;
            if (method.isMirror()) continue;
            if (method.isOverwrite()) {
                MethodNode node = method.getMethodNode();
                if ((node.access & ACC_ABSTRACT) != 0) {
                    throw new IllegalArgumentException("@Overwrite cannot be used on abstract methods! (" + node.name + " in " + info.getMixinName() + ")");
                }
                try {
					MethodNode underlying = classNode.methods.stream().filter(c -> c.name.equals(method.getOverwrittenName()) && c.desc.equals(node.desc))
					        .findFirst().orElseThrow(() -> new NoSuchMethodException(method.getOverwrittenName()));
					underlying.instructions = new InsnList();
					underlying.instructions.add(node.instructions);
					for (AbstractInsnNode instruction : underlying.instructions) {
					    remapInstruction(classNode, info, instruction);
					}
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
}