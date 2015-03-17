package com.qingbo.ginkgo.ygb.cms.service;

import java.io.IOException;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.qingbo.ginkgo.ygb.cms.repository.BaseRepositoryTester;

@ContextConfiguration(locations = { "classpath:cms-service.xml" })
public class BaseServiceTester extends BaseRepositoryTester {
	@Test
	public void provide() throws IOException {
		System.in.read();
		System.out.println("Provider Exit");
	}
}
