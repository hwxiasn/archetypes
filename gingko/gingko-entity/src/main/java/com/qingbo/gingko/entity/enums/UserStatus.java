package com.qingbo.gingko.entity.enums;

public enum UserStatus {
	INACTIVE("I", "未激活"), ACTIVE("A", "已激活"), LOCKED("L", "锁定"), DISABLED("D", "禁用");
	private String code, name;
	private UserStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public static UserStatus getByCode(String code) {
		for(UserStatus userStatus : values()) {
			if(userStatus.getCode().equals(code))
				return userStatus;
		}
		return null;
	}
}
