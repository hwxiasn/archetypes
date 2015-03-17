package com.qingbo.ginkgo.ygb.account.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;

public class AccountErrorTester {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test public void errors() {
		ErrorMessage errors = new ErrorMessage("account-error.properties");
		logger.info(errors.newFailure("ACT0001", null).toString());
		System.out.println(errors.newFailure("ACT0002", 1));
	}
}
