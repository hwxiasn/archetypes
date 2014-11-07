package com.qingbo.gingko.entity.enums;

public enum AccountLogTransferType {
	INVESTMENT("INVESTMENT", "投资"), 
	COMMISSION("COMMISSION", "分佣"), 
	REPAY("REPAY", "还款"),
	PRIZE("PRIZE","奖励"),
	TRANSFER("TRANSFER","转账");
	private String code, name;
	private AccountLogTransferType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public static AccountLogTransferType getByCode(String code) {
		for(AccountLogTransferType item : values()) {
			if(item.getCode().equals(code))
				return item;
		}
		return null;
	}
}
