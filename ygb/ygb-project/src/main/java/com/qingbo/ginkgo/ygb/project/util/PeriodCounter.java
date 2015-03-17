package com.qingbo.ginkgo.ygb.project.util;

/**
 * 借款天数计算方法
 *
 */
public class PeriodCounter {

	public static final int getDays(String periodType,int period){
		if(periodType == null || periodType.trim().equals("")){
			return 0;
		}
		int days = 0;
		if(periodType.trim().equalsIgnoreCase("DAYS")){
			days = period;
		}else if(periodType.trim().equalsIgnoreCase("MONTHS")
				|| periodType.trim().equalsIgnoreCase("FIX_PERIOD")){
			days = period * 30 ;
		} 
		return days;
	}
	
}
