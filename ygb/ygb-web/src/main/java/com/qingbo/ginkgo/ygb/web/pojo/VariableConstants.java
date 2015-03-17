package com.qingbo.ginkgo.ygb.web.pojo;

/**
 * 公共变量表,采用大写
 * 结构：对象类型_对象名称
 * ENTITY  表明返回的为一个ENTITY对象
 * PAGER   表明返回的为一个PAGER对象
 * LIST	         表明返回的为一个LIST对象
 * MAP     表明返回的为一个MAP对象
 *
 */
public class VariableConstants {
	/**
	 * 交互结果状态相关
	 */
	public static final String 	Status_Error				 = "Status_Error";
	public static final String 	Status_Message				 = "Status_Message";
	public static final String 	Result				 		 = "Result";
	/**
	 * 查询条件
	 */
	public static final String 	Entity_Search				 = "Entity_Search";
	/**
	 * 项目相关的返回对象
	 */
	public static final String 	Entity_ProjectDetail         = "Entity_ProjectDetail";             
	public static final String 	Pager_ProjectDetail          = "Pager_ProjectDetail";              
	public static final String 	Pager_Project              	 = "Pager_Project";              
	public static final String 	List_Project               	 = "List_Project";               
	public static final String 	Map_Project                	 = "Map_Project";
	public static final String 	Entity_Project				 = "Entity_Project";             
	/**
	 * 担保函相关的返回对象
	 */
	public static final String 	Entity_Guarantee           	 = "Entity_Guarantee";           
	public static final String 	Pager_Guarantee            	 = "Pager_Guarantee";            
	public static final String 	List_Guarantee             	 = "List_Guarantee";             
	public static final String 	Map_Guarantee              	 = "Map_Guarantee";
	
	public static final String 	Entity_LoaneeInfo				 = "Entity_LoaneeInfo";
	/**
	 * 担保机构用户信息
	 */
	public static final String  Entity_User_Guarantee		="Entity_User_Guarantee";

	/**
	 * 状态信息
	 */
	public static final String	Map_CodeList_GuaranteeStatus ="Map_CodeList_GuaranteeStatus";
	public static final String	List_CodeList_GuaranteeStatus ="List_CodeList_GuaranteeStatus";
	/**
	 * 分佣模板相关的返回对象
	 */
	public static final String 	Entity_CommissionTemplate  	 = "Entity_CommissionTemplate";  
	public static final String 	Pager_CommissionTemplate   	 = "Pager_CommissionTemplate";   
	public static final String 	List_CommissionTemplate    	 = "List_CommissionTemplate";    
	public static final String 	Map_CommissionTemplate     	 = "Map_CommissionTemplate";

	/**
	 * 分佣模板相关的CODELIST对象
	 */
	public static final String 	Map_CodeList_CommissionTemplateStatus = "Map_CodeList_CommissionTemplateStatus";
	public static final String 	Map_CodeList_CommissionTemplatePhase = "Map_CodeList_CommissionTemplatePhase";
	public static final String 	Map_CodeList_CommissionTemplateRole = "Map_CodeList_CommissionTemplateRole";
	public static final String 	Map_CodeList_CommissionTemplateType = "Map_CodeList_CommissionTemplateType";
	
	/**
	 * 借款期限类型及借款期限 
	 * 数据结构  Key=期限类型实体  Values=该类型可选参数列表
	 * Key: CodeList Values:List<CodeList>
	 */
	public static final String 	Map_CodeList_LoanType = "Map_CodeList_LoanType";
	/**
	 * 借款期限类型及借款期限 
	 * 数据结构  Key=期限类型Code  Values=该类型可选参数Map 该Map数据结构：Key=可选参数实体Code Value=可选参数实体
	 * Key: CodeList.getCode() Values:Map<String,CodeList> == Map<CodeList.getCode(),CodeList>
	 */
	public static final String 	Map_CodeList_LoanType_Map = "Map_CodeList_LoanType_Map";
	//最低条件
	public static final String 	List_CodeList_SettleType = "List_CodeList_SettleType";
	//最低投资额
	public static final String 	List_CodeList_MinInvestAmount = "List_CodeList_MinInvestAmount";
	//募集阶段分佣模板
	public static final String 	List_CommissionTemplate_Fundrasing = "List_CommissionTemplate_Fundrasing";
	public static final String 	Map_CommissionTemplate_Fundrasing = "Map_CommissionTemplate_Fundrasing";
	public static final String 	Entity_CommissionTemplate_Funding  	 = "Entity_CommissionTemplate_Funding";  
	//还款阶段分润模板
	public static final String 	List_CommissionTemplate_Repay = "List_CommissionTemplate_Repay";
	public static final String 	Map_CommissionTemplate_Repay = "Map_CommissionTemplate_Repay";
	public static final String 	Entity_CommissionTemplate_Repay  	 = "Entity_CommissionTemplate_Repay";  
	//担保机构列表
	public static final String 	List_User_Guarantee = "List_User_Guarantee";
	//担保机构字典
	public static final String 	Map_User_Guarantee = "Map_User_Guarantee";
	//保荐机构列表
	public static final String 	List_User_Sponsor = "List_User_Sponsor";
	//企业用户角色
	public static final String Map_Agency_role="Map_Agency_role";
	//个人用户角色
	public static final String Map_user_role="Map_user_role";
	//用户状态
	public static final String Map_user_status="Map_user_status";
	
	
	/**项目状态*/ public static final String MAP_PROJECT_STATUS		= "Map_Project_Status";
	/**担保状态*/ public static final String MAP_GUARANTEE_STATUS	= "Map_Guarantee_Status";
	/**投资状态*/ public static final String MAP_INVESTMENT_STATUS	= "Map_Investment_Status";
	/**还款状态*/ public static final String MAP_REPAYMENT_STATUS	= "Map_Repayment_Status";
	/**交易状态*/ public static final String MAP_TRADE_STATUS		= "Map_Trade_Status";
	
	/**
	 * 投资凭证相关的返回对象
	 */
	public static final String 	List_Investment            	 = "List_Investment";            
	public static final String 	Map_Investment             	 = "Map_Investment";             
	public static final String 	Entity_Investment          	 = "Entity_Investment";          
	public static final String 	Pager_Investment           	 = "Pager_Investment";  
	public static final String 	Map_CodeList_InvestmentStatus = "Map_CodeList_InvestmentStatus";

	/**
	 * 还款凭证相关的返回对象
	 */
	public static final String 	List_Repayment            	 = "List_Repayment";            
	public static final String 	Map_Repayment             	 = "Map_Repayment";             
	public static final String 	Entity_Repayment          	 = "Entity_Repayment";          
	public static final String 	Pager_Repayment           	 = "Pager_Repayment";           
	public static final String 	Map_CodeList_RepaymentStatus = "Map_CodeList_RepaymentStatus";
	
	/**
	 * 用户实体信息
	 */
	public static final String  Entity_User					="Entity_User";
	public static final String 	List_User            	 	= "List_User";            
	public static final String 	Map_User             	 	= "Map_User";             
	public static final String 	Pager_User           	 	= "Pager_User";           
	public static final String  Entity_UserGroup			="Entity_UserGroup";
	/**
	 * 交易实体
	 */
	public static final String 	Entity_Trade           	 	= "Entity_Trade";           
	public static final String 	List_Trade            	 	= "List_Trade";            
	public static final String 	Map_Trade             		= "Map_Trade";             
	public static final String 	Pager_Trade           	 	= "Pager_Trade";           
	/**
	 * 审核记录
	 */
	public static final String 	List_ProjectReview            	 	= "List_ProjectReview";
	
	
	/**
	 * 分佣模板相关的CODE_LIST类型定义
	 */
	public static final String CODElIST_COMMISSION_STATUS 				= "COMMISSION_STATUS";
	public static final String CODELIST_COMMISSION_PHASE 				= "COMMISSION_PHASE";
	public static final String CODELIST_COMMISSION_ROLE 				= "COMMISSION_ROLE";
	public static final String CODELIST_COMMISSION_TYPE 				= "COMMISSION_TYPE";
	
	

	/**
	 * 借款期限类型
	 * @author 
	 *
	 */
	public static class InvestmentPeriodType{
		/**
		 * 定制月份
		 */
		public static String MONTHS = "MONTHS";
		/**
		 * 定制天数
		 */
		public static String DAYS = "DAYS";
		/**
		 * 固定期限
		 */
		public static String FIX_PERIOD = "FIX_PERIOD";
		
	}
	
	public static enum GuaranteeLetterReviewLevel{
		LEVEL1("L1", "一级审核员"),
		LEVEL2("L2", "二级审核员"),
		LEVEL3("L3", "普通人员");
		private GuaranteeLetterReviewLevel(String code, String name) {
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
		public static GuaranteeLetterReviewLevel getByCode(String code) {
			for (GuaranteeLetterReviewLevel item : values()) {
				if (item.getCode().equalsIgnoreCase(code)) {
					return item;
				}
			}
			return null;
		}
		
	}
	
	
}
