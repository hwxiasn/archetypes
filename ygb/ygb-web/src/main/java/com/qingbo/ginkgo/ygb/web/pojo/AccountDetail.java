package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class AccountDetail implements Serializable {

	private static final long serialVersionUID = -6214809538740296696L;

	// 用户基本资料
	/** 用户名  */
	private String userName;
	/** 用户真实姓名  */
	private String realName;
	/** 编号  */
	private String userNum;
	/**用户状态，冻结等*/
	private String locked;
	/** 上次登录时间  */
	private String frontTime;
	
	// 用户详细资料  
	/** 用户的序列号  */
	private String serializeNum;
	/** 性别  */
	private String sex;
	/** 邮箱  */
	private String email;
	/** 联系电话 */
	private String telephone;

	// 用户状态信息
	/** 判断用户是否绑定了银行卡  */
	private String isBinding;
	/** 判断用户是否绑定了手机号  */
	private String isMobile;
	/** 判断用户是否绑定了邮箱  */
	private String isEmail;
	/** 判断用户是否实名认证  */
	private String isRealName;
	/** 绑定银行图片的地址  */
	private String picImage;
	
	// 用户绑定银行卡信息
	/** 开户银行 */
	private String bank;
	/** 银行卡账号信息 */
	private String bankCardNum;
	/** 所属省份 */
	private String province;
	/** 所属城市 */
	private String city;
	/** 在绑定银行卡是显示的身份证号  */
	private String cardNum;
	
	// 用户账户情况
	/** 账户可用余额  */
	private String balance = "0.00";
	/** 账户冻结余额  */
	private String lockBalance = "0.00";
	
	// 投资融资信息
	/** 投资/则融资或则投资了几笔交易 */
	private String investCount;
	/** 累计投资/融资的金额  */
	private String totalInvest;
	/** 累计成功  融资的金额  */
	private String successTotalInvest;
	
	public String getLocked() {
		return locked;
	}
	public void setLocked(String locked) {
		this.locked = locked;
	}
	public AccountDetail() {
		super();
	}
	public AccountDetail(String userName, String realName, String userNum,
			String frontTime, String balance, String isBinding,
			String picImage, String investCount, String totalInvest,
			String cardNum, String serializeNum, String sex, String email,
			String telephone, String bank, String bankCardNum, String provice,
			String city) {
		super();
		this.userName = userName;
		this.realName = realName;
		this.userNum = userNum;
		this.frontTime = frontTime;
		this.balance = balance;
		this.isBinding = isBinding;
		this.picImage = picImage;
		this.investCount = investCount;
		this.totalInvest = totalInvest;
		this.cardNum = cardNum;
		this.serializeNum = serializeNum;
		this.sex = sex;
		this.email = email;
		this.telephone = telephone;
		this.bank = bank;
		this.bankCardNum = bankCardNum;
		this.province = provice;
		this.city = city;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNum() {
		return userNum;
	}
	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}
	public String getFrontTime() {
		return frontTime;
	}
	public void setFrontTime(String frontTime) {
		this.frontTime = frontTime;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getPicImage() {
		return picImage;
	}
	public void setPicImage(String picImage) {
		this.picImage = picImage;
	}
	public String getInvestCount() {
		return investCount;
	}

	public void setInvestCount(String investCount) {
		this.investCount = investCount;
	}

	public String getTotalInvest() {
		return totalInvest;
	}
	public void setTotalInvest(String totalInvest) {
		this.totalInvest = totalInvest;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public String getIsBinding() {
		return isBinding;
	}
	public void setIsBinding(String isBinding) {
		this.isBinding = isBinding;
	}
	public String getIsEmail() {
		return isEmail;
	}
	public void setIsEmail(String isEmail) {
		this.isEmail = isEmail;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getSerializeNum() {
		return serializeNum;
	}
	public void setSerializeNum(String serializeNum) {
		this.serializeNum = serializeNum;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getBankCardNum() {
		return bankCardNum;
	}
	public void setBankCardNum(String bankCardNum) {
		this.bankCardNum = bankCardNum;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String provice) {
		this.province = provice;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getIsMobile() {
		return isMobile;
	}
	public void setIsMobile(String isMobile) {
		this.isMobile = isMobile;
	}
	public String getIsRealName() {
		return isRealName;
	}
	public void setIsRealName(String isRealName) {
		this.isRealName = isRealName;
	}
	public String getLockBalance() {
		return lockBalance;
	}
	public void setLockBalance(String lockBalance) {
		this.lockBalance = lockBalance;
	}
	public String getSuccessTotalInvest() {
		return successTotalInvest;
	}
	public void setSuccessTotalInvest(String successTotalInvest) {
		this.successTotalInvest = successTotalInvest;
	}	
}
