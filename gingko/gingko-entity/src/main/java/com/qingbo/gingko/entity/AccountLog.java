package com.qingbo.gingko.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;

import com.qingbo.gingko.entity.base.BaseEntity;

@Entity
public class AccountLog extends BaseEntity {
	private static final long serialVersionUID = -6113403037086109874L;

	private String type;//类型AccountLogType，in收入，out支出，freeze冻结，unfreeze解冻
	private String subType;//子类型AccountLogSubType，deposit充值，withdraw提现，transfer转账，fee手续费
	private String transferType;//转账类型AccountLogTransferType，investment投资、commission分佣，repay还款，prize奖励，transfer转账
	private Long transactionId;//交易
	private Long subAccountId;//账户
	private Long otherSubAccountId;//相关账户，如转账交易的另一方等
	private BigDecimal balance = BigDecimal.ZERO;//账户变化金额
	private BigDecimal transAmount = BigDecimal.ZERO;//交易金额，充值+转账时与账户变化金额相等，提现时可能会有不同
	private BigDecimal fee = BigDecimal.ZERO;//手续费
	private Long feeSubAccountId;//手续费支付方，如果不是subAccountId支付手续费，则另记一笔日志
	private String memo;//备注或描述
	private boolean executed;//是否已同步到账户金额
	private BigDecimal accountBalance;//账务日志同步后，账户的余额和冻结金额
	private BigDecimal accountFreezeBalance;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	public Long getSubAccountId() {
		return subAccountId;
	}
	public void setSubAccountId(Long subAccountId) {
		this.subAccountId = subAccountId;
	}
	public Long getOtherSubAccountId() {
		return otherSubAccountId;
	}
	public void setOtherSubAccountId(Long otherSubAccountId) {
		this.otherSubAccountId = otherSubAccountId;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(BigDecimal transAmount) {
		this.transAmount = transAmount;
	}
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	public Long getFeeSubAccountId() {
		return feeSubAccountId;
	}
	public void setFeeSubAccountId(Long feeSubAccountId) {
		this.feeSubAccountId = feeSubAccountId;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public boolean isExecuted() {
		return executed;
	}
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}
	public BigDecimal getAccountBalance() {
		return accountBalance;
	}
	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}
	public BigDecimal getAccountFreezeBalance() {
		return accountFreezeBalance;
	}
	public void setAccountFreezeBalance(BigDecimal accountFreezeBalance) {
		this.accountFreezeBalance = accountFreezeBalance;
	}
}
