package com.hwxiasn.contentserver.client;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.hwxiasn.contentserver.entity.User;
import com.hwxiasn.contentserver.facade.BaseDao;
import com.hwxiasn.contentserver.facade.JdbcDao;
import com.hwxiasn.contentserver.facade.TestService;

@RestController
@RequestMapping(value="test",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
public class TestController {
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	@Autowired private TestService testService;
	@Autowired private BaseDao baseDao;
	@Autowired private JdbcDao jdbcDao;

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
	@RequestMapping("time")
	public Object time() {
		String time = testService.time();
		logger.info(time);
		return time;
	}
	@RequestMapping("dao")
	public Object dao() {
		User user = baseDao.findOne(User.class, 1);
		logger.info(JSON.toJSONString(user));
		return JSON.toJSONString(user);
	}
	@RequestMapping("jdbc")
	public Object jdbc() {
		List<?> list = jdbcDao.list("select * from user");
		logger.info(JSON.toJSONString(list));
		return JSON.toJSONString(list);
	}
	@RequestMapping("request")
	public Object testRequest(HttpServletRequest request) {
		String msg = request.getParameter("msg");
		logger.info("testRequest: "+msg);
		return msg;
	}
}
