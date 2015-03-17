package com.qingbo.ginkgo.ygb.base.entity;

import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class CodeList extends BaseEntity {
	private static final long serialVersionUID = 8485138011810116643L;

	private String type;//类型，参考CodeListType
	private String name;//名称
	private String code;//值
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
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
}
