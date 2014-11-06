package com.qingbo.gingko.common.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("serial")
public class SpecParam<T> implements Serializable {
	List<Object[]> specs = new ArrayList<Object[]>();
	
	public SpecParam<T> eq(String fieldName, Object value) {
		if(!isEmpty(fieldName) && !isEmpty(value))
			specs.add(new Object[] {fieldName, "eq", value});
		return this;
	}
	
	public SpecParam<T> ne(String fieldName, Object value) {
		if(!isEmpty(value))
			specs.add(new Object[] {fieldName, "ne", value});
		return this;
	}
	
	public SpecParam<T> like(String fieldName, String value) {
		if(!isEmpty(value))
			specs.add(new Object[] {fieldName, "like", value});
		return this;
	}
	
	public SpecParam<T> between(String fieldName, Comparable<?> value, Comparable<?> value2) {
		if(!isEmpty(value) || !isEmpty(value2)) 
			specs.add(new Object[] {fieldName, "between", new Object[] {value, value2}});
		return this;
	}
	
	public SpecParam<T> in(String fieldName, Collection<?> value) {
		if(!isEmpty(value))
			specs.add(new Object[] {fieldName, "in", value});
		return this;
	}
	
	public SpecParam<T> notIn(String fieldName, Collection<?> value) {
		if(!isEmpty(value))
			specs.add(new Object[] {fieldName, "notIn", value});
		return this;
	}
	
    private static boolean isEmpty(Object value) {
    	if(value == null) return true;
    	if("".equals(value)) return true;
    	if(value instanceof Collection)
    		return ((Collection<?>)value).size()==0;
    	if(value instanceof String)
    		return ((String)value).trim().length()==0;
    	return false;
    }

	public List<Object[]> getSpecs() {
		return specs;
	}

	public void setSpecs(List<Object[]> specs) {
		this.specs = specs;
	}
}
