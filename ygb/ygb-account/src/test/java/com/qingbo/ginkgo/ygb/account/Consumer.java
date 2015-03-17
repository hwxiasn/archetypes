package com.qingbo.ginkgo.ygb.account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.qingbo.ginkgo.ygb.account.service.AccountService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:consumer.xml" })
public class Consumer {
	@Autowired AccountService accountService;
	
	@Test
	public void accountLog() {
		System.out.println(accountService.depositPage(1L, null));
		System.out.println(accountService.withdrawPage(1L, null));
		System.out.println(accountService.transferPage(1L, null));
		System.out.println(accountService.accountLogPage(1L, null));
	}
}
