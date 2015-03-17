package com.qingbo.ginkgo.ygb.trade.impl.util;

import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;

public class TradeError {
	private static ErrorMessage errorMessage = null;

	public static ErrorMessage getMessage(){
		if(errorMessage == null){
			errorMessage = new ErrorMessage("trade-error.properties");
		}
		return errorMessage;
	}
	
	public static final String TradeSys0002 = "TXN0002";

	public static final String TradeSys1000 = "TXN1000";
	public static final String TradeSys1001 = "TXN1001";
	public static final String TradeSys1002 = "TXN1001";
	public static final String TradeSys1003 = "TXN1001";
	public static final String TradeSys1004 = "TXN1001";
	public static final String TradeSys1005 = "TXN1001";
	public static final String TradeSys1006 = "TXN1001";
	public static final String TradeSys1007 = "TXN1001";
	public static final String TradeSys1008 = "TXN1001";
	
	
	
}
