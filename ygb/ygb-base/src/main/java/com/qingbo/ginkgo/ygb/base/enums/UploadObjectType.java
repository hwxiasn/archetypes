package com.qingbo.ginkgo.ygb.base.enums;

/**
 * 上传图片类型
 * @author hongwei
 */
public enum UploadObjectType {
	PROJECT("project", "项目相关图片"),
	LETTER("letter", "担保函相关图片"),
	;
	
	private String code, name;
	private UploadObjectType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public static UploadObjectType getByCode(String code) {
		for(UploadObjectType item : values()) {
			if(item.getCode().equals(code))
				return item;
		}
		return null;
	}
}