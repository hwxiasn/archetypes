package com.qingbo.ginkgo.ygb.customer.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;


@Entity
public class UserStatus extends BaseEntity{

	private static final long serialVersionUID = 456829478512563215L;
	
	private Long userId;//用户主键
	private String activated;//是否激活(1：已激活 0 未激活)
	private String emailbinding;//邮箱是否绑定(1：已绑定 0 未绑定)
	private String mobilebinding;//手机是否绑定(1：已绑定 0 未绑定)
	private String realNameAuthStatus;//是否实名认证(1：已认证 0 未认证)
	private String bankbinding;//银行卡绑定
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getActivated() {
		return activated;
	}
	public void setActivated(String activated) {
		this.activated = activated;
	}
	public String getEmailbinding() {
		return emailbinding;
	}
	public void setEmailbinding(String emailbinding) {
		this.emailbinding = emailbinding;
	}
	public String getMobilebinding() {
		return mobilebinding;
	}
	public void setMobilebinding(String mobilebinding) {
		this.mobilebinding = mobilebinding;
	}
	public String getRealNameAuthStatus() {
		return realNameAuthStatus;
	}
	public void setRealNameAuthStatus(String realNameAuthStatus) {
		this.realNameAuthStatus = realNameAuthStatus;
	}
	public String getBankbinding() {
		return bankbinding;
	}
	public void setBankbinding(String bankbinding) {
		this.bankbinding = bankbinding;
	}

	
	
}
