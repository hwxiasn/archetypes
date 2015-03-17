package com.qingbo.ginkgo.ygb.base.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.qingbo.ginkgo.ygb.base.repository.TongjiRepository;

@Repository
public class TongjiReposityImpl implements TongjiRepository {
	@PersistenceContext private EntityManager entityManager;

	public int count(String sql) {
		Query query = entityManager.createNativeQuery(sql);
		return ((Number)query.getSingleResult()).intValue();
	}

	public Object[] sums(String sql) {
		List<?> list = list(sql);
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

	public List<?> list(String sql) {
		Query query = entityManager.createNativeQuery(sql);
		return query.getResultList();
	}
}
