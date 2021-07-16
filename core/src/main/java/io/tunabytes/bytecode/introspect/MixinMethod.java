package io.tunabytes.bytecode.introspect;

import io.tunabytes.Inject.At;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public final class MixinMethod {

    private final String name;
    private final int access;
    private final Type descriptor;
    private final int injectLine;
    private final String injectMethod;
    private final At injectAt;
    private final boolean overwrite, accessor, inject, mirror, definalize;
    private final String mirrorName;
    private final String overwrittenName; // or accessed method
    private final String accessedProperty; // or accessed method
    private final MethodNode methodNode;
    private final CallType type;

    public enum CallType {
        INVOKE,
        GET,
        SET
    }

    public boolean isPrivate() {
        return (access & Opcodes.ACC_PRIVATE) != 0;
    }

}
