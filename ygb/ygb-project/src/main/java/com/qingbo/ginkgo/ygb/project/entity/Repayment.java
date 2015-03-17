package com.qingbo.ginkgo.ygb.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;

@Entity
public class Repayment extends BaseEntity {

	private static final long serialVersionUID = -8845804466421735639L;
	private Long loaneeId = 0L;// 借款人ID
	private Long projectId = 0L;// 项目ID
	private Long tradeId = 0L;// 还款交易ID
	private BigDecimal balance = BigDecimal.ZERO; // 还款金额（精度到分，单位：元）
	private String status = "";// 还款状态
	private Date issueDate;// 预还款时间
	private Date repayDate;// 还款成功时间
	
	@Transient Project project;//项目详情
	@Transient private String formatIssueDate = "";
	@Transient private String formatRepayDate = "";
	
	/**
	 * 借款人ID
	 * @return
	 */
	public Long getLoaneeId() {
		return loaneeId;
	}

	public void setLoaneeId(Long loaneeId) {
		this.loaneeId = loaneeId;
	}

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
	 * 还款金额（精度到分，单位：元）
	 * @return
	 */
	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	/**
	 * 还款状态
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 预还款时间
	 * @return
	 */
	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	/**
	 * 还款成功时间
	 * @return
	 */
	public Date getRepayDate() {
		return repayDate;
	}

	public void setRepayDate(Date repayDate) {
		this.repayDate = repayDate;
	}
	/**
	 * 还款交易ID
	 * @return
	 */
	public Long getTradeId() {
		return tradeId;
	}

	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getFormatIssueDate() {
		if(this.getIssueDate() != null){
			return DateUtil.format(this.getIssueDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatIssueDate;
	}

	public String getFormatRepayDate() {
		if(this.getRepayDate() != null){
			return DateUtil.format(this.getRepayDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatRepayDate;
	}

}
