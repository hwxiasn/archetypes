package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;
/**
 * 营销机构主界面显示
 * @author Administrator
 *
 */
public class MarketingItem implements Serializable{
	private static final long serialVersionUID = -1388900447409873297L;
	
	
	private String userName;
	private String enterpriseName;
	private String enterpriseBriefCode;
	private String createTime;
	private String status;
	private String statusName;
	private Long marketingUserId;//营销机构id
	private Long brokerUserId;//经纪人id
	private Long investUsetId;//投资人id
	private String customerNum;
	private String realName;
	
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
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public String getEnterpriseBriefCode() {
		return enterpriseBriefCode;
	}
	public void setEnterpriseBriefCode(String enterpriseBriefCode) {
		this.enterpriseBriefCode = enterpriseBriefCode;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
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
	public Long getInvestUsetId() {
		return investUsetId;
	}
	public void setInvestUsetId(Long investUsetId) {
		this.investUsetId = investUsetId;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
}
