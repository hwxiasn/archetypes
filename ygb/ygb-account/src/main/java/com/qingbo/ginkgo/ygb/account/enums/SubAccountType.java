package com.qingbo.ginkgo.ygb.account.enums;

public enum SubAccountType {
	DEFAULT("DEFAULT", "默认账户"), 
	QDD_BY("QDD_BY", "乾多多倍赢平台"),
	QDD_GH("QDD_GH", "乾多多光华平台");
	private String code, name;
	private SubAccountType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public static SubAccountType getByCode(String code) {
		for(SubAccountType item : values()) {
			if(item.getCode().equals(code))
				return item;
		}
		return null;
	}
}
