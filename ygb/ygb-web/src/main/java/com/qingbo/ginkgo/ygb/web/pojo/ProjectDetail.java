package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.qingbo.ginkgo.ygb.project.entity.Project;

public class ProjectDetail implements Serializable {
	private static final long serialVersionUID = -225515790913957700L;

	private Project project;
	
	private String interestPercent;//收益百分比;
	private String investPeriod;//投资期限
	private String investProgress;//投资百分比进度，例如80表示80%
	private String investStart;//投资开始时间
	private String investEnd;//投资结束时间
	private String statusName;//状态名称
	private String guarantee;//担保公司userId
	private String guaranteeName;//担保公司userName
	private String borrowerName;//借款人姓名
	private String realName;//真实姓名
	private String investRemains;//剩余投资金额
	private String commitmentLetterSn;//担保承诺函编号
	private String guaranteeLetterPath;//担保函路径
	private int totalAmount;//万
	private String templateName;
	public Map<String, List<String>> projectImages;//尽职调查图片
	
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public String getInterestPercent() {
		return interestPercent;
	}
	public void setInterestPercent(String interestPercent) {
		this.interestPercent = interestPercent;
	}
	public String getInvestPeriod() {
		return investPeriod;
	}
	public void setInvestPeriod(String investPeriod) {
		this.investPeriod = investPeriod;
	}
	public String getInvestProgress() {
		return investProgress;
	}
	public void setInvestProgress(String investProgress) {
		this.investProgress = investProgress;
	}
	public String getInvestStart() {
		return investStart;
	}
	public void setInvestStart(String investStart) {
		this.investStart = investStart;
	}
	public String getInvestEnd() {
		return investEnd;
	}
	public void setInvestEnd(String investEnd) {
		this.investEnd = investEnd;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getGuarantee() {
		return guarantee;
	}
	public void setGuarantee(String guarantee) {
		this.guarantee = guarantee;
	}
	public String getGuaranteeName() {
		return guaranteeName;
	}
	public void setGuaranteeName(String guaranteeName) {
		this.guaranteeName = guaranteeName;
	}
	public String getBorrowerName() {
		return borrowerName;
	}
	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getInvestRemains() {
		return investRemains;
	}
	public void setInvestRemains(String investRemains) {
		this.investRemains = investRemains;
	}
	public String getCommitmentLetterSn() {
		return commitmentLetterSn;
	}
	public void setCommitmentLetterSn(String commitmentLetterSn) {
		this.commitmentLetterSn = commitmentLetterSn;
	}
	public String getGuaranteeLetterPath() {
		return guaranteeLetterPath;
	}
	public void setGuaranteeLetterPath(String guaranteeLetterPath) {
		this.guaranteeLetterPath = guaranteeLetterPath;
	}
	public Map<String, List<String>> getProjectImages() {
		return projectImages;
	}
	public void setProjectImages(Map<String, List<String>> projectImages) {
		this.projectImages = projectImages;
	}
	public int getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
}
