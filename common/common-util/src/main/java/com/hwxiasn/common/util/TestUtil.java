package com.hwxiasn.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtil {
	private static final Logger logger = LoggerFactory.getLogger(TestUtil.class);

	public static String test(String msg) {
		logger.info(msg);
		return "test: "+msg;
	}
}
