package com.qingbo.ginkgo.ygb.account.enums;

public enum AccountLogType {
	IN("IN", "收入"), //充值DEPOSIT，转入TRANSFER（投资INVESTMENT，分佣COMMISSION，还款REPAY，奖励PRIZE）
	OUT("OUT", "支出"), //提现WITHDRAW，转出TRANSFER（投资INVESTMENT，分佣COMMISSION，还款REPAY，奖励PRIZE，手续费FEE）
	FREEZE("FREEZE","冻结"), //（投资INVESTMENT，分佣COMMISSION，还款REPAY）
	UNFREEZE("UNFREEZE","解冻"); //（投资INVESTMENT，分佣COMMISSION，还款REPAY）
	private String code, name;
	private AccountLogType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public static AccountLogType getByCode(String code) {
		for(AccountLogType item : values()) {
			if(item.getCode().equals(code))
				return item;
		}
		return null;
	}
}
