package com.qingbo.ginkgo.ygb.cms.enums;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {

	SYSTEM(1, "系统消息"), GROUP(2, "群组消息"), PRIVATE(3, "私信");
	
	private Integer code;
	private String name;
	
	private MessageType(Integer code, String name) {
		this.code = code;
		this.name = name;
	}
	public Integer getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	
	private static Map<Integer, String> codeNameMap;
	private static Map<Integer, MessageType> codeMap;
	private static Map<String, MessageType> nameMap;
	static {
		codeNameMap = new HashMap<Integer, String>();
		codeMap = new HashMap<Integer, MessageType>();
		nameMap = new HashMap<String, MessageType>();
		for (MessageType type : MessageType.values()) {
			codeNameMap.put(type.getCode(), type.getName());
			codeMap.put(type.getCode(), type);
			nameMap.put(type.getName(), type);
		}
	}
	
	public static Map<Integer, String> getCodeNameMap(){
		return codeNameMap;
	}

	public static MessageType getByCode(Integer code) {
		return codeMap.get(code);
	}
	
	public static MessageType getByName(String name) {
		return nameMap.get(name);
	}
}
