package com.qingbo.ginkgo.ygb.project.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProjectCount implements Serializable {

	private static final long serialVersionUID = -8071750644382544888L;

	public ProjectCount(BigDecimal settleAmout, BigDecimal dueAmount) {
		this.settleAmout = settleAmout;
		this.dueAmount = dueAmount;
	}

	private BigDecimal dueAmount = BigDecimal.ZERO;
	private BigDecimal settleAmout = BigDecimal.ZERO;
	
	/**
	 * 还款总额
	 * @return
	 */
	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}
	
	/**
	 * 融资总额
	 * @return
	 */
	public BigDecimal getSettleAmout() {
		return settleAmout;
	}

	public void setSettleAmout(BigDecimal settleAmout) {
		this.settleAmout = settleAmout;
	}

}
