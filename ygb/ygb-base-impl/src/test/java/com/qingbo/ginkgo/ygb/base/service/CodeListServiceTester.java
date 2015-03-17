package com.qingbo.ginkgo.ygb.base.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.base.entity.BankType;
import com.qingbo.ginkgo.ygb.common.result.Result;

public class CodeListServiceTester extends BaseServiceTester {
	@Autowired CodeListService codeListService;
	
	@Test
	public void bankType() {
		for(int i=0;i<10;i++) {
		Result<BankType> bank = codeListService.bank("ABC");
		System.out.println(bank);
		}
	}
}
