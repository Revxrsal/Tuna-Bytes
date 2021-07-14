package io.tunabytes.bytecode.editor;

import io.tunabytes.bytecode.introspect.MixinInfo;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

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

}
