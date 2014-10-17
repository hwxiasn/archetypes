package com.hwxiasn.contentserver.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User extends BaseEntity {
	private static final long serialVersionUID = 4267467282380976239L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer userId;
	
	private String userName;
	private String password;
	private String salt;
	
	private String status;
	private String roleIds;
	private String permissionIds;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
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
}
