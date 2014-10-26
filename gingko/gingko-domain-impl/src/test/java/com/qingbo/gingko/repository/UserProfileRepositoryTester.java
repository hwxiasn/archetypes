package com.qingbo.gingko.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.gingko.entity.UserProfile;

public class UserProfileRepositoryTester extends BaseTester {
	@Autowired private UserProfileRepository userProfileRepository;
	
	@Test
	public void findByUserId() {
		UserProfile userProfile = userProfileRepository.findByUserId(1);
		System.out.println(JSON.toJSONString(userProfile));
	}
}
