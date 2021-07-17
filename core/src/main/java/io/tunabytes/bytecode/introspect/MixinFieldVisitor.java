package io.tunabytes.bytecode.introspect;

import io.tunabytes.ActualType;
import io.tunabytes.Definalize;
import io.tunabytes.Mirror;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.FieldNode;

public class MixinFieldVisitor extends FieldVisitor {

    private static final Type MIRROR = Type.getType(Mirror.class);
    private static final Type DEFINALIZE = Type.getType(Definalize.class);
    private static final String ACTUAL_TYPE = Type.getDescriptor(ActualType.class);

    protected boolean mirror, definalize, remapped;
    protected Type type;
    protected String name, desc;

    public MixinFieldVisitor(FieldNode node) {
        super(Opcodes.ASM8, node);
        desc = node.desc;
    }

    @Override public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        boolean mirrorAnn = MIRROR.getDescriptor().equals(descriptor);
        if (mirrorAnn)
            mirror = true;
        if (DEFINALIZE.getDescriptor().equals(descriptor))
            definalize = true;
        return new AnnotationVisitor(Opcodes.ASM8) {
            @Override public void visit(String name, Object value) {
                if (ACTUAL_TYPE.equals(descriptor)) {
                    remapped = true;
                    desc = MixinMethodVisitor.fromActualType(desc, (String) value).getDescriptor();
                }
                if (mirrorAnn && name.equals("value")) {
                    MixinFieldVisitor.this.name = (String) value;
                }
            }
        };
    }

    @Override public void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }

}
