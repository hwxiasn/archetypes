package com.qingbo.ginkgo.ygb.trade.entity;

import java.util.Date;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;


@Entity
public class TradeLog extends BaseEntity {

	private static final long serialVersionUID = 5509902540106399711L;

	private Long   tradeId= 0L;//交易流水号
	private String tradeParam= "";//交易输出参数
	private Date   exeTime = new Date();//执行时间
	private String exeResult= "";//执行结果
	private String exeMsg= "";//执行反馈
	private String exeEngine= "";//驱动方式（手动、自动）
	private String exeUser= "";//执行者
	private String memo= "";//备注
	
	public Long getTradeId() {
		return tradeId;
	}
	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}
	public String getTradeParam() {
		return tradeParam;
	}
	public void setTradeParam(String tradeParam) {
		this.tradeParam = tradeParam;
	}
	public Date getExeTime() {
		return exeTime;
	}
	public void setExeTime(Date exeTime) {
		this.exeTime = exeTime;
	}
	public String getExeResult() {
		return exeResult;
	}
	public void setExeResult(String exeResult) {
		this.exeResult = exeResult;
	}
	public String getExeMsg() {
		return exeMsg;
	}
	public void setExeMsg(String exeMsg) {
		this.exeMsg = exeMsg;
	}
	public String getExeEngine() {
		return exeEngine;
	}
	public void setExeEngine(String exeEngine) {
		this.exeEngine = exeEngine;
	}
	public String getExeUser() {
		return exeUser;
	}
	public void setExeUser(String exeUser) {
		this.exeUser = exeUser;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}

}
