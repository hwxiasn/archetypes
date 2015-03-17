package com.qingbo.ginkgo.ygb.project.enums;

import java.util.HashMap;
import java.util.Map;

public enum BorrowerInfoStatus {
	NEW(1, "新提交"), IGNORED(2, "已忽略"), DONE(3, "已借款");
	
	private Integer code;
	private String name;
	
	private BorrowerInfoStatus(Integer code, String name) {
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
	private static Map<Integer, BorrowerInfoStatus> codeMap;
	private static Map<String, BorrowerInfoStatus> nameMap;
	static {
		codeNameMap = new HashMap<Integer, String>();
		codeMap = new HashMap<Integer, BorrowerInfoStatus>();
		nameMap = new HashMap<String, BorrowerInfoStatus>();
		for (BorrowerInfoStatus status : BorrowerInfoStatus.values()) {
			codeNameMap.put(status.getCode(), status.getName());
			codeMap.put(status.getCode(), status);
			nameMap.put(status.getName(), status);
		}
	}
	
	public static Map<Integer, String> getCodeNameMap(){
		return codeNameMap;
	}

	public static BorrowerInfoStatus getByCode(Integer code) {
		return codeMap.get(code);
	}
	
	public static BorrowerInfoStatus getByName(String name) {
		return nameMap.get(name);
	}
}
