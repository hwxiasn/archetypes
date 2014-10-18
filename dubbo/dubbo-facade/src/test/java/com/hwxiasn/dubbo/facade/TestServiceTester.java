package com.hwxiasn.dubbo.facade;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hwxiasn.dubbo.repository.TongjiRepository;
import com.hwxiasn.dubbo.repository.UserRepository;

public class TestServiceTester {
	@Test
	public void consumer() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath:consumer.xml" });
		context.start();
		TestService testService = (TestService) context.getBean("testService"); // 获取远程服务代理
		String hello = testService.echo("Hello World"); // 执行远程方法
		System.out.println(hello); // 显示调用结果
		String time = testService.time();
		System.out.println(time);
		
		UserRepository userRepository = (UserRepository)context.getBean("userRepository");
		System.out.println(userRepository.count());
		
		TongjiRepository tongjiRepository = context.getBean("tongjiRepository", TongjiRepository.class);
		System.out.println(tongjiRepository.count("select count(*) from user"));
	}
}
