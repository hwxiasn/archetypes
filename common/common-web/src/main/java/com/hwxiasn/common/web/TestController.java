package com.hwxiasn.common.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="test",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
public class TestController {
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	@RequestMapping("test")
	public Object test(String msg) {
		logger.info(msg);
		return msg;
	}
	
	@RequestMapping("request")
	public Object testRequest(HttpServletRequest request) {
		String msg = request.getParameter("msg");
		logger.info("testRequest: "+msg);
		return msg;
	}
}
