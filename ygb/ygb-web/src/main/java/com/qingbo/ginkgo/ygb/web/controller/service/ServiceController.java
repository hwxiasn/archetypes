package com.qingbo.ginkgo.ygb.web.controller.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.DateUtil.FormatType;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;

/** ginkgo接口服务 */
@RestController
@RequestMapping("service")
public class ServiceController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Map<String, ServiceHandler> serviceHandlers = new HashMap<>();//v1/name => handler
	public static String secret = GinkgoConfig.getProperty("service.controller.secret", "zbnxex5oe9cz");
	public static JSONObject BadRequest = JSON.parseObject("{\"status\":\"500\", \"error\":\"bad request\"}");
	
	public ServiceController() {
		ServiceHandler datetime = new ServiceHandler() {
			private String name = "datetime";
			private JSONObject json = new JSONObject();
			@Override
			public String name() {
				return name;
			}
			@Override
			public JSONObject handle(Map<String, String> data) {
				String datetime = DateUtil.format(new Date(), FormatType.DAYTIME);
				json.put(name, datetime);
				return json;
			}
		};
		register(datetime);
	}
	
	/** 版本号v1 */
	@RequestMapping("v1/{name}")
	public Callable<JSONObject> service(final HttpServletRequest request, final @PathVariable String name) {
		return new Callable<JSONObject>() {
			@Override
			public JSONObject call() throws Exception {
				Map<String, String> params = RequestUtil.stringMap(request);
				
				//检查加密授权
				if(!secret.equals(params.get("secret"))) {
					logger.info("bad service request: "+request.getRequestURI()+params);
					return BadRequest;
				}
				
				//处理请求
				ServiceHandler serviceHandler = serviceHandlers.get(name);
				if(serviceHandler!=null) {
					JSONObject json = serviceHandler.handle(params);
					logger.info("answer service request: "+request.getRequestURI()+", with data: "+json);
					return json!=null ? json : BadRequest;
				}
				
				//非法请求
				logger.info("bad service request: "+request.getRequestURI()+params);
				return BadRequest;
			}
		};
	}
	
	public boolean register(ServiceHandler serviceHandler) {
		if(serviceHandler!=null && StringUtils.isNotBlank(serviceHandler.name())) {
			ServiceHandler handler = serviceHandlers.get(serviceHandler.name());
			if(handler!=null) logger.warn("service handler key already exist, replace it: "+handler+", using this: "+serviceHandler);
			serviceHandlers.put(serviceHandler.name(), serviceHandler);
			return true;
		}
		logger.warn("bad service handler to register: "+serviceHandler);
		return false;
	}
	
	public static interface ServiceHandler {
		String name();//服务名
		JSONObject handle(Map<String, String> data);//数据处理并响应json
	}
}
