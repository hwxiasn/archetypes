package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class OpenResult<T> implements Serializable {

	private static final long serialVersionUID = 8741989579372293866L;

	private Integer symbol;
	private String message;
	private Integer total;
	private Integer pageSize;
	private Integer pageNum;
	private T result;

	public Integer getSymbol() {
		return symbol;
	}
	public void setSymbol(Integer symbol) {
		this.symbol = symbol;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	public T getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}
	
	
	/**
	 * 创建返回结果
	 */
	public static <T> OpenResult<T> newResult(Integer symbol, String message, T object){
		OpenResult<T> result = new OpenResult<T>();
		result.setSymbol(symbol);
		result.setMessage(message);
		result.setResult(object);
		return result;
	}
	
	/**
	 * 创建返回结果
	 */
	public static <T> OpenResult<T> newResult(Integer symbol, String message, Integer total, Integer pageSize, Integer pageNum, T object){
		OpenResult<T> result = new OpenResult<T>();
		result.setSymbol(symbol);
		result.setMessage(message);
		result.setTotal(total);
		result.setPageSize(pageSize);
		result.setPageNum(pageNum);
		result.setResult(object);
		return result;
	}
	
	public static final int OK 				= 0;
	public static final int INVALID_PARAM	= 1;
	public static final int AK_FAILURE 		= 2;
	public static final int CK_FAILURE 		= 3;
	public static final int DATA_ERROR		= 4;
}
