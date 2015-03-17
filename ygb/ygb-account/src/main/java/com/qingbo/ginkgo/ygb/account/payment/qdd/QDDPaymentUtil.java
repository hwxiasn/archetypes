package com.qingbo.ginkgo.ygb.account.payment.qdd;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;

public class QDDPaymentUtil {
	private static SimpleDateFormat orderNoDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
	private static DecimalFormat decimalFormat = new DecimalFormat("0000");
	private static AtomicInteger counter = new AtomicInteger(1);
	private static URLCodec urlCodec = new URLCodec();//default is CharEncoding.UTF_8
	
	/**
	 * 16位唯一orderNo生成（16位订单号=8位年月日+8位序列号=4位时分|4位序列号，既提供日期信息，又保证同时大量请求时订单号不重复）
	 */
	public static String orderNo16() {
		int i = counter.getAndIncrement();
		if(i>9999) {
			counter.set(1);
			i = counter.getAndIncrement();
		}
		return orderNoDateFormat.format(new Date())+decimalFormat.format(i);
	}
	
	/**
	 * 解码链接参数中的特殊支付
	 */
	public static String decodeURL(String value) {
		try{
			return urlCodec.decode(value);
		}catch(DecoderException e) {
			return value;
		}
	}
}
