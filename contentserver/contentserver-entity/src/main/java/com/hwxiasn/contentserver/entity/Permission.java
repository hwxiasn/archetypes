package com.hwxiasn.contentserver.entity;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Permission extends BaseEntity {
	private static final long serialVersionUID = -7267541569936646615L;

	@Id private Integer id;
	
	private String name;
	private String nameZh;
	private Integer parentId;
	
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
	public String getNameZh() {
		return nameZh;
	}
	public void setNameZh(String nameZh) {
		this.nameZh = nameZh;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
}
