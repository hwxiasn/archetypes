package com.qingbo.ginkgo.ygb.account.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class QddAccountLog extends BaseEntity {
	private static final long serialVersionUID = -6802295242780406692L;

	private Long tradeId;//唯一交易号，充值和提现等异步通知会有
	private String orderNo;//唯一订单号
	
	private String platformId;
	private String moneyMoreMoreId;
	private BigDecimal amount = BigDecimal.ZERO;
	
	private String otherMoneyMoreMoreId;//另一方，转账会有
	
	private String bankCode;//提现银行代码
	private String cardNo;//提现银行卡号
	private BigDecimal fee = BigDecimal.ZERO;//手续费，提现会有
	private String feeMoneyMoreMoreId;//手续费支付方，平台垫付手续费会有
	
	private String memo;//备注
	
	private int auditPass;//1-审核通过，2-审核拒绝
	@Temporal(TemporalType.TIMESTAMP) @Column(insertable=false, updatable=true) private Date updateAt;
	
	private String loanNo;//乾多多唯一流水号
	private String resultCode;
	private String message;
	
	private String callback;//完整回调信息
	private String orderquery;//对账信息

	public boolean success() {
		return "88".equals(resultCode);
	}
	
	public Long getTradeId() {
		return tradeId;
	}
	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getPlatformId() {
		return platformId;
	}
	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}
	public String getMoneyMoreMoreId() {
		return moneyMoreMoreId;
	}
	public void setMoneyMoreMoreId(String moneyMoreMoreId) {
		this.moneyMoreMoreId = moneyMoreMoreId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getOtherMoneyMoreMoreId() {
		return otherMoneyMoreMoreId;
	}
	public void setOtherMoneyMoreMoreId(String otherMoneyMoreMoreId) {
		this.otherMoneyMoreMoreId = otherMoneyMoreMoreId;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	public String getFeeMoneyMoreMoreId() {
		return feeMoneyMoreMoreId;
	}
	public void setFeeMoneyMoreMoreId(String feeMoneyMoreMoreId) {
		this.feeMoneyMoreMoreId = feeMoneyMoreMoreId;
	}
	public int getAuditPass() {
		return auditPass;
	}
	public void setAuditPass(int auditPass) {
		this.auditPass = auditPass;
	}
	public Date getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getLoanNo() {
		return loanNo;
	}
	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getOrderquery() {
		return orderquery;
	}
	public void setOrderquery(String orderquery) {
		this.orderquery = orderquery;
	}
}
