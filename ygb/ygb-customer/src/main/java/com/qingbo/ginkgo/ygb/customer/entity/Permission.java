package com.qingbo.ginkgo.ygb.customer.entity;

import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
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
