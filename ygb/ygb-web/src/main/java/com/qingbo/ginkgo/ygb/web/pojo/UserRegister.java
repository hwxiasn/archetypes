package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;

public class UserRegister implements Serializable{

	private static final long serialVersionUID = 7354395244195506552L;

	/** 唯一标示 */
	public Long id;
	/** 用户名 */
	public String userName;
	/** 真实姓名 */
	public String realName;
	/** 常用邮箱 */
	
	public String email;
	/** 推荐人编号 */
	public String refererNumber;
	/** 手机号码 **/
	private String mobile;
	/** 用户角色 **/
	private String role;
	/** 注册类型 **/
	private String registerType;
	/** 注册来源 **/
	private String registerSource;
	//身份证号码(用于激活界面提取用户在后台注册时填写的身份证号码)
	private String idNum;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRefererNumber() {
		return refererNumber;
	}
	public void setRefererNumber(String refererNumber) {
		this.refererNumber = refererNumber;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getRegisterType() {
		return registerType;
	}
	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getRegisterSource() {
		return registerSource;
	}
	public void setRegisterSource(String registerSource) {
		this.registerSource = registerSource;
	}
	
	public String getIdNum() {
		return idNum;
	}
	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}
	public static UserRegister formUser(User user) {
		UserRegister register = new UserRegister();
		register.setId(user.getId());
		register.setUserName(user.getUserName());
		if((CustomerConstants.UserRegisterType.PERSONAL.getCode()).equals(user.getRegisterType()) && user.getUserProfile()!=null) {
			register.setEmail(user.getUserProfile().getEmail());
			register.setRealName(user.getUserProfile().getRealName());
			register.setMobile(user.getUserProfile().getMobile());
			register.setIdNum(user.getUserProfile().getIdNum());
		}else {
			if(user.getEnterpriseProfile()!=null) {
				register.setEmail(user.getEnterpriseProfile().getContactEmail());
				register.setRealName(user.getEnterpriseProfile().getContactName());
				register.setMobile(user.getEnterpriseProfile().getContactPhone());
				register.setIdNum(user.getEnterpriseProfile().getLegalPersonIdNum());
			}
		}
		return register;
	}
	
	public  User toUser() {
		User user = new User();
		user.setUserName(userName);
		user.setRole(role);
		user.setRegSource(registerSource);
		user.setRegisterType(registerType);
		UserProfile userProfile = new UserProfile();
		userProfile.setMobile(mobile);
		userProfile.setRealName(realName);
		userProfile.setEmail(email);
		user.setUserProfile(userProfile);
		return user;
		
	}
}
