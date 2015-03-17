package com.qingbo.ginkgo.ygb.project.enums;

import java.util.HashMap;
import java.util.Map;

public class ProjectConstants {
	//平台中间账户ID 
	public static final String PLATFORM_TEMP_ACCOUNT = "141229211141010005";
	//平台账户ID
	public static final String PLATFORM_ACCOUNT = "141229211141010004";

	public static final String TradeSource = "01";
	public static String PROJECT_QUEUING = "05";
	//募集分佣阶段
	public static final String PhaseForFundraise = "F";
	//还款分润阶段
	public static final String PhaseForRepay = "P";

	public enum GuaranteeStatus {
		COMMITING("PR","承诺担保中"),
		FAIL("FL","失败"),
		CHECKING("CH","担保公司审核中"),
		SIGN("PS","担保函签发中"),
		SIGNCHECK("PA","担保函签发审核中"),
		SIGNSUCCESS("PB","担保函签发成功"),
		SIGNFAIL("PC","担保函签发失败"),
		PERFORM("P", "担保函履约中"),
		PERFORMED("PD", "担保函履约完毕"),
		RENEGE("F","担保函违约"),
		COMPENSATION("FC","代偿还款"),
		JUSTICEING("FJ","司法处置中"),
		JUSREPAY("FR","司法处置还款"),
		LEVELONEING("L", "一级审核完成"),
		BRACK("B", "合约违约");

		private static Map<String, String> codeNameMap;
		static {
			codeNameMap = new HashMap<String, String>();
			for (GuaranteeStatus status : GuaranteeStatus.values()) {
				codeNameMap.put(status.getCode(), status.getName());
			}
		}
		public static Map<String, String> getCodeNameMap(){
			return codeNameMap;
		}
		
		private String code, name;
		private GuaranteeStatus(String code, String name) {
			this.code = code;
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
		public static GuaranteeStatus getByCode(String code) {
			for(GuaranteeStatus item : values()) {
				if(item.getCode().equals(code))
					return item;
			}
			return null;
		}
	}

	public enum ProjectStatus {
		DRAFT("DR", "草稿"),
		PENDING_FOR_APPROVE("R", "待审核"),
		APPROVED("A", "已审核"),
		REJECT_PROJECT("RP", "已驳回"),
		PENDING_FOR_PUBLISH("PP", "待发布"),
		PUBLISHED("P", "已发布"),
		PUBLISH_FAILURE("PF", "发布失败"),
		FUNDRAISING("F", "募集中"),
		FUNDRAISE_FALURE("FF", "募集失败"),
		FUNDRAISE_COMPLETE("FC", "募集完成"),
		REVIEWED_BY_GUARANTEE_COMPANY("GR", "担保公司审核中"),
		PROJECT_DEAL("PD", "项目成立待审核"),
		PROJECT_DEAL_D("PB", "项目成立"),
		PROJECT_REPAYING("PR", "项目还款中"),
		REPAYED("RC", "项目已还款"),
		COMPENSATION_REPAYING("CR", "项目分润中"),
		COMPENSATION_REPAYED("CRC", "项目分润完成"),
		BEING_JUDICIAL_PROCEDURES("J", "司法受理");

		private static Map<String, String> codeNameMap;
		static {
			codeNameMap = new HashMap<String, String>();
			for (ProjectStatus status : ProjectStatus.values()) {
				codeNameMap.put(status.getCode(), status.getName());
			}
		}
		public static Map<String, String> getCodeNameMap(){
			return codeNameMap;
		}
		
		private String code, name;
		private ProjectStatus(String code, String name) {
			this.code = code;
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
		public static ProjectStatus getByCode(String code) {
			for(ProjectStatus item : values()) {
				if(item.getCode().equals(code))
					return item;
			}
			return null;
		}
	}
	
	
	public enum InvestmentStatus {
		ADDING("A", "创建中"),
		PENDING("P", "意向投资"),
		EXECUTED("E", "投资成立"),
		FAILURE("F", "投资失败"),
		EXCHANGE("X", "投资兑付"),
		EXECUTING("I", "投资中");
		
		private static Map<String, String> codeNameMap;
		static {
			codeNameMap = new HashMap<String, String>();
			for (InvestmentStatus status : InvestmentStatus.values()) {
				codeNameMap.put(status.getCode(), status.getName());
			}
		}
		public static Map<String, String> getCodeNameMap(){
			return codeNameMap;
		}

		private String code, name;
		private InvestmentStatus(String code, String name) {
			this.code = code;
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
		public static InvestmentStatus getByCode(String code) {
			for(InvestmentStatus item : values()) {
				if(item.getCode().equals(code))
					return item;
			}
			return null;
		}
	}
	
	public enum RepaymentStatus {
		PENDING("P", "待还款"),
		EXECUTED("E", "已还款"),
		FAILURE("F", "还款逾期");
		
		private static Map<String, String> codeNameMap;
		static {
			codeNameMap = new HashMap<String, String>();
			for (RepaymentStatus status : RepaymentStatus.values()) {
				codeNameMap.put(status.getCode(), status.getName());
			}
		}
		public static Map<String, String> getCodeNameMap(){
			return codeNameMap;
		}

		private String code, name;
		private RepaymentStatus(String code, String name) {
			this.code = code;
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
		public static RepaymentStatus getByCode(String code) {
			for(RepaymentStatus item : values()) {
				if(item.getCode().equals(code))
					return item;
			}
			return null;
		}
	}
	
}
