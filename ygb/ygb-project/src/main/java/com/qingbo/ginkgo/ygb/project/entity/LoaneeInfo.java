package com.qingbo.ginkgo.ygb.project.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class LoaneeInfo extends BaseEntity {

	private static final long serialVersionUID = -1628326564917648602L;
	private	Long	projectId	;//	'项目ID',
	private	String	loaneeName	;//	'借款人姓名',
	private	String	loaneeAge	;//	'借款人年龄',
	private	String	loaneeIdSerial	;//	'借款人身份证号码',
	private	String	loaneeSex	;//	'借款人性别',
	private	String	loaneeMaritalStatus	;//	'借款人婚姻状况',
	private	String	loaneeAddress	;//	'借款人居住地',
	private	String	loaneeHadCount	;//	'累计成功借款：*笔',
	private	String	loaneeHadAmount	;//	'累计借款金额：***万',
	private	String	loaneeNeedRepay	;//	'当前待还金额：***万',
	private	String	loaneeMissedRepay	;//	'累计逾期还款：0元',
	private	String	loaneePurpose	;//	'资金用途',
	private	String	loaneeSource	;//	'还款来源',
	private	String	loaneeEntAddress	;//	'经营地址',
	private	String	loaneeEntScope	;//	'经营范围',
	private	String	loaneeEntMoney	;//	'注册资金',
	private	String	loaneeEntBorn	;//	'成立时间',
	private	String	loaneeEntLastYear	;//	'上年营业收入',
	private	String	loaneeEntThisYear	;//	'本年**月止营业收入',
	private	String	loaneePbVoucher	;//	'人民银行征信查询',
	private	String	loaneeLoaneeVoucher	;//	'全国法院被执行人信息查询',
	private	String	loaneeEntVoucher	;//	'全国企业信用信息公示系统查询',
	
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getLoaneeName() {
		return loaneeName;
	}
	public void setLoaneeName(String loaneeName) {
		this.loaneeName = loaneeName;
	}
	public String getLoaneeAge() {
		return loaneeAge;
	}
	public void setLoaneeAge(String loaneeAge) {
		this.loaneeAge = loaneeAge;
	}
	public String getLoaneeIdSerial() {
		return loaneeIdSerial;
	}
	public void setLoaneeIdSerial(String loaneeIdSerial) {
		this.loaneeIdSerial = loaneeIdSerial;
	}
	public String getLoaneeSex() {
		return loaneeSex;
	}
	public void setLoaneeSex(String loaneeSex) {
		this.loaneeSex = loaneeSex;
	}
	public String getLoaneeMaritalStatus() {
		return loaneeMaritalStatus;
	}
	public void setLoaneeMaritalStatus(String loaneeMaritalStatus) {
		this.loaneeMaritalStatus = loaneeMaritalStatus;
	}
	public String getLoaneeAddress() {
		return loaneeAddress;
	}
	public void setLoaneeAddress(String loaneeAddress) {
		this.loaneeAddress = loaneeAddress;
	}
	public String getLoaneeHadCount() {
		return loaneeHadCount;
	}
	public void setLoaneeHadCount(String loaneeHadCount) {
		this.loaneeHadCount = loaneeHadCount;
	}
	public String getLoaneeHadAmount() {
		return loaneeHadAmount;
	}
	public void setLoaneeHadAmount(String loaneeHadAmount) {
		this.loaneeHadAmount = loaneeHadAmount;
	}
	public String getLoaneeNeedRepay() {
		return loaneeNeedRepay;
	}
	public void setLoaneeNeedRepay(String loaneeNeedRepay) {
		this.loaneeNeedRepay = loaneeNeedRepay;
	}
	public String getLoaneeMissedRepay() {
		return loaneeMissedRepay;
	}
	public void setLoaneeMissedRepay(String loaneeMissedRepay) {
		this.loaneeMissedRepay = loaneeMissedRepay;
	}
	public String getLoaneePurpose() {
		return loaneePurpose;
	}
	public void setLoaneePurpose(String loaneePurpose) {
		this.loaneePurpose = loaneePurpose;
	}
	public String getLoaneeSource() {
		return loaneeSource;
	}
	public void setLoaneeSource(String loaneeSource) {
		this.loaneeSource = loaneeSource;
	}
	public String getLoaneeEntAddress() {
		return loaneeEntAddress;
	}
	public void setLoaneeEntAddress(String loaneeEntAddress) {
		this.loaneeEntAddress = loaneeEntAddress;
	}
	public String getLoaneeEntScope() {
		return loaneeEntScope;
	}
	public void setLoaneeEntScope(String loaneeEntScope) {
		this.loaneeEntScope = loaneeEntScope;
	}
	public String getLoaneeEntMoney() {
		return loaneeEntMoney;
	}
	public void setLoaneeEntMoney(String loaneeEntMoney) {
		this.loaneeEntMoney = loaneeEntMoney;
	}
	public String getLoaneeEntBorn() {
		return loaneeEntBorn;
	}
	public void setLoaneeEntBorn(String loaneeEntBorn) {
		this.loaneeEntBorn = loaneeEntBorn;
	}
	public String getLoaneeEntLastYear() {
		return loaneeEntLastYear;
	}
	public void setLoaneeEntLastYear(String loaneeEntLastYear) {
		this.loaneeEntLastYear = loaneeEntLastYear;
	}
	public String getLoaneeEntThisYear() {
		return loaneeEntThisYear;
	}
	public void setLoaneeEntThisYear(String loaneeEntThisYear) {
		this.loaneeEntThisYear = loaneeEntThisYear;
	}
	public String getLoaneePbVoucher() {
		return loaneePbVoucher;
	}
	public void setLoaneePbVoucher(String loaneePbVoucher) {
		this.loaneePbVoucher = loaneePbVoucher;
	}
	public String getLoaneeLoaneeVoucher() {
		return loaneeLoaneeVoucher;
	}
	public void setLoaneeLoaneeVoucher(String loaneeLoaneeVoucher) {
		this.loaneeLoaneeVoucher = loaneeLoaneeVoucher;
	}
	public String getLoaneeEntVoucher() {
		return loaneeEntVoucher;
	}
	public void setLoaneeEntVoucher(String loaneeEntVoucher) {
		this.loaneeEntVoucher = loaneeEntVoucher;
	}
	
}
