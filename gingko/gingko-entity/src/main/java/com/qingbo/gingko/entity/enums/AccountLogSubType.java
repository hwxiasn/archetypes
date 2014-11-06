package com.qingbo.gingko.entity.enums;

public enum AccountLogSubType {
	DEPOSIT("DEPOSIT", "充值"), WITHDRAW("WITHDRAW", "提现"), FREEZE("FREEZE","冻结"), UNFREEZE("UNFREEZE", "解冻")
	, TRANSFER("TRANSFER", "转账"), INVESTMENT("INVESTMENT", "投资"), COMMISSION("COMMISSION", "分佣"), REPAY("REPAY","还款")
	, FEE("FEE", "手续费"), PRIZE("PRIZE","奖励");
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
