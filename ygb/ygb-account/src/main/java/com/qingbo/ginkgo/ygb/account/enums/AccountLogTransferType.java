package com.qingbo.ginkgo.ygb.account.enums;

public enum AccountLogTransferType {
	INVESTMENT("INVESTMENT", "投资"), REPAY("REPAY", "还款"),//冻结、解冻、支出、收入都有
	TRANSFER("TRANSFER","转账"),COMMISSION("COMMISSION", "分佣"), //收入、支出
	;
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
