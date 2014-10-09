package com.qingbo.project.common.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * 获取请求参数工具类
 * @author hongwei
 */
public class RequestUtil {
	/**
	 * 获取字符串参数并置入属性值
	 */
	public static String getStringParam(HttpServletRequest request, String name, String defVal) {
		String value = request.getParameter(name);
		if(value == null) value = defVal;
		if(value != null) request.setAttribute(name, value);
		return value;
	}
	
	/**
	 * 获取整数参数并置入属性值
	 */
	public static Integer getIntParam(HttpServletRequest request, String name, Integer defVal) {
		String value = request.getParameter(name);
		Integer retVal = NumberUtil.parseInt(value, defVal);
		if(retVal != null) request.setAttribute(name, retVal);
		return retVal;
	}
	
	/**
	 * 获取长整数参数并置入属性值
	 */
	public static Long getLongParam(HttpServletRequest request, String name, Long defVal) {
		String value = request.getParameter(name);
		Long retVal = NumberUtil.parseLong(value, defVal);
		if(retVal != null) request.setAttribute(name, retVal);
		return retVal;
	}
	
	/**
	 * 获取整数参数并置入属性值
	 */
	public static Double getDoubleParam(HttpServletRequest request, String name, Double defVal) {
		String value = request.getParameter(name);
		Double retVal = NumberUtil.parseDouble(value, defVal);
		if(retVal != null) request.setAttribute(name, retVal);
		return retVal;
	}
	
	/**
	 * 获取布尔参数并置入属性值，支持true|yes|on|y|1
	 */
	public static Boolean getBoolParam(HttpServletRequest request, String name, Boolean defVal) {
		String value = request.getParameter(name);
		Boolean retVal = NumberUtil.parseBoolean(value, defVal);
		if(retVal != null) request.setAttribute(name, retVal);
		return retVal;
	}
	
	/**
	 * 打印请求参数为字符串
	 * @param request
	 */
	public static String print(HttpServletRequest request) {
		StringBuilder str = new StringBuilder(request.getMethod());
		str.append(request.getRequestURI());
		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String[] parameterValues = request.getParameterValues(parameterName);
			str.append("\n");
			str.append(parameterName+"=");
			str.append(parameterValues.length==1?parameterValues[0]:Arrays.toString(parameterValues));
		}
		return str.toString();
	}
	
	/**
	 * 返回请求参数为映射，Object为String或String[]
	 */
	public static Map<String, Object> paramsMap(HttpServletRequest request){
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String[] parameterValues = request.getParameterValues(parameterName);
			if(parameterValues==null || parameterValues.length==0) continue;
			paramsMap.put(parameterName, parameterValues.length==1 ? parameterValues[0] : parameterNames);
		}
		return paramsMap;
	}
	
	/**
	 * 返回请求参数为映射
	 */
	public static Map<String, String> stringMap(HttpServletRequest request){
		Map<String, String> paramsMap = new HashMap<>();
		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String[] parameterValues = request.getParameterValues(parameterName);
			if(parameterValues==null || parameterValues.length==0) continue;
			paramsMap.put(parameterName, parameterValues.length==1 ? parameterValues[0] : StringUtils.join(parameterValues, ","));
		}
		return paramsMap;
	}
}
