package com.qingbo.ginkgo.ygb.account.inner;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.account.service.BaseServiceTester;
import com.qingbo.ginkgo.ygb.common.result.Result;

public class QddPaymentServiceTester extends BaseServiceTester {
	@Autowired QddPaymentService qddPaymentService;
	
	@Test
	public void withdraw() {
		//withdraw[141227113318020009, 141224134902030001, 500.0, ICBC, 6222023100048007912]
		Result<String> loanWithdraw = qddPaymentService.loanWithdraw(141227113319030002L);
		System.out.println(loanWithdraw);
	}
}
