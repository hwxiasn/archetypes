package com.qingbo.ginkgo.ygb.cms.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

/**
 * 专题实体
 */

@Entity
public class Subject extends BaseEntity {
	
	private static final long serialVersionUID = -3179797786290585734L;

	private String site;		// 所属站点
	private String code;		// 专题编码
	private String name;		// 专题名称
	private String parentCode;	// 父专题表面
	private Integer level;		// 专题层级
	private Boolean leaf;		// 是否叶子节点
	private Integer serial;		// 同级序列
	private String link;		// 链接
	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}
	public Integer getSerial() {
		return serial;
	}
	public void setSerial(Integer serial) {
		this.serial = serial;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
}
