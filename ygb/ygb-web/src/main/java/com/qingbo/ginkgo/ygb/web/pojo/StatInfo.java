package com.qingbo.ginkgo.ygb.web.pojo;

import java.math.BigDecimal;

public class StatInfo {
	
	private int count = 0;
	private BigDecimal amount = BigDecimal.ZERO;
	
	public StatInfo() {
	}
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


}
