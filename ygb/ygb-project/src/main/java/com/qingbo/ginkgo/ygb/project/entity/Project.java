package com.qingbo.ginkgo.ygb.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.project.util.AmountCalculator;

@Entity
public class Project extends BaseEntity {
	private static final long serialVersionUID = -2214298322464846679L;
	private String name = ""; // ' 项目名称 ',
	private BigDecimal totalAmount = BigDecimal.ZERO; // ' 融资金额（精度到分，单位：元） ',
	private BigDecimal interestRate = BigDecimal.ZERO; // ' 投资收益率（年化） ',
	private String periodType = ""; // ' 借款期限类型 ',
	private String period = ""; // ' 借款期限（与类型合并可计算出具体周期） ',
	private int periodDays = 0;//借款天数,创建时计算出来
	private BigDecimal minimalInvestment = BigDecimal.ZERO; // '最低投资额（精度到分，单位：元）
	private Long fundingTemplateId = 0L; // ' 分佣模板ID ',
	private Long repayTemplateId = 0L; // ' 分润模板ID ',
	private Long loaneeId = 0L; // ' 借款人ID ',
	private String purpose = ""; // ' 借款目的 ',
	private Long sponsorUserId = 0L; // ' 保荐机构用户ID ',
	private String sponsorMemo = ""; // ' 保荐机构推荐语 ',
	private String type = ""; // ' 项目类型（担保贷还是接力贷等） ',
	private String status = ""; // ' 项目状态 ',
	private BigDecimal progressAmount = BigDecimal.ZERO; // ' 已投资金额（精度到分，单位：元）
	private int progress = 0; // ' 投资百分比（百分数） ',
	private int totalInvestor = 0; // ' 投资总人数 ',
	private Long guaranteeLetterId = 0L; // ' 担保函ID ',
	private String contractNo = ""; // ' 项目合同编码 ',
	private String contractStatus = "";//项目合同制作状态
	private Date publishDate; // ' 项目发布时间 ',
	private Date investStartDate; // ' 项目开始投资时间 ',
	private Date investEndDate; // ' 项目结束投资时间 ',
	private String settleTerms = ""; // ' 满标条件 ',
	private BigDecimal settleTermsAmount = BigDecimal.ZERO; // ' 满标金额（精度到分，单位：元）
	private Date settleDate; // ' 项目成立时间 ',
	private BigDecimal settleAmout = BigDecimal.ZERO; // ' 项目实际融资金额（精度到分，单位：元）
	private Date effectiveDate; // ' 项目生效时间 ',
	private Date dueDate; // ' 项目预计还款时间 ',
	private BigDecimal dueAmount = BigDecimal.ZERO; // ' 项目还款金额（精度到分，单位：元） ',
	private Long fundraiseTradeId = 0L;//募集交易ID
	private Long repayTradeId = 0L;//还款交易ID
	private Long fundraiseFeeTradeId = 0L;//募集分佣父交易ID
	private String loaneeUserName = "";//借款人用户名
	private String guaranteeUserId = "";//担保机构用户ID
	
	//以下为页面呈现及辅助信息，不作为实体信息
	@Transient private String loaneeRealName = "";//借款人真实姓名
	@Transient private String sponsorUserName = "";//保荐人用户名
	@Transient private double termAmountFix = 0.0;//固定金额
	@Transient private double termAmountPercent = 0.0;//固定百分比
	@Transient private BigDecimal yieldPerUnitInvestment = BigDecimal.ZERO;//单位投资收益
	
	@Transient private String formatPublishDate = "";
	@Transient private String formatInvestStartDate="";
	@Transient private String formatInvestEndDate="";
	@Transient private String formatSettleDate="";
	@Transient private String formatEffectiveDate="";
	@Transient private String formatDueDate="";
	
	
	/**
	 * 项目名称
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 融资金额（精度到分，单位：元）
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * 投资收益率（年化）
	 */
	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	/**
	 * 借款期限类型
	 */
	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	/**
	 * 借款期限（与类型合并可计算出具体周期）
	 */
	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	/**
	 * 最低投资额（精度到分，单位：元）
	 * 
	 * @return
	 */
	public BigDecimal getMinimalInvestment() {
		return minimalInvestment;
	}

	public void setMinimalInvestment(BigDecimal minimalInvestment) {
		this.minimalInvestment = minimalInvestment;
	}

	/**
	 * 分佣模板ID
	 * 
	 * @return
	 */
	public Long getFundingTemplateId() {
		return fundingTemplateId;
	}

	public void setFundingTemplateId(Long fundingTemplateId) {
		this.fundingTemplateId = fundingTemplateId;
	}

	/**
	 * 分润模板ID
	 * 
	 * @return
	 */
	public Long getRepayTemplateId() {
		return repayTemplateId;
	}

	public void setRepayTemplateId(Long repayTemplateId) {
		this.repayTemplateId = repayTemplateId;
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
	 * 借款目的
	 * 
	 * @return
	 */
	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	/**
	 * 保荐机构用户ID
	 * 
	 * @return
	 */
	public Long getSponsorUserId() {
		return sponsorUserId;
	}

	public void setSponsorUserId(Long sponsorUserId) {
		this.sponsorUserId = sponsorUserId;
	}

	/**
	 * 保荐机构推荐语
	 * 
	 * @return
	 */
	public String getSponsorMemo() {
		return sponsorMemo;
	}

	public void setSponsorMemo(String sponsorMemo) {
		this.sponsorMemo = sponsorMemo;
	}

	/**
	 * 项目类型（担保贷还是接力贷等）
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 项目状态
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
	 * 已投资金额（精度到分，单位：元）
	 * 
	 * @return
	 */
	public BigDecimal getProgressAmount() {
		return progressAmount;
	}

	public void setProgressAmount(BigDecimal progressAmount) {
		this.progressAmount = progressAmount;
	}

	/**
	 * 投资百分比（百分数）
	 * 
	 * @return
	 */
	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * 投资总人数
	 * 
	 * @return
	 */
	public int getTotalInvestor() {
		return totalInvestor;
	}

	public void setTotalInvestor(int totalInvestor) {
		this.totalInvestor = totalInvestor;
	}

	/**
	 * 担保函ID
	 * 
	 * @return
	 */
	public Long getGuaranteeLetterId() {
		return guaranteeLetterId;
	}

	public void setGuaranteeLetterId(Long guaranteeLetterId) {
		this.guaranteeLetterId = guaranteeLetterId;
	}

	/**
	 * 项目合同编码
	 * 
	 * @return
	 */
	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	/**
	 * 项目发布时间
	 * 
	 * @return
	 */
	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	/**
	 * 项目开始投资时间
	 * 
	 * @return
	 */
	public Date getInvestStartDate() {
		return investStartDate;
	}

	public void setInvestStartDate(Date investStartDate) {
		this.investStartDate = investStartDate;
	}

	/**
	 * 项目结束投资时间
	 * 
	 * @return
	 */
	public Date getInvestEndDate() {
		return investEndDate;
	}

	public void setInvestEndDate(Date investEndDate) {
		this.investEndDate = investEndDate;
	}

	/**
	 * 满标条件
	 * 
	 * @return
	 */
	public String getSettleTerms() {
		return settleTerms;
	}

	public void setSettleTerms(String settleTerms) {
		this.settleTerms = settleTerms;
	}

	/**
	 * 满标金额（精度到分，单位：元）
	 * 
	 * @return
	 */
	public BigDecimal getSettleTermsAmount() {
		return settleTermsAmount;
	}

	public void setSettleTermsAmount(BigDecimal settleTermsAmount) {
		this.settleTermsAmount = settleTermsAmount;
	}

	/**
	 * 项目成立时间
	 * 
	 * @return
	 */
	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	/**
	 * 项目实际融资金额（精度到分，单位：元）
	 * 
	 * @return
	 */
	public BigDecimal getSettleAmout() {
		return settleAmout;
	}

	public void setSettleAmout(BigDecimal settleAmout) {
		this.settleAmout = settleAmout;
	}

	/**
	 * 项目生效时间
	 * 
	 * @return
	 */
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	/**
	 * 项目预计还款时间
	 * 
	 * @return
	 */
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * 项目还款金额（精度到分，单位：元）
	 * 
	 * @return
	 */
	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}

	public Long getFundraiseTradeId() {
		return fundraiseTradeId;
	}

	public void setFundraiseTradeId(Long fundraiseTradeId) {
		this.fundraiseTradeId = fundraiseTradeId;
	}

	public Long getRepayTradeId() {
		return repayTradeId;
	}

	public void setRepayTradeId(Long repayTradeId) {
		this.repayTradeId = repayTradeId;
	}

	public String getLoaneeUserName() {
		return loaneeUserName;
	}

	public void setLoaneeUserName(String loaneeUserName) {
		this.loaneeUserName = loaneeUserName;
	}

	public String getSponsorUserName() {
		return sponsorUserName;
	}

	public void setSponsorUserName(String sponsorUserName) {
		this.sponsorUserName = sponsorUserName;
	}

	public String getLoaneeRealName() {
		return loaneeRealName;
	}

	public void setLoaneeRealName(String loaneeRealName) {
		this.loaneeRealName = loaneeRealName;
	}

	public double getTermAmountFix() {
		return termAmountFix;
	}

	public void setTermAmountFix(double termAmountFix) {
		this.termAmountFix = termAmountFix;
	}

	public double getTermAmountPercent() {
		return termAmountPercent;
	}

	public void setTermAmountPercent(double termAmountPercent) {
		this.termAmountPercent = termAmountPercent;
	}

	public String getGuaranteeUserId() {
		return guaranteeUserId;
	}

	public void setGuaranteeUserId(String userId) {
		this.guaranteeUserId = userId;
	}

	public int getPeriodDays() {
		return periodDays;
	}

	public void setPeriodDays(int periodDays) {
		this.periodDays = periodDays;
	}

	public Long getFundraiseFeeTradeId() {
		return fundraiseFeeTradeId;
	}

	public void setFundraiseFeeTradeId(Long fundraiseFeeTradeId) {
		this.fundraiseFeeTradeId = fundraiseFeeTradeId;
	}

	public String getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}

	public String getFormatPublishDate() {
		if(this.getPublishDate() != null){
			return DateUtil.format(this.getPublishDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatPublishDate;
	}

	public String getFormatInvestStartDate() {
		if(this.getInvestStartDate() != null){
			return DateUtil.format(this.getInvestStartDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatInvestStartDate;
	}

	public String getFormatInvestEndDate() {
		if(this.getInvestEndDate() != null){
			return DateUtil.format(this.getInvestEndDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatInvestEndDate;
	}

	public String getFormatSettleDate() {
		if(this.getSettleDate() != null){
			return DateUtil.format(this.getSettleDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatSettleDate;
	}

	public String getFormatEffectiveDate() {
		if(this.getEffectiveDate() != null){
			return DateUtil.format(this.getEffectiveDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatEffectiveDate;
	}

	public String getFormatDueDate() {
		if(this.getDueDate() != null){
			return DateUtil.format(this.getDueDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatDueDate;
	}

	public BigDecimal getYieldPerUnitInvestment() {
		BigDecimal unit = BigDecimal.valueOf(10000);
		yieldPerUnitInvestment = AmountCalculator.calculate(unit, this.getInterestRate(), this.getPeriodDays(),false);
		return yieldPerUnitInvestment;
	}

}
