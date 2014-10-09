package com.qingbo.project.repository.extend.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.qingbo.project.repository.extend.TongjiRepository;
import com.qingbo.project.repository.search.JpqlBuilder;
import com.qingbo.project.repository.search.SqlBuilder;

public class TongjiReposityImpl implements TongjiRepository {
	@PersistenceContext private EntityManager entityManager;

	public CriteriaBuilder builder() {
		return entityManager.getCriteriaBuilder();
	}

	public long count(CriteriaQuery<Long> query) {
		TypedQuery<Long> typedQuery = entityManager.createQuery(query);
		return typedQuery.getSingleResult();
	}

	public Object[] sums(CriteriaQuery<Tuple> query) {
		List<Tuple> list = list(query, 0, 0);
		return list!=null && list.size()>0 ? list.get(0).toArray() : null;
	}

	public List<Tuple> list(CriteriaQuery<Tuple> query, int firstResult, int maxResults) {
		TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult(firstResult);
		if(maxResults>0) typedQuery.setMaxResults(maxResults);
		return typedQuery.getResultList();
	}

	public int count(JpqlBuilder jpqlBuilder) {
		String jpql = jpqlBuilder.jpql();
		Object[] args = jpqlBuilder.args();
		Query query = entityManager.createQuery(jpql);
		if(args!=null && args.length>0) {
			for(int i=0; i<args.length; i++) {
				query.setParameter(i+1, args[i]);
			}
		}
		return ((Number)query.getSingleResult()).intValue();
	}

	public Object[] sums(JpqlBuilder jpqlBuilder) {
		List<?> list = list(jpqlBuilder);
		if(list!=null && list.size()>0) {
			Object obj = list.get(0);
			if(obj instanceof Object[]) {
				return (Object[])obj;
			}else {
				return new Object[] {obj};
			}
		}
		return null;
	}

	public List<?> list(JpqlBuilder jpqlBuilder) {
		String jpql = jpqlBuilder.jpql();
		Object[] args = jpqlBuilder.args();
		Query query = entityManager.createQuery(jpql);
		if(args!=null && args.length>0) {
			for(int i=0; i<args.length; i++) {
				query.setParameter(i+1, args[i]);
			}
		}
		return query.getResultList();
	}

	public int count(SqlBuilder sqlBuilder) {
		String sql = sqlBuilder.sql();
		Query query = entityManager.createNativeQuery(sql);
		return ((Number)query.getSingleResult()).intValue();
	}

	public Object[] sums(SqlBuilder sqlBuilder) {
		List<?> list = list(sqlBuilder);
		if(list!=null && list.size()>0) {
			Object obj = list.get(0);
			if(obj instanceof Object[]) {
				return (Object[])obj;
			}else {
				return new Object[] {obj};
			}
		}
		return null;
	}

	public List<?> list(SqlBuilder sqlBuilder) {
		String sql = sqlBuilder.sql();
		Query query = entityManager.createNativeQuery(sql);
		return query.getResultList();
	}

}
