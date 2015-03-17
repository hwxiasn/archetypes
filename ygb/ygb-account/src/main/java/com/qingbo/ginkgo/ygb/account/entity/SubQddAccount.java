package com.qingbo.ginkgo.ygb.account.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

/**
 * 乾多多子账户
 */
@Entity
public class SubQddAccount extends BaseEntity {
	private static final long serialVersionUID = 5357874851055232049L;

	private String platformId;//乾多多平台标识
	private String moneyMoreMoreId;//乾多多账户标识
	private boolean authorised;//乾多多账户是否已授权
	
	public String getPlatformId() {
		return platformId;
	}
	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}
	public String getMoneyMoreMoreId() {
		return moneyMoreMoreId;
	}
	public void setMoneyMoreMoreId(String moneyMoreMoreId) {
		this.moneyMoreMoreId = moneyMoreMoreId;
	}
	public boolean isAuthorised() {
		return authorised;
	}
	public void setAuthorised(boolean authorised) {
		this.authorised = authorised;
	}
}
