package com.qingbo.ginkgo.ygb.base.entity;

import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class BankType extends BaseEntity {
	private static final long serialVersionUID = 7844533234900871713L;

	private String name;//名称
	private String code;//值
	private String qddCode;//乾多多对应的值
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getQddCode() {
		return qddCode;
	}
	public void setQddCode(String qddCode) {
		this.qddCode = qddCode;
	}
}
