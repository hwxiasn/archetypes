package com.qingbo.gingko.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;

import com.qingbo.gingko.entity.base.BaseEntity;

@Entity
public class SubAccount extends BaseEntity {
	private static final long serialVersionUID = 8642927456566456659L;

	private Long accountId;//主账户
	private String type;//子账户类型，如默认账户、乾多多账户等
	private BigDecimal balance = BigDecimal.ZERO;//账户可用余额
	private BigDecimal freezeBalance = BigDecimal.ZERO;//账户冻结金额
	
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getFreezeBalance() {
		return freezeBalance;
	}
	public void setFreezeBalance(BigDecimal freezeBalance) {
		this.freezeBalance = freezeBalance;
	}
}
