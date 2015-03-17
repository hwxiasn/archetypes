package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class UserSercurityInfo implements Serializable {

	private static final long serialVersionUID = 8713843153930770026L;
	
	/**安全等级*/
	public String securityRank;
	
	/**登陆旧密码*/
	public String password;
	/**登陆新密码*/
	public String newPassword;
	/**登陆确认新密码*/
	public String confirmPassword;
	
	/**支付旧密码*/
	public String paymentCode;
	/**支付新密码*/
	public String newPaymentCode;
	/**支付确认新密码*/
	public String confirmPaymentCode;
	
	/**验证码*/
	public String securityCode;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getPaymentCode() {
		return paymentCode;
	}
	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}
	public String getNewPaymentCode() {
		return newPaymentCode;
	}
	public void setNewPaymentCode(String newPaymentCode) {
		this.newPaymentCode = newPaymentCode;
	}
	public String getConfirmPaymentCode() {
		return confirmPaymentCode;
	}
	public void setConfirmPaymentCode(String confirmPaymentCode) {
		this.confirmPaymentCode = confirmPaymentCode;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public String getSecurityRank() {
		return securityRank;
	}
	public void setSecurityRank(String securityRank) {
		this.securityRank = securityRank;
	}
}
