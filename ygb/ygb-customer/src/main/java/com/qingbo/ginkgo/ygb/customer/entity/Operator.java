package com.qingbo.ginkgo.ygb.customer.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class Operator extends BaseEntity{
	
	private static final long serialVersionUID=4267467282380971239L;
	
	private Long userId;//用户id
	private String userName;//用户名
	private String status;//状态
	private String auditPassword;//审核密码
	private String auditSalt;//审核密码盐
	private String level;//级别
	private Long  organizationId;//所属机构
	private String displayOrgName;//企业简称
	private String memo;//备注
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getAuditPassword() {
		return auditPassword;
	}
	public void setAuditPassword(String auditPassword) {
		this.auditPassword = auditPassword;
	}
	public String getAuditSalt() {
		return auditSalt;
	}
	public void setAuditSalt(String auditSalt) {
		this.auditSalt = auditSalt;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public Long getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getDisplayOrgName() {
		return displayOrgName;
	}
	public void setDisplayOrgName(String displayOrgName) {
		this.displayOrgName = displayOrgName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
