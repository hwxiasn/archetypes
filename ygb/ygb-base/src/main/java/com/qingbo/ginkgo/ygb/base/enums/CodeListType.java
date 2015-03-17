package com.qingbo.ginkgo.ygb.base.enums;

public enum CodeListType {
	USER_ROLE("USER_ROLE", "用户角色"),
	AGENCY_ROLE("AGENCY_ROLE", "机构角色"),
	USER_STATUS("USER_STATUS", "用户状态"),
	IDENTITY_STATUS("IDENTITY_STATUS", "实名状态"),
	GROUP_USER("USER_RELATIONSHIP", "用户关系"),
	COMMISSION_ROLE("COMMISSION_ROLE", "分润参与角色"),
	ALLOCATION_PHASE("COMMISSION_PHASE", "分润阶段"),
	ALLOCATION_TYPE("COMMISSION_TYPE", "分润方式"),
	LOAN_TYPE("LOAN_TYPE", "借款期限类型"),
	FIX_PERIOD("FIX_PERIOD", "固定期限"),
	DAYS("DAYS", "定制天数"),
	MONTHS("MONTHS", "定制月份"),
	MIN_AMOUNT("MIN_AMOUNT", "最低投资额"),	
	SETTLE_TYPE("SETTLE_TYPE", "最低成交条件"),
	PROJECT_STATUS("PROJECT_STATUS", "项目状态"),
	BANKBRANCH("BANK_BRANCH", "银行支行"),
	TRANSACTION_TYPE("TRANSACTION_TYPE", "交易类型"),
	TRANSACTION_STATUS("TRANSACTION_STATUS", "交易状态"),
	GUARANTEELETTER_STATUS("GUARANTEELETTER_STATUS", "担保函状态"),
	OPERATOR_TYPE("OPERATOR_TYPE", "操作员类型"),
	;
	
	private String code, name;
	private CodeListType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public static CodeListType getByCode(String code) {
		for(CodeListType item : values()) {
			if(item.getCode().equals(code))
				return item;
		}
		return null;
	}
}
