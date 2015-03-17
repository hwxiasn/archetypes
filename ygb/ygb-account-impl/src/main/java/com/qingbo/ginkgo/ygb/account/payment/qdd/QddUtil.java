package com.qingbo.ginkgo.ygb.account.payment.qdd;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class QddUtil {
	//接口相对请求地址
	public static final String loanRegister = "loan/toloanregisterbind.action";
	public static final String loadFastpay = "loan/toloanfastpay.action";
	public static final String loanAuthorise = "loan/toloanauthorize.action";
	public static final String loanRecharge = "loan/toloanrecharge.action";
	public static final String loanBalanceQuery = "loan/balancequery.action";
	public static final String loanTransfer = "loan/loan.action";
	public static final String loanAudit = "loan/toloantransferaudit.action";
	public static final String loanWithdraw = "loan/toloanwithdraws.action";
	public static final String loanFastpay = "loan/toloanfastpay.action";
	public static final String balanceQuery = "loan/balancequery.action";
	public static final String orderQuery = "loan/loanorderquery.action";
	public static final String loanRelease = "loan/toloanrelease.action";
	
	/**
	 * 校验乾多多账户
	 */
	public static boolean validMoneymoremoreId(String moneyMoreMoreId) {
		if(StringUtils.isBlank(moneyMoreMoreId)) return false;
		return moneyMoreMoreId.startsWith("m") || moneyMoreMoreId.startsWith("p");
	}
	
	/**
	 * 解析单个账户的乾多多余额
	 */
	public static BigDecimal[] balanceQuery(String response) {
		if(StringUtils.isBlank(response)) return null;
		String[] moneys = response.split("[|]");//200004.00|200004.00|0.00
		if(moneys.length==3) {//网贷平台子账户可用余额|总可用余额(子账户可用余额+公共账户可用余额)|子账户冻结余额
			BigDecimal[] balances = new BigDecimal[3];
			balances[0] = new BigDecimal(moneys[0]);
			balances[1] = new BigDecimal(moneys[1]);
			balances[2] = new BigDecimal(moneys[2]);
			return balances;
		}
		return null;
	}
	
	/**
	 * 解析多个账户的乾多多余额
	 */
	public static Map<String, Map<String, String>> balanceQuerys(String response) {
		Map<String, Map<String, String>> balances = new HashMap<>();
		if(StringUtils.isBlank(response)) return balances;
		
		//[200, m16359|5625.00|5625.00|0.00,m15568|105975.00|105975.00|1420000.00,m15693|3500.00|3500.00|0.00]
		String[] accountBalances = response.split("[,]");
		for(String accountBalance:accountBalances) {
			String[] moneys = accountBalance.split("[|]");
			if(moneys.length==4) {//网贷平台子账户可用余额|总可用余额(子账户可用余额+公共账户可用余额)|子账户冻结余额
				Map<String, String> balanceMap = new HashMap<>();
				if(moneys[0].startsWith("p")) {//平台是2+3，0.00|1500.00|0.00
					balanceMap.put("balance", moneys[2]);
				}else {//普通用户是1+3，1500.00|1500.00|0.00
					balanceMap.put("balance", moneys[1]);
				}
				balanceMap.put("freezeBalance", moneys[3]);
				balances.put(moneys[0], balanceMap);
			}
		}
		return balances;
	}
}
