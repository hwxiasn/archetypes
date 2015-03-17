package com.qingbo.ginkgo.ygb.common.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ServiceRequester {
	public static Charset UTF8 = Charset.forName("utf-8");
	public static String serviceGinkgo = GinkgoConfig.getProperty("service.ginkgo", "http://localhost:8080/service/v1/");
	public static String serviceWboss = GinkgoConfig.getProperty("service.wboss", "http://localhost:8080/service/v1/");
	
	private static int connectionRequestTimeout = NumberUtil.parseInt(GinkgoConfig.getProperty("service.requester.connectionRequestTimeout"), 15000);
	private static int connectionTimeout = NumberUtil.parseInt(GinkgoConfig.getProperty("service.requester.connectionTimeout"), 15000);
	private static int socketTimeout = NumberUtil.parseInt(GinkgoConfig.getProperty("service.requester.socketTimeout"), 30000);
	
	private static int maxConnTotal = NumberUtil.parseInt(GinkgoConfig.getProperty("service.requester.maxConnTotal"), 256);
	private static int maxConnPerRoute = NumberUtil.parseInt(GinkgoConfig.getProperty("service.requester.maxConnPerRoute"), maxConnTotal);
	
	private static RequestConfig requestConfig = RequestConfig.custom()
			.setConnectionRequestTimeout(connectionRequestTimeout)
			.setConnectTimeout(connectionTimeout)
			.setSocketTimeout(socketTimeout)
			.build();
	private static CloseableHttpClient httpClient = HttpClients.custom()
			.setDefaultRequestConfig(requestConfig)
			.setMaxConnTotal(maxConnTotal)
			.setMaxConnPerRoute(maxConnPerRoute)
			.build();
	private static String secret = GinkgoConfig.getProperty("service.controller.secret", "zbnxex5oe9cz");
	private static Logger logger = LoggerFactory.getLogger(ServiceRequester.class);
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					logger.info("shutdown ServiceRequestor httpclient");
					httpClient.close();
				} catch (IOException e) {
					logger.warn("fail to shutdown ServiceRequester httpclient", e);
				}
			}
		});
	}
	
	/**
	 * 提交数据，返回JSON对象
	 */
	public static JSONObject request(String service, Map<String, String> params) {
		JSONObject json = null;
		HttpPost post = null;
		
		try {
			post = new HttpPost(service);
			
			logger.info("request: "+service);
			logger.info("with params: "+JSON.toJSONString(params));
			
			if (params != null) {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (String name : params.keySet()) {
					String value = params.get(name);
					nvps.add(new BasicNameValuePair(name, value));
				}
				post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			}
			
			HttpResponse response = httpClient.execute(post);
			
			int status = response.getStatusLine().getStatusCode();
			String content = EntityUtils.toString(response.getEntity(), UTF8);
			
			logger.info("status: "+status);
			logger.info("content: "+content);
			
			if(200 == status) {
				try {
					json = JSON.parseObject(content);
				}catch(Exception e) {
					logger.warn("fail to parse service response to json: "+content);
					throw new RuntimeException(e);
				}
			}else {
				logger.warn("fail to request service: "+service+", with status: "+status);
			}
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
		} catch (RuntimeException e) {
			if (post != null) {
				post.abort();
			}
			logger.warn(e.getMessage(), e);
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
		
		return json;
	}
	
	public static Map<String, String> paramsWithSecret() {
		Map<String, String> params = new HashMap<>();
		params.put("secret", secret);
		return params;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> convert(Object entity){
		Map<String, String> params = new HashMap<>();
		String json = JSON.toJSONString(entity);
		Map<String, ?> map = JSON.parseObject(json, Map.class);
		for(String key:map.keySet()) {
			Object value = map.get(key);
			if(value!=null) params.put(key, value.toString());
		}
		return params;
	}
}
