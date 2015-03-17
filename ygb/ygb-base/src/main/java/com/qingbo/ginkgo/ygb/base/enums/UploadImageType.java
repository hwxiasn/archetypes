package com.qingbo.ginkgo.ygb.base.enums;

/**
 * 上传图片关联对象类型
 * @author hongwei
 */
public enum UploadImageType {
	BASE("base", "基础资料"),
	PLEDGE("pledge", "抵押物资料"),
	INVESTIGATION("investigation", "调查报告"),
	ACCOUNTANT_AUDIT("accountant_audit", "会计事务所审核意见"),
	LAYWER_AUDIT("lawyer_audit", "律师事务所审核意见"),
	ASSETS_APPRAISAL("assets_appraisal", "资产评估"),
	GUARANTEE_LETTER("guarantee_letter", "担保函信息"),
	;
	
	private String code, name;
	private UploadImageType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public static UploadImageType getByCode(String code) {
		for(UploadImageType item : values()) {
			if(item.getCode().equals(code))
				return item;
		}
		return null;
	}
}