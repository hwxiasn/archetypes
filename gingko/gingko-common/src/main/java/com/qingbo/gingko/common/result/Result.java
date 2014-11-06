package com.qingbo.gingko.common.result;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Result<T> implements Serializable {
	/**
	 * 接口调用成功，不需要返回对象
	 */
	public static <T> Result<T> newSuccess(){
		Result<T> result = new Result<>();
		return result;
	}
	
	/**
	 * 接口调用成功，有返回对象
	 */
	public static <T> Result<T> newSuccess(T object) {
		Result<T> result = new Result<>();
		result.setObject(object);
		return result;
	}
	
	/**
	 * 接口调用失败，有错误码和描述，没有返回对象
	 */
	public static <T> Result<T> newFailure(int code, String message){
		Result<T> result = new Result<>();
		result.setCode(code!=0 ? code : -1);
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 接口调用失败，有错误字符串码和描述，没有返回对象
	 */
	public static <T> Result<T> newFailure(String error, String message){
		Result<T> result = new Result<>();
		result.setCode(-1);
		result.setError(error);
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 接口调用失败，返回异常信息
	 */
	public static <T> Result<T> newException(Exception e){
		Result<T> result = new Result<>();
		result.setCode(-1);
		result.setException(e);
		result.setMessage(e.getMessage());
		return result;
	}
	
	private int code;
	private T object;
	private String error;
	private String message;
	private Exception exception;
	
	public boolean success() {
		return code == 0;
	}
	public boolean hasException() {
		return exception != null;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public T getObject() {
		return object;
	}
	public void setObject(T object) {
		this.object = object;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String toString() {
		StringBuilder result = new StringBuilder("Result");
		if(object!=null) result.append("<"+object.getClass().getSimpleName()+">");
		result.append(": {code="+code);
		if(object!=null) result.append(", object="+object);
		if(error!=null) result.append(", error="+error);
		if(message!=null) result.append(", message="+message);
		if(exception!=null) result.append(", exception="+exception);
		result.append(" }");
		return result.toString();
	}
}
