package com.qingbo.ginkgo.ygb.customer.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;
import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;


@Entity
public class User extends BaseEntity {
	private static final long serialVersionUID = 4267467282380976239L;
	
	
	private String userName;//用户名
	private String password;//密码
	private String salt;//密码盐
	private String registerType;//注册类型
	private String regSource;//注册来源
	private String activateCode;
	private Date regDate;//注册时间
	private String role;//角色
	private String status;//用户状态(A：正常  I:未激活   L：已冻结 D：已禁用)
	private Date lastLoginTime;//最后一次登录时间
	
	@Transient private UserProfile userProfile;//个人详细信息
	@Transient private UserEnterpriseProfile enterpriseProfile;//企业详细信息
	
	
	
	public String getActivateCode() {
		return activateCode;
	}
	public void setActivateCode(String activateCode) {
		this.activateCode = activateCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
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
	public String getRegisterType() {
		return registerType;
	}
	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}
	public String getRegSource() {
		return regSource;
	}
	public void setRegSource(String regSource) {
		this.regSource = regSource;
	}
	public Date getRegDate() {
		return regDate;
	}
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public UserProfile getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}
	public UserEnterpriseProfile getEnterpriseProfile() {
		return enterpriseProfile;
	}
	public void setEnterpriseProfile(UserEnterpriseProfile enterpriseProfile) {
		this.enterpriseProfile = enterpriseProfile;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
