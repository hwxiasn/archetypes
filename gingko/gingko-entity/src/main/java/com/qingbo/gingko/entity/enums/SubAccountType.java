package com.qingbo.gingko.entity.enums;

public enum SubAccountType {
	DEFAULT("DEFAULT", "默认账户"), QDD("QDD", "乾多多");
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
