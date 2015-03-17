package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;


/**
 * pojo类实现Serializable有利于做缓存
 * @author hongwei
 */
public class UserDetail implements Serializable {
	private static final long serialVersionUID = 219055270199571988L;
	private Long id;
	private String userName;
	private String role;
	private Long userId;
	private String realName;
	private String mobile;
	private String email;
	private String idNum;
	private	String idType;//证件类型
	private String gender;//性别
	private String idValidTo;//证件到期时间
	private String idCopyFront;//证件扫描件正面照片
	private String idCopyBank;//证件照扫描件反面
	private String customerNum;//客户编号
	private String memo;//备注
	private String bankCardAccountName;//账户名
	private String bankCardNum;//银行卡号
	private String regSource;
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
	//状态
	private String status;
	//激活码
	private String activateCode;
	//状态的名称
	private String statusName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBankCardType() {
		return bankCardType;
	}
	public void setBankCardType(String bankCardType) {
		this.bankCardType = bankCardType;
	}
	public String getRegSource() {
		return regSource;
	}
	public void setRegSource(String regSource) {
		this.regSource = regSource;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	public String getIdValidTo() {
		return idValidTo;
	}
	public void setIdValidTo(String idValidTo) {
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
	public String getBankCardAccountName() {
		return bankCardAccountName;
	}
	public void setBankCardAccountName(String bankCardAccountName) {
		this.bankCardAccountName = bankCardAccountName;
	}
	public String getBankCardNum() {
		return bankCardNum;
	}
	public void setBankCardNum(String bankCardNum) {
		this.bankCardNum = bankCardNum;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getActivateCode() {
		return activateCode;
	}
	public void setActivateCode(String activateCode) {
		this.activateCode = activateCode;
	}
	
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public User toUser()  {
		User user = new User();
		//表示是个人类型:P
		user.setRegisterType(CustomerConstants.UserRegisterType.PERSONAL.getCode());
		user.setUserName(userName);
		//表示注册来源为：by 倍赢
		user.setRegSource(regSource);
		user.setRole(role);
		
		UserProfile userProfile = new UserProfile();
		userProfile.setRealName(realName);
		userProfile.setMobile(mobile);
		userProfile.setEmail(email);
		userProfile.setIdNum(idNum);
		userProfile.setIdType(idType);
		userProfile.setGender(gender);
		userProfile.setIdValidTo(DateUtil.parse(idValidTo));
		userProfile.setIdCopyBank(idCopyBank);
		userProfile.setIdCopyFront(idCopyFront);
		userProfile.setMemo(memo);
		user.setUserProfile(userProfile);
		return user;
		// TODO Auto-generated method stub
	}
	
	
	public UserBankCard toUserBank() {
		UserBankCard userBank = new UserBankCard();
		
		//userBank.setBankBranch(bankBranch);
		//银行卡类型
		userBank.setBankCardType(bankCardType);
		userBank.setBankCode(bankCode);
		userBank.setBankCardAccountName(bankCardAccountName);
		userBank.setBankCardNum(bankCardNum);
		userBank.setProvince(province);
		userBank.setCity(city);
		userBank.setIdType(idType);
		userBank.setIdNum(idNum);
		userBank.setAddress(address);
		userBank.setMemo(memo);
		userBank.setUserId(userId);
		//userBank.setBankBranch(bankBranch);
		return userBank;
	}
	
	public static  UserDetail formUser(User user) {
		// TODO Auto-generated method stub
		UserDetail userDetail = new UserDetail();
		userDetail.setUserId(user.getId());
		userDetail.setUserName(user.getUserName());
		userDetail.setRole(user.getRole());
		userDetail.setStatus(user.getStatus());
		if(user.getActivateCode()!=null && !("").equals(user.getActivateCode())) {
			userDetail.setActivateCode(user.getActivateCode());
		}
		if(CustomerConstants.UserRegisterType.PERSONAL.getCode().equals(user.getRegisterType())){
			if(user.getUserProfile()!=null && !("").equals(user.getUserProfile())) {
				userDetail.setCustomerNum(user.getUserProfile().getCustomerNum());
				userDetail.setRealName(user.getUserProfile().getRealName());
				userDetail.setMobile(user.getUserProfile().getMobile());
				userDetail.setEmail(user.getUserProfile().getEmail());
				userDetail.setIdNum(user.getUserProfile().getIdNum());
				//userDetail.setCustomerNum(user.getUserProfile().getCustomerNum());
			}
		}else if(CustomerConstants.UserRegisterType.ENTERPRISE.getCode().equals(user.getRegisterType())) {
			if(user.getEnterpriseProfile()!=null && !("").equals(user.getEnterpriseProfile())) {
				userDetail.setRealName(user.getEnterpriseProfile().getEnterpriseName());
				userDetail.setMobile(user.getEnterpriseProfile().getContactPhone());
				userDetail.setEmail(user.getEnterpriseProfile().getContactEmail());
				userDetail.setIdNum(user.getEnterpriseProfile().getLegalPersonIdNum());
			}
		}
		return userDetail;
	}
}
