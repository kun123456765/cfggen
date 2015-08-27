package configgen.data;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import configgen.FlatStream;
import configgen.type.Config;
import configgen.type.Field;

public class FMap extends Type {
	public final Map<Type, Type> values = new LinkedHashMap<Type, Type>();

	public FMap(FStruct host, Field define, FlatStream is) {
		super(host, define);
		Field keyDefine = define.stripAdoreType();
		Field valueDefine = keyDefine.stripAdoreType();
		while(!is.isSectionEnd()) {
			final Type key = Type.create(host, keyDefine, is);
			if(values.put(key, Type.create(host, valueDefine, is)) != null) {
				throw new RuntimeException(String.format("field:%s key:%s dunplicate", define, key));
			}
		}
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Map<").append(define.getFullType()).append(">{");
		for(Map.Entry<Type, Type> e : values.entrySet()) {
			sb.append("<").append(e.getKey()).append(",").append(e.getValue()).append(">,");
		}
		sb.append("}");
		return sb.toString();
	}
	

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}
	
	@Override
	public void verifyData() {
		final String keyRef = define.getKeyRef();
		if(!keyRef.isEmpty()) {
			HashSet<Type> validValues = Config.getData(keyRef);
			for(Type d : values.keySet()) {
				if(!validValues.contains(d))
					System.out.println("field:" + define.getName() + " value:" + d + " can't find in index:" + keyRef);
			}
		}
		
		final String valueRef = define.getValueRef();
		if(!valueRef.isEmpty()) {
			HashSet<Type> validValues = Config.getData(valueRef);
			for(Type d : values.values()) {
				if(!validValues.contains(d))
					System.out.println("field:" + define.getName() + " value:" + d + " can't find in index:" + valueRef);
			}
		}
	}
	
}
