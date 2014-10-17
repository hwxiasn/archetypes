package com.hwxiasn.contentserver.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Role extends BaseEntity {
	private static final long serialVersionUID = 8603825964037642906L;

	@Id private Integer id;
	
	private String name;
	private String nameEn;
	private String nameZh;
	private String permissionIds;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameEn() {
		return nameEn;
	}
	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}
	public String getNameZh() {
		return nameZh;
	}
	public void setNameZh(String nameZh) {
		this.nameZh = nameZh;
	}
	public String getPermissionIds() {
		return permissionIds;
	}
	public void setPermissionIds(String permissionIds) {
		this.permissionIds = permissionIds;
	}
}
