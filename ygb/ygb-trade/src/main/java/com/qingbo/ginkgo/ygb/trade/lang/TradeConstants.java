package com.qingbo.ginkgo.ygb.trade.lang;

import java.util.HashMap;
import java.util.Map;

public class TradeConstants {
	public static final String TRADE_QUEUING = "02";
	//交易回退
	public static final String TRADE_ROLLBACK = "ROLL";
	//交易回退
	public static final String TRADE_ROLLBACK_NAME = "ACCOUNT_ROLL";
	
	/**
	 * 交易系统是否处于可执行状态:true可执行，false不可执行
	 */
	public static boolean EXECUTE_FLAG = true;
	
	/**
	 * 待执行队列长度
	 */
	public static int EXECUTE_LIST_SIZE = 0;
	
	/**
	 * 交易对象之间数量关系
	 */
	public enum TradeRelation{
		ONE_ONE("OO", "一对一交易"),
		ONE_MANY("OM", "一对多交易"),
		MANY_ONE("MO", "多对一交易");

		private String code, name;

		private TradeRelation(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static TradeRelation getByCode(String code) {
			for (TradeRelation item : values()) {
				if (item.getCode().equalsIgnoreCase(code)) {
					return item;
				}
			}
			return null;
		}
	}
	
	/**
	 * 交易动作类型 
	 */
	public enum TradeActionType {
		NO_ACTION("NA", "无"),
		FREEZE("L", "冻结"),
		UNFREEZE("UL", "解冻"),
		TRANSFER("T", "转账");
		
		private String code, name;

		private TradeActionType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static TradeActionType getByCode(String code) {
			for (TradeActionType item : values()) {
				if (item.getCode().equalsIgnoreCase(code)) {
					return item;
				}
			}
			return null;
		}
	}
	
	/**
	 * 交易子类型
	 * 包含 父交易、子交易、单笔交易
	 */
	public enum TradeKind {
		PARENT("P", "父交易"),
		SINGLE("S", "单笔交易"),
		CHILDREN("C", "子交易");
		
		private String code, name;

		private TradeKind(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static TradeKind getByCode(String code) {
			for (TradeKind item : values()) {
				if (item.getCode().equalsIgnoreCase(code)) {
					return item;
				}
			}
			return null;
		}
	}
	
	/**
	 * 交易父类型
	 */
	public enum TradeType {
		INVEST("I", "投资"),
		OUT_INVEST("OI", "对外投资"),
		REPAY("R", "还款"),
		COMMISSION("C", "分佣"),
		SPLIT("S", "分润"),
		DEPOSIT("D", "存款"),
		WITHDRAW("W", "提现"),
		TRANSFER("T", "转账");
		
		private String code, name;

		private TradeType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static TradeType getByCode(String code) {
			for (TradeType item : values()) {
				if (item.getCode().equalsIgnoreCase(code)) {
					return item;
				}
			}
			return null;
		}
	}
	
	public enum TradeRestrictType {
		ALL("ALL", "全部成功"),
		ROLL("ROLL", "不成功回退"),
		ALWAYS("ALWAYS", "默认成功");
		private String code, name;

		private TradeRestrictType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static TradeRestrictType getByCode(String code) {
			for (TradeRestrictType item : values()) {
				if (item.getCode().equalsIgnoreCase(code)) {
					return item;
				}
			}
			return null;
		}
	}

	public enum TradePkgType {
		ROLL("R", "回滚"), 
		DN("DN", "网银充值"), 
		F("F", "募集"), 
		P("P", "还款"), 
		I("I", "投资成立"), 
		C("C", "分佣"), 
		IL("IL", "投资冻结"), 
		W("W", "提现");
		private String code, name;
		private TradePkgType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static TradePkgType getByCode(String code) {
			for (TradePkgType item : values()) {
				if (item.getCode().equalsIgnoreCase(code)) {
					return item;
				}
			}
			return null;
		}

	}
	
	public enum TradeCheckType {
		TRUE("T", "通过"), 
		FAILED("F", "不通过"); 
		private String code, name;
		private TradeCheckType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static TradeCheckType getByCode(String code) {
			for (TradeCheckType item : values()) {
				if (item.getCode().equalsIgnoreCase(code)) {
					return item;
				}
			}
			return null;
		}

	}
	
	/**
	 *	交易状态机变化：
	 *创建时为待成立
	 *若为单次交易，则处理后为已成立或者成立失败，
	 *若为批交易则为成立中，依据最终结果处理 
	 */
	public enum TradeStatusType {
		ADDING("A", "创建中"),
		PENDING("P", "待成立"),
		EXECUTED("E", "已成立"),
		FAILURE("F", "成立失败"),
		EXECUTING("I", "成立中");
		
		private static Map<String, String> codeNameMap;
		static {
			codeNameMap = new HashMap<String, String>();
			for (TradeStatusType status : TradeStatusType.values()) {
				codeNameMap.put(status.getCode(), status.getName());
			}
		}
		public static Map<String, String> getCodeNameMap(){
			return codeNameMap;
		}

		private String code, name;
		private TradeStatusType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static TradeStatusType getByCode(String code) {
			for (TradeStatusType item : values()) {
				if (item.getCode().equalsIgnoreCase(code)) {
					return item;
				}
			}
			return null;
		}

	}
	public enum TradeEngineType {
		AUTO("A", "自动"),
		MAN("M", "手动"),
		SCHEDULE("S", "定时"); 
		private String code, name;
		private TradeEngineType(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static TradeEngineType getByCode(String code) {
			for (TradeEngineType item : values()) {
				if (item.getCode().equalsIgnoreCase(code)) {
					return item;
				}
			}
			return null;
		}

	}
}
