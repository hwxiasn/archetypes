package com.hwxiasn.contentserver.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.hwxiasn.contentserver.facade.JdbcDao;

public class JdbcDaoImpl implements JdbcDao {
	private SessionFactory sessionFactory = SpringContext.getBean(SessionFactory.class);
	private List<?> EMPTY_LIST = new ArrayList<>();
	private Object[] EMPTY_ARRAY = new Object[0];

	public int count(String sql) {
		List<?> list = list(sql);
		if(list!=null && list.size()>0) {
			Object obj = list.get(0);
			try {
				return Integer.parseInt(obj.toString());
			}catch (Exception e) {
				return 0;
			}
		}
		return 0;
	}

	public List<?> list(String sql) {
		Session session = sessionFactory.openSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		List<?> list = sqlQuery.list();
		session.disconnect();
		return list != null ? list : EMPTY_LIST;
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
		return EMPTY_ARRAY;
	}
}
