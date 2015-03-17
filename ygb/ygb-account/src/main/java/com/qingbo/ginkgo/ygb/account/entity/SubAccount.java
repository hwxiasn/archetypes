package com.qingbo.ginkgo.ygb.account.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class SubAccount extends BaseEntity {
	private static final long serialVersionUID = 8642927456566456659L;

	private Long accountId;//主账户
	private String type;//子账户类型，如默认账户、乾多多账户等
	private BigDecimal balance = BigDecimal.ZERO;//账户可用余额
	private BigDecimal freezeBalance = BigDecimal.ZERO;//账户冻结金额
	
	/**
	 * 判断子账户是不是乾多多账户类型
	 */
	@Transient
	public boolean isQddAccount() {
		return type!=null && type.startsWith("QDD_");
	}
	
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
