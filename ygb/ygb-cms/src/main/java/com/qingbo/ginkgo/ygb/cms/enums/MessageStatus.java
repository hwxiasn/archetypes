package com.qingbo.ginkgo.ygb.cms.enums;

import java.util.HashMap;
import java.util.Map;

public enum MessageStatus {

	NOREAD(1, "未读"), READ(2, "已读");
	
	private Integer code;
	private String name;
	
	private MessageStatus(Integer code, String name) {
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
	private static Map<Integer, MessageStatus> codeMap;
	private static Map<String, MessageStatus> nameMap;
	static {
		codeNameMap = new HashMap<Integer, String>();
		codeMap = new HashMap<Integer, MessageStatus>();
		nameMap = new HashMap<String, MessageStatus>();
		for (MessageStatus status : MessageStatus.values()) {
			codeNameMap.put(status.getCode(), status.getName());
			codeMap.put(status.getCode(), status);
			nameMap.put(status.getName(), status);
		}
	}
	
	public static Map<Integer, String> getCodeNameMap(){
		return codeNameMap;
	}

	public static MessageStatus getByCode(Integer code) {
		return codeMap.get(code);
	}
	
	public static MessageStatus getByName(String name) {
		return nameMap.get(name);
	}
}
