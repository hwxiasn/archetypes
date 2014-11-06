package com.qingbo.gingko.entity.enums;

public enum AccountLogType {
	IN("IN", "收入"), OUT("OUT", "支出"), FREEZE("FREEZE","冻结或解冻");
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
