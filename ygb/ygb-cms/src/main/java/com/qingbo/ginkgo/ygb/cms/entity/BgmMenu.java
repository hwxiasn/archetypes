package com.qingbo.ginkgo.ygb.cms.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class BgmMenu extends BaseEntity {

	private static final long serialVersionUID = -4137703909934026453L;

	private Long platform;	// 所属平台
	private Integer level;	// 菜单层级
	private Integer serial;	// 同级序列
	private String name;	// 菜单名称
	private String entity;	// 对应实体

	@ManyToOne
	private BgmMenu parent;	// 父菜单

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "parent")
	private Set<BgmMenu> children = new HashSet<BgmMenu>();

	@Transient List<BgmField> viewFields;
	@Transient List<BgmField> filterFields;

	public Long getPlatform() {
		return platform;
	}

	public void setPlatform(Long platform) {
		this.platform = platform;
	}

	public BgmMenu getParent() {
		return parent;
	}

	public void setParent(BgmMenu parent) {
		this.parent = parent;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getSerial() {
		return serial;
	}

	public void setSerial(Integer serial) {
		this.serial = serial;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void addChild(BgmMenu child) {
		if (!this.children.contains(child)) {
			this.children.add(child);
			child.setParent(this);
		}
	}

	public void removeChild(BgmMenu child) {
		if (this.children.contains(child)) {
			child.setParent(null);
			this.children.remove(child);
		}
	}

	public Set<BgmMenu> getChildren() {
		return children;
	}

	public void setChildren(Set<BgmMenu> children) {
		this.children = children;
	}

	public List<BgmField> getViewFields() {
		return viewFields;
	}

	public void setViewFields(List<BgmField> viewFields) {
		this.viewFields = viewFields;
	}

	public List<BgmField> getFilterFields() {
		return filterFields;
	}

	public void setFilterFields(List<BgmField> filterFields) {
		this.filterFields = filterFields;
	}
	
	
}
