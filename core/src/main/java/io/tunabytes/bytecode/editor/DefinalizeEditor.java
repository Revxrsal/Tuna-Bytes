package io.tunabytes.bytecode.editor;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import io.tunabytes.bytecode.introspect.MixinField;
import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;

/**
 * A mixins editor for processing {@link io.tunabytes.Definalize} fields.
 */
public class DefinalizeEditor implements MixinsEditor {

    	 @Override public void edit(ClassNode node, MixinInfo info) {
        for (MixinField field : info.getFields()) {
            if (field.isDefinalize() && field.isMirror()) {
                FieldNode fnode;
				try {
					fnode = node.fields.stream().filter(c -> c.name.equals(field.getName())).findFirst()
					        .orElseThrow(() -> new NoSuchFieldException(field.getName()));
	                fnode.access &= ~Opcodes.ACC_FINAL;

				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        for (MixinMethod method : info.getMethods()) {
            if (method.isDefinalize() && method.isMirror()) {
                try {
					MethodNode mnode = node.methods.stream().filter(c -> c.name.equals(method.getName())).findFirst()
					        .orElseThrow(() -> new NoSuchFieldException(method.getName()));
					mnode.access &= ~Opcodes.ACC_FINAL;
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
}
