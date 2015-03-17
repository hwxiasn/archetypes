package com.qingbo.ginkgo.ygb.project.entity;

import java.util.Date;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class BorrowerInfo extends BaseEntity {

	private static final long serialVersionUID = -2922810897632434924L;

	private String name;			// 借款人姓名
	private Integer age;			// 借款人年龄
	private String idSerial;		// 借款人身份证号码
	private String sex;				// 借款人性别
	private Integer maritalStatus;	// 借款人婚姻状况
	private String phone;			// 借款人电话号码
	private String contactQq;		// 借款人QQ号码
	private String contact1;		// 借款人备用联系方式1
	private String contact2;		// 借款人备用联系方式2
	private String address;			// 借款人现住址
	private Integer loanAmount;		// 借款金额
	private Integer loanTerm;		// 借款期限
	private String purpose;			// 借款用途
	private String returnSource;	// 还款来源
	private String businessScope;	// 公司经营范围
	private Integer registerCapital;// 公司注册资金
	private Integer foundYear;		// 公司成立年份
	private Integer foundMonth;		// 公司成立月份
	private Date createTime;		// 信息录入时间
	private Integer status;			// 信息状态
	private String message;			// 留言附注
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getIdSerial() {
		return idSerial;
	}
	public void setIdSerial(String idSerial) {
		this.idSerial = idSerial;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(Integer maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getContactQq() {
		return contactQq;
	}
	public void setContactQq(String contactQq) {
		this.contactQq = contactQq;
	}
	public String getContact1() {
		return contact1;
	}
	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}
	public String getContact2() {
		return contact2;
	}
	public void setContact2(String contact2) {
		this.contact2 = contact2;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(Integer loanAmount) {
		this.loanAmount = loanAmount;
	}
	public Integer getLoanTerm() {
		return loanTerm;
	}
	public void setLoanTerm(Integer loanTerm) {
		this.loanTerm = loanTerm;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getReturnSource() {
		return returnSource;
	}
	public void setReturnSource(String returnSource) {
		this.returnSource = returnSource;
	}
	public String getBusinessScope() {
		return businessScope;
	}
	public void setBusinessScope(String businessScope) {
		this.businessScope = businessScope;
	}
	public Integer getRegisterCapital() {
		return registerCapital;
	}
	public void setRegisterCapital(Integer registerCapital) {
		this.registerCapital = registerCapital;
	}
	public Integer getFoundYear() {
		return foundYear;
	}
	public void setFoundYear(Integer foundYear) {
		this.foundYear = foundYear;
	}
	public Integer getFoundMonth() {
		return foundMonth;
	}
	public void setFoundMonth(Integer foundMonth) {
		this.foundMonth = foundMonth;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
