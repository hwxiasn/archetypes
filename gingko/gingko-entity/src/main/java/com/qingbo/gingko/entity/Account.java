package com.qingbo.gingko.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.gingko.entity.base.BaseEntity;

@Entity
public class Account extends BaseEntity {
	private static final long serialVersionUID = -8137124955713943810L;
	
	private String password;//账户密码，保护资金安全
	private String salt;

	@Transient List<SubAccount> subAccounts;//子账户列表（不为null）
	@Transient BigDecimal balance;//子账户余额之和
	@Transient BigDecimal freezeBalance;//子账户冻结金额之和

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public List<SubAccount> getSubAccounts() {
		return subAccounts;
	}
	public void setSubAccounts(List<SubAccount> subAccounts) {
		this.subAccounts = subAccounts;
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
