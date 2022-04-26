package io.tunabytes.bytecode.introspect;

import java.util.List;


public class MixinInfo {

	
	private final String mixinName, mixinInternalName;
    private final boolean mixinInterface;
    private final List<MixinField> fields;
    private final List<MixinMethod> methods;
    public MixinInfo(String name, String replace, boolean isInterface, List<MixinField> fields2,
			List<MixinMethod> methods2) {
		//TODO Auto-generated constructor stub
    	mixinName=name;
    	mixinInternalName=replace;
    	mixinInterface=isInterface;
    	fields=fields2;
    	methods=methods2;
    }
	public boolean isMixinInterface() {
		// TODO Auto-generated method stub
		return mixinInterface;
	}
	public String getMixinInternalName() {
		// TODO Auto-generated method stub
		return mixinInternalName;
	}
	public List<MixinMethod> getMethods() {
		// TODO Auto-generated method stub
		return methods;
	}
	public String getMixinName() {
		// TODO Auto-generated method stub
		return mixinName;
	}
	public List<MixinField> getFields() {
		// TODO Auto-generated method stub
		return fields;
	}


}
