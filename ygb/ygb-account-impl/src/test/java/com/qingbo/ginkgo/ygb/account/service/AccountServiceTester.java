package com.qingbo.ginkgo.ygb.account.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountServiceTester extends BaseServiceTester {
	@Autowired private AccountService accountService;
	@Autowired private PaymentService paymentService;
	@Autowired QddAccountLogService qddAccountLogService;
	@Autowired QddAccountService qddAccountService;
	
	@Test
	public void accountLog() {
		System.out.println(accountService.depositPage(1L, null));
		System.out.println(accountService.withdrawPage(1L, null));
		System.out.println(accountService.transferPage(1L, null));
		System.out.println(accountService.accountLogPage(1L, null));
		accountService.getAccount(null);
		accountService.getSubAccount(1L, "abc");
	}
	
	@Test
	public void cache() {
		accountService.getAccount(1L);
		accountService.getAccount(1L);
	}
}
