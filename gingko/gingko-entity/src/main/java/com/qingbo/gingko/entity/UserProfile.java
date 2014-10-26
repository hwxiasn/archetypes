package com.qingbo.gingko.entity;

import javax.persistence.Entity;

import com.qingbo.gingko.entity.base.BaseEntity;

@Entity
public class UserProfile extends BaseEntity {
	private static final long serialVersionUID = 4276497957897571247L;
	
	private Integer userId;
	private String realName;
	private String mobile;
	private String email;
	private String idNumber;
	private Integer age;
	private Boolean sex;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Boolean getSex() {
		return sex;
	}
	public void setSex(Boolean sex) {
		this.sex = sex;
	}
}
