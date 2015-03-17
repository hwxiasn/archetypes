package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;

public class UserActivate implements Serializable {

	private static final long serialVersionUID = -7939502199088974280L;
	
	public Long id;
	/** 帐户名 */
	public String userName;
	/** 登录密码 */
	public String password;
	/** 确认登录密码 */
	public String confirmPassword;
	/** 支付密码 */
	public String payPassword;
	/** 确认支付密码 */
	public String confirmPayPassword;
	/** 手机号码 */
	public String mobile;
	/**用户身份证信息*/
	public String usersnid;
	/**用户真实姓名*/
	public String realName;
	
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getUsersnid() {
		return usersnid;
	}
	public void setUsersnid(String usersnid) {
		this.usersnid = usersnid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getPayPassword() {
		return payPassword;
	}
	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}
	public String getConfirmPayPassword() {
		return confirmPayPassword;
	}
	public void setConfirmPayPassword(String confirmPayPassword) {
		this.confirmPayPassword = confirmPayPassword;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public static User toUser(UserActivate userActivate){
		User user = new User();
		user.setId(userActivate.getId());
		user.setUserName(userActivate.getUserName());
		user.setPassword(userActivate.getPassword());
		UserProfile profile = new UserProfile();
		profile.setRealName(userActivate.getRealName());
		profile.setMobile(userActivate.getMobile());
		profile.setIdNum(userActivate.getUsersnid());
		user.setUserProfile(profile);
		return user;
	}
}
