package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinField;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.*;

/**
 * A mixins editor for processing {@link io.tunabytes.Overwrite} methods.
 */
public class OverwriteEditor implements MixinsEditor {

    @SneakyThrows @Override public void edit(ClassNode classNode, MixinInfo info) {
        for (MixinField field : info.getFields()) {
            if (field.isMirror()) continue;
            classNode.fields.add(field.getNode());
        }
        for (MixinMethod method : info.getMethods()) {
            if (method.isInject()) continue;
            if (method.isOverwrite()) {
                MethodNode node = method.getMethodNode();
                if ((node.access & ACC_ABSTRACT) != 0) {
                    throw new IllegalArgumentException("@Overwrite cannot be used on abstract methods! (" + node.name + " in " + info.getMixinName() + ")");
                }
                MethodNode underlying = classNode.methods.stream().filter(c -> c.name.equals(method.getOverwrittenName()) && c.desc.equals(node.desc))
                        .findFirst().orElseThrow(() -> new NoSuchMethodException(method.getOverwrittenName()));
                underlying.instructions = new InsnList();
                underlying.instructions.add(node.instructions);
                for (AbstractInsnNode instruction : underlying.instructions) {
                    if (instruction instanceof FieldInsnNode) {
                        FieldInsnNode insn = (FieldInsnNode) instruction;
                        if (insn.owner.equals(info.getMixinInternalName()))
                            insn.owner = classNode.name;
                    }
                    if (instruction instanceof MethodInsnNode) {
                        MethodInsnNode insn = (MethodInsnNode) instruction;
                        if (insn.getOpcode() == INVOKEINTERFACE && insn.itf && insn.owner.equals(info.getMixinInternalName())) {
                            insn.setOpcode(INVOKEVIRTUAL);
                            insn.itf = false;
                        }
                        if (insn.owner.equals(info.getMixinInternalName()))
                            insn.owner = classNode.name;
                    }
                }
            }
        }
    }
}