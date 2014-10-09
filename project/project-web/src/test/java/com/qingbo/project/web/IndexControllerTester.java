package com.qingbo.project.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class IndexControllerTester extends BaseTester {
	@Autowired private IndexController indexController;
	
	@Test
	public void index() {
		System.out.println(indexController.index(null, null, null));
		System.out.println(indexController.login("admin", "123456"));
	}
}
