package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

/** 我要投资：搜索项目 */
public class ProjectSearch implements Serializable {
	private static final long serialVersionUID = -5795711385950996770L;
	/**
	 * 项目状态：最新发布，最新还款
	 */
	private int type = -1;
	/** 还款期限 */
	public int repayPeriod;
	/** 金额范围 */
	public int moneyRange;
	/** 筹资进度 */
	public int fundingProgress;
	/** 担保公司 */
	public String guaranteeEnterprise;
	/** 借款人ID */
	private Long loaneeId;
	
	public Long getLoaneeId() {
		return loaneeId;
	}
	public void setLoaneeId(Long loaneeId) {
		this.loaneeId = loaneeId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getRepayPeriod() {
		return repayPeriod;
	}
	public void setRepayPeriod(int repayPeriod) {
		this.repayPeriod = repayPeriod;
	}
	public int getMoneyRange() {
		return moneyRange;
	}
	public void setMoneyRange(int moneyRange) {
		this.moneyRange = moneyRange;
	}
	public int getFundingProgress() {
		return fundingProgress;
	}
	public void setFundingProgress(int fundingProgress) {
		this.fundingProgress = fundingProgress;
	}
	public String getGuaranteeEnterprise() {
		return guaranteeEnterprise;
	}
	public void setGuaranteeEnterprise(String guaranteeEnterprise) {
		this.guaranteeEnterprise = guaranteeEnterprise;
	}

	/** 还款期限 */
	public static enum RepayPeriods {
		ThirtyDaysIn(0, 1), 
		OneToThreeMonths(1, 3), 
		ThreeToSixMonths(3, 6), 
		SixToNineMonths(6, 9), 
		NineToTwelveMonths(9, 12);
		public int minMonths;
		public int maxMonths;
		private RepayPeriods(int minMonths, int maxMonths) {
			this.minMonths = minMonths;
			this.maxMonths = maxMonths;
		}
	}

	/** 金额范围 */
	public static enum MoneyRanges {
		TenToFiftyWan(10, 50),
		FiftyToOneHundredWan(50, 100),
		OneHundredToFiveHundredWan(100, 500),
		FiveHundredToTenHundredWan(500, 1000),
		TenHundredToFiftyHundredWan(1000, 5000),
		FiftyHundredToMuchMoreWan(5000, -1);
		public int minMoney;
		public int maxMoney;
		private MoneyRanges(int minMoney, int maxMoney) {
			this.minMoney = minMoney;
			this.maxMoney = maxMoney;
		}
	}
	
	/** 筹资进度 */
	public static enum FundingProgresses {
		ZeroToTenPercent(0, 10),
		TenToTwentyPercent(10, 20),
		TwentyToFiftyPercent(20, 50),
		FiftyToEightyPercent(50, 80),
		EightyToNinetyPercent(80, 90),
		NinetyToAHundredPercent(90, 100);
		public int minPercent;
		public int maxPercent;
		private FundingProgresses(int minPercent, int maxPercent) {
			this.minPercent = minPercent;
			this.maxPercent = maxPercent;
		}
	}
}
