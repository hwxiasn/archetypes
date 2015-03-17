package com.qingbo.ginkgo.ygb.account.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

import com.alibaba.fastjson.JSON;

public class PaymentUtil {
	public static final String ACCOUNT_QUEUING = "03";
	
	private static URLCodec urlCodec = new URLCodec();//default is CharEncoding.UTF_8
	
	/**
	 * 检查必填参数
	 * @return 缺少的必填参数列表，null 必填参数完整
	 */
	public static List<String> missedRequiredParams(Map<String, String> params, String[] requiredParamNames) {
		if(requiredParamNames!=null && requiredParamNames.length>0) {
			List<String> missedRequiredParams = null;
			for(String param:requiredParamNames) {
				String value = params.get(param);
				if(value==null || value.length()==0) {
					if(missedRequiredParams == null)
						missedRequiredParams = new ArrayList<>();
					missedRequiredParams.add(param);
				}
			}
			return missedRequiredParams;
		}
		return null;
	}
	
	/**
	 * object -> json
	 */
	public static String toJSON(Object object) {
		return JSON.toJSONString(object);
	}
	
	/**
	 * json -> object
	 */
	public static <T> T fromJSON(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}
	
	/**
	 * 编码链接参数中的空格引号等特殊字符
	 */
	public static String encodeURL(String value) {
		try{
			return urlCodec.encode(value);
		}catch(EncoderException e) {
			return value;
		}
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
