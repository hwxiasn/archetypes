package com.hwxiasn.common.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

public class TestControllerTester extends BaseTester {
	private @Autowired TestController testController;
	
	@Test
	public void test() {
		System.out.println(testController.test("TestControllerTester"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("msg", "Tester");
		System.out.println(testController.testRequest(request));
	}
}
