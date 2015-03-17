package com.qingbo.ginkgo.ygb.project.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class Guarantee extends BaseEntity {
	private static final long serialVersionUID = -5075721332774101768L;
	private Long projectId = 0L; // 担保函对应的项目ID
	private BigDecimal fee = BigDecimal.ZERO;; // 应收担保费（精度到分，单位：元）
	private String status = ""; // 担保函状态
	private int year = 0; // 担保函年份
	private int serial = 0; // 担保函顺序号
	private String userId = ""; // 担保公司用户ID
	private String commitmentLetterSn = ""; // 担保承诺函编号
	private String commitmentLetterPath = ""; // 担保承诺函保存路径
	private String guaranteeContractSn = ""; // 履约回购合同编号（与项目中的编号一致）
	private String guaranteeContractPath = ""; // 履约回购合同保存路径
	private String guaranteeLetterSn = ""; // 担保函编号
	private String guaranteeLetterPath = ""; // 担保函保存路径
	@Transient private Project project;
	@Transient private String statusName;

	/**
	 * 担保函对应的项目ID
	 * 
	 * @return
	 */
	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	/**
	 * 应收担保费（精度到分，单位：元）
	 * 
	 * @return
	 */
	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	/**
	 * 担保函状态
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
	 * 担保函年份
	 * 
	 * @return
	 */
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * 担保函顺序号
	 * 
	 * @return
	 */
	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	/**
	 * 担保公司用户ID
	 * 
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 担保承诺函编号
	 * 
	 * @return
	 */
	public String getCommitmentLetterSn() {
		return commitmentLetterSn;
	}

	public void setCommitmentLetterSn(String commitmentLetterSn) {
		this.commitmentLetterSn = commitmentLetterSn;
	}

	/**
	 * 担保承诺函保存路径
	 * 
	 * @return
	 */
	public String getCommitmentLetterPath() {
		return commitmentLetterPath;
	}

	public void setCommitmentLetterPath(String commitmentLetterPath) {
		this.commitmentLetterPath = commitmentLetterPath;
	}

	/**
	 * 履约回购合同编号（与项目中的编号一致）
	 * 
	 * @return
	 */
	public String getGuaranteeContractSn() {
		return guaranteeContractSn;
	}

	public void setGuaranteeContractSn(String guaranteeContractSn) {
		this.guaranteeContractSn = guaranteeContractSn;
	}

	/**
	 * 履约回购合同保存路径
	 * 
	 * @return
	 */
	public String getGuaranteeContractPath() {
		return guaranteeContractPath;
	}

	public void setGuaranteeContractPath(String guaranteeContractPath) {
		this.guaranteeContractPath = guaranteeContractPath;
	}

	/**
	 * 担保函编号
	 * 
	 * @return
	 */
	public String getGuaranteeLetterSn() {
		return guaranteeLetterSn;
	}

	public void setGuaranteeLetterSn(String guaranteeLetterSn) {
		this.guaranteeLetterSn = guaranteeLetterSn;
	}

	/**
	 * 担保函保存路径
	 * 
	 * @return
	 */
	public String getGuaranteeLetterPath() {
		return guaranteeLetterPath;
	}

	public void setGuaranteeLetterPath(String guaranteeLetterPath) {
		this.guaranteeLetterPath = guaranteeLetterPath;
	}

	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	

}
