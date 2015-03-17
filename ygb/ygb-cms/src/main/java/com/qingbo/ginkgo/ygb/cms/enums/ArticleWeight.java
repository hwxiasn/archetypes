package com.qingbo.ginkgo.ygb.cms.enums;

public enum ArticleWeight {
	BOTTOM	(0,  "底级"),
	LOW1	(1,  "下一"), 
	LOW2	(2,  "下二"), 
	LOW3	(3,  "下三"), 
	LOW4	(4,  "下四"), 
	ODINARY	(5,  "普通"), 
	HIGH1	(6,  "上一"), 
	HIGH2	(7,  "上二"), 
	HIGH3	(8,  "上三"), 
	HIGH4	(9,  "上四"), 
	TOP		(10, "顶级");
	
	private Integer code;
	private String name;
	
	private ArticleWeight(Integer code, String name) {
		this.code = code;
		this.name = name;
	}
	public Integer getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public static ArticleWeight getByCode(Integer code) {
		for(ArticleWeight articleWeight : values()) {
			if(articleWeight.getCode().equals(code))
				return articleWeight;
		}
		return null;
	}
}
