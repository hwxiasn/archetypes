package com.qingbo.ginkgo.ygb.project.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AmountCalculator {

	/**
	 * 计算收益金额：根据金额、收益率及期间，计算收益金额；</br> 公式为：金额x收益率x天数÷360；</br>
	 * 取2位小数，末尾收；
	 * @param totalAmount
	 * @param interestRate
	 * @param days
	 * 天数（金融月 每月按30天计算）
	 */
	public static final BigDecimal calculate(BigDecimal totalAmount,BigDecimal interestRate, int days) {
//		BigDecimal interestAmount = BigDecimal.ZERO;
//		if (totalAmount != null && interestRate != null && days > 0) {
//			interestAmount = totalAmount.multiply(interestRate);// 计算年佣金
//			interestAmount = interestAmount.multiply(BigDecimal.valueOf(days));
//			interestAmount = interestAmount.divide(BigDecimal.valueOf(360), 15,RoundingMode.HALF_EVEN);
//			interestAmount = interestAmount.setScale(2, RoundingMode.UP);
//		}
		return calculate(totalAmount,interestRate,days,true);
	}

	/**
	 * 计算收益金额：根据金额、收益率及期间，计算收益金额；</br> 公式为：金额x收益率x天数÷360；</br>
	 * 取2位小数，末尾收；
	 * @param totalAmount
	 * @param interestRate
	 * @param days
	 * 天数（金融月 每月按30天计算）
	 * @param up
	 * 尾数向上收取还是向下收取 true=向上收取,false=向下收取
	 * @return
	 */
	public static final BigDecimal calculate(BigDecimal totalAmount,BigDecimal interestRate, int days,boolean up) {
		BigDecimal interestAmount = BigDecimal.ZERO;
		if (totalAmount != null && interestRate != null && days > 0) {
			interestAmount = totalAmount.multiply(interestRate);// 计算年佣金
			interestAmount = interestAmount.multiply(BigDecimal.valueOf(days));
			interestAmount = interestAmount.divide(BigDecimal.valueOf(360), 15,RoundingMode.HALF_EVEN);
			interestAmount = interestAmount.setScale(2, up?RoundingMode.UP:RoundingMode.DOWN);
		}
		return interestAmount;
	}
}
