package com.qingbo.ginkgo.ygb.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 常见日期类型处理：字符串、日期Date、长整数new Date(long)，parse(time).getTime()
 * @author hongwei
 */
public class DateUtil {
	public static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat microSeconds = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public static final SimpleDateFormat smallMicroSeconds = new SimpleDateFormat("yyMMddHHmmss");
	
	public static enum FormatType { 
		/** yyyy, curdate() */
		YEAR, 
		/** yyyy-MM-dd, curdate() */
		DAY, 
		/** yyyy-MM-dd HH:mm:ss, now() */
		DAYTIME, 
		/** yyyyMMddHHmmssSSS, 17位毫秒时间序列号 */
		MICROSECONDS,
		/** yyMMddHHmmss, 12位毫秒时间序列号 */
		SMALLMICROSECONDS,
		/** 1406167122870, System.currentTimeInMillis() */
		JAVA, 
		/** 1406166160, unix_timestamp(now( )) */
		MYSQL
	};

	/** 
	 * @param time 支持格式：<li>yyyy-MM-dd, mysql: curdate()</li>
	 * <li>yyyy-MM-dd HH:mm:ss, mysql: now()</li>
	 * <li>1406167122870，java: System.currentTimeInMillis()</li> 
	 * <li>1406166160，mysql: unix_timestamp(now())</li> 
	 */
	public static Date parse(String time) {
		if(time==null || time.isEmpty()) return null;
		try {
			return dayTime.parse(time);
		}catch(Exception ex) {}
		try{
			return day.parse(time);
		}catch (Exception e) {}
		if(time.matches("\\d{10,17}")) {
			if(time.length()==17) {
				try{
					return microSeconds.parse(time);
				}catch(Exception e) {}
			}else if(time.length()==13) {
				return new Date(Long.valueOf(time));
			}else if(time.length()==12) {
				try{
					return smallMicroSeconds.parse(time);
				}catch(Exception e) {}
			}else if(time.length()==10) {
				return new Date(Long.valueOf(time)*1000);
			}
		}
		return null;
	}
	
	/** 
	 * @param type 格式化日期<li>DAY yyyy-MM-dd, mysql: curdate()</li>
	 * <li>DAYTIME yyyy-MM-dd HH:mm:ss, mysql: now()</li>
	 * <li>JAVA 1406167122870，java: System.currentTimeInMillis()</li> 
	 * <li>MYSQL 1406166160，mysql: unix_timestamp(now())</li> 
	 */
	public static String format(Date date, FormatType type) {
		if (date == null) {
			return null;
		}
		switch(type) {
		case DAY:
			return day.format(date);
		case DAYTIME:
			return dayTime.format(date);
		case MICROSECONDS:
			return microSeconds.format(date);
		case SMALLMICROSECONDS:
			return smallMicroSeconds.format(date);
		case JAVA:
			return String.valueOf(date.getTime());
		case MYSQL:
			return String.valueOf(date.getTime()/1000);
		}
		return null;
	}
}
