package com.qingbo.gingko.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.gingko.entity.base.BaseEntity;

@Entity
public class User extends BaseEntity {
	private static final long serialVersionUID = 4267467282380976239L;

	private String userName;
	private String password;
	private String salt;
	
	private String status;
	private String roleIds;
	private String permissionIds;
	
	@Transient private UserProfile userProfile;
	@Transient private UserEnterpriseProfile enterpriseProfile;
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRoleIds() {
		return roleIds;
	}
	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}
	public String getPermissionIds() {
		return permissionIds;
	}
	public void setPermissionIds(String permissionIds) {
		this.permissionIds = permissionIds;
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
}
