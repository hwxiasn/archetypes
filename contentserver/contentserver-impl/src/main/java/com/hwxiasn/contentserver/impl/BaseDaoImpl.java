package com.hwxiasn.contentserver.impl;

import java.io.Serializable;

import org.springframework.orm.hibernate4.HibernateTemplate;

import com.hwxiasn.contentserver.facade.BaseDao;

public class BaseDaoImpl implements BaseDao {
	private HibernateTemplate hibernateTemplate = SpringContext.getBean(HibernateTemplate.class);
	
	@Override
	public <T> T findOne(Class<T> clazz, Serializable id) {
		return hibernateTemplate.get(clazz, id);
	}

}
