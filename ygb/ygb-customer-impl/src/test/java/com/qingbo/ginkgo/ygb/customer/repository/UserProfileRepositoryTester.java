package com.qingbo.ginkgo.ygb.customer.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;

public class UserProfileRepositoryTester extends BaseRepositoryTester {
	@Autowired private UserProfileRepository userProfileRepository;
	
	@Test
	public void profile() {
		List<UserProfile> findAll = userProfileRepository.findAll();
		printList(findAll);
	}
}
