package com.hwxiasn.contentserver.facade;

import java.io.Serializable;

import com.sohu.wap.cms.content.annotation.Local;
import com.sohu.wap.cms.content.annotation.Remote;

@Local(implementClass="com.hwxiasn.contentserver.impl.BaseDaoImpl")
@Remote(serviceName="common-content")
public interface BaseDao {
	<T> T findOne(Class<T> clazz, Serializable ID);
}
