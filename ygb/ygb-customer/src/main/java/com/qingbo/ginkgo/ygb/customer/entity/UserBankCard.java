package com.qingbo.ginkgo.ygb.customer.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;


@Entity
public class UserBankCard extends BaseEntity{
	private static final long serialVersionUID = 125356391542321458L;
	
	private Long userId;//用户主键
	private String bankCardType;//银行卡类型
	private String bankCode;//银行代码
	private String bankCardNum;//银行卡号
	private String bankCardAccountName;//账户名
	private String bankBranch;//开户支行
	private String idType;//证件类型
	private String idNum;//证件号
	private String province;//省
	private String city;//市
	private String address;//地区
	private String memo;//备注
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getBankCardType() {
		return bankCardType;
	}
	public void setBankCardType(String bankCardType) {
		this.bankCardType = bankCardType;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBankCardNum() {
		return bankCardNum;
	}
	public void setBankCardNum(String bankCardNum) {
		this.bankCardNum = bankCardNum;
	}
	public String getBankCardAccountName() {
		return bankCardAccountName;
	}
	public void setBankCardAccountName(String bankCardAccountName) {
		this.bankCardAccountName = bankCardAccountName;
	}
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNum() {
		return idNum;
	}
	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
	
	
	
}
