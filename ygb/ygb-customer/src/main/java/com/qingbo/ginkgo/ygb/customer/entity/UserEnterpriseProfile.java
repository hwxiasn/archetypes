package com.qingbo.ginkgo.ygb.customer.entity;

import javax.persistence.Entity;
import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;


@Entity
public class UserEnterpriseProfile extends BaseEntity {
	private static final long serialVersionUID = 473163279717143760L;

	private Long userId;//用户主键
	private String enterpriseName;//企业名称
	private String enterpriseShortName;//企业简称
	private String enterpriseBriefCode;//机构简码
	private String enterpriseSubcodeFrom;//机构号段
	private String enterpriseSubcodeTo;//机构号段
	private String contactName;//联系人姓名
	private String contactEmail;//联系人邮箱
	private String contactPhone;//联系人电话
	private String legalPersonName;//法人代表姓名
	private String legalPersonIdNum;//法人身份证号
	private String organizationCode;//组织机构代码
	private String taxRegistrationNo;//税务登记号
	private String licenseNum;//营业执照注册号
	private String registerProvince;//营业执照注册所属省份
	private String registerCity;//营业执照注册所属市
	private String registerAddress;//公司常用地址
	private String licenseValidPeriod;//经营期限
	private String phoneNum;//公司联系电话
	private String licensePath;//营业执照副本扫描件
	private String licenseCachetPath;//  盖章的营业执照副本扫描件
	private String legalPersonIdCopyFont;//法人代表身份证正面照
	private String legalPersonIdCopyBack;//法人代表身份证反面照
	private String openningLicensePath;//开户许可证照片
	private String memo;//备注
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public String getEnterpriseShortName() {
		return enterpriseShortName;
	}
	public void setEnterpriseShortName(String enterpriseShortName) {
		this.enterpriseShortName = enterpriseShortName;
	}
	public String getEnterpriseBriefCode() {
		return enterpriseBriefCode;
	}
	public void setEnterpriseBriefCode(String enterpriseBriefCode) {
		this.enterpriseBriefCode = enterpriseBriefCode;
	}
	public String getEnterpriseSubcodeFrom() {
		return enterpriseSubcodeFrom;
	}
	public void setEnterpriseSubcodeFrom(String enterpriseSubcodeFrom) {
		this.enterpriseSubcodeFrom = enterpriseSubcodeFrom;
	}
	public String getEnterpriseSubcodeTo() {
		return enterpriseSubcodeTo;
	}
	public void setEnterpriseSubcodeTo(String enterpriseSubcodeTo) {
		this.enterpriseSubcodeTo = enterpriseSubcodeTo;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getLegalPersonName() {
		return legalPersonName;
	}
	public void setLegalPersonName(String legalPersonName) {
		this.legalPersonName = legalPersonName;
	}
	public String getLegalPersonIdNum() {
		return legalPersonIdNum;
	}
	public void setLegalPersonIdNum(String legalPersonIdNum) {
		this.legalPersonIdNum = legalPersonIdNum;
	}
	public String getOrganizationCode() {
		return organizationCode;
	}
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}
	public String getTaxRegistrationNo() {
		return taxRegistrationNo;
	}
	public void setTaxRegistrationNo(String taxRegistrationNo) {
		this.taxRegistrationNo = taxRegistrationNo;
	}
	public String getLicenseNum() {
		return licenseNum;
	}
	public void setLicenseNum(String licenseNum) {
		this.licenseNum = licenseNum;
	}
	public String getRegisterProvince() {
		return registerProvince;
	}
	public void setRegisterProvince(String registerProvince) {
		this.registerProvince = registerProvince;
	}
	public String getRegisterCity() {
		return registerCity;
	}
	public void setRegisterCity(String registerCity) {
		this.registerCity = registerCity;
	}
	public String getRegisterAddress() {
		return registerAddress;
	}
	public void setRegisterAddress(String registerAddress) {
		this.registerAddress = registerAddress;
	}
	public String getLicenseValidPeriod() {
		return licenseValidPeriod;
	}
	public void setLicenseValidPeriod(String licenseValidPeriod) {
		this.licenseValidPeriod = licenseValidPeriod;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getLicensePath() {
		return licensePath;
	}
	public void setLicensePath(String licensePath) {
		this.licensePath = licensePath;
	}
	public String getLegalPersonIdCopyFont() {
		return legalPersonIdCopyFont;
	}
	public void setLegalPersonIdCopyFont(String legalPersonIdCopyFont) {
		this.legalPersonIdCopyFont = legalPersonIdCopyFont;
	}
	public String getLegalPersonIdCopyBack() {
		return legalPersonIdCopyBack;
	}
	public void setLegalPersonIdCopyBack(String legalPersonIdCopyBack) {
		this.legalPersonIdCopyBack = legalPersonIdCopyBack;
	}
	public String getOpenningLicensePath() {
		return openningLicensePath;
	}
	public void setOpenningLicensePath(String openningLicensePath) {
		this.openningLicensePath = openningLicensePath;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getLicenseCachetPath() {
		return licenseCachetPath;
	}
	public void setLicenseCachetPath(String licenseCachetPath) {
		this.licenseCachetPath = licenseCachetPath;
	}
	
	
}
