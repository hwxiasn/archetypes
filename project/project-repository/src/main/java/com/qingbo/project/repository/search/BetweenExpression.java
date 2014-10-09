package com.qingbo.project.repository.search;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.util.StringUtils;

/**
 * 区间between查询，支持一边为空
 */
public class BetweenExpression extends AbstractExpression {
    private Object value;           //对应值  
    private Object value2;
	protected BetweenExpression(String fieldName, Object value, Object value2) {
		this.fieldName = fieldName;
		this.value = value;
		this.value2 = value2;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		Path expression = getExpression(root, fieldName);
		if(StringUtils.isEmpty(value) && StringUtils.isEmpty(value2)) {
			return null;
		}else if(StringUtils.isEmpty(value)) {
			return builder.greaterThanOrEqualTo(expression, (Comparable)value);
		}else if(StringUtils.isEmpty(value2)) {
			return builder.lessThanOrEqualTo(expression, (Comparable)value2);
		}else {
			return builder.between(expression, (Comparable)value, (Comparable)value2);
		}
	}
}
