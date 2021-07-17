package io.tunabytes.bytecode.introspect;

import io.tunabytes.*;
import io.tunabytes.Inject.At;
import io.tunabytes.bytecode.introspect.MixinMethod.CallType;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class MixinMethodVisitor extends MethodVisitor {

    private static final String OVERWRITE = Type.getDescriptor(Overwrite.class);
    private static final String INJECT = Type.getDescriptor(Inject.class);
    private static final String ACCESSOR = Type.getDescriptor(Accessor.class);
    private static final String MIRROR = Type.getDescriptor(Mirror.class);
    private static final String DEFINALIZE = Type.getDescriptor(Definalize.class);
    private static final String ACTUAL_TYPE = Type.getDescriptor(ActualType.class);

    protected MethodNode node;
    protected String mirrorName;
    protected Type returnType;
    protected Type[] argumentTypes;
    protected boolean overwrite, inject, accessor, mirror, definalize, remap;
    protected String overwrittenName;
    protected String injectMethodName;
    protected String accessorName;
    protected int injectLine;
    protected At injectAt;
    protected CallType type = CallType.INVOKE;

    public MixinMethodVisitor(MethodNode node) {
        super(Opcodes.ASM8, node);
        this.node = node;
        Type desc = Type.getMethodType(node.desc);
        returnType = desc.getReturnType();
        argumentTypes = desc.getArgumentTypes();
    }

    @Override public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        boolean visitingAccessor = ACCESSOR.equals(descriptor);
        boolean visitingOverwrite = OVERWRITE.equals(descriptor);
        boolean visitingInject = INJECT.equals(descriptor);
        boolean visitingMirror = MIRROR.equals(descriptor);
        boolean visitingActualType = ACTUAL_TYPE.equals(descriptor);
        if (visitingOverwrite)
            overwrite = true;
        if (visitingInject)
            inject = true;
        if (visitingAccessor)
            accessor = true;
        if (visitingMirror)
            mirror = true;
        if (DEFINALIZE.equals(descriptor))
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
                if (visitingActualType && name.equals("value")) {
                    remap = true;
                    String rtype = returnType.getDescriptor();
                    returnType = fromActualType(rtype, (String) value);
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

    @Override public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        return new AnnotationVisitor(Opcodes.ASM8, super.visitParameterAnnotation(parameter, descriptor, visible)) {
            @Override public void visit(String name, Object value) {
                remap = true;
                if (ACTUAL_TYPE.equals(descriptor))
                    argumentTypes[parameter] = fromActualType(argumentTypes[parameter].getDescriptor(), (String) value);
                super.visit(name, value);
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

    public static Type fromActualType(String descriptor, String actualType) {
        String arrayAddition = "";
        if (descriptor.startsWith("["))
            arrayAddition = descriptor.substring(0, descriptor.lastIndexOf('[') + 1);
        return Type.getType(arrayAddition + "L" + actualType.replace('.', '/') + ";");
    }

}
