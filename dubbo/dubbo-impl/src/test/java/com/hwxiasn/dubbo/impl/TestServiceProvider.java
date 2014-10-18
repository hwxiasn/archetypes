package com.hwxiasn.dubbo.impl;

import java.io.IOException;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestServiceProvider {
	@Test
	public void provide() throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath:repository.xml", "classpath:provider.xml" });
		context.start();
		System.in.read(); // 按任意键退出
	}
}
