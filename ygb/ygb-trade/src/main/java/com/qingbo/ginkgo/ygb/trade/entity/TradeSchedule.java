package com.qingbo.ginkgo.ygb.trade.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class TradeSchedule extends BaseEntity {

	private static final long serialVersionUID = -6764425015090354274L;
	private String scheduleInfo= "";//调度信息描述
	private String scheduleCode= "";//调度指令代码
	private String scheduleStatus= "";//调度指令状态
	private String scheduleTime= "";//调度时间
	private String scheduleFormat= "";//调度时间格式
	private String scheduleTarget= "";//调度影响范围
	private String scheduleSource= "";//调度指令来源
	private String scheduleCreater= "";//调度指令发布人
	private String memo= "";//备注
	
	
	public String getScheduleInfo() {
		return scheduleInfo;
	}
	public void setScheduleInfo(String scheduleInfo) {
		this.scheduleInfo = scheduleInfo;
	}
	public String getScheduleCode() {
		return scheduleCode;
	}
	public void setScheduleCode(String scheduleCode) {
		this.scheduleCode = scheduleCode;
	}
	public String getScheduleStatus() {
		return scheduleStatus;
	}
	public void setScheduleStatus(String scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}
	public String getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	public String getScheduleFormat() {
		return scheduleFormat;
	}
	public void setScheduleFormat(String scheduleFormat) {
		this.scheduleFormat = scheduleFormat;
	}
	public String getScheduleTarget() {
		return scheduleTarget;
	}
	public void setScheduleTarget(String scheduleTarget) {
		this.scheduleTarget = scheduleTarget;
	}
	public String getScheduleSource() {
		return scheduleSource;
	}
	public void setScheduleSource(String scheduleSource) {
		this.scheduleSource = scheduleSource;
	}
	public String getScheduleCreater() {
		return scheduleCreater;
	}
	public void setScheduleCreater(String scheduleCreater) {
		this.scheduleCreater = scheduleCreater;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}

	
}
