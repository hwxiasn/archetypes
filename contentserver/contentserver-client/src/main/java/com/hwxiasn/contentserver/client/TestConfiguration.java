package com.hwxiasn.contentserver.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hwxiasn.contentserver.facade.BaseDao;
import com.hwxiasn.contentserver.facade.JdbcDao;
import com.hwxiasn.contentserver.facade.TestService;
import com.sohu.wap.cms.content.Manager;

@Configuration
public class TestConfiguration {
	private Manager manager = Manager.getInstance();
	@Bean
	public TestService testService() {
		return manager.createInstance(TestService.class);
	}
	@Bean
	public BaseDao baseDao() {
		return manager.createInstance(BaseDao.class);
	}
	@Bean
	public JdbcDao jdbcDao() {
		return manager.createInstance(JdbcDao.class);
	}
}
