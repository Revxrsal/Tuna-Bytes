package io.tunabytes.bytecode.introspect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@AllArgsConstructor
public class MixinInfo {

    private final String mixinName, mixinInternalName;
    private final boolean mixinInterface;
    private final List<MixinField> fields;
    private final List<MixinMethod> methods;

}
