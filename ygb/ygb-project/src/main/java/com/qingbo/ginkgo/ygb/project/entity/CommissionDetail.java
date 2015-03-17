package com.qingbo.ginkgo.ygb.project.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class CommissionDetail extends BaseEntity {

	private static final long serialVersionUID = 2095181181128050782L;
	private Long templateId = 0L;
	private String role = "";
	private BigDecimal rate = BigDecimal.ZERO;

	/**
	 * 模板ID
	 * 
	 * @return
	 */
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	/**
	 * 角色代码
	 * 
	 * @return
	 */
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * 费率
	 * 
	 * @return
	 */
	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
}
