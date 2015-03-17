package com.qingbo.ginkgo.ygb.account.enums;

public enum AccountLogSubType {
	DEPOSIT("DEPOSIT", "充值"), WITHDRAW_BACK("WITHDRAW_BACK", "提现退回"),//单纯收入
	WITHDRAW("WITHDRAW", "提现"), FEE("FEE","手续费"),//单纯支出
	TRANSFER("TRANSFER", "转账"),//转账，两个账户之间资金流动，直接转账TRANSFER，投资INVESTMENT，分佣COMMISSION，还款REPAY
	RELEASE("RELEASE","手续费"),//资金释放，单纯收入或支出
	;
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
