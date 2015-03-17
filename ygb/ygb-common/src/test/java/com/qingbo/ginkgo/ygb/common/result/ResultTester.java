package com.qingbo.ginkgo.ygb.common.result;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qingbo.ginkgo.ygb.common.util.MailUtil;

public class ResultTester {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void success() {
		Assert.assertTrue(Result.newSuccess().success());
	}
	
	@Test
	public void string() {
//		System.out.println(Result.newSuccess());
//		System.out.println(Result.newSuccess(true));
//		System.out.println(Result.newSuccess(1));
//		System.out.println(Result.newSuccess(Arrays.asList(1,2,3)));
//		System.out.println(Result.newFailure(-1, "not exist"));
//		System.out.println(Result.newFailure("TNT0001", "business error"));
//		System.out.println(Result.newException(new RuntimeException("some error happens")));
//		logger.info(Result.newException(new RuntimeException("some error happens")).toString());
		MailUtil.sendHtmlEmail("xhongwei@qingber.com", "来自新浪企业邮箱的邮件，请签收", "an email from sina enterprise mail service");
		System.out.println("ok");
	}
}
