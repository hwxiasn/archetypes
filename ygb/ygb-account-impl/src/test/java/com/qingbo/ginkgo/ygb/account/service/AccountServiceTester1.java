package com.qingbo.ginkgo.ygb.account.service;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.account.entity.Account;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.common.result.Result;

public class AccountServiceTester1 extends BaseServiceTester {
	@Autowired private AccountService accountService;
	@Autowired private PaymentService paymentService;
	@Autowired QddAccountLogService qddAccountLogService;
	@Autowired QddAccountService qddAccountService;
	
	
	
	public void accountLog() {
		System.out.println(accountService.depositPage(1L, null));
		System.out.println(accountService.withdrawPage(1L, null));
		System.out.println(accountService.transferPage(1L, null));
		System.out.println(accountService.accountLogPage(1L, null));
		accountService.getAccount(null);
		accountService.getSubAccount(1L, "abc");
	}
	
	
	public void cache() {
		accountService.getAccount(1L);
		accountService.getAccount(1L);
	}
	
	//创建账户
	@Test
	public void testercreateAccount() {
		accountService.createAccount(141208143534010001L);
		
//		Result<Account> result=accountService.getAccount(141213170731010007L);
//		assertEquals(141213170731010007L+"",result.getObject().getId()+"");
	}
	
	//校验账户密码
	
	public void testervalidatePassword() {
		accountService.validatePassword(141213170731010007L, "qweasd");
	}
	
	//更新账户密码
	
	public void testerupdatePassword() {
		accountService.updatePassword(141213170731010007L, "12345678", "");
	}
	
	//重置账户密码
	
	public void testerresetPassword() {
		accountService.resetPassword(141213170731010007L, "12345678");
	}
	
	//创建子账户
	
	public void testercreateSubAccount() {
		SubAccount subAccount=new SubAccount();
		BigDecimal balance=BigDecimal.valueOf(3654);
		BigDecimal freezeBalance=BigDecimal.valueOf(3658);
		subAccount.setBalance(balance);
		subAccount.setFreezeBalance(freezeBalance);
		accountService.createSubAccount(141213092148010009L,"QDD_BY");
		
//		Result<SubAccount> result=accountService.getSubAccount(141213162536010001L);
//		assertEquals("3654",result.getObject().getBalance());
		
	}
	
	//获取账户和子账户信息
	
	public void testergetAccount() {
		Result<Account> account=accountService.getAccount(141213170731010007L);
		System.out.println(account.getObject().getId());
		System.out.println(account.getObject().getPassword());
		System.out.println(account.getObject().getSalt());
		System.out.println(account.getObject().getCreateAt());
		System.out.println(account.getObject().getVersion());
	}
	
	//获取默认子账户
	
	public void testergetSubAccount() {
		Result<SubAccount> subaccount=accountService.getSubAccount(141213162536010000L);
		System.out.println(subaccount.getObject().getId());
		System.out.println(subaccount.getObject().getAccountId());
		
//		Result<SubAccount> result=accountService.getSubAccount(141213162536010001L);
//		assertEquals(141215124830030003L+"",result.getObject().getId()+"");
	}
	
	//获取子账户信息
	
	public void testergetSubAccount1() {
		Result<SubAccount> subaccount=accountService.getSubAccount(141209092136010016L, "QDD_BY");
		System.out.println(subaccount.getObject().getId());
		System.out.println(subaccount.getObject().getAccountId());
	}
	
}
