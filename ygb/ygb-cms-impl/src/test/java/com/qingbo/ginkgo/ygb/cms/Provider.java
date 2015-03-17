package com.qingbo.ginkgo.ygb.cms;

import java.io.IOException;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.qingbo.ginkgo.ygb.cms.service.BaseServiceTester;

@ContextConfiguration(locations = { "classpath:provider.xml" })
public class Provider extends BaseServiceTester {
	@Test
	public void provide() throws IOException {
		System.in.read();
		System.out.println("Provider Exit");
	}
}
