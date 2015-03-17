package com.qingbo.ginkgo.ygb.account.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.account.entity.AccountLog;

public class AccountLogRepositoryTester extends BaseRepositoryTester {
	@Autowired private AccountLogRepository accountLogRepository;
	
	@Test
	public void deleted() {
		List<AccountLog> list = accountLogRepository.findByTypeAndTradeIdAndDeletedFalse("IN", 1L);
		printList(list);
	}
	
	@Test
	public void findByTypeAndSubAccountLogId() {
		AccountLog accountLog = accountLogRepository.findByTypeAndSubAccountLogId("IN", 141212170626020010L);
		System.out.println(accountLog);
	}
}
