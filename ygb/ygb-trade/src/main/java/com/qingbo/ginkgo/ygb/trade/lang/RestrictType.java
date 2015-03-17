package com.qingbo.ginkgo.ygb.trade.lang;

import java.io.Serializable;

public class RestrictType implements Serializable {
	private static final long serialVersionUID = -7331937266038576314L;

	private String tradePkgType;
	private String restrictType;
	
	public RestrictType(){
	}
	public String getTradePkgType() {
		return tradePkgType;
	}
	public void setTradePkgType(String tradePkgType) {
		this.tradePkgType = tradePkgType;
	}
	public String getRestrictType() {
		return restrictType;
	}
	public void setRestrictType(String restrictType) {
		this.restrictType = restrictType;
	}
}
