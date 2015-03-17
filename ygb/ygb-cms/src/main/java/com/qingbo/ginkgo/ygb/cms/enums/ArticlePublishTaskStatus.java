package com.qingbo.ginkgo.ygb.cms.enums;

import java.util.HashMap;
import java.util.Map;

public enum ArticlePublishTaskStatus {
	CREATED(1, "创建未执行"), EXECUTED(2, "已执行");
	
	private Integer code;
	private String name;
	
	private ArticlePublishTaskStatus(Integer code, String name) {
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
	private static Map<Integer, ArticlePublishTaskStatus> codeMap;
	private static Map<String, ArticlePublishTaskStatus> nameMap;
	static {
		codeNameMap = new HashMap<Integer, String>();
		codeMap = new HashMap<Integer, ArticlePublishTaskStatus>();
		nameMap = new HashMap<String, ArticlePublishTaskStatus>();
		for (ArticlePublishTaskStatus status : ArticlePublishTaskStatus.values()) {
			codeNameMap.put(status.getCode(), status.getName());
			codeMap.put(status.getCode(), status);
			nameMap.put(status.getName(), status);
		}
	}
	
	public static Map<Integer, String> getCodeNameMap(){
		return codeNameMap;
	}

	public static ArticlePublishTaskStatus getByCode(Integer code) {
		return codeMap.get(code);
	}
	
	public static ArticlePublishTaskStatus getByName(String name) {
		return nameMap.get(name);
	}
}
