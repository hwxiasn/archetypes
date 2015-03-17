package com.qingbo.ginkgo.ygb.web.controller.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.omg.CORBA.IntHolder;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.common.util.ServiceRequester;
import com.qingbo.ginkgo.ygb.common.util.TaskUtil;

public class ServiceRequesterTester {
	String service = GinkgoConfig.getProperty("front_url")+"/service/v1/";
	String datetime = service+"datetime.json";
	int requestCount = 1000;
	
	@Test public void request() {
		final CountDownLatch latch = new CountDownLatch(requestCount);
		final IntHolder success = new IntHolder(0);
		final Map<String, String> params = new HashMap<>();
		params.put("secret", ServiceController.secret);
		
		long s = System.currentTimeMillis();
		for(int i=0;i<requestCount;i++) {
			TaskUtil.submit(new Runnable() {
				@Override
				public void run() {
					JSONObject json = ServiceRequester.request(datetime, params);
					System.out.println(json);
					if(json!=null) success.value++;
					latch.countDown();
				}
			});
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//success: 10000, total ms: 60368
		System.out.println("success: "+success.value+"/"+requestCount+", total ms: "+(System.currentTimeMillis()-s));
	}
	
	String account = service+"account.json";
	String[] accountMethod = {"isRegistered", "register", "isAuthorised", "authorise", "balance"};
	Map<String, List<Map<String, String>>> paramsMap = new HashMap<>();
	@Test public void account() {
		final CountDownLatch latch = new CountDownLatch(requestCount);
		final IntHolder success = new IntHolder(0);
		
		List<Map<String, String>> paramsList = new ArrayList<>();
		Map<String, String> params = new HashMap<>();
		params.put("secret", ServiceController.secret);
		params.put("method", "isRegistered");
		params.put("userId", "141229211141010003");//registered
		paramsList.add(params);
		params = new HashMap<>(params);
		params.put("userId", "141229211141010006");//not registered
		paramsList.add(params);
		paramsMap.put("isRegistered", paramsList);
		
		paramsList = clone(paramsList, "register");
		paramsMap.put("register", paramsList);
		
		paramsList = clone(paramsList, "isAuthorised");
		params = new HashMap<>(params);
		params.put("userId", "141229211141010029");//not authorised
		paramsList.add(params);
		paramsMap.put("isAuthorised", paramsList);
		
		paramsList = clone(paramsList, "authorise");
		paramsMap.put("authorise", paramsList);
		
		paramsList = clone(paramsList, "balance");
		params = new HashMap<>(params);
		params.put("userId", "141229211141010022");//750.00
		paramsList.add(params);
		paramsMap.put("balance", paramsList);
		
		long s = System.currentTimeMillis();
		for(int i=0;i<requestCount;i++) {
			TaskUtil.submit(new Runnable() {
				@Override
				public void run() {
					int random = RandomUtils.nextInt(accountMethod.length);
					String method = accountMethod[random];
					List<Map<String, String>> paramsList = paramsMap.get(method);
					Map<String, String> params = paramsList.get(RandomUtils.nextInt(paramsList.size()));
					JSONObject json = ServiceRequester.request(account, params);
					System.out.println(json);
					if(json!=null) success.value++;
					latch.countDown();
				}
			});
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//success: 10000, total ms: 60368
		System.out.println("success: "+success.value+"/"+requestCount+", total ms: "+(System.currentTimeMillis()-s));
	}

	private List<Map<String, String>> clone(List<Map<String, String>> list, String method){
		List<Map<String, String>> paramsList = new ArrayList<>();
		Map<String, String> params = null;
		for(Map<String, String> data : list) {
			params = new HashMap<>(data);
			params.put("method", method);
			paramsList.add(params);
		}
		return paramsList;
	}
}
