package com.qingbo.ginkgo.ygb.cms.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class BgmPlatform extends BaseEntity {

	private static final long serialVersionUID = 4346025275493483243L;

	private String tag;					// 平台标识
	private String fullName;			// 平台全名
	private String shortName;			// 平台简称
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
}
