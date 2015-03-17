package com.qingbo.ginkgo.ygb.web.controller.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.web.BaseTester;

public class ServiceControllerTester extends BaseTester {
	@Autowired ServiceController serviceController;
	
	@Test public void service() {
		request.addParameter("secret", ServiceController.secret);
		for(int i=0;i<1000;i++) {
		serviceController.service(request, "datetime");
		}
	}
}
