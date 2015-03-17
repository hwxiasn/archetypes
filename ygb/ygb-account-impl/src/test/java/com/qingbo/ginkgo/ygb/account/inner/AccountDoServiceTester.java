package com.qingbo.ginkgo.ygb.account.inner;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.account.entity.AccountLog;
import com.qingbo.ginkgo.ygb.account.inner.impl.AccountDoServiceImpl;
import com.qingbo.ginkgo.ygb.account.repository.AccountLogRepository;
import com.qingbo.ginkgo.ygb.account.service.BaseServiceTester;

public class AccountDoServiceTester extends BaseServiceTester {
	@Autowired AccountLogRepository accountLogRepository;
	@Autowired AccountDoServiceImpl accountDoServiceImpl;
	
	@Test public void notifyAccountLog() {
		AccountLog accountLog = accountLogRepository.findOne(141229211141013714L);
//		accountDoServiceImpl.notifyAccountLog(accountLog);
	}
}
