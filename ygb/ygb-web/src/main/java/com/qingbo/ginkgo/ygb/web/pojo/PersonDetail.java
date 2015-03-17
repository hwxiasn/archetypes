package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

import com.qingbo.ginkgo.ygb.customer.entity.User;

public class PersonDetail implements Serializable {
	
	private static final long serialVersionUID = 4383252778461054846L;
	
	/** 客户编号 */
	private String userNum;
	/** 真实姓名 */
	private String realName;
	/** 手机号码 */
	private String mobile;
	/** 是否实名验证*/
	private String realNameBind;
	/** 是否绑定银行卡账号 */
	private String bankCardBind;
	
	public PersonDetail() {
		super();
	}
	
	public PersonDetail(String userNum, String realName, String mobile, String realNameBind, String bankCardBind) {
		super();
		this.userNum = userNum;
		this.realName = realName;
		this.mobile = mobile;
		this.realNameBind = realNameBind;
		this.bankCardBind = bankCardBind;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getRealNameBind() {
		return realNameBind;
	}
	public void setRealNameBind(String realNameBind) {
		this.realNameBind = realNameBind;
	}
	public String getBankCardBind() {
		return bankCardBind;
	}
	public void setBankCardBind(String bankCardBind) {
		this.bankCardBind = bankCardBind;
	}
	public String getUserNum() {
		return userNum;
	}
	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}

	public static PersonDetail formUser(User user) {
		PersonDetail detail = new PersonDetail();
		
		detail.setUserNum(user.getUserProfile() == null ? null : user.getUserProfile().getCustomerNum());
		detail.setMobile(user.getUserProfile() == null ? null : user.getUserProfile().getMobile());
		detail.setRealName(user.getUserProfile() == null ? null : user.getUserProfile().getRealName());

		// TODO 俩状态
		
		return detail;
	}
}
