package com.qingbo.ginkgo.ygb.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;

@Entity
public class Investment extends BaseEntity {

	private static final long serialVersionUID = -2041781035022927672L;
	private Long inverstorId = 0L;// 投资人ID
	private Long projectId = 0L;// 项目ID
	private Long loaneeId = 0L;// 借款人ID
	private Long tradeId = 0L;// 交易ID
	private BigDecimal balance = BigDecimal.ZERO; // 投资金额（精度到分，单位：元）
	private BigDecimal balanceDue = BigDecimal.ZERO; // 预期收益金额（精度到分，单位：元）
	private String investNo = "";// 投资流水号
	private String investAccNo = "";// 借款流水号
	private String status = "";// 投资状态
	private Date issueDate;// 预投资时间
	private Date settledDate;// 投资生效时间
	private Date dueDate;// 预计还款时间
	private String investPath = "";//投资凭证地址
	@Transient private Project project;
	@Transient private String statusName;
	@Transient private String formatIssueDate;// 投资意向时间_格式化
	@Transient private String formatSettledDate;// 投资生效时间_格式化
	@Transient private String formatDueDate;// 预计还款时间_格式化
	
	@Transient private String startDate;//查询开始时间
	@Transient private String endDate;//查询结束时间
	@Transient private String searchDate;//按指定日期范围查询
	@Transient private String minAmount;//最小金额
	@Transient private String maxAmount;//最大金额
	
	@Transient private String investorName;//投资者用户名
	@Transient private BigDecimal commissionFee = BigDecimal.ZERO;//分佣金额

	/**
	 * 投资人ID
	 * 
	 * @return
	 */
	public Long getInverstorId() {
		return inverstorId;
	}

	public void setInverstorId(Long inverstorId) {
		this.inverstorId = inverstorId;
	}

	/**
	 * 项目ID
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
	 * 借款人ID
	 * 
	 * @return
	 */
	public Long getLoaneeId() {
		return loaneeId;
	}

	public void setLoaneeId(Long loaneeId) {
		this.loaneeId = loaneeId;
	}

	/**
	 * 投资金额（精度到分，单位：元）
	 * 
	 * @return
	 */
	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	/**
	 * 预期收益金额（精度到分，单位：元）
	 * 
	 * @return
	 */
	public BigDecimal getBalanceDue() {
		return balanceDue;
	}

	public void setBalanceDue(BigDecimal balanceDue) {
		this.balanceDue = balanceDue;
	}

	/**
	 * 投资流水号
	 * 
	 * @return
	 */
	public String getInvestNo() {
		return investNo;
	}

	public void setInvestNo(String investNo) {
		this.investNo = investNo;
	}

	/**
	 * 借款流水号
	 * 
	 * @return
	 */
	public String getInvestAccNo() {
		return investAccNo;
	}

	public void setInvestAccNo(String investAccNo) {
		this.investAccNo = investAccNo;
	}

	/**
	 * 投资状态
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
	 * 预投资时间
	 * 
	 * @return
	 */
	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	/**
	 * 投资生效时间
	 * 
	 * @return
	 */
	public Date getSettledDate() {
		return settledDate;
	}

	public void setSettledDate(Date settledDate) {
		this.settledDate = settledDate;
	}

	/**
	 * 预计还款时间
	 * 
	 * @return
	 */
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

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

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getFormatIssueDate() {
		if(this.getIssueDate() != null){
			return DateUtil.format(this.getIssueDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatIssueDate;
	}

	public void setFormatIssueDate(String formatIssueDate) {
		this.formatIssueDate = formatIssueDate;
	}

	public String getFormatSettledDate() {
		if(this.getSettledDate() != null){
			return DateUtil.format(this.getSettledDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatSettledDate;
	}

	public void setFormatSettledDate(String formatSettledDate) {
		this.formatSettledDate = formatSettledDate;
	}

	public String getFormatDueDate() {
		if(this.getDueDate() != null){
			return DateUtil.format(this.getDueDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatDueDate;
	}

	public void setFormatDueDate(String formatDueDate) {
		this.formatDueDate = formatDueDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}

	public String getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(String minAmount) {
		this.minAmount = minAmount;
	}

	public String getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(String maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getInvestPath() {
		return investPath;
	}

	public void setInvestPath(String investPath) {
		this.investPath = investPath;
	}

	public String getInvestorName() {
		return investorName;
	}

	public void setInvestorName(String investorName) {
		this.investorName = investorName;
	}

	public BigDecimal getCommissionFee() {
		return commissionFee;
	}

	public void setCommissionFee(BigDecimal commissionFee) {
		this.commissionFee = commissionFee;
	}
}
