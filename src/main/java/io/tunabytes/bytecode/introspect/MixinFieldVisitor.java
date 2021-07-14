package io.tunabytes.bytecode.introspect;

import io.tunabytes.Definalize;
import io.tunabytes.FieldMirror;
import org.objectweb.asm.*;

public class MixinFieldVisitor extends FieldVisitor {

    private static final Type MIRROR = Type.getType(FieldMirror.class);
    private static final Type DEFINALIZE = Type.getType(Definalize.class);

    protected boolean mirror, definalize;
    protected String name;

    public MixinFieldVisitor() {
        super(Opcodes.ASM8);
    }

    public MixinFieldVisitor(FieldVisitor fieldVisitor) {
        super(Opcodes.ASM8, fieldVisitor);
    }

    @Override public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        boolean mirrorAnn = MIRROR.getDescriptor().equals(descriptor);
        if (mirrorAnn)
            mirror = true;
        if (DEFINALIZE.getDescriptor().equals(descriptor))
            definalize = true;
        return new AnnotationVisitor(Opcodes.ASM8) {
            @Override public void visit(String name, Object value) {
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
