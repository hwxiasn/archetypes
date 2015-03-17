package com.qingbo.ginkgo.ygb.account.payment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QDDPaymentUtil;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddApi;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddConfig;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddUtil;

public class QDDApiTester {
	private QddApi qddApi = QddApi.get("QDD_BY");
	private QddConfig qddConfig = qddApi.getQddConfig();
	
	/**
	 * 开户（半自动跳转+全自动接口）
	 */
	@Test
	public void loanRegister() {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("RegisterType", "2");//1-全自动注册，2-半自动注册
		params.put("AccountType", "");//空-个人账户，1-企业账户
		params.put("Mobile", "13983801118");//换手机号
		params.put("Email", "candisjia@163.com");//邮箱
		params.put("RealName", "贾雪鸿");//真实姓名或企业名称
		params.put("IdentificationNo", "50010519900828122X");//身份证号或营业执照号
		params.put("Image1", "");
		params.put("Image2", "");
		params.put("LoanPlatformAccount", "192");//平台用户ID
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		params.put("ReturnURL", qddConfig.ReturnURLPrefix() + "loanRegister");//有双引号时需要UrlEncode
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanRegister");//redirect时会自动encode全部参数
		
		String SubmitURL = qddConfig.SubmitURLPrefix() + "loan/toloanregisterbind.action";
		qddApi.redirect(SubmitURL, params);
	}
	
	/**
	 * 授权（跳转，用户输入支付密码）
	 */
	@Test
	public void loanAuthorise() {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("MoneymoremoreId", "m13433");
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("AuthorizeTypeOpen", "1,2,3");//1.投标2.还款3.二次分配审核
		params.put("AuthorizeTypeClose", "");//默认全部关闭，期望全部打开
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		params.put("ReturnURL", qddConfig.ReturnURLPrefix() + "loanAuthorise");
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanAuthorise");
		qddApi.redirect(qddConfig.SubmitURLPrefix() + "loan/toloanauthorize.action", params);
	}

	/**
	 * 查询余额（接口）
	 */
	@Test
	public void loanBalanceQuery() {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("PlatformId", "p106");//以m或p开头的乾多多账户标识
		params.put("PlatformType", "2");//1-托管，2-自有/资金
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		qddApi.post(qddConfig.SubmitURLPrefix() + "loan/balancequery.action", params);
	}
	
	/**
	 * 充值（跳转）
	 */
	@Test
	public void loanRecharge() {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("RechargeMoneymoremore", "p251");
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("OrderNo", QDDPaymentUtil.orderNo16());
		params.put("Amount", "700000000");
		params.put("RechargeType", "4");//""|1，代扣充值暂不支持（乾多多开发中）
		params.put("FeeType", "1");
		params.put("CardNo", "");
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		params.put("ReturnURL", qddConfig.ReturnURLPrefix() + "loanRecharge");
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanRecharge");
		qddApi.redirect(QddUtil.loanRecharge, params);
		//{"Amount":"100000.00","CardNoList":"","Fee":"","LoanNo":"6347956677548472","Message":"成功","OrderNo":"2014090118180001","PlatformMoneymoremore":"p251","RandomTimeStamp":"","RechargeMoneymoremore":"m5581","RechargeType":"","Remark1":"","Remark2":"","Remark3":"","ResultCode":"88","SignInfo":"rushdmK/PzLMWUiLafd2MTKi5u75iXmbJPJw1r72u1zqKBxBzz4Z/c4NCW6tLFCw6ZwJhLgz3eQw\r\nThom/utxzs5mcTi75+uKu7Ze/jcBJSksPYpOPfzhP+SHe/DRFoe5m2kft5K1bbMemSQqcEmC7Why\r\nOCZ5KBhyLuQVqnzlQNE="}
	}
	
	/**
	 * 转账
	 */
	@Test
	public void loanTransfer() {
		Map<String, String> params = new LinkedHashMap<>();
		
		List<Map<String, String>> loans = new ArrayList<>();
		Map<String, String> loan = new LinkedHashMap<>();
		loan.put("LoanOutMoneymoremore", "m11899");
		loan.put("LoanInMoneymoremore", "m11189");
		loan.put("OrderNo", QDDPaymentUtil.orderNo16());
		loan.put("BatchNo", "9871465153");//网贷平台标号
		loan.put("ExchangeBatchNo", "");
		loan.put("AdvanceBatchNo", "");
		loan.put("Amount", "10");
		loan.put("FullAmount", "2000");//满标金额，以标号第一笔转账成功的记录里的满标金额为准
		loan.put("TransferName", "继续投标");
		loan.put("Remark", "投标备注");
		List<Map<String, String>> secondarys = new ArrayList<>();
		Map<String, String> secondary = new LinkedHashMap<>();
		secondary.put("LoanInMoneymoremore", "m5508");
		secondary.put("Amount", "2");
		secondary.put("TransferName", "二次分配");
		secondary.put("Remark", "二次分配备注");
		secondarys.add(secondary);
		loan.put("SecondaryJsonList", JSON.toJSONString(secondarys));
		loans.add(loan);
		
		params.put("LoanJsonList", JSON.toJSONString(loans));//计算签名时不转码，传递参数时才转码
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("TransferAction", "1");//转账类型，1-投标，2-还款，3-其他（手续费等）
		params.put("Action", "2");//操作类型，1-手动跳转，2-自动接口
		params.put("TransferType", "2");//转账方式，1-桥连，2-直连
		params.put("NeedAudit", "1");//空-需要审核，1-自动通过
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		params.put("ReturnURL", qddConfig.ReturnURLPrefix() + "loanTransfer");
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanTransfer");
		
		String SubmitURL = qddConfig.SubmitURLPrefix() + "loan/loan.action";
		if("1".equals(params.get("Action"))) {
			qddApi.redirect(SubmitURL, secondary);
		}else if("2".equals(params.get("Action"))) {
			qddApi.encode(params, "LoanJsonList");
			qddApi.post(SubmitURL, params);
			//{"Action":"","LoanJsonList":"%5B%7B%22LoanOutMoneymoremore%22%3A%22m5508%22%2C%22LoanInMoneymoremore%22%3A%22m5509%22%2C%22LoanNo%22%3A%22LN19368092014090211572254639%22%2C%22OrderNo%22%3A%222014090211560001%22%2C%22BatchNo%22%3A%229871465153%22%2C%22Amount%22%3A%22100.00%22%2C%22TransferName%22%3A%22%E7%BB%A7%E7%BB%AD%E6%8A%95%E6%A0%87%22%2C%22Remark%22%3A%22%E6%8A%95%E6%A0%87%E5%A4%87%E6%B3%A8%22%2C%22SecondaryJsonList%22%3A%22%5B%7B%5C%22LoanInMoneymoremore%5C%22%3A%5C%22m5581%5C%22%2C%5C%22Amount%5C%22%3A%5C%222%5C%22%2C%5C%22TransferName%5C%22%3A%5C%22%E4%BA%8C%E6%AC%A1%E5%88%86%E9%85%8D%5C%22%2C%5C%22Remark%5C%22%3A%5C%22%E4%BA%8C%E6%AC%A1%E5%88%86%E9%85%8D%E5%A4%87%E6%B3%A8%5C%22%7D%5D%22%7D%5D","Message":"成功","PlatformMoneymoremore":"p251","RandomTimeStamp":"","Remark1":"","Remark2":"","Remark3":"","ResultCode":"88","SignInfo":"jRwaRVk4YaFJ35hYedMkHpJ1z8zRPiVfUsGenszr1Cn5OFZi9egVvtm1DqlR1DGQ7u4w6hPRswqb\r\nHWKjl+UdMVHs2+W2EdVE1Nqhu5+eYwhI3lDyqfrAAMPl8QwmgQOWsiXNGthLr2y/snsZSYa2HCjA\r\no6fFUwBwzJiGBevB3cQ="}
		}
	}
	
	/**
	 * 转账审核（跳转）
	 */
	@Test
	public void loanAudit() {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("LoanNoList", "LN19368092014090214235835957");
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("AuditType", "1");//审核类型，1-通过，2-退回，3-二次分配同意，4-二次分配不同意
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		params.put("ReturnURL", qddConfig.ReturnURLPrefix() + "loanAudit");
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanAudit");
		qddApi.post(qddConfig.SubmitURLPrefix() + "loan/toloantransferaudit.action", params);
		//{"AuditType":"1","LoanNoList":"LN19368092014090211572254639","LoanNoListFail":"","Message":"成功","PlatformMoneymoremore":"p251","RandomTimeStamp":"","Remark1":"","Remark2":"","Remark3":"","ResultCode":"88","SignInfo":"Y772Y3u6psStGjFhc4jaZTtlxdAbiTRcBd72SR4F2GXM3PD/GCDgWe+X/zL72RkRlcKry0P3q1B7\r\nQ5Ws/lfEwIxadlxWzOWpjjmF3wbLOt+jtwww19KVng0smMWevSvTd7m7X2UX+fH0q2mr+kZPIh60\r\nu51dgyt/ZkQnrreDdLA="}
	}
	
	/**
	 * 提现
	 */
	@Test
	public void loanWithdraw() {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("WithdrawMoneymoremore", "m5508");
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("OrderNo", QDDPaymentUtil.orderNo16());
		params.put("Amount", "100");
		params.put("FeePercent", "100");
		params.put("FeeMax", "");
		params.put("CardNo", "6222021102028120627");
		params.put("CardType", "0");
		params.put("BankCode", "2");
		params.put("BranchBankName", "");
		params.put("Province", "10");
		params.put("City", "1078");
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		params.put("ReturnURL", qddConfig.ReturnURLPrefix() + "loanWithdraw");
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanWithdraw");
		
		qddApi.sign(params);
		qddApi.encrypt(params, "CardNo");
		
		qddApi.redirect(qddConfig.SubmitURLPrefix() + "loan/toloanwithdraws.action", params);
		//{"Amount":"100.00","Fee":"0.00","FeeMax":"","FeePercent":"0.00","FeeRate":"","FeeSplitting":"","FeeWithdraws":"1.00","FreeLimit":"0.00","LoanNo":"T20140902001409647925515","Message":"成功","OrderNo":"2014090216500001","PlatformMoneymoremore":"p251","RandomTimeStamp":"","Remark1":"","Remark2":"","Remark3":"","ResultCode":"88","SignInfo":"MdyP7YQwYvCFP7Tz10+n/ZZh9Jysvqf8lOteGQF54QH025BlPzupkYDefraF6lALa//XETE1uay8\r\nW2MNrBO/W98PKfHHqNPvTV9CbdqIwQ1uresFblyhPp9ENcdmtGvNf1ZG7TdeoEe9FrOpOp/NDB8M\r\nxNoiFsVDONBFRpfNnrQ=","WithdrawMoneymoremore":"m5509"}
	}
}
