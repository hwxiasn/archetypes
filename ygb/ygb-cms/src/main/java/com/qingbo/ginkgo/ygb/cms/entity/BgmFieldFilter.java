package com.qingbo.ginkgo.ygb.cms.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class BgmFieldFilter extends BaseEntity {

	private static final long serialVersionUID = 5849863243424283608L;
	
	private Long menuId;
	private Long fieldId;
	
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
}
