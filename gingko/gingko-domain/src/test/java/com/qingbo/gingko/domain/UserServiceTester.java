package com.qingbo.gingko.domain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.gingko.common.util.Pager;
import com.qingbo.gingko.entity.User;

public class UserServiceTester extends BaseTester {
	@Autowired private UserService userService;
	
	@Test
	public void getUser() {
		User user = userService.getUser("admin");
		System.out.println(JSON.toJSONString(user));
	}
	
	@Test
	public void validatePassword() {
		boolean validatePassword = userService.validatePassword("admin", "123456");
		System.out.println(validatePassword);
	}
	
	@Test
	public void pageUser() {
		Pager pager = new Pager();
		userService.page(null, pager);
		printPage(pager);
	}
}
