package com.qingbo.ginkgo.ygb.account.payment;

import java.util.Map;

/**
 * 支付接口响应，通常是返回json字符串
 * @author hongwei
 * @date 2014-08-26
 */
public interface PaymentResponse {
	/**
	 * 接口调用是否成功
	 */
	boolean success();
	
	/**
	 * 接口调用提示消息
	 */
	String message();
	
	/**
	 * 原始响应内容
	 */
	String rawText();
	
	/**
	 * 解析响应内容为映射
	 */
	Map<String, String> map();
}
