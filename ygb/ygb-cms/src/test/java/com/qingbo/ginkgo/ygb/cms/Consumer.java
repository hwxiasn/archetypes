package com.qingbo.ginkgo.ygb.cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:consumer.xml" })
public class Consumer {
	@Test
	public void tongji() {
		System.out.println(toString());
	}
}
