package com.qingbo.ginkgo.ygb.cms.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class BgmField extends BaseEntity {

	private static final long serialVersionUID = -1767233659960515893L;

	private String field;
	private String name;
	
    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.MERGE }, optional = false)
    private BgmEntity entity;

	public BgmEntity getEntity() {
		return entity;
	}
	public void setEntity(BgmEntity entity) {
		this.entity = entity;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
