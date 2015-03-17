package com.qingbo.ginkgo.ygb.cms.entity;

import java.io.Serializable;

public class MessageCounter implements Serializable {

	private static final long serialVersionUID = -1790632956155684928L;

	private Integer totalCnt;
	private Integer noReadCnt;

	public Integer getTotalCnt() {
		return totalCnt;
	}
	public void setTotalCnt(Integer totalCnt) {
		this.totalCnt = totalCnt;
	}
	public Integer getNoReadCnt() {
		return noReadCnt;
	}
	public void setNoReadCnt(Integer noReadCnt) {
		this.noReadCnt = noReadCnt;
	}
	
	public MessageCounter(){
		super();
	}
	
	public MessageCounter(Integer total, Integer noread){
		super();
		this.totalCnt = total;
		this.noReadCnt = noread;
	}
}
