package com.qingbo.gingko.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;

import com.qingbo.gingko.entity.base.BaseEntity;

@Entity
public class AccountLog extends BaseEntity {
	private static final long serialVersionUID = -6113403037086109874L;

	private String type;//类型：in收入，out支出，freeze冻结或解冻
	private String subType;//子类型：deposit充值，withdraw提现，transfer转账（investment投资、commission分佣），freeze冻结，un_freeze解冻，prize奖励，fee手续费
	private Integer transactionId;
	private String outBizNo;//外部流水号
	private Integer subAccountId;//日志操作账户，其他信息如userName+accountId+orderNo等通过查询获得，或直接使用sql连接查询
	private Integer otherSubAccountId;//日志操作相关账户，如转账交易的另一方等
	private BigDecimal balance;//账户变化金额
	private BigDecimal transAmount;//交易金额，充值+转账时与账户变化金额相等，提现时可能会有不同
	private BigDecimal fee;//手续费
	private Integer feeSubAccountId;//手续费支付方，如果不是subAccountId支付手续费，则另记一笔日志
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
	public Integer getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	public String getOutBizNo() {
		return outBizNo;
	}
	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}
	public Integer getSubAccountId() {
		return subAccountId;
	}
	public void setSubAccountId(Integer subAccountId) {
		this.subAccountId = subAccountId;
	}
	public Integer getOtherSubAccountId() {
		return otherSubAccountId;
	}
	public void setOtherSubAccountId(Integer otherSubAccountId) {
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
	public Integer getFeeSubAccountId() {
		return feeSubAccountId;
	}
	public void setFeeSubAccountId(Integer feeSubAccountId) {
		this.feeSubAccountId = feeSubAccountId;
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
