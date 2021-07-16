package io.tunabytes.bytecode.introspect;

import io.tunabytes.*;
import io.tunabytes.Inject.At;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import io.tunabytes.bytecode.introspect.MixinMethod.CallType;

public class MixinMethodVisitor extends MethodVisitor {

    private static final Type OVERWRITE = Type.getType(Overwrite.class);
    private static final Type INJECT = Type.getType(Inject.class);
    private static final Type ACCESSOR = Type.getType(Accessor.class);
    private static final Type MIRROR = Type.getType(Mirror.class);
    private static final Type DEFINALIZE = Type.getType(Definalize.class);

    protected MethodNode node;
    protected String mirrorName;
    protected boolean overwrite, inject, accessor, mirror, definalize;
    protected String overwrittenName;
    protected String injectMethodName;
    protected String accessorName;
    protected int injectLine;
    protected At injectAt;
    protected CallType type = CallType.INVOKE;

    public MixinMethodVisitor(MethodNode node) {
        super(Opcodes.ASM8, node);
        this.node = node;
    }

    @Override public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        boolean visitingAccessor = ACCESSOR.getDescriptor().equals(descriptor);
        boolean visitingOverwrite = OVERWRITE.getDescriptor().equals(descriptor);
        boolean visitingInject = INJECT.getDescriptor().equals(descriptor);
        boolean visitingMirror = MIRROR.getDescriptor().equals(descriptor);
        if (visitingOverwrite)
            overwrite = true;
        if (visitingInject)
            inject = true;
        if (visitingAccessor)
            accessor = true;
        if (visitingMirror)
            mirror = true;
        if (DEFINALIZE.getDescriptor().equals(descriptor))
            definalize = true;
        return new AnnotationVisitor(Opcodes.ASM8, super.visitAnnotation(descriptor, visible)) {
            @Override public void visit(String name, Object value) {
                super.visit(name, value);
                if (visitingAccessor && name.equals("value")) {
                    accessorName = (String) value;
                }
                if (visitingOverwrite && name.equals("value")) {
                    overwrittenName = (String) value;
                }
                if (visitingMirror && name.equals("value")) {
                    mirrorName = (String) value;
                }
                if (visitingInject) {
                    switch (name) {
                        case "method": {
                            injectMethodName = (String) value;
                            break;
                        }
                        case "lineNumber": {
                            injectLine = (int) value;
                            break;
                        }
                    }
                }
            }

            @Override public void visitEnum(String name, String descriptor, String value) {
                super.visitEnum(name, descriptor, value);
                if (visitingInject && name.equals("at")) {
                    injectAt = At.at(value);
                }
            }
        };
    }

    protected String getActualName(String accessorName) {
        if (accessorName.startsWith("get")) {
            type = CallType.GET;
            return normalize("get", accessorName);
        }
        if (accessorName.startsWith("set")) {
            type = CallType.SET;
            return normalize("set", accessorName);
        }
        if (accessorName.startsWith("is")) {
            type = CallType.GET;
            return normalize("is", accessorName);
        }
        if (accessorName.startsWith("call")) {
            type = CallType.INVOKE;
            return normalize("call", accessorName);
        }
        if (accessorName.startsWith("invoke")) {
            type = CallType.INVOKE;
            return normalize("invoke", accessorName);
        }
        return accessorName;
    }

    private static String normalize(String prefix, String value) {
        if (value.length() > prefix.length()) {
            return Character.toLowerCase(value.charAt(prefix.length())) + value.substring(prefix.length() + 1);
        }
        return value;
    }

}
