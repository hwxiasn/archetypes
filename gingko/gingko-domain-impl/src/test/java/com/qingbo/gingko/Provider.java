package com.qingbo.gingko;

import java.io.IOException;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.qingbo.gingko.domain.BaseTester;

@ContextConfiguration(locations = { 
		"classpath:provider.xml",
		})
public class Provider extends BaseTester {
	@Test
	public void provide() throws IOException {
		System.in.read();
		System.out.println("Provider Exit");
	}
}
