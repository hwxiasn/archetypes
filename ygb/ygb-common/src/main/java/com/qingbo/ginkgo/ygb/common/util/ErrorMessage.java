package com.qingbo.ginkgo.ygb.common.util;

import java.util.Properties;

import com.qingbo.ginkgo.ygb.common.result.Result;

/**
 * <pre>
 *
 * //ACT0001=simple message
 * //ACT0002=message with args:{0}
 * ErrorMessage errors = new ErrorMessage("errors.properties");
 * 
 * //simple error result
 * Result result = errors.newFailure("ACT0001");
 * 
 * //result with args
 * Result result2 = errors.newFailure("ACT0002", userId);
 * </pre>
 * @author hongwei
 *
 */
public class ErrorMessage {
	private Properties props = new Properties();
	private Object nullArg = "null";
	private Object[] nullArgs = new Object[] {nullArg};
	
	public ErrorMessage(String resource) {
		if(resource!=null && resource.length()>0) {
			props = PropertiesUtil.get(resource, "utf-8");
		}
	}
	
	/**
	 * 返回简单错误消息
	 */
	public <T> Result<T> newFailure(String error){
		Result<T> result = new Result<>();
		result.setCode(-1);
		result.setError(error);
		if(props!=null && error!=null) result.setMessage(props.getProperty(error));
		return result;
	}
	
	/**
	 * 返回带参数的错误消息
	 */
	public <T> Result<T> newFailure(String error, Object ... args){
		Result<T> result = new Result<>();
		result.setCode(-1);
		result.setError(error);
		if(props!=null && error!=null) {
			if(args==null) args = nullArgs;
			else for(int idx=0; idx<args.length; idx++) {
					if(args[idx]==null) args[idx] = nullArg;
					else args[idx] = String.valueOf(args[idx]);
				}
			result.setMessage(PropertiesUtil.getMessage(props, error, args));
		}
		return result;
	}
}
