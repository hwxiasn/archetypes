package com.hwxiasn.contentserver.facade;

import org.junit.Test;

import com.sohu.wap.cms.content.Manager;

public class TestServiceTester {
	@Test
	public void echo() {
		TestService testService = Manager.getInstance().createInstance(TestService.class);
		System.out.println(testService.echo("client tester"));
	}
}
