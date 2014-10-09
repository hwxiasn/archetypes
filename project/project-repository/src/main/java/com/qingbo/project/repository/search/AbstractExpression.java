package com.qingbo.project.repository.search;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;


public abstract class AbstractExpression implements Criterion {
	protected String fieldName;
	
	public Path<?> getExpression(Root<?> root, String fieldName) {
		Path<?> expression = null;  
        if(fieldName.contains(".")){  
            String[] names = fieldName.split("[\\.]");  
            expression = root.get(names[0]);  
            for (int i = 1; i < names.length; i++) {  
                expression = expression.get(names[i]);  
            }  
        }else{  
            expression = root.get(fieldName);  
        }
        return expression;
	}

	public String getFieldName() {
		return fieldName;
	}
}
