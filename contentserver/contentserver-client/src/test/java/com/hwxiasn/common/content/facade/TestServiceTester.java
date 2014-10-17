package com.hwxiasn.common.content.facade;

import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.hwxiasn.contentserver.entity.User;
import com.hwxiasn.contentserver.facade.BaseDao;
import com.hwxiasn.contentserver.facade.JdbcDao;
import com.hwxiasn.contentserver.facade.TestService;
import com.sohu.wap.cms.content.Manager;
import com.sohu.wap.cms.content.net.ServerIdentify;

public class TestServiceTester {
	@Test
	public void echo() {
		List<String> allServer = Manager.getInstance().getAllServer();
		System.out.println(allServer);
		for(String serverName:allServer) {
			ServerIdentify server = Manager.getInstance().getServer(serverName);
			System.out.println(server.toString());
		}
		TestService testService = Manager.getInstance().createInstance(TestService.class);
		System.out.println(testService.echo("TestServiceTester.echo"));
		System.out.println(testService);
		
		BaseDao baseDao = Manager.getInstance().createInstance(BaseDao.class);
		System.out.println(JSON.toJSON(baseDao.findOne(User.class, 1)));
		
		JdbcDao jdbcDao = Manager.getInstance().createInstance(JdbcDao.class);
		System.out.println(jdbcDao.list("select * from user"));
		System.out.println(jdbcDao.count("select count(*) from user"));
	}
}
