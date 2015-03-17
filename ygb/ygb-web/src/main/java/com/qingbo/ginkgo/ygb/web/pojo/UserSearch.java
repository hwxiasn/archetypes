package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class UserSearch implements Serializable{
	private static final long serialVersionUID = -2548307896260927234L;
	/** 用户名 */
	private String userName;
	/** 真实姓名 */
	private String realName;
	/** 客户编号**/
	private String customerNum;
	/** 支付账户ID */
	//private String moneyMoreMoreId;
	/** 角色 */
	private String role;
	/** 状态*/
	private String status;
	
	
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
	public String getCustomerNum() {
		return customerNum;
	}
	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}
//	public String getMoneyMoreMoreId() {
//		return moneyMoreMoreId;
//	}
//	public void setMoneyMoreMoreId(String moneyMoreMoreId) {
//		this.moneyMoreMoreId = moneyMoreMoreId;
//	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
