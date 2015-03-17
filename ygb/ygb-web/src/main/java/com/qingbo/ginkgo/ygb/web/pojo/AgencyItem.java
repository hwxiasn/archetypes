package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class AgencyItem implements Serializable{

	private static final long serialVersionUID = -5584884278480942773L;
	/** 用户标识 */
	private Long userId;
	
	private Long userProfileId;
	/** 支付账户ID */
	private String moneyMoreMoreId;
	
	private String userName;
	
	private String enterpriseName;
	
	private String role;
	
	private String contactName;
	
	private String status;
	
	private String  createTime;

	private String statusName;
	
	private String roleName;
	
	

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public String getMoneyMoreMoreId() {
		return moneyMoreMoreId;
	}

	public void setMoneyMoreMoreId(String moneyMoreMoreId) {
		this.moneyMoreMoreId = moneyMoreMoreId;
	}

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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
	
	
}
