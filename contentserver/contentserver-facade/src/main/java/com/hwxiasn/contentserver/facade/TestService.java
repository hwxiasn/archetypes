package com.hwxiasn.contentserver.facade;

import com.sohu.wap.cms.content.annotation.Local;
import com.sohu.wap.cms.content.annotation.Remote;

@Remote(serviceName="common-content")
@Local(implementClass="com.hwxiasn.contentserver.impl.TestServiceImpl")
public interface TestService {
	String echo(String msg);
}
