package com.qingbo.ginkgo.ygb.common.util;

import org.junit.Test;

public class ErrorMessageTester {
	@Test
	public void error() {
		ErrorMessage errors = new ErrorMessage(null);
		System.out.println(errors.newFailure(null));
		System.out.println(errors.newFailure("error"));
		
		errors = new ErrorMessage("test.properties");
		System.out.println(errors.newFailure(null));
		System.out.println(errors.newFailure("error"));
		
		errors = new ErrorMessage("test-error.properties");
		System.out.println(errors.newFailure(null));
		System.out.println(errors.newFailure("error"));
		System.out.println(errors.newFailure("ACT0001"));
		System.out.println(errors.newFailure("ACT0002"));
		System.out.println(errors.newFailure("ACT0002", "testarg"));
	}
}
