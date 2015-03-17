package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.entity.UserEnterpriseProfile;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;

public class AgencyDetail implements Serializable{

	private static final long serialVersionUID = 8527057438591945431L;
	/** 用户ID */
	private Long userId;
	/** 企业ID */
	private Long agencyId;
	/** 用户名 */
	private String userName;
	/** 用户角色 */
	private String role;
	/** 注册来源 */
	private String regSource;
	/** 机构简码 */
	private String enterpriseBriefCode;
	/** 机构号段（from） */
	private String enterpriseSubcodeFrom;
	/** 机构号段（to） */
	private String enterpriseSubcodeTo;
	/** 联系人姓名 */
	private String contactName;
	/** 联系人邮箱 */
	private String contactEmail;
	/** 联系人手机号 */
	private String contactPhone;
	
	/** 帐户名 */
	private String paymentAccountName;
	/** 企业名称 */
	private String enterpriseName;
	/** 法人姓名 */
	private String legalPersonName;
	/** 法人证件号 */
	private String legalPersonIdNum;
	/** 组织机构代码 */
	private String organizationCode;
	/** 税务登记号 */
	private String taxRegistrationNo;
	/** 营业执照注册号 */
	private String licenseNum;
	/** 营业执照所在地（省） */
	private String registerProvince;
	/** 营业执照所在地（市） */
	private String registerCity;
	/** 常用地址 */
	private String registerAddress;
	/** 经营期限，长期为NA */
	private String licenseValidPeriod;
	/** 公司联系电话 */
	private String phoneNum;
	/** 营业执照副本 */
	private String licensePath;
	/** 营业执照副本（加盖公章） */
	private String licenseCachetPath;
	/** 法人证件照正面 */
	private String legalPersonIdCopyFont;
	/** 法人证件照反面 */
	private String legalPersonIdCopyBack;
	/** 开户许可证 */
	private String openningLicensePath;
	/** 银行开户名 */
	private String bankCardAccountName;
	/** 开户银行 */
	private String bankCode;
	/** 开户支行 */
	private String bankBranch;
	/** 开户支行所在省 */
	private String province;
	/** 开户支行所在市 */
	private String city;
	/** 邮寄地址 */
	private String address;
	/** 邮编 */
	private String zipCode;
	//银行卡类型
	private String bankCardType;
	private String bankCardNum;//银行卡号
	private	String idType;//证件类型
	/** 客户备注 */
	private String memo;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getAgencyId() {
		return agencyId;
	}
	public void setAgencyId(Long agencyId) {
		this.agencyId = agencyId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
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
	public String getPaymentAccountName() {
		return paymentAccountName;
	}
	public void setPaymentAccountName(String paymentAccountName) {
		this.paymentAccountName = paymentAccountName;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
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
	public String getLicenseCachetPath() {
		return licenseCachetPath;
	}
	public void setLicenseCachetPath(String licenseCachetPath) {
		this.licenseCachetPath = licenseCachetPath;
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
	public String getBankCardAccountName() {
		return bankCardAccountName;
	}
	public void setBankCardAccountName(String bankCardAccountName) {
		this.bankCardAccountName = bankCardAccountName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getBankCardType() {
		return bankCardType;
	}
	public void setBankCardType(String bankCardType) {
		this.bankCardType = bankCardType;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public String getRegSource() {
		return regSource;
	}
	public void setRegSource(String regSource) {
		this.regSource = regSource;
	}
	public String getBankCardNum() {
		return bankCardNum;
	}
	public void setBankCardNum(String bankCardNum) {
		this.bankCardNum = bankCardNum;
	}
	
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public User toUser() {
		// TODO Auto-generated method stub
		User user = new User();
		user.setRole(role);
		user.setUserName(userName);
		//注册类型为企业用户
		user.setRegisterType(CustomerConstants.UserRegisterType.ENTERPRISE.getCode());
		user.setRegSource(regSource);
		
		UserEnterpriseProfile enterPrise = new UserEnterpriseProfile();
		enterPrise.setEnterpriseName(enterpriseName);
		enterPrise.setEnterpriseBriefCode(enterpriseBriefCode);
		enterPrise.setEnterpriseSubcodeFrom(enterpriseSubcodeFrom);
		enterPrise.setEnterpriseSubcodeTo(enterpriseSubcodeTo);
		enterPrise.setLegalPersonIdCopyBack(legalPersonIdCopyBack);
		enterPrise.setLegalPersonIdCopyFont(legalPersonIdCopyFont);
		enterPrise.setLegalPersonIdNum(legalPersonIdNum);
		enterPrise.setLicenseCachetPath(licenseCachetPath);
		enterPrise.setLegalPersonName(legalPersonName);
		enterPrise.setLicenseValidPeriod(licenseValidPeriod);
		enterPrise.setLicenseNum(licenseNum);
		enterPrise.setLicensePath(licensePath);
		enterPrise.setOpenningLicensePath(openningLicensePath);
		enterPrise.setOrganizationCode(organizationCode);
		enterPrise.setPhoneNum(phoneNum);
		enterPrise.setRegisterProvince(registerProvince);
		enterPrise.setRegisterCity(registerCity);
		enterPrise.setRegisterAddress(registerAddress);
		enterPrise.setTaxRegistrationNo(taxRegistrationNo);
		enterPrise.setContactEmail(contactEmail);
		enterPrise.setContactName(contactName);
		enterPrise.setContactPhone(contactPhone);
		enterPrise.setMemo(memo);
		user.setEnterpriseProfile(enterPrise);
		return user;
	}
	
	public UserBankCard toUserBank() {
		UserBankCard userBankCard = new UserBankCard();
		
		userBankCard.setBankCardType(bankCardType);
		userBankCard.setBankCode(bankCode);
		userBankCard.setBankCardAccountName(bankCardAccountName);
		userBankCard.setBankCardNum(bankCardNum);
		userBankCard.setProvince(province);
		userBankCard.setCity(city);
		userBankCard.setIdType(idType);
		//法人代表证件号码
		userBankCard.setIdNum(legalPersonIdNum);
		userBankCard.setAddress(address);
		userBankCard.setMemo(memo);
		userBankCard.setUserId(userId);
		return userBankCard;
	}
	
	
	
}
