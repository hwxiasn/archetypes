package com.qingbo.project.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;



/**
 * 解析字符串值为基本类型：整数，长整数，浮点数，布尔值
 * @author hongwei
 */
public class NumberUtil {
	
	public static Integer parseInt(String number, Integer defValue) {
		try {
			if(number!=null && !number.isEmpty()) return Integer.parseInt(number);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}

	public static Long parseLong(String number, Long defValue) {
		try {
			if(number!=null && !number.isEmpty()) return Long.parseLong(number);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}

	public static Double parseDouble(String number, Double defValue) {
		try {
			if(number!=null && !number.isEmpty()) return Double.parseDouble(number);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}
	
	public static BigInteger parseBigInteger(String number, BigInteger defValue) {
		try {
			if(number!=null && !number.isEmpty()) return new BigInteger(number);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}
	
	/**
	 * 字符串转换成BigDecimal，最好的方式就是new BigDecimal(string)
	 */
	public static BigDecimal parseBigDecimal(String number, BigDecimal defValue) {
		try {
			if(number!=null && !number.isEmpty()) return new BigDecimal(number);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T parse(String number, Class<T> type, T defValue) {
		if(type==Integer.class) {
			return (T) parseInt(number, (Integer)defValue);
		}else if(type==Long.class) {
			return (T) parseLong(number, (Long)defValue);
		}else if(type==Double.class) {
			return (T) parseDouble(number, (Double)defValue);
		}else if(type==Boolean.class) {
			return (T) parseBoolean(number, (Boolean)defValue);
		}else if(type==BigDecimal.class) {
			return (T) parseBigDecimal(number, (BigDecimal)defValue);
		}else if(type==BigInteger.class) {
			return (T) parseBigInteger(number, (BigInteger)defValue);
		}
		return null;
	}
	
    private static String[] trueStrings = {"true", "yes", "y", "on", "1"};
    private static String[] falseStrings = {"false", "no", "n", "off", "0"};
    public static Boolean parseBoolean(String value, Boolean defValue) {
		if(value!=null && !value.isEmpty()) {
	        String stringValue = value.toString().toLowerCase();
	        for(int i=0; i<trueStrings.length; ++i) 
	            if (trueStrings[i].equals(stringValue)) return Boolean.TRUE;
	        for(int i=0; i<falseStrings.length; ++i) 
	            if (falseStrings[i].equals(stringValue)) return Boolean.FALSE;
		}
		return defValue;
    }
}
