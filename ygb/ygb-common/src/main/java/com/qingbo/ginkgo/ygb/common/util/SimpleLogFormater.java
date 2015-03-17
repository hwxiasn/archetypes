package com.qingbo.ginkgo.ygb.common.util;

import com.alibaba.fastjson.JSON;

/**
 * 日志格式化输出工具
 * @author Kent
 */
public class SimpleLogFormater {

	/**
	 * 将方法调用的参数格式化输出
	 * @param params
	 * @return
	 */
	public static String formatParams(Object... params){
		int paramsLength = params.length;
		
		StringBuffer formated = new StringBuffer("Parameters :");
        if(paramsLength == 0){
        	formated.append(" NO PARAM!");
        }else{
        	for(int i = 0; i < paramsLength; i++){
        		formated.append(" PARAM" + (i + 1) + ":" + JSON.toJSON(params[i]) + ";");
        	}
        }
		return formated.toString();
	}

	/**
	 * 将方法调用的返回结果格式化输出
	 * @param result
	 * @return
	 */
	public static String formatResult(Object result){
		return "Result : " + JSON.toJSON(result) + ".";
	}
	
	/**
	 * 将捕捉到的异常格式化输出
	 * @param result
	 * @return
	 */
	public static String formatException(String bizErrorInfo, Exception e){
		StringBuffer formated = new StringBuffer("Error : " + bizErrorInfo + "\n");
		formated.append("Exception message : " + e.getMessage() + "\n");
		formated.append("Exception trace : \n");
		for(StackTraceElement elment : e.getStackTrace()){
			formated.append(elment.toString() + "\n");
		}
		return formated.toString();
	}
}