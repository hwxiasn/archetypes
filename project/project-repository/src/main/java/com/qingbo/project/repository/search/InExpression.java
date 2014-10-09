package com.qingbo.project.repository.search;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@SuppressWarnings({"rawtypes","unchecked"})
public class InExpression extends AbstractExpression {
	private Collection values;
	private boolean notIn =false;

	protected InExpression(String fieldName, Collection values) {
		this.fieldName = fieldName;
		this.values = values;
	}
	
	protected InExpression(String fieldName, Collection values, boolean notIn) {
		this.fieldName = fieldName;
		this.values = values;
		this.notIn = notIn;
	}
	
	public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		Path path=getExpression(root, fieldName);
		Predicate in = path.in(values);
		return notIn ? in.not() : in;
	}
}
