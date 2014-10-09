package com.qingbo.project.user;

import javax.annotation.Resource;

import org.junit.Test;

import com.qingbo.project.entity.User;
import com.qingbo.project.user.facade.UserService;

public class UserServiceTester extends BaseTester {
	@Resource(name="userService") private UserService userService;
	@Resource(name="userService2") private UserService userService2;
	@Resource(name="userService3") private UserService userService3;
	
	@Test
	public void echo() {
		System.out.println(userService.echo("UserService OK!"));
		System.out.println(userService2.echo("UserService2 OK!"));
		System.out.println(userService3.echo("UserService3 OK!"));
		System.out.println("useService: "+userService);
		System.out.println("useService2: "+userService2);
		System.out.println("useService3: "+userService3);
		User user = new User();
		user.setUserName("admin");
		System.out.println(userService.echo(user));
	}
}
