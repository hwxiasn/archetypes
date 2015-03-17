package com.qingbo.ginkgo.ygb.account.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class AccountLog extends BaseEntity {
	private static final long serialVersionUID = -6113403037086109874L;

	private String type;//类型AccountLogType，in收入，out支出，freeze冻结，unfreeze解冻
	private String subType;//子类型AccountLogSubType，deposit充值，withdraw提现，transfer转账，fee手续费
	private String transferType;//转账类型AccountLogTransferType，investment投资、commission分佣，repay还款，prize奖励，transfer转账
	private Long tradeId;//交易，异步处理时有值，可能是dealId
	private Long subAccountLogId;//子账户交易日志，例如QddAccountLog
	private Long subAccountId;//账户
	private Long otherSubAccountId;//相关账户，如转账交易的另一方等
	private BigDecimal balance = BigDecimal.ZERO;//账户变化金额
	private BigDecimal transAmount = BigDecimal.ZERO;//交易金额，充值+转账时与账户变化金额相等，提现时可能会有不同
	private BigDecimal fee = BigDecimal.ZERO;//手续费
	private Long feeSubAccountId;//手续费支付方，如果不是subAccountId支付手续费，则另记一笔日志
	private String memo;//备注或描述
	private boolean executed;//是否已同步到账户金额
	private BigDecimal accountBalance = BigDecimal.ZERO;//账务日志同步前，账户的余额和冻结金额
	private BigDecimal accountFreezeBalance = BigDecimal.ZERO;
	private BigDecimal accountBalance2 = BigDecimal.ZERO;//账务日志同步后，账户的余额和冻结金额
	private BigDecimal accountFreezeBalance2 = BigDecimal.ZERO;
	
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
	public Long getTradeId() {
		return tradeId;
	}
	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}
	public Long getSubAccountLogId() {
		return subAccountLogId;
	}
	public void setSubAccountLogId(Long subAccountLogId) {
		this.subAccountLogId = subAccountLogId;
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
	public BigDecimal getAccountBalance2() {
		return accountBalance2;
	}
	public void setAccountBalance2(BigDecimal accountBalance2) {
		this.accountBalance2 = accountBalance2;
	}
	public BigDecimal getAccountFreezeBalance2() {
		return accountFreezeBalance2;
	}
	public void setAccountFreezeBalance2(BigDecimal accountFreezeBalance2) {
		this.accountFreezeBalance2 = accountFreezeBalance2;
	}
}
