package com.hwxiasn.common.content.facade;

import java.util.List;

import org.junit.Test;

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
	}
}
