package com.qingbo.ginkgo.ygb.web.controller;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.web.BaseTester;

public class IndexControllerTester extends BaseTester {
	@Autowired private IndexController indexController;
	
	@Test
	public void index() {
		System.out.println(indexController.index(request, model));
		checkcode();
		String login = indexController.login(request, model, "admin", "123456");
		Assert.assertTrue(login.contains("/index.html"));//后台正确登录
		indexController.logout();//注销后错误登录
		String login2 = indexController.login(request, model, "admin", "12345");
		Assert.assertTrue(login2.contains("login"));//正确登录
		login("admin", "123456");
	}
}
