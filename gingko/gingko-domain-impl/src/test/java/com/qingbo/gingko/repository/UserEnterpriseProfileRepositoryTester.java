package com.qingbo.gingko.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.gingko.entity.UserEnterpriseProfile;

public class UserEnterpriseProfileRepositoryTester extends BaseTester {
	@Autowired private UserEnterpriseProfileRepository userEnterpriseProfileRepository;
	
	@Test
	public void findByUserId() {
		UserEnterpriseProfile userEnterpriseProfile = userEnterpriseProfileRepository.findByUserId(1);
		System.out.println(JSON.toJSONString(userEnterpriseProfile));
	}
}
