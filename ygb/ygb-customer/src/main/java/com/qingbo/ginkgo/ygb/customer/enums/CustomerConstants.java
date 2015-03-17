package com.qingbo.ginkgo.ygb.customer.enums;


public class CustomerConstants {

	public static String CUSTOMER_MARK="01";
	
	//平台默认经纪人  user_name
	public static String DEFAULT_BROKER_USERNAME = "platform_broker";
	
	//平台默认的营销机构
	public static String DEFAULT_AGENCY_USERNAME = "platform_agency"; 
	
	//默认的操作员名称
	public static String OPERATOR_REALNAME="操作员";
	
	
	//会员管理用户类型
	public static enum UserRegisterType {
		PERSONAL("P", "个人用户"),
		ENTERPRISE("E", "企业用户"),
		ADMIN("A", "管理员");
		
		private UserRegisterType(String code, String name) {
			this.code = code;
			this.name = name;
		}
		
		private String code;
		
		private String name;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	//注册用户来源
	public static enum RegisterSource{
		GUANGHUA("gh","光华"),
		BEIYING("by","倍赢"),
		QINGBO("wzg","吴掌柜");
		private String code;
		private String name;
		
		private RegisterSource(String code,String name) {
			this.code = code;
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	
	//证件类型
	public static enum IdType{
		
		IDENTITY_CARD("ID","身份证"),
		DRIVING_LICENCE("DR","驾驶证"),
		MILITARY_CARD("MI","军人证");
		
		
		private String code;
		private String name;
		
		private IdType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		
	}
	
	
	
	//用户状态
	public static enum Status{
		INACTIVE("I", "未激活"), 
		ACTIVE("A", "正常"), 
		LOCKED("L", "已锁定"),
		DISABLED("D", "已禁用");
		
		private String code;
		private String name;
		
		private Status(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public static Status getByCode(String code) {
			
			for(Status userStatus : values()) {
				if(userStatus.getCode().equals(code))
					return userStatus;
			}
			
			return null;
		}
		
	}
	
	//通用绑定（银行卡绑定  邮箱绑定  手机绑定）
	public static enum SomethingBinding{
		BINDED("1","已绑定"),
		UNBIND("0","未绑定");
		private SomethingBinding(String code, String name) {
			this.code = code;
			this.name = name;
		}
		private String code;
		private String name;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	//是否激活
	public static enum IsActivated{
		ISACTIVATED("1","已激活"),
		ISNOTACTIVATED("0","未激活");
		
		private IsActivated(String code,String name) 
		{
			this.code = code;
			this.name = name;
		}
		private String code;
		private String name;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}

	//实名认证
	public static enum RealNameAuthStatus{
		REALNAME("1","已实名认证"),
		UNREALNAME("0","未实名认证");
		
		private RealNameAuthStatus(String code,String name) {
			this.code = code;
			this.name = name;
		}
		private String code;
		private String name;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		
	}
	
	//用户角色
	public static enum Role{
		ADMIN("A", "管理员"),
		ADVISOR_ADMIN("AA", "高级管理员"),
		OPERATOR("O", "操作员"),
		INVESTOR("I", "投资者"),
		BORROWER("B", "投资接受人"),
		BROKER("BR", "经纪人"),
		AGENCY("AG", "代理机构"),
		SPONSOR("SP", "保荐人"),
		SPONORG("SE","保荐机构"),
		GUARANTEE("G", "担保机构"),
		LOAN("L", "小贷机构"),
		PLATFORM("P", "平台分润角色"),
		SUPERBROKER("S", "超级经纪人");
		
		private String code;
		
		private String name;
		
		private Role(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	//银行卡类型
	public static enum bangCardType{
		DEBIT_CARD("DE","借记卡"),
		CREDIT_CARD("CR","信用卡");
		
		private String code;
		private String name;
		
		private bangCardType(String code, String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static enum UserRelationship{
		AGENCY_BROKER("A-B", "营销公司与经纪人"),
		BROKER("B", "经纪人"),
		OPREATOR("O", "操作员"),
		ENTERPRISE_OPERATOR("E-O", "企业用户与操作员"),
		AGENCY_INVESTOR("A-I", "营销公司与投资人"),
		INVESTOR_INVESTOR("I-I", "投资人与投资人"),
		BROKER_INVESTOR("B-I", "经纪人与投资人"),
		AGENCY_SUPERBROKER("A-S","营销机构与超级经纪人"),
		SUPERBROKER_BROKER("S-B","超级经纪人与经纪人");
		private String code;
		
		private String name;
		
		private UserRelationship(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static enum Level{
		L1("1","一级"),
		L2("2","二级"),
		L3("3","三级");
		private String code;
		
		private String name;
		
		private Level(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static enum operatorLevel{
		LEVEL1("L1", "一级审核员"),
		LEVEL2("L2", "二级审核员"),
		LEVEL3("L3", "普通人员");
		
		private String code;
		private String name;
		private operatorLevel(String code, String name) {
			this.code = code;
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	
}
