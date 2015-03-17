package com.qingbo.ginkgo.ygb.cms.enums;

import java.util.HashMap;
import java.util.Map;

public enum ArticleStatus {
	DRAFT(0, "草稿"), READY(1, "编辑完成"), TOPUBLISH(2, "等待发布"), ONLINE(3, "已发布"), OFFLINE(4, "已下线");
	
	private Integer code;
	private String name;
	
	private ArticleStatus(Integer code, String name) {
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
	private static Map<Integer, ArticleStatus> codeMap;
	private static Map<String, ArticleStatus> nameMap;
	static {
		codeNameMap = new HashMap<Integer, String>();
		codeMap = new HashMap<Integer, ArticleStatus>();
		nameMap = new HashMap<String, ArticleStatus>();
		for (ArticleStatus status : ArticleStatus.values()) {
			codeNameMap.put(status.getCode(), status.getName());
			codeMap.put(status.getCode(), status);
			nameMap.put(status.getName(), status);
		}
	}
	
	public static Map<Integer, String> getCodeNameMap(){
		return codeNameMap;
	}

	public static ArticleStatus getByCode(Integer code) {
		return codeMap.get(code);
	}
	
	public static ArticleStatus getByName(String name) {
		return nameMap.get(name);
	}
}
