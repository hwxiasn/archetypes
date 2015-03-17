package com.qingbo.ginkgo.ygb.account.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.account.entity.SubQddAccount;

public class SubQDDAccountRepositoryTester extends BaseRepositoryTester {
	@Autowired private SubQddAccountRepository subQDDAccountRepository;
	
	@Test
	public void findAll() {
		List<SubQddAccount> list = subQDDAccountRepository.findAll();
		printList(list);
	}
}
