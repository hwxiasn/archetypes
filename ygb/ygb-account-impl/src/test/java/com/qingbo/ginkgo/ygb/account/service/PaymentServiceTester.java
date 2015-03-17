package com.qingbo.ginkgo.ygb.account.service;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.common.result.Result;

public class PaymentServiceTester extends BaseServiceTester {
	@Autowired AccountService accountService;
	@Autowired PaymentService paymentService;
	
	@Test
	public void deposit() {
		Long userId = 1L, tradeId = 1L;
		BigDecimal balance = new BigDecimal("10");
		SubAccount subAccount = accountService.getSubAccount(userId).getObject();
		BigDecimal oldBalance = subAccount.getBalance();
		Result<String> deposit = paymentService.deposit(tradeId, subAccount.getId(), balance);
		System.out.println(deposit);
		subAccount = accountService.getSubAccount(userId).getObject();
		BigDecimal newBalance = subAccount.getBalance();
		Assert.assertEquals("充值后余额错误", oldBalance.add(balance), newBalance);
	}
	
	@Test
	public void withdraw() {
		Long userId = 1L, tradeId = 2L;
		BigDecimal balance = new BigDecimal("10");
		SubAccount subAccount = accountService.getSubAccount(userId).getObject();
		BigDecimal oldBalance = subAccount.getBalance();
		Result<String> withdraw = paymentService.withdraw(tradeId, subAccount.getId(), balance, "ABC", "123456789101112");
		System.out.println(withdraw);
		subAccount = accountService.getSubAccount(userId).getObject();
		BigDecimal newBalance = subAccount.getBalance();
		Assert.assertEquals("充值后余额错误", oldBalance.subtract(balance), newBalance);
	}
}
