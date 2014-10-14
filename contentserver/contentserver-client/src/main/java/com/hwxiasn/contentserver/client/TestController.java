package com.hwxiasn.contentserver.client;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hwxiasn.contentserver.facade.TestService;

@RestController
@RequestMapping(value="test",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
public class TestController {
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@Autowired private TestService testService;

	@RequestMapping("test")
	public Object test(String msg) {
		logger.info(msg);
		return msg;
	}
	
	@RequestMapping("service")
	public Object service(String msg) {
		msg = testService.echo(msg);
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
