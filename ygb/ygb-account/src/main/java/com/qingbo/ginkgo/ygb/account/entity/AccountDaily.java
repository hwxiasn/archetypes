package com.qingbo.ginkgo.ygb.account.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class AccountDaily extends BaseEntity {
	private static final long serialVersionUID = 1L;
	private Long accountId;
	private String daily;//yyyy-mm-dd
	private BigDecimal balance = BigDecimal.ZERO;
	private BigDecimal freezeBalance = BigDecimal.ZERO;
	private BigDecimal dailyBalance = BigDecimal.ZERO;
	private BigDecimal dailyFreezeBalance = BigDecimal.ZERO;
	private String status;
	private String subBalances;//所有子账户状态id,balance,freezeBalance;
	private String dailySubBalances;
	@Transient BigDecimal totalBalance = BigDecimal.ZERO;
	@Transient Map<Long, SubAccount> subAccounts = new HashMap<>();
	@Transient Map<Long, SubAccount> dailySubAccounts = new HashMap<>();
	
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getDaily() {
		return daily;
	}
	public void setDaily(String daily) {
		this.daily = daily;
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
	public BigDecimal getDailyBalance() {
		return dailyBalance;
	}
	public void setDailyBalance(BigDecimal dailyBalance) {
		this.dailyBalance = dailyBalance;
	}
	public BigDecimal getDailyFreezeBalance() {
		return dailyFreezeBalance;
	}
	public void setDailyFreezeBalance(BigDecimal dailyFreezeBalance) {
		this.dailyFreezeBalance = dailyFreezeBalance;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSubBalances() {
		return subBalances;
	}
	public void setSubBalances(String subBalances) {
		this.subBalances = subBalances;
	}
	public String getDailySubBalances() {
		return dailySubBalances;
	}
	public void setDailySubBalances(String dailySubBalances) {
		this.dailySubBalances = dailySubBalances;
	}
	public BigDecimal getTotalBalance() {
		return totalBalance;
	}
	public void setTotalBalance(BigDecimal totalBalance) {
		this.totalBalance = totalBalance;
	}
	public Map<Long, SubAccount> getSubAccounts() {
		return subAccounts;
	}
	public void setSubAccounts(Map<Long, SubAccount> subAccounts) {
		this.subAccounts = subAccounts;
	}
	public Map<Long, SubAccount> getDailySubAccounts() {
		return dailySubAccounts;
	}
	public void setDailySubAccounts(Map<Long, SubAccount> dailySubAccounts) {
		this.dailySubAccounts = dailySubAccounts;
	}
}
