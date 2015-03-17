package com.qingbo.ginkgo.ygb.account.inner.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.account.entity.QddAccountLog;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.entity.SubQddAccount;
import com.qingbo.ginkgo.ygb.account.enums.SubAccountType;
import com.qingbo.ginkgo.ygb.account.inner.QddPaymentService;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QDDPaymentResponse;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddApi;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddConfig;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddUtil;
import com.qingbo.ginkgo.ygb.account.repository.QddAccountLogRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubAccountRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubQddAccountRepository;
import com.qingbo.ginkgo.ygb.base.entity.BankType;
import com.qingbo.ginkgo.ygb.base.entity.StateCity;
import com.qingbo.ginkgo.ygb.base.service.CodeListService;
import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants.Role;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.service.UserBankCardService;

@Service("qddPaymentService")
public class QddPaymentServiceImpl implements QddPaymentService {
	private ErrorMessage errors = new ErrorMessage("account-error.properties");
	
	@Autowired private SubAccountRepository subAccountRepository;
	@Autowired private SubQddAccountRepository subQddAccountRepository;
	@Autowired private QddAccountLogRepository qddAccountLogRepository;
	
	@Autowired private CustomerService customerService;
	@Autowired private UserBankCardService userBankCardService;
	@Autowired private CodeListService codeListService;
	@Autowired private QueuingService queuingService;

	@Override
	public Result<String> loanRecharge(Long qddAccountLogId) {
		QddAccountLog qddAccountLog = qddAccountLogRepository.findOne(qddAccountLogId);
		if(qddAccountLog.success()) return errors.newFailure("ACT0102", qddAccountLog.getTradeId());
		
		QddApi qddApi = QddApi.getByPlatformId(qddAccountLog.getPlatformId());
		QddConfig qddConfig = qddApi.getQddConfig();
		
		Map<String, String> params = new LinkedHashMap<>();
		params.put("RechargeMoneymoremore", qddAccountLog.getMoneyMoreMoreId());
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("OrderNo", qddAccountLog.getOrderNo());
		params.put("Amount", qddAccountLog.getAmount().toPlainString());
		if(qddAccountLog.getAuditPass()==4) {//企业网银充值
			params.put("RechargeType", "4");//""|1，空-网银充值，1-代扣充值，代扣充值暂不支持（乾多多开发中）
			params.put("FeeType", "1");
		}else {
			params.put("RechargeType", "");//""|1，空-网银充值，1-代扣充值，代扣充值暂不支持（乾多多开发中）
			params.put("FeeType", "");
		}
		params.put("CardNo", "");//代扣充值时填卡号（空则代扣默认银行卡），卡号需加密传输
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		String ReturnURL = qddConfig.ReturnURLPrefix() + "loanRecharge";
		params.put("ReturnURL", ReturnURL);
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanRecharge");
		
		String redirect = qddApi.redirect(QddUtil.loanRecharge, params);
		return Result.newSuccess(redirect);
	}

	@Override
	public Result<QDDPaymentResponse> loanTransfer(Long qddAccountId, boolean freeze) {
		QddAccountLog qddAccountLog = qddAccountLogRepository.findOne(qddAccountId);
		if(qddAccountLog.success()) return errors.newFailure("ACT0102", qddAccountLog.getOrderNo());
		
		QddApi qddApi = QddApi.getByPlatformId(qddAccountLog.getPlatformId());
		QddConfig qddConfig = qddApi.getQddConfig();
		
		Map<String, String> params = new LinkedHashMap<>();
		List<Map<String, String>> loans = new ArrayList<>();
		Map<String, String> loan = new LinkedHashMap<>();
		loan.put("LoanOutMoneymoremore", qddAccountLog.getMoneyMoreMoreId());
		loan.put("LoanInMoneymoremore", qddAccountLog.getOtherMoneyMoreMoreId());
		loan.put("OrderNo", qddAccountLog.getOrderNo());//订单号，很重要的一条线索
		loan.put("BatchNo", qddAccountLog.getOrderNo());//网贷平台标号，暂使用项目ID
		loan.put("ExchangeBatchNo", "");
		loan.put("AdvanceBatchNo", "");
		loan.put("Amount", qddAccountLog.getAmount().toPlainString());
		loan.put("FullAmount", qddAccountLog.getAmount().toPlainString());//满标金额，以标号第一笔转账成功的记录里的满标金额为准
		loan.put("TransferName", "");
		loan.put("Remark", freeze ? "投资冻结" : "转账");
		loan.put("SecondaryJsonList", "");
		loans.add(loan);
		
		params.put("LoanJsonList", JSON.toJSONString(loans));//计算签名时不转码，传递参数时才转码
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("TransferAction", "1");//转账类型，1-投标，2-还款，3-其他（手续费等）
		params.put("Action", "2");//操作类型，1-手动跳转，2-自动接口
		params.put("TransferType", "2");//转账方式，1-桥连，2-直连
		params.put("NeedAudit", freeze ? "" : "1");//空-需要审核，1-自动通过
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		String ReturnURL = qddConfig.ReturnURLPrefix() + "loanTransfer";
		params.put("ReturnURL", ReturnURL);
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanTransfer");
		
		QDDPaymentResponse post = qddApi.post(QddUtil.loanTransfer, params);
		return Result.newSuccess(post);
	}

	@Override
	public Result<QDDPaymentResponse> loanAudit(String loanNo, boolean auditPass) {
		QddAccountLog qddAccountLog = qddAccountLogRepository.findByLoanNo(loanNo);
		if(!qddAccountLog.success()) return Result.newFailure(-1, "投资状态不正确："+qddAccountLog.getMessage());
		
		QddApi qddApi = QddApi.getByPlatformId(qddAccountLog.getPlatformId());
		QddConfig qddConfig = qddApi.getQddConfig();
		
		Map<String, String> params = new LinkedHashMap<>();
		params.put("LoanNoList", loanNo);
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("AuditType", auditPass ? "1" : "2");//审核类型，1-通过，2-退回，3-二次分配同意，4-二次分配不同意
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		String ReturnURL = qddConfig.ReturnURLPrefix() + "loanAudit";
		params.put("ReturnURL", ReturnURL);
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanAudit");
		
		QDDPaymentResponse post = qddApi.post(QddUtil.loanAudit, params);
		return Result.newSuccess(post);
	}

	@Override
	public Result<String> loanWithdraw(Long qddAccountLogId) {
		QddAccountLog qddAccountLog = qddAccountLogRepository.findOne(qddAccountLogId);
		if(qddAccountLog.success()) return errors.newFailure("ACT0102", qddAccountLog.getTradeId());
		
		QddApi qddApi = QddApi.getByPlatformId(qddAccountLog.getPlatformId());
		QddConfig qddConfig = qddApi.getQddConfig();
		
		SubQddAccount subQddAccount = subQddAccountRepository.findByMoneyMoreMoreId(qddAccountLog.getMoneyMoreMoreId());
		Long subAccountId = subQddAccount.getId();
		String BankCode = qddAccountLog.getBankCode();
		String CardNo = qddAccountLog.getCardNo();
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		Result<List<UserBankCard>> bankCards = userBankCardService.getBankCardByUserId(subAccount.getAccountId());
		if(!bankCards.success()) return Result.newFailure(bankCards);
		UserBankCard userBankCard = null;
		for(UserBankCard bankCard : bankCards.getObject()) {
			if(bankCard.getBankCode().equals(BankCode) 
					&& bankCard.getBankCardNum().equals(CardNo)) {
				userBankCard = bankCard;
				break;
			}
		}
		if(userBankCard==null) return errors.newFailure("ACT0404", qddAccountLogId, subAccountId, qddAccountLog.getAmount(), BankCode, CardNo);
		
		String Province = userBankCard.getProvince();
		String City = userBankCard.getCity();
		if(StringUtils.isBlank(Province) || StringUtils.isBlank(City)) return errors.newFailure("ACT0405", qddAccountLogId, subAccountId, BankCode, CardNo, Province, City);
		
		//将BankCode+Province+City转换为qdd code
		Result<BankType> bankTypeResult = codeListService.bank(BankCode);
		if(!bankTypeResult.success()) return Result.newFailure(bankTypeResult);
		BankType bankType = bankTypeResult.getObject();
		BankCode = bankType.getQddCode();
		
		Result<StateCity> stateCityResult = codeListService.stateCity(City);
		if(!stateCityResult.success()) return Result.newFailure(stateCityResult);
		StateCity stateCity = stateCityResult.getObject();
		Province = stateCity.getQddParent();
		City = stateCity.getQddCode();
		
		Map<String, String> params = new LinkedHashMap<>();
		params.put("WithdrawMoneymoremore", qddAccountLog.getMoneyMoreMoreId());
		params.put("PlatformMoneymoremore", qddAccountLog.getPlatformId());
		params.put("OrderNo", qddAccountLog.getOrderNo());
		params.put("Amount", qddAccountLog.getAmount().toPlainString());
		String subAccountType = QddApi.subAccountTypeOf(qddAccountLog.getPlatformId());
//		if(GinkgoConfig.platform.equalsIgnoreCase("gh")) {
		if(SubAccountType.QDD_GH.getCode().equals(subAccountType)) {//根据子账户类型判断更准确
			params.put("FeePercent", "0");//光华全部由用户承担手续费
		}else {
			Result<User> userResult = customerService.getUserByUserId(subAccount.getAccountId());
			if(!userResult.success()) return Result.newFailure(userResult);
			User user = userResult.getObject();
			boolean isInvestor = Role.INVESTOR.getCode().equals(user.getRole());
			if(isInvestor) {
				params.put("FeePercent", "100");//倍赢全部由平台承担手续费
			}else {
				params.put("FeePercent", "0");//只有投资者提现，才由平台垫付手续费
			}
		}
		params.put("FeeMax", "");
		params.put("FeeRate", "");
		params.put("CardNo", CardNo);
		params.put("CardType", "0");
		params.put("BankCode", BankCode);
		params.put("BranchBankName", "");
		params.put("Province", Province);
		params.put("City", City);
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		String ReturnURL = GinkgoConfig.getProperty("front_url") + "/account/index.html";
		params.put("ReturnURL", ReturnURL);
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanWithdraw");
		
		qddApi.sign(params);
		qddApi.encrypt(params, "CardNo");
		
		String redirect = qddApi.redirect(QddUtil.loanWithdraw, params);
		return Result.newSuccess(redirect);
	}
}
