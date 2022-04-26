package io.tunabytes.bytecode.introspect;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import io.tunabytes.Inject.At;


public final class MixinMethod {

    private final String name;
    private final int access;
    private final Type descriptor;
    private final String realDescriptor;
    private final int injectLine;
    private final String injectMethod;
    private final At injectAt;
    private final boolean overwrite, accessor, inject, mirror, definalize, requireTypeRemapping;
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

    public MixinMethod(String name2, int access2, Type desc, String descriptor2, int injectLine2,
			String injectMethodName, At injectAt2, boolean overwrite2, boolean accessor2, boolean inject2,
			boolean mirror2, boolean definalize2, boolean remap, Object object, Object object2, Object object3,
			MethodNode node, CallType type2) {
		//TODO Auto-generated constructor stub
	name=name2;
	access = access2;
    descriptor=desc;
    realDescriptor=descriptor2;
    injectLine = injectLine2;
    injectMethod=injectMethodName;
    injectAt=injectAt2;
    overwrite=overwrite2;
    accessor = accessor2;
    inject=inject2;
    mirror=mirror2;
    definalize=definalize2;
    requireTypeRemapping=remap;
    mirrorName=(String)object;
    overwrittenName=(String)object2;
    accessedProperty = (String)object3;
    methodNode=node;
    type=type2;
    
    }

	public boolean isPrivate() {
        return (access & Opcodes.ACC_PRIVATE) != 0;
    }

	public boolean isAccessor() {
		// TODO Auto-generated method stub
		return accessor;
	}

	public MethodNode getMethodNode() {
		// TODO Auto-generated method stub
		return methodNode;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	public Type getDescriptor() {
		// TODO Auto-generated method stub
		return descriptor;
	}

	public CallType getType() {
		// TODO Auto-generated method stub
		return type;
	}

	public String getAccessedProperty() {
		// TODO Auto-generated method stub
		return accessedProperty;
	}

	public boolean isDefinalize() {
		// TODO Auto-generated method stub
		return definalize;
	}

	public boolean isMirror() {
		// TODO Auto-generated method stub
		return mirror;
	}

	public boolean isInject() {
		// TODO Auto-generated method stub
		return inject;
	}

	public At getInjectAt() {
		// TODO Auto-generated method stub
		return injectAt;
	}

	public int getInjectLine() {
		// TODO Auto-generated method stub
		return injectLine;
	}

	public String getInjectMethod() {
		// TODO Auto-generated method stub
		return injectMethod;
	}

	public boolean isOverwrite() {
		// TODO Auto-generated method stub
		return overwrite;
	}

	public Object getRealDescriptor() {
		// TODO Auto-generated method stub
		return realDescriptor;
	}

	public String getOverwrittenName() {
		// TODO Auto-generated method stub
		return overwrittenName;
	}

}
