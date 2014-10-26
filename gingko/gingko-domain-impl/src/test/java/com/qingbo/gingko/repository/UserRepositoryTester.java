package com.qingbo.gingko.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.gingko.entity.User;

public class UserRepositoryTester extends BaseTester {
	@Autowired private UserRepository userRepository;
	
	@Test
	public void findUser() {
		User user = userRepository.findByUserName("admin");
		System.out.println(user);
	}
}
