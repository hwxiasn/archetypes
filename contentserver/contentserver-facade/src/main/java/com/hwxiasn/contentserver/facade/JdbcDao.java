package com.hwxiasn.contentserver.facade;

import java.util.List;

import com.sohu.wap.cms.content.annotation.Local;
import com.sohu.wap.cms.content.annotation.Remote;

@Local(implementClass="com.hwxiasn.contentserver.impl.JdbcDaoImpl")
@Remote(serviceName="common-content")
public interface JdbcDao {
	int count(String sql);
	List<?> list(String sql);
	Object[] sums(String sql);
}
