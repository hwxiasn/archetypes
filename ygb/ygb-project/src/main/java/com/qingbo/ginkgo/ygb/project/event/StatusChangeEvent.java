package com.qingbo.ginkgo.ygb.project.event;

import org.springframework.context.ApplicationEvent;


public class StatusChangeEvent extends ApplicationEvent {
	//担保函合同制作
	public static final String GUARANTEE = "GUARANTEE";
	//分佣列表
	public static final String FundraiseFeeList = "FundraiseFeeList";
	//分润列表
	public static final String RepayFeeList = "RepayFeeList";
	//项目已还款后的自动分润
	public static final String RepayedId = "RepayedId";

	private static final long serialVersionUID = 492113822342420415L;
	private String type;
	
	public StatusChangeEvent(Object source,String type) {
		super(source);
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
