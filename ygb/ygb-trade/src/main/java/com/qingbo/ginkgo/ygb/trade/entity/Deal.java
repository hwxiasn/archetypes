package com.qingbo.ginkgo.ygb.trade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class Deal extends BaseEntity {

	private static final long serialVersionUID = -9069160055825047720L;
	private Long      batchId = 0L;// 交易批次
	private Long   tradeId= 0L;//交易流水号
	private Long      sourceDealId = 0L;//关联交易执行流水号
	private String    creditAccount= "";//出借方交易账户ID（帐务流水号）
	private String    debitAccount= "";//贷入方交易账户ID（帐务流水号）
	private BigDecimal tradeAmount= BigDecimal.ZERO;//  实际交易金额，单位：人民币，元，保留2位精度
	private String    tradeCheck= "";//交易检验结果 （检验通过、检验失败、无需检验）
	private String    tradeType= "";//交易类型 （投资、还款、分佣、充值、提现、转账）
	private String    tradeAction= "";//交易操作类型  （冻结、操作）
	private String    tradeParam= "";//交易参数
	private String    tradeDispatch= "";//交易目的地址（需要手工参与的业务需要）
	private String    tradeStatus= "";//交易执行状态
	private String 	  callBack = "";
	private String    message = "";
	private String    memo= "";//备注
	private Date 	dealDate ;//执行结束时间

	
	
	public String getTradeAction() {
		return tradeAction;
	}
	public void setTradeAction(String tradeAction) {
		this.tradeAction = tradeAction;
	}
	public String getTradeCheck() {
		return tradeCheck;
	}
	public void setTradeCheck(String tradeCheck) {
		this.tradeCheck = tradeCheck;
	}
	public String getTradeParam() {
		return tradeParam;
	}
	public void setTradeParam(String tradeParam) {
		this.tradeParam = tradeParam;
	}
	public String getTradeDispatch() {
		return tradeDispatch;
	}
	public void setTradeDispatch(String tradeDispatch) {
		this.tradeDispatch = tradeDispatch;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Long getTradeId() {
		return tradeId;
	}
	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}
	public Long getSourceDealId() {
		return sourceDealId;
	}
	public void setSourceDealId(Long sourceTradeId) {
		this.sourceDealId = sourceTradeId;
	}
	public String getCreditAccount() {
		return creditAccount;
	}
	public void setCreditAccount(String creditAccount) {
		this.creditAccount = creditAccount;
	}
	public String getDebitAccount() {
		return debitAccount;
	}
	public void setDebitAccount(String debitAccount) {
		this.debitAccount = debitAccount;
	}
	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	public String getTradeStatus() {
		return tradeStatus;
	}
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	public String getCallBack() {
		return callBack;
	}
	public void setCallBack(String callBack) {
		this.callBack = callBack;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getDealDate() {
		return dealDate;
	}
	public void setDealDate(Date dealDate) {
		this.dealDate = dealDate;
	}
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

}
