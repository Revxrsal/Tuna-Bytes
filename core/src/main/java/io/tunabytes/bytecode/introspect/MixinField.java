package io.tunabytes.bytecode.introspect;

import org.objectweb.asm.tree.FieldNode;


public final class MixinField {

	
	private final int access;
    private final boolean mirror;
    private final boolean definalize;
    private final String name, desc;
    private final boolean remapped;
    private final String type;
    private final FieldNode node;
    public MixinField(int access2, boolean mirror2, boolean definalize2, Object object, String desc2, boolean remapped2,
			String descriptor, FieldNode fv) {
		//TODO Auto-generated constructor stub
	access = access2;
	mirror = mirror2;
	definalize=definalize2;
	name=(String)object;
	//name="";
	desc=desc2;
	remapped = remapped2;
	type = descriptor;
    node=fv;
    
    }
	public boolean isDefinalize() {
		// TODO Auto-generated method stub
		return definalize;
	}
	public boolean isMirror() {
		// TODO Auto-generated method stub
		return mirror;
	}
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	public Object getType() {
		// TODO Auto-generated method stub
		return type;
	}
	public String getDesc() {
		// TODO Auto-generated method stub
		return desc;
	}
	public FieldNode getNode() {
		// TODO Auto-generated method stub
		return node;
	}


}
