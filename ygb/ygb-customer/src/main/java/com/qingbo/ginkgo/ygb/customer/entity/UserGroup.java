package com.qingbo.ginkgo.ygb.customer.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;


@Entity
public class UserGroup extends BaseEntity{

	
	private static final long serialVersionUID = 451225637412231231L;
	
	private Long parentUserId;//父用户id
	private Long childUserId;//子用户id
	private String relationship;//用户关系
	private String level;//级别
	private String memo;//备注
	private Long rootId;//营销机构user_id
	private Long superBrokerId;//超级经纪人user_id
	private Long brokerId;//经纪人user_id
	
	public Long getParentUserId() {
		return parentUserId;
	}
	public void setParentUserId(Long parentUserId) {
		this.parentUserId = parentUserId;
	}
	public Long getChildUserId() {
		return childUserId;
	}
	public void setChildUserId(Long childUserId) {
		this.childUserId = childUserId;
	}
	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Long getRootId() {
		return rootId;
	}
	public void setRootId(Long rootId) {
		this.rootId = rootId;
	}
	public Long getSuperBrokerId() {
		return superBrokerId;
	}
	public void setSuperBrokerId(Long superBrokerId) {
		this.superBrokerId = superBrokerId;
	}
	public Long getBrokerId() {
		return brokerId;
	}
	public void setBrokerId(Long brokerId) {
		this.brokerId = brokerId;
	}

}
