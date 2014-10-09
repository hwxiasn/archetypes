package com.qingbo.project.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.project.entity.User;

public class UserServiceTester extends BaseTester {
	@Autowired
	private UserService userService;

	@Test
	public void getUser() {
		User user = userService.getUser("admin");
		System.out.println(JSON.toJSON(user));
	}
}
