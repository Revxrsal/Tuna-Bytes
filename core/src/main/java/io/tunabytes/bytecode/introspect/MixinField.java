package io.tunabytes.bytecode.introspect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.objectweb.asm.tree.FieldNode;

@Getter
@AllArgsConstructor
@ToString
public final class MixinField {

    private final int access;
    private final boolean mirror;
    private final boolean definalize;
    private final String name, desc;
    private final boolean remapped;
    private final String type;
    private final FieldNode node;

}
