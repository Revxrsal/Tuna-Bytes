package io.tunabytes.bytecode.introspect;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

public class MixinClassVisitor extends ClassVisitor {

    private final List<MixinField> fields = new ArrayList<>();
    private final List<MixinMethod> methods = new ArrayList<>();
    private boolean isInterface;
    private String name;
    private MixinInfo info;

    public MixinClassVisitor() {
        super(Opcodes.ASM8);
    }

    @Override public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if ((access & Opcodes.ACC_INTERFACE) != 0)
            isInterface = true;
        this.name = name.replace('/', '.');
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override public FieldVisitor visitField(int access, String fname, String descriptor, String signature, Object value) {
        return new MixinFieldVisitor(new FieldNode(access, fname, descriptor, signature, value)) {
            @Override public void visitEnd() {
                FieldNode node = (FieldNode) fv;
                node.desc = desc;
                        fields.add(new MixinField(access, mirror, definalize, name == null ? fname : name, desc, remapped, descriptor, (FieldNode) fv));
            }
        };
    }

    @Override public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new MixinMethodVisitor(new MethodNode(access, name, descriptor, signature, exceptions)) {
            @Override public void visitEnd() {
                Type desc = Type.getMethodType(returnType, argumentTypes);
                node.desc = desc.getDescriptor();
                methods.add(new MixinMethod(
                        name,
                        access,
                        desc,
                        descriptor,
                        injectLine,
                        injectMethodName,
                        injectAt,
                        overwrite,
                        accessor,
                        inject,
                        mirror,
                        definalize,
                        remap,
                        mirrorName == null ? name : mirrorName,
                        overwrittenName == null ? name : overwrittenName,
                        accessorName == null ? getActualName(name) : accessorName,
                        node,
                        type
                ));
            }
        };
    }

    @Override public void visitEnd() {
        info = new MixinInfo(name, name.replace('.', '/'), isInterface, fields, methods);
        super.visitEnd();
    }

    public MixinInfo getInfo() {
        return info;
    }
}
