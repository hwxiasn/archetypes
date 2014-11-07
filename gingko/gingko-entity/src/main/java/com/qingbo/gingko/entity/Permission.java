package com.qingbo.gingko.entity;

import javax.persistence.Entity;

import com.qingbo.gingko.entity.base.BaseEntity;

@Entity
public class Permission extends BaseEntity {
	private static final long serialVersionUID = -7267541569936646615L;

	private String name;
	private String nameZh;
	private Long parentId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameZh() {
		return nameZh;
	}
	public void setNameZh(String nameZh) {
		this.nameZh = nameZh;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
}
