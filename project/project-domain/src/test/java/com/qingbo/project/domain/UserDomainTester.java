package com.qingbo.project.domain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.project.common.util.Pager;
import com.qingbo.project.entity.User;

public class UserDomainTester extends BaseTester {
	@Autowired private UserDomain userService;
	
	@Test
	public void getUser() {
		User user = userService.getUser("admin");
		System.out.println(JSON.toJSONString(user));
	}
	
	@Test
	public void saveUser() {
		User user = userService.getUser("admin");
		user.setStatus("A");
		userService.saveUser(user);
	}
	
	@Test
	public void validatePassword() {
		boolean validatePassword = userService.validatePassword("hongwei", "123456");
		System.out.println(validatePassword);
	}
	
	@Test
	public void pageUser() {
		Pager pager = new Pager();
		userService.pageUser(null, pager);
		printPage(pager);
	}
}
