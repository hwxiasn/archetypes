package com.qingbo.ginkgo.ygb.trade;

import java.io.IOException;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.qingbo.ginkgo.ygb.trade.repository.BaseRepositoryTester;

@ContextConfiguration(locations = { "classpath:provider.xml" })
public class Provider extends BaseRepositoryTester {
	@Test
	public void provide() throws IOException {
		System.in.read();
		System.out.println("Provider Exit");
	}
}
