package com.qingbo.ginkgo.ygb.web.controller.service;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.common.result.Result;

public class ServiceResponse {

	public static JSONObject newSuccess() {
		JSONObject json = new JSONObject();
		json.put("status", 200);
		return json;
	}
	
	public static JSONObject newError(Result<?> error) {
		JSONObject json = newSuccess();
		json.put("error", error.getMessage());
		return json;
	}
	
	public static JSONObject newErrorMessage(String message) {
		JSONObject json = newSuccess();
		json.put("error", message);
		return json;
	}
	
}
