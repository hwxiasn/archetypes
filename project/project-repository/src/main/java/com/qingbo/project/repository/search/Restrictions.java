package com.qingbo.project.repository.search;

import java.util.Collection;

import org.springframework.util.StringUtils;

import com.qingbo.project.repository.search.Criterion.Operator;

/** 
 * 条件构造器 
 * 用于创建条件表达式 
 * @Class Name Restrictions 
 * @Author lee 
 */  
public class Restrictions {  

    /** 
     * 等于 
     * @param fieldName 
     * @param value 
     * @param ignoreNull 
     * @return 
     */  
    public static SimpleExpression eq(String fieldName, Object value) {  
        if(StringUtils.isEmpty(value))return null;  
        return new SimpleExpression (fieldName, value, Operator.EQ);  
    }  
      
    /** 
     * 不等于 
     * @param fieldName 
     * @param value 
     * @param ignoreNull 
     * @return 
     */  
    public static SimpleExpression ne(String fieldName, Object value) {  
        if(StringUtils.isEmpty(value))return null;  
        return new SimpleExpression (fieldName, value, Operator.NE);  
    }  

    /** 
     * 模糊匹配 
     * @param fieldName 
     * @param value 
     * @param ignoreNull 
     * @return 
     */  
    public static SimpleExpression like(String fieldName, String value) {  
        if(StringUtils.isEmpty(value))return null;  
        return new SimpleExpression (fieldName, value, Operator.LIKE);  
    }  

    /** 
     * 大于 
     * @param fieldName 
     * @param value 
     * @param ignoreNull 
     * @return 
     */  
    public static SimpleExpression gt(String fieldName, Object value) {  
        if(StringUtils.isEmpty(value))return null;  
        return new SimpleExpression (fieldName, value, Operator.GT);  
    }  

    /** 
     * 小于 
     * @param fieldName 
     * @param value 
     * @param ignoreNull 
     * @return 
     */  
    public static SimpleExpression lt(String fieldName, Object value) {  
        if(StringUtils.isEmpty(value))return null;  
        return new SimpleExpression (fieldName, value, Operator.LT);  
    }  

    /** 
     * 大于等于 
     * @param fieldName 
     * @param value 
     * @param ignoreNull 
     * @return 
     */  
    public static SimpleExpression lte(String fieldName, Object value) {  
        if(StringUtils.isEmpty(value))return null;  
        return new SimpleExpression (fieldName, value, Operator.GTE);  
    }  

    /** 
     * 小于等于 
     * @param fieldName 
     * @param value 
     * @param ignoreNull 
     * @return 
     */  
    public static SimpleExpression gte(String fieldName, Object value) {  
        if(StringUtils.isEmpty(value))return null;  
        return new SimpleExpression (fieldName, value, Operator.LTE);  
    }  

    /** 
     * 或者 
     * @param criterions 
     * @return 
     */  
    public static LogicalExpression or(Criterion... criterions){  
        return new LogicalExpression(criterions, Operator.OR);  
    }  
    /** 
     * 包含于 
     * @param fieldName 
     * @param value 
     * @return 
     */  
    @SuppressWarnings("rawtypes")  
    public static InExpression in(String fieldName, Collection value) {  
        return new InExpression(fieldName, value);  
    }  
    
    @SuppressWarnings("rawtypes")  
    public static InExpression notIn(String fieldName, Collection value) {  
    	return new InExpression(fieldName, value, true);  
    }  
    
    /**
     * 区间between查询，支持一边为空
     */
    public static BetweenExpression between(String fieldName, Object value, Object value2) {
    	if(StringUtils.isEmpty(value) && StringUtils.isEmpty(value2)) return null;
    	return new BetweenExpression(fieldName, value, value2);
    }
}  