package com.qingbo.ginkgo.ygb.project.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class CommissionTemplate extends BaseEntity {

	private static final long serialVersionUID = 4358703145800570090L;
	private String name = "";// 模板名称
	private String status = "";// 模板状态
	private String allotPhase = "";// 分配阶段
	private String allotType = "";// 分配方式
	private String memo = "";// 模板描述
	private boolean locked = false;// 是否锁定
	//模板下角色分佣列表
	@Transient List<CommissionDetail> details;
	//简述角色分佣清单
	@Transient String roleRates;

	/**
	 * 模板名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 模板状态
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 分配阶段
	 * 
	 * @return
	 */
	public String getAllotPhase() {
		return allotPhase;
	}

	public void setAllotPhase(String allotPhase) {
		this.allotPhase = allotPhase;
	}

	/**
	 * 分配方式
	 * 
	 * @return
	 */
	public String getAllotType() {
		return allotType;
	}

	public void setAllotType(String allotType) {
		this.allotType = allotType;
	}

	/**
	 * 模板描述
	 * 
	 * @return
	 */
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 是否锁定
	 * 
	 * @return
	 */
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	/**
	 * 包含的详细角色费用率
	 * @return
	 */
	public List<CommissionDetail> getDetails() {
		return details;
	}

	public void setDetails(List<CommissionDetail> details) {
		this.details = details;
	}

	public String getRoleRates() {
		return roleRates;
	}

	public void setRoleRates(String roleRates) {
		this.roleRates = roleRates;
	}

}
