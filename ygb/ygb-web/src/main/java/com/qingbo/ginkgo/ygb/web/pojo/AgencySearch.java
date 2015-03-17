package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class AgencySearch implements Serializable{

	private static final long serialVersionUID = -2424623714150022097L;
	/** 用户名 */
	public String userName;
	/** 企业名称 */
	public String enterpriseName;
	/** 联系人姓名 */
	public String contactName;
	/** 支付账户ID */
	//public String paymentAccountId;
	/** 用户角色 */
	public String role;
	/** 用户状态：正常，已冻结 */
	public String status;
	
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
}
