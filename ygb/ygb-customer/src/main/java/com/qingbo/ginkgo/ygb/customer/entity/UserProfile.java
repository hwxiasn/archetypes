package com.qingbo.ginkgo.ygb.customer.entity;

import java.util.Date;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class UserProfile extends BaseEntity {
	private static final long serialVersionUID = 4276497957897571247L;
	
	private Long userId;//用户id
	private String realName;//真实姓名
	private String mobile;//电话
	private String email;//邮箱
	private String idNum;//证件号码
	private	String idType;//证件类型
	private String gender;//性别
	private Date idValidTo;//证件到期时间
	private String idCopyFront;//证件扫描件正面照片
	private String idCopyBank;//证件照扫描件反面
	private String auditPassword;//审核密码
	private String auditSalt; //审核密码盐
	private String customerNum;//客户编号
	private String memo;//备注
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
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
	public String getIdNum() {
		return idNum;
	}
	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getIdValidTo() {
		return idValidTo;
	}
	public void setIdValidTo(Date idValidTo) {
		this.idValidTo = idValidTo;
	}
	public String getIdCopyFront() {
		return idCopyFront;
	}
	public void setIdCopyFront(String idCopyFront) {
		this.idCopyFront = idCopyFront;
	}
	public String getIdCopyBank() {
		return idCopyBank;
	}
	public void setIdCopyBank(String idCopyBank) {
		this.idCopyBank = idCopyBank;
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

	public String getCustomerNum() {
		return customerNum;
	}
	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
}
