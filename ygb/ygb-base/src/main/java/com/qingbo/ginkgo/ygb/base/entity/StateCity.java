package com.qingbo.ginkgo.ygb.base.entity;

import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class StateCity extends BaseEntity {
	private static final long serialVersionUID = 5934041511325816428L;

	private String name;
	private String parentName;
	private String code;
	private String parentCode;
	private String qddCode;
	private String qddParent;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getQddCode() {
		return qddCode;
	}
	public void setQddCode(String qddCode) {
		this.qddCode = qddCode;
	}
	public String getQddParent() {
		return qddParent;
	}
	public void setQddParent(String qddParent) {
		this.qddParent = qddParent;
	}
}
