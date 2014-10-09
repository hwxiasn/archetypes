package com.qingbo.project.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.project.entity.User;
import com.qingbo.project.repository.UserRepository;

public class UserRepositoryTester extends BaseTester {
	@Autowired UserRepository userRepository;
	
	@Test
	public void findByUserName() {
		User user = userRepository.findByUserName("admin");
		System.out.println(user.getUserId());
	}
}
