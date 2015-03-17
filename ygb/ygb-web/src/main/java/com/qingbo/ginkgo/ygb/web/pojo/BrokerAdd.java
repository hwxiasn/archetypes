package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class BrokerAdd implements Serializable{

	private static final long serialVersionUID = -6153623054869466896L;
	
	
	//营销机构userId
	private Long marketingUserId;
	private String customerNum;
	private String userName;
	private String realName;
	
	public Long getMarketingUserId() {
		return marketingUserId;
	}
	public void setMarketingUserId(Long marketingUserId) {
		this.marketingUserId = marketingUserId;
	}
	public String getCustomerNum() {
		return customerNum;
	}
	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}
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
	
	
	
	
	
}
