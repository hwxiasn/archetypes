package com.qingbo.ginkgo.ygb.web.controller.admin;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.web.BaseTester;
import com.qingbo.ginkgo.ygb.web.controller.IndexController;

public class AdminControllerTester extends BaseTester {
	@Autowired private AdminController adminController;
	@Autowired private IndexController indexController;
	
	@Test
	public void admin() {
		try {
			adminController.home();
			Assert.fail();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			//处理验证码
			checkcode();
			adminController.login(request, "admin", "123456", model);
			adminController.home();
		}catch(Exception e) {
			System.out.println(e.getMessage());
			Assert.fail();
		}
		
		try {
			logout();
			login("admin", "123456");
			adminController.home();
		}catch(Exception e) {
			System.out.println(e.getMessage());
			Assert.fail();
		}
	}
}
