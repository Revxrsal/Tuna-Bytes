package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinInfo;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * Represents a transformer for editing class nodes.
 */
public interface MixinsEditor extends Opcodes {

    /**
     * Edits the class node as needed.
     *
     * @param node Class node to edit.
     * @param info Information about the mixin being transformed
     */
    void edit(ClassNode node, MixinInfo info);

    /**
     * Applies simple changes to methods and fields instructions to make sure they
     * have correct references
     *
     * @param classNode   Class node to remap to
     * @param info        Mixins information
     * @param instruction The instruction to remap
     */
    default void remapInstruction(ClassNode classNode, MixinInfo info, AbstractInsnNode instruction) {
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
