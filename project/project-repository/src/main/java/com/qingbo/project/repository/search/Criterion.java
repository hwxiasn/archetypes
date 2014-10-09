package com.qingbo.project.repository.search;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/** 
 * 条件接口 
 * 用户提供条件表达式接口 
 * @Class Name Criterion 
 * @Author lee 
 * @Create In 2012-2-8 
 */  
public interface Criterion {  
    public static enum Operator {  
        EQ, NE, LIKE, NOTLIKE, GT, LT, GTE, LTE, OR  
    }  
    /**
     * @param query not used
     */
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder);  
    public String getFieldName();
    public Path<?> getExpression(Root<?> root, String fieldName);
} 