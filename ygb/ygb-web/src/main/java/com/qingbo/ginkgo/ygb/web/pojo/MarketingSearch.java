package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class MarketingSearch implements Serializable{

	private static final long serialVersionUID = -1373875378211558493L;
	
	
	private String userName;
	private String enterpriseName;
	private String realName;
	private String customerNum;
	private Long marketingUserId;
	private Long brokerUserId;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
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
	public Long getMarketingUserId() {
		return marketingUserId;
	}
	public void setMarketingUserId(Long marketingUserId) {
		this.marketingUserId = marketingUserId;
	}
	public Long getBrokerUserId() {
		return brokerUserId;
	}
	public void setBrokerUserId(Long brokerUserId) {
		this.brokerUserId = brokerUserId;
	}
}
