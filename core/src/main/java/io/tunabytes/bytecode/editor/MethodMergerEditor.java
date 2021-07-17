package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * A mixins editor for copying methods from the mixins class to the target class.
 */
public class MethodMergerEditor implements MixinsEditor {

    @Override public void edit(ClassNode classNode, MixinInfo info) {
        for (MixinMethod method : info.getMethods()) {
            if (method.isOverwrite()) continue;
            if (method.isAccessor()) continue;
            if (method.isInject()) continue;
            if (method.isMirror()) continue;
            // inject no-args constructors into each constructor of the mixed class
            if (method.getName().equals("<init>")) {
                if (method.getDescriptor().getArgumentTypes().length == 0) {
                    InsnList list = method.getMethodNode().instructions;
                    for (AbstractInsnNode node : list) {
                        if (node instanceof LineNumberNode || RETURN_OPCODES.contains(node.getOpcode()))
                            list.remove(node);
                        else if (node.getOpcode() == Opcodes.INVOKESPECIAL) {
                            MethodInsnNode nm = (MethodInsnNode) node;
                            if (nm.name.equals("<init>") && nm.owner.equals("java/lang/Object"))
                                list.remove(nm);
                        } else
                            remapInstruction(classNode, info, node);
                    }
                    for (MethodNode c : classNode.methods) {
                        if (c.name.equals("<init>")) {
                            AbstractInsnNode lastReturn = null;
                            for (AbstractInsnNode n : c.instructions) {
                                if (RETURN_OPCODES.contains(n.getOpcode())) lastReturn = n;
                            }
                            c.instructions.insertBefore(lastReturn, InjectionEditor.cloneInsnList(list));
                        }
                    }
                }
                continue;
            }
            if (info.isMixinInterface() && (method.getMethodNode().access & ACC_ABSTRACT) != 0) continue;
            MethodNode mn = method.getMethodNode();
            MethodNode underlying = new MethodNode(mn.access, mn.name, mn.desc, mn.signature, mn.exceptions.toArray(new String[0]));
            underlying.instructions = new InsnList();
            underlying.instructions.add(mn.instructions);
            for (AbstractInsnNode instruction : underlying.instructions) {
                remapInstruction(classNode, info, instruction);
            }
            classNode.methods.add(underlying);
        }
    }
}
