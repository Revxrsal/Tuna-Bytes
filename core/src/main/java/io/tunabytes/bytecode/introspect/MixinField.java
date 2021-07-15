package io.tunabytes.bytecode.introspect;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public final class MixinField {

    private final int access;
    private final boolean mirror;
    private final boolean definalize;
    private final String name;
    private final Type type;
    private final FieldNode node;

}
