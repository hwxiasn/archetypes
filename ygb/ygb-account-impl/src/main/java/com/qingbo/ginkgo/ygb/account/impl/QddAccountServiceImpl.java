package com.qingbo.ginkgo.ygb.account.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.entity.SubQddAccount;
import com.qingbo.ginkgo.ygb.account.enums.SubAccountType;
import com.qingbo.ginkgo.ygb.account.payment.PaymentUtil;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QDDPaymentResponse;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddApi;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddConfig;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddUtil;
import com.qingbo.ginkgo.ygb.account.repository.AccountRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubAccountRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubQddAccountRepository;
import com.qingbo.ginkgo.ygb.account.service.QddAccountService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants.UserRegisterType;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;

@Service("qddAccountService")
public class QddAccountServiceImpl implements QddAccountService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private ErrorMessage errors = new ErrorMessage("account-error.properties");
	private Map<String, String> configs = new HashMap<>();
	
	@Autowired private AccountRepository accountRepository;
	@Autowired private SubAccountRepository subAccountRepository;
	@Autowired private SubQddAccountRepository subQddAccountRepository;
	
	@Autowired private CustomerService customerService;

	@Override
	public Result<String> register(Long subAccountId) {
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0301", subAccountId);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", subAccountId);
		//默认子账户也可以开户乾多多账户，回调中更改子账户类型
//		if(!subAccount.isQddAccount()) return errors.newFailure("ACT0303", subAccountId, subAccount.getType());
		
		Result<User> userResult = customerService.getUserByUserId(subAccount.getAccountId());
		if(!userResult.success()) return errors.newFailure("ACT0304", subAccountId, subAccount.getAccountId());
		User user = userResult.getObject();
		
		String type = subAccount.getType();
		if(!subAccount.isQddAccount()) {//根据用户来源判断子账户类型
//			if(RegisterSource.BEIYING.getCode().equals(user.getRegSource())) {
//				type = SubAccountType.QDD_BY.getCode();
//			}else if(RegisterSource.GUANGHUA.getCode().equals(user.getRegSource())) {
//				type = SubAccountType.QDD_GH.getCode();
//			}
			if("by".equals(GinkgoConfig.platform)) {
				type = SubAccountType.QDD_BY.getCode();
			}else if("gh".equals(GinkgoConfig.platform)) {
				type = SubAccountType.QDD_GH.getCode();
			}
		}
		QddApi qddApi = QddApi.get(type);
		if(qddApi==null) return errors.newFailure("ACT0303", subAccountId, type);
		QddConfig qddConfig = qddApi.getQddConfig();
		
		Map<String, String> params = new LinkedHashMap<>();
		params.put("RegisterType", "2");//1-全自动注册，2-半自动注册
		if(UserRegisterType.PERSONAL.getCode().equals(user.getRegisterType())) {
			params.put("AccountType", "");//空-个人，1-企业
		}else if(UserRegisterType.ENTERPRISE.getCode().equals(user.getRegisterType())) {
			params.put("AccountType", "1");//空-个人，1-企业
		}else {
			logger.info("bad registerType: " + user.getRegisterType());
			return errors.newFailure("ACT0305", subAccountId, subAccount.getAccountId(), user.getRegisterType());
		}
		if(UserRegisterType.PERSONAL.getCode().equals(user.getRegisterType())) {
			params.put("Mobile", user.getUserProfile().getMobile());
			params.put("Email", user.getUserProfile().getEmail());
			params.put("RealName", user.getUserProfile().getRealName());
			String idNumber = user.getUserProfile().getIdNum();
			if(idNumber.length()!=18) return errors.newFailure("ACT0306", subAccountId, subAccount.getAccountId(), idNumber);
			params.put("IdentificationNo", idNumber);
			
		}else {
			params.put("Mobile", user.getEnterpriseProfile().getContactPhone());
			params.put("Email", user.getEnterpriseProfile().getContactEmail());
			params.put("RealName", user.getEnterpriseProfile().getEnterpriseName());
			String licenseNum = user.getEnterpriseProfile().getLicenseNum();
			if(licenseNum.length()!=15)  return errors.newFailure("ACT0307", subAccountId, subAccount.getAccountId(), licenseNum);
			params.put("IdentificationNo", licenseNum);
		}
		params.put("Image1", "");
		params.put("Image2", "");
		params.put("LoanPlatformAccount", String.valueOf(subAccountId));
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		params.put("ReturnURL", GinkgoConfig.getProperty("front_url")+"/callback/qdd/loanRegisterReturn.html?subAccountId="+subAccountId);
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanRegister");
		
		String[] requiredParams = {"Mobile", "Email", "RealName", "IdentificationNo"};
		List<String> missedRequiredParams = PaymentUtil.missedRequiredParams(params, requiredParams);
		if(missedRequiredParams != null) {
			StringBuilder logMsg = new StringBuilder("AccountQDDService.register");
			logMsg.append("\nparams: " + PaymentUtil.toJSON(params));
			logMsg.append("\nrequiredParams: " + Arrays.toString(requiredParams));
			logMsg.append("\nmissedRequiredParams: " + missedRequiredParams);
			logger.warn(logMsg.toString());
			return errors.newFailure("ACT0308", subAccountId, subAccount.getAccountId(), user.getUserName(), missedRequiredParams);
		}
		
		String redirect = qddApi.redirect(QddUtil.loanRegister, params);
		return Result.newSuccess(redirect);
	}

	@Override
	public Result<String> authorise(Long subAccountId) {
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0301", subAccountId);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", subAccountId);
		if(!subAccount.isQddAccount()) return errors.newFailure("ACT0303", subAccountId, subAccount.getType());
		
		SubQddAccount subQddAccount = subQddAccountRepository.findOne(subAccountId);
		if(subQddAccount==null) return errors.newFailure("ACT0309", subAccountId);
		if(StringUtils.isBlank(subQddAccount.getMoneyMoreMoreId())) return errors.newFailure("ACT0310", subAccountId, subQddAccount.getMoneyMoreMoreId());
		if(!subQddAccount.getMoneyMoreMoreId().startsWith("m") && !subQddAccount.getMoneyMoreMoreId().startsWith("p")) return errors.newFailure("ACT0311", subAccountId, subQddAccount.getMoneyMoreMoreId());
		if(subQddAccount.isAuthorised()) return errors.newFailure("ACT0312", subAccountId, subQddAccount.getMoneyMoreMoreId(), subQddAccount.isAuthorised());
		
		QddApi qddApi = QddApi.get(subAccount.getType());
		QddConfig qddConfig = qddApi.getQddConfig();
		
		Map<String, String> params = new LinkedHashMap<>();
		params.put("MoneymoremoreId", subQddAccount.getMoneyMoreMoreId());
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		params.put("AuthorizeTypeOpen", "1,2,3");//1.投标2.还款3.二次分配审核
		params.put("AuthorizeTypeClose", "");//默认全部关闭，期望全部打开
		params.put("RandomTimeStamp", "");
		params.put("Remark1", "");
		params.put("Remark2", "");
		params.put("Remark3", "");
		params.put("ReturnURL", GinkgoConfig.getProperty("front_url") + "/account/index.html");
		params.put("NotifyURL", qddConfig.NotifyURLPrefix() + "loanAuthorise");
		
		String redirect = qddApi.redirect(QddUtil.loanAuthorise, params);
		return Result.newSuccess(redirect);
	}

	@Override
	public Result<BigDecimal[]> balancequery(Long subAccountId) {
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0301", subAccountId);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", subAccountId);
		if(!subAccount.isQddAccount()) return errors.newFailure("ACT0303", subAccountId, subAccount.getType());
		
		SubQddAccount subQddAccount = subQddAccountRepository.findOne(subAccountId);
		if(subQddAccount==null) return errors.newFailure("ACT0309", subAccountId);
		if(StringUtils.isBlank(subQddAccount.getMoneyMoreMoreId())) return errors.newFailure("ACT0310", subAccountId, subQddAccount.getMoneyMoreMoreId());
		if(!subQddAccount.getMoneyMoreMoreId().startsWith("m") && !subQddAccount.getMoneyMoreMoreId().startsWith("p")) return errors.newFailure("ACT0311", subAccountId, subQddAccount.getMoneyMoreMoreId());
		
		QddApi qddApi = QddApi.get(subAccount.getType());
		QddConfig qddConfig = qddApi.getQddConfig();
		
		Map<String, String> params = new LinkedHashMap<>();
		params.put("MoneymoremoreId", subQddAccount.getMoneyMoreMoreId());
		params.put("PlatformType", "2");//1-托管，2-自有/资金
		params.put("PlatformMoneymoremore", qddConfig.PlatformMoneymoremore());
		
		QDDPaymentResponse qddPaymentResponse = qddApi.post(QddUtil.loanBalanceQuery, params);
		BigDecimal[] balanceQuery = QddUtil.balanceQuery(qddPaymentResponse.rawText());
		if(balanceQuery==null) return errors.newFailure("ACT0313", subAccountId, subQddAccount.getMoneyMoreMoreId(), qddPaymentResponse.rawText());
		
		BigDecimal[] balances = new BigDecimal[2];
		if(subQddAccount.getMoneyMoreMoreId().startsWith("p")) {
			balances[0] = balanceQuery[1];
			balances[1] = balanceQuery[2];
		}else {
			balances[0] = balanceQuery[0];
			balances[1] = balanceQuery[2];
		}
		return Result.newSuccess(balances);
	}

	@Override
	public Result<Map<String, Map<String, String>>> balancequerys(String platform, String moneyMoreMoreIds) {
		QddApi qddApi = QddApi.getByPlatformId(platform);
		if(qddApi==null) return Result.newFailure(-1, "not support platform: "+platform);

		Map<String, String> params = new LinkedHashMap<>();
		params.put("PlatformId", moneyMoreMoreIds);
		params.put("PlatformType", "2");//1-托管，2-自有/资金
		params.put("PlatformMoneymoremore", platform);
		
		QDDPaymentResponse qddPaymentResponse = qddApi.post(QddUtil.loanBalanceQuery, params);
		Map<String, Map<String, String>> balanceQuerys = QddUtil.balanceQuerys(qddPaymentResponse.rawText());
		return Result.newSuccess(balanceQuerys);
	}

	@Override
	public Result<Boolean> isRegistered(Long subAccountId) {
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0301", subAccountId);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", subAccountId);
		if(!subAccount.isQddAccount()) return errors.newFailure("ACT0303", subAccountId, subAccount.getType());
		
		if("test".equals(GinkgoConfig.platform)) return Result.newSuccess(true);
		
		SubQddAccount subQddAccount = subQddAccountRepository.findOne(subAccountId);
		if(subQddAccount==null) return errors.newFailure("ACT0309", subAccountId);
		if(StringUtils.isBlank(subQddAccount.getMoneyMoreMoreId())) return Result.newSuccess(false);
		
		if(!subQddAccount.getMoneyMoreMoreId().startsWith("m") && !subQddAccount.getMoneyMoreMoreId().startsWith("p")) return errors.newFailure("ACT0311", subAccountId, subQddAccount.getMoneyMoreMoreId());
		return Result.newSuccess(true);
	}

	@Override
	public Result<Boolean> isAuthorised(Long subAccountId) {
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0301", subAccountId);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", subAccountId);
		if(!subAccount.isQddAccount()) return errors.newFailure("ACT0303", subAccountId, subAccount.getType());
		
		if("test".equals(GinkgoConfig.platform)) return Result.newSuccess(true);
		
		SubQddAccount subQddAccount = subQddAccountRepository.findOne(subAccountId);
		if(subQddAccount==null) return errors.newFailure("ACT0309", subAccountId);
		if(StringUtils.isBlank(subQddAccount.getMoneyMoreMoreId())) return errors.newFailure("ACT0310", subAccountId, subQddAccount.getMoneyMoreMoreId());
		if(!subQddAccount.getMoneyMoreMoreId().startsWith("m") && !subQddAccount.getMoneyMoreMoreId().startsWith("p")) return errors.newFailure("ACT0311", subAccountId, subQddAccount.getMoneyMoreMoreId());
		
		return Result.newSuccess(subQddAccount.isAuthorised());
	}

	@Override
	public Result<String> config(Long userId, String key, String value) {
		if(userId==null || userId<1 || StringUtils.isBlank(key)) return Result.newFailure(-1, "bad params");
		String configKey = String.valueOf(userId)+key;
		if(StringUtils.isNotBlank(value)) {
			String put = configs.put(configKey, value);
			return Result.newSuccess(put);
		}else {
			String remove = configs.remove(configKey);
			return Result.newSuccess(remove);
		}
	}
}
