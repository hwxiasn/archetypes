package com.hwxiasn.contentserver.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContext {
	public static final ApplicationContext context;
	static {
		context = new ClassPathXmlApplicationContext("classpath:repository.xml");
	}
	public static <T> T getBean(Class<T> clazz) {
		return context.getBean(clazz);
	}
}
