package com.hwxiasn.dubbo.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hwxiasn.contentserver.entity.User;

public class UserRepositoryTester extends BaseTester {
	@Autowired UserRepository userRepository;
	
	@Test
	public void findByUserName() {
		User user = userRepository.findByUserName("admin");
		System.out.println(user.getUserId());
	}
}
