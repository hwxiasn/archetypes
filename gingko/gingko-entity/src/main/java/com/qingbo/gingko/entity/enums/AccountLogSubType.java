package com.qingbo.gingko.entity.enums;

public enum AccountLogSubType {
	DEPOSIT("DEPOSIT", "充值"), 
	WITHDRAW("WITHDRAW", "提现"), 
	TRANSFER("TRANSFER", "转账"),//直接转账TRANSFER，投资INVESTMENT，分佣COMMISSION，还款REPAY，奖励PRIZE，
	FEE("FEE","手续费"); //手续费FEE
	private String code, name;
	private AccountLogSubType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public static AccountLogSubType getByCode(String code) {
		for(AccountLogSubType item : values()) {
			if(item.getCode().equals(code))
				return item;
		}
		return null;
	}
}
