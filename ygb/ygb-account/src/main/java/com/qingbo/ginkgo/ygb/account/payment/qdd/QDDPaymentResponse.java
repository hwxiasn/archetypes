package com.qingbo.ginkgo.ygb.account.payment.qdd;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.account.payment.PaymentResponse;

public class QDDPaymentResponse implements PaymentResponse {
	private String rawText;
	private Map<String, String> map;
	private String message;
	private boolean success;
	
	@SuppressWarnings("unchecked")
	public QDDPaymentResponse(String rawText) {
		this.rawText = rawText;
		if(rawText == null || (rawText=rawText.trim()).length() == 0) {
			this.map = new HashMap<>();
		}else if(rawText.startsWith("[")) {//转账可能会返回两个结果：转账成功+审核通过，这里只取转账成功
			Map<String, String>[] maps = JSON.parseObject(rawText, HashMap[].class);
			this.map = maps[0];
		}else if(rawText.startsWith("{")){
			this.map = JSON.parseObject(rawText, HashMap.class);
		}else {
			this.map = new HashMap<>();
		}
		this.message = this.map.get("Message");
		this.success = "88".equals(this.map.get("ResultCode"));
	}

	@Override
	public boolean success() {
		return success;
	}

	@Override
	public String message() {
		return message;
	}

	@Override
	public String rawText() {
		return rawText;
	}

	@Override
	public Map<String, String> map() {
		return map;
	}
}
