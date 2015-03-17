package com.qingbo.ginkgo.ygb.project.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class ProjectReview extends BaseEntity {

	private static final long serialVersionUID = -2618390608516498998L;
	private Long projectId= 0L;
	private Long reviewerId= 0L;
	private String reviewerUserName = "";
	private String reviewerName = "";
	private String reviewCode = "";
	private String reviewName = "";
	private String result = "";
	private String reason = "";
	
	/**
	 * 项目ID
	 * @return
	 */
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	/**
	 * 审核者ID
	 * @return
	 */
	public Long getReviewerId() {
		return reviewerId;
	}
	public void setReviewerId(Long reviewerId) {
		this.reviewerId = reviewerId;
	}
	/**
	 * 审核代码
	 * @return
	 */
	public String getReviewCode() {
		return reviewCode;
	}
	public void setReviewCode(String reviewCode) {
		this.reviewCode = reviewCode;
	}
	/**
	 * 结果
	 * @return
	 */
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	/**
	 * 原因
	 * @return
	 */
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getReviewerUserName() {
		return reviewerUserName;
	}
	public void setReviewerUserName(String reviewerUserName) {
		this.reviewerUserName = reviewerUserName;
	}
	public String getReviewerName() {
		return reviewerName;
	}
	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}
	public String getReviewName() {
		return reviewName;
	}
	public void setReviewName(String reviewName) {
		this.reviewName = reviewName;
	}
	
	
}
