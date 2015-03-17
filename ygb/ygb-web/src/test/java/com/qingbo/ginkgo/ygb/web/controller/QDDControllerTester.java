package com.qingbo.ginkgo.ygb.web.controller;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.web.BaseTester;

public class QDDControllerTester extends BaseTester {
	@Autowired QDDController qddController;
	
	@Test
	public void orderQuery() {
		request.addParameter("PlatformMoneymoremore", "p106");
		request.addParameter("BeginTime", "2014-12-01");
		request.addParameter("EndTime", "2014-12-10");
		qddController.orderQuery(request);
	}
	
	@Test
	public void balanceCheck() throws IOException {
		request.addParameter("PlatformMoneymoremore", "p106");
		Object balanceCheck = qddController.balanceCheck(request);
		System.out.println(balanceCheck);
	}
}
