package io.tunabytes.bytecode.editor;

import io.tunabytes.Inject.At;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mixins editor for processing {@link io.tunabytes.Inject} methods.
 */
public class InjectionEditor implements MixinsEditor {

    private static final List<Integer> RETURN_OPCODES = Arrays.asList(
            RETURN,
            ARETURN,
            IRETURN,
            DRETURN,
            FRETURN,
            LRETURN
    );

    @SneakyThrows @Override public void edit(ClassNode classNode, MixinInfo info) {
        for (MixinMethod method : info.getMethods()) {
            if (!method.isInject()) continue;
            if ((method.getMethodNode().access & ACC_ABSTRACT) != 0) {
                throw new IllegalArgumentException("@Inject cannot be used on abstract methods! (" + method.getMethodNode().name + " in " + info.getMixinName() + ")");
            }
            At at = method.getInjectAt();
            int line = method.getInjectLine();
            String injectIn = method.getInjectMethod();
            MethodNode targetMethod = classNode.methods.stream().filter(c -> c.name.equals(injectIn))
                    .findFirst().orElseThrow(() -> new NoSuchMethodException(injectIn));
            InsnList list = method.getMethodNode().instructions;
            for (AbstractInsnNode instruction : list) {
                remapInstruction(classNode, info, instruction);
            }

            AbstractInsnNode lastInjectedReturn = null;
            for (AbstractInsnNode abstractInsnNode : list) {
                if (abstractInsnNode instanceof LineNumberNode) {
                    list.remove(abstractInsnNode);
                } else if (RETURN_OPCODES.contains(abstractInsnNode.getOpcode())) {
                    lastInjectedReturn = abstractInsnNode;
                }
            }

            if (lastInjectedReturn != null) list.remove(lastInjectedReturn);

            if (at == At.BEGINNING) {
                AbstractInsnNode first = targetMethod.instructions.getFirst();
                if (first != null) {
                    targetMethod.instructions.insert(first, list);
                } else
                    targetMethod.instructions.add(list);
            } else if (at == At.END) {
                AbstractInsnNode lastReturn = null;
                for (AbstractInsnNode instruction : targetMethod.instructions) {
                    if (instruction instanceof InsnNode && RETURN_OPCODES.contains(instruction.getOpcode()))
                        lastReturn = instruction;
                }
                targetMethod.instructions.insertBefore(lastReturn, list);
            } else if (at == At.BEFORE_EACH_RETURN) {
                for (AbstractInsnNode insnNode : targetMethod.instructions) {
                    if (RETURN_OPCODES.contains(insnNode.getOpcode())) {
                        targetMethod.instructions.insertBefore(insnNode, cloneInsnList(list));
                    }
                }
            } else if (at == At.BEFORE_LINE) {
                for (AbstractInsnNode insnNode : targetMethod.instructions) {
                    if (!(insnNode instanceof LineNumberNode)) continue;
                    int currentLine = ((LineNumberNode) insnNode).line;
                    if (currentLine == line)
                        targetMethod.instructions.insertBefore(insnNode, list);
                }
            } else if (at == At.AFTER_LINE) {
                for (AbstractInsnNode insnNode : targetMethod.instructions) {
                    if (!(insnNode instanceof LineNumberNode)) continue;
                    int currentLine = ((LineNumberNode) insnNode).line;
                    if (currentLine == line)
                        targetMethod.instructions.insert(insnNode, list);
                }
            }
        }
    }

    private static Map<LabelNode, LabelNode> cloneLabels(InsnList insns) {
        Map<LabelNode, LabelNode> labelMap = new HashMap<>();
        for (AbstractInsnNode insn = insns.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn.getType() == 8) {
                labelMap.put((LabelNode) insn, new LabelNode());
            }
        }
        return labelMap;
    }

    private static InsnList cloneInsnList(InsnList insns) {
        return cloneInsnList(cloneLabels(insns), insns);
    }

    private static InsnList cloneInsnList(Map<LabelNode, LabelNode> labelMap, InsnList insns) {
        InsnList clone = new InsnList();
        for (AbstractInsnNode insn = insns.getFirst(); insn != null; insn = insn.getNext()) {
            clone.add(insn.clone(labelMap));
        }

        return clone;
    }
}
