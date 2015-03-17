package com.qingbo.ginkgo.ygb.account.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.account.entity.AccountLog;
import com.qingbo.ginkgo.ygb.account.entity.QddAccountLog;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.entity.SubQddAccount;
import com.qingbo.ginkgo.ygb.account.enums.AccountLogSubType;
import com.qingbo.ginkgo.ygb.account.enums.AccountLogType;
import com.qingbo.ginkgo.ygb.account.inner.AccountDoService;
import com.qingbo.ginkgo.ygb.account.inner.QddPaymentService;
import com.qingbo.ginkgo.ygb.account.payment.PaymentUtil;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QDDPaymentResponse;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QDDPaymentUtil;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddApi;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddConfig;
import com.qingbo.ginkgo.ygb.account.repository.AccountLogRepository;
import com.qingbo.ginkgo.ygb.account.repository.QddAccountLogRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubAccountRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubQddAccountRepository;
import com.qingbo.ginkgo.ygb.account.service.PaymentService;
import com.qingbo.ginkgo.ygb.account.service.QddAccountService;
import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.TaskUtil;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants.Status;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.trade.service.DealService;

@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {
	public static String success = "success";
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private ErrorMessage errors = new ErrorMessage("account-error.properties");
	
	@Autowired private QueuingService queuingService;
	
	@Autowired private AccountLogRepository accountLogRepository;
	@Autowired private QddAccountLogRepository qddAccountLogRepository;
	@Autowired private SubAccountRepository subAccountRepository;
	@Autowired private SubQddAccountRepository subQddAccountRepository;
	@Autowired private QddPaymentService qddPaymentService;
	@Autowired private QddAccountService qddAccountService;
	@Autowired private AccountDoService accountDoService;
	@Autowired private CustomerService customerService;
	@Autowired private DealService dealService;
	
	@Override
	public Result<String> deposit(Long dealId, Long subAccountId, BigDecimal balance) {
		if(dealId==null || dealId<1) return errors.newFailure("ACT0101", dealId);
		
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0103", dealId, subAccountId);
		if(balance==null || balance.compareTo(BigDecimal.ZERO)<=0) return errors.newFailure("ACT0104", dealId, balance);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", dealId, subAccountId);
		
		if("test".equals(GinkgoConfig.platform)) {//内部账务测试直接入账
			AccountLog accountLog = new AccountLog();
			accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			accountLog.setType(AccountLogType.IN.getCode());
			accountLog.setSubType(AccountLogSubType.DEPOSIT.getCode());
			accountLog.setTradeId(dealId);
			accountLog.setSubAccountId(subAccount.getId());
			accountLog.setBalance(balance);
			accountLog.setAccountBalance(subAccount.getBalance());
			accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
			accountLog.setMemo("充值");
			accountLog = accountLogRepository.save(accountLog);
			
			accountDoService.execute(accountLog.getId());
			notifyTrade(accountLog);
			return Result.newSuccess(GinkgoConfig.getProperty("front_url") + "/account/myAccountIn.html");
		}else {
			SubQddAccount subQddAccount = subQddAccountRepository.findOne(subAccountId);
			if(subQddAccount==null) return errors.newFailure("ACT0309", subAccountId);
			if(StringUtils.isBlank(subQddAccount.getMoneyMoreMoreId())) return errors.newFailure("ACT0310", subAccountId, subQddAccount.getMoneyMoreMoreId());
			if(!subQddAccount.getMoneyMoreMoreId().startsWith("m") && !subQddAccount.getMoneyMoreMoreId().startsWith("p")) return errors.newFailure("ACT0311", subAccountId, subQddAccount.getMoneyMoreMoreId());
			
			QddAccountLog qddAccountLog = qddAccountLogRepository.findByTradeId(dealId);
			if(qddAccountLog==null) {
				qddAccountLog = new QddAccountLog();//充值交易可以中断后继续执行，只记一条乾多多支付日志
				qddAccountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			}else {
				if(qddAccountLog.success()) return errors.newFailure("ACT0102", dealId);
			}
			
			QddApi qddApi = QddApi.get(subAccount.getType());
			QddConfig qddConfig = qddApi.getQddConfig();
			
			qddAccountLog.setTradeId(dealId);
			qddAccountLog.setOrderNo(QDDPaymentUtil.orderNo16());
			qddAccountLog.setPlatformId(qddConfig.PlatformMoneymoremore());
			qddAccountLog.setMoneyMoreMoreId(subQddAccount.getMoneyMoreMoreId());
			qddAccountLog.setAmount(balance);
			String RechargeType = qddAccountService.config(subAccount.getAccountId(), "RechargeType", null).getObject();
			if(StringUtils.isNotBlank(RechargeType)) qddAccountLog.setAuditPass(NumberUtil.parseInt(RechargeType, 0));
			qddAccountLog = qddAccountLogRepository.save(qddAccountLog);
			
			Result<String> redirect = qddPaymentService.loanRecharge(qddAccountLog.getId());
			logger.info("recharge redirect: "+redirect.getObject());
			return redirect;
		}
	}

	@Override
	public Result<String> withdraw(Long dealId, Long subAccountId, BigDecimal balance, String BankCode, String CardNo) {
		if(dealId==null || dealId<1) return errors.newFailure("ACT0101", dealId);
		
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0103", dealId, subAccountId);
		if(balance==null || balance.compareTo(BigDecimal.ZERO)<=0) return errors.newFailure("ACT0104", dealId, balance);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", dealId, subAccountId);
		if(subAccount.getBalance()==null || subAccount.getBalance().compareTo(balance)<0) return errors.newFailure("ACT0106", dealId, subAccountId, balance, subAccount.getBalance());
		
		Result<User> userResult = customerService.getUserByUserId(subAccount.getAccountId());
		if(!userResult.hasObject() || Status.LOCKED.getCode().equals(userResult.getObject().getStatus())) return Result.newFailure(-1, "user not exist or locked: "+subAccount.getAccountId());
		
		if("test".equals(GinkgoConfig.platform)) {
			AccountLog accountLog = new AccountLog();
			accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			accountLog.setType(AccountLogType.OUT.getCode());
			accountLog.setSubType(AccountLogSubType.WITHDRAW.getCode());
			accountLog.setTradeId(dealId);
			accountLog.setSubAccountId(subAccount.getId());
			accountLog.setBalance(balance);
			accountLog.setAccountBalance(subAccount.getBalance());
			accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
			accountLog.setMemo("提现");
			accountLog = accountLogRepository.save(accountLog);
			
			accountDoService.execute(accountLog.getId());
			notifyTrade(accountLog);
			return Result.newSuccess(GinkgoConfig.getProperty("front_url") + "/account/myAccountOut.html");
		}else {
			SubQddAccount subQddAccount = subQddAccountRepository.findOne(subAccountId);
			if(subQddAccount==null) return errors.newFailure("ACT0309", subAccountId);
			if(StringUtils.isBlank(subQddAccount.getMoneyMoreMoreId())) return errors.newFailure("ACT0310", subAccountId, subQddAccount.getMoneyMoreMoreId());
			if(!subQddAccount.getMoneyMoreMoreId().startsWith("m") && !subQddAccount.getMoneyMoreMoreId().startsWith("p")) return errors.newFailure("ACT0311", subAccountId, subQddAccount.getMoneyMoreMoreId());
			
			QddAccountLog qddAccountLog = qddAccountLogRepository.findByTradeId(dealId);
			if(qddAccountLog==null) {
				qddAccountLog = new QddAccountLog();//充值交易可以中断后继续执行，只记一条乾多多支付日志
				qddAccountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			}else {
				if(qddAccountLog.success()) return errors.newFailure("ACT0102", dealId);
			}
			
			QddApi qddApi = QddApi.get(subAccount.getType());
			QddConfig qddConfig = qddApi.getQddConfig();
			
			qddAccountLog.setTradeId(dealId);
			qddAccountLog.setOrderNo(QDDPaymentUtil.orderNo16());
			qddAccountLog.setPlatformId(qddConfig.PlatformMoneymoremore());
			qddAccountLog.setMoneyMoreMoreId(subQddAccount.getMoneyMoreMoreId());
			qddAccountLog.setAmount(balance);
			qddAccountLog.setBankCode(BankCode);
			qddAccountLog.setCardNo(CardNo);
			qddAccountLog = qddAccountLogRepository.save(qddAccountLog);
			
			Result<String> redirect = qddPaymentService.loanWithdraw(qddAccountLog.getId());
			logger.info("withdraw redirect: "+ redirect.getObject());
			return redirect;
		}
	}

	@Override
	public Result<Boolean> transfer(Long subAccountId, Long otherSubAccountId, BigDecimal balance) {
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0103", "", subAccountId);
		if(balance==null || balance.compareTo(BigDecimal.ZERO)<=0) return errors.newFailure("ACT0104", "", balance);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", "", subAccountId);
		//检查余额是否足够
		if(subAccount.getBalance()==null || subAccount.getBalance().compareTo(balance)<0) return errors.newFailure("ACT0106", "", subAccountId, balance, subAccount.getBalance());
		
		SubAccount otherSubAccount = subAccountRepository.findOne(otherSubAccountId);
		if(otherSubAccount==null) return errors.newFailure("ACT0105", otherSubAccountId);
		if(!otherSubAccount.isQddAccount()) return errors.newFailure("ACT0303", otherSubAccountId, otherSubAccount.getType());
		
		if("test".equals(GinkgoConfig.platform)) {
			AccountLog accountLog = new AccountLog();
			accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			accountLog.setType(AccountLogType.OUT.getCode());
			accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
			accountLog.setSubAccountId(subAccount.getId());
			accountLog.setBalance(balance);
			accountLog.setAccountBalance(subAccount.getBalance());
			accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
			accountLog.setMemo("转出");
			accountLog.setOtherSubAccountId(otherSubAccount.getId());
			accountLog = accountLogRepository.save(accountLog);
			
			accountDoService.execute(accountLog.getId());
			
			accountLog = new AccountLog();
			accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			accountLog.setType(AccountLogType.IN.getCode());
			accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
			accountLog.setSubAccountId(otherSubAccount.getId());
			accountLog.setBalance(balance);
			accountLog.setAccountBalance(otherSubAccount.getBalance());
			accountLog.setAccountFreezeBalance(otherSubAccount.getFreezeBalance());
			accountLog.setMemo("转入");
			accountLog.setOtherSubAccountId(subAccount.getId());
			accountLog = accountLogRepository.save(accountLog);
			
			accountDoService.execute(accountLog.getId());
			return Result.newSuccess(true);
		}else {
			SubQddAccount subQddAccount = subQddAccountRepository.findOne(subAccountId);
			if(subQddAccount==null) return errors.newFailure("ACT0309", subAccountId);
			if(StringUtils.isBlank(subQddAccount.getMoneyMoreMoreId())) return errors.newFailure("ACT0310", subAccountId, subQddAccount.getMoneyMoreMoreId());
			if(!subQddAccount.getMoneyMoreMoreId().startsWith("m") && !subQddAccount.getMoneyMoreMoreId().startsWith("p")) return errors.newFailure("ACT0311", subAccountId, subQddAccount.getMoneyMoreMoreId());
			
			
			SubQddAccount otherSubQddAccount = subQddAccountRepository.findOne(otherSubAccountId);
			if(otherSubQddAccount==null) return errors.newFailure("ACT0309", otherSubAccountId);
			if(StringUtils.isBlank(otherSubQddAccount.getMoneyMoreMoreId())) return errors.newFailure("ACT0310", otherSubAccountId, otherSubQddAccount.getMoneyMoreMoreId());
			if(!otherSubQddAccount.getMoneyMoreMoreId().startsWith("m") && !otherSubQddAccount.getMoneyMoreMoreId().startsWith("p")) return errors.newFailure("ACT0311", subAccountId, subQddAccount.getMoneyMoreMoreId());
			
			if(!subAccount.getType().equals(otherSubAccount.getType())) //转账子账户类型必须相同
				return errors.newFailure("ACT0408", "", subAccount.getType(), subAccount.getId(), otherSubAccount.getType(), otherSubAccount.getId());
			
			QddApi qddApi = QddApi.get(subAccount.getType());
			QddConfig qddConfig = qddApi.getQddConfig();
			
			QddAccountLog qddAccountLog = new QddAccountLog();
			qddAccountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			qddAccountLog.setOrderNo(QDDPaymentUtil.orderNo16());
			qddAccountLog.setPlatformId(qddConfig.PlatformMoneymoremore());
			qddAccountLog.setMoneyMoreMoreId(subQddAccount.getMoneyMoreMoreId());
			qddAccountLog.setAmount(balance);
			qddAccountLog.setOtherMoneyMoreMoreId(otherSubQddAccount.getMoneyMoreMoreId());
			qddAccountLog = qddAccountLogRepository.save(qddAccountLog);
			
			Result<QDDPaymentResponse> loanTransfer = qddPaymentService.loanTransfer(qddAccountLog.getId(), false);
			if(loanTransfer.success()) {
				QDDPaymentResponse qddPaymentResponse = loanTransfer.getObject();
				if(qddPaymentResponse.success()) {
					logger.info("transfer ok: "+qddAccountLog.getPlatformId()+" "+qddAccountLog.getMoneyMoreMoreId()+" transfer "+qddAccountLog.getAmount()+" to "+qddAccountLog.getOtherMoneyMoreMoreId());
					return Result.newSuccess(true);
				}else {
					logger.warn("transfer fail: "+qddAccountLog.getId()+","+qddPaymentResponse.message());
					return Result.newFailure(-1, qddPaymentResponse.message());
				}
			}else {
				logger.warn("transfer fail: "+qddAccountLog.getId()+","+loanTransfer.getMessage());
				return Result.newFailure(loanTransfer);
			}
		}
	}

	@Override
	public Result<String> freeze(Long subAccountId, Long otherSubAccountId, BigDecimal balance) {
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0103", "", subAccountId);
		if(balance==null || balance.compareTo(BigDecimal.ZERO)<=0) return errors.newFailure("ACT0104", "", balance);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", "", subAccountId);
		//检查余额是否足够
		if(subAccount.getBalance()==null || subAccount.getBalance().compareTo(balance)<0) return errors.newFailure("ACT0107", "", subAccountId, balance, subAccount.getBalance());
		
		Result<User> userResult = customerService.getUserByUserId(subAccount.getAccountId());
		if(!userResult.hasObject() || Status.LOCKED.getCode().equals(userResult.getObject().getStatus())) return Result.newFailure(-1, "user not exist or locked: "+subAccount.getAccountId());
		
		if("test".equals(GinkgoConfig.platform)) {
			AccountLog accountLog = new AccountLog();
			accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			accountLog.setType(AccountLogType.FREEZE.getCode());
			accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
			accountLog.setSubAccountId(subAccountId);//冻结账户
			accountLog.setBalance(balance);//冻结金额
			accountLog.setAccountBalance(subAccount.getBalance());
			accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
			accountLog.setOtherSubAccountId(otherSubAccountId);
			accountLog.setMemo("冻结");
			accountLog = accountLogRepository.save(accountLog);
			
			accountDoService.execute(accountLog.getId());

			return Result.newSuccess(accountLog.getId().toString());
		}else {
			SubQddAccount subQddAccount = subQddAccountRepository.findOne(subAccountId);
			if(subQddAccount==null) return errors.newFailure("ACT0309", subAccountId);
			if(StringUtils.isBlank(subQddAccount.getMoneyMoreMoreId())) return errors.newFailure("ACT0310", subAccountId, subQddAccount.getMoneyMoreMoreId());
			if(!subQddAccount.getMoneyMoreMoreId().startsWith("m") && !subQddAccount.getMoneyMoreMoreId().startsWith("p")) return errors.newFailure("ACT0311", subAccountId, subQddAccount.getMoneyMoreMoreId());
			
			SubAccount otherSubAccount = subAccountRepository.findOne(otherSubAccountId);
			if(otherSubAccount==null) return errors.newFailure("ACT0105", otherSubAccountId);
			if(!otherSubAccount.isQddAccount()) return errors.newFailure("ACT0303", otherSubAccountId, otherSubAccount.getType());
			
			SubQddAccount otherSubQddAccount = subQddAccountRepository.findOne(otherSubAccountId);
			if(otherSubQddAccount==null) return errors.newFailure("ACT0309", otherSubAccountId);
			if(StringUtils.isBlank(otherSubQddAccount.getMoneyMoreMoreId())) return errors.newFailure("ACT0310", otherSubAccountId, otherSubQddAccount.getMoneyMoreMoreId());
			if(!otherSubQddAccount.getMoneyMoreMoreId().startsWith("m") && !otherSubQddAccount.getMoneyMoreMoreId().startsWith("p")) return errors.newFailure("ACT0311", subAccountId, subQddAccount.getMoneyMoreMoreId());
			
			if(!subAccount.getType().equals(otherSubAccount.getType())) //转账子账户类型必须相同
				return errors.newFailure("ACT0408", "", subAccount.getType(), subAccount.getId(), otherSubAccount.getType(), otherSubAccount.getId());
			
			QddAccountLog qddAccountLog = new QddAccountLog();
			qddAccountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			
			QddApi qddApi = QddApi.get(subAccount.getType());
			QddConfig qddConfig = qddApi.getQddConfig();
			
			qddAccountLog.setOrderNo(QDDPaymentUtil.orderNo16());
			qddAccountLog.setPlatformId(qddConfig.PlatformMoneymoremore());
			qddAccountLog.setMoneyMoreMoreId(subQddAccount.getMoneyMoreMoreId());
			qddAccountLog.setAmount(balance);
			qddAccountLog.setOtherMoneyMoreMoreId(otherSubQddAccount.getMoneyMoreMoreId());
			qddAccountLog.setAuditPass(-1);//-1标记转账冻结
			qddAccountLog = qddAccountLogRepository.save(qddAccountLog);
			
			Result<QDDPaymentResponse> loanTransfer = qddPaymentService.loanTransfer(qddAccountLog.getId(), true);
			if(loanTransfer.success()) {
				QDDPaymentResponse qddPaymentResponse = loanTransfer.getObject();
				if(qddPaymentResponse.success()) {
					String LoanJsonList = qddPaymentResponse.map().get("LoanJsonList");
					LoanJsonList = QDDPaymentUtil.decodeURL(LoanJsonList);
					@SuppressWarnings("unchecked")
					Map<String, String> LoanJsonMap = LoanJsonList.startsWith("[") ? JSON.parseObject(LoanJsonList, Map[].class)[0] : JSON.parseObject(LoanJsonList, Map.class);
					String LoanNo = LoanJsonMap.get("LoanNo");
					logger.info("freeze ok: "+qddAccountLog.getPlatformId()+" "+qddAccountLog.getMoneyMoreMoreId()+" freeze "+qddAccountLog.getAmount()+" to "+qddAccountLog.getOtherMoneyMoreMoreId()+", LoanNo="+LoanNo);
					return Result.newSuccess(LoanNo);
				}else {
					logger.warn("freeze fail: "+qddAccountLog.getId()+","+qddPaymentResponse.message());
					return Result.newFailure(-1, qddPaymentResponse.message());
				}
			}else {
				logger.warn("freeze fail: "+qddAccountLog.getId()+","+loanTransfer.getMessage());
				return Result.newFailure(loanTransfer);
			}
		}
	}

	@Override
	public Result<Boolean> unfreeze(String LoanNo, boolean auditPass) {
		if(StringUtils.isBlank(LoanNo)) return errors.newFailure("ACT0504", LoanNo);
		
		if("test".equals(GinkgoConfig.platform)) {
			AccountLog accountLogFreeze = accountLogRepository.findOne(NumberUtil.parseLong(LoanNo, null));
			SubAccount subAccount = subAccountRepository.findOne(accountLogFreeze.getSubAccountId());
			SubAccount otherSubAccount = subAccountRepository.findOne(accountLogFreeze.getOtherSubAccountId());
			
			AccountLog accountLog = new AccountLog();
			accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			accountLog.setType(AccountLogType.UNFREEZE.getCode());
			accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
			accountLog.setSubAccountId(subAccount.getId());
			accountLog.setBalance(accountLogFreeze.getBalance());
			accountLog.setAccountBalance(subAccount.getBalance());
			accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
			accountLog.setMemo(auditPass?"投资通过":"投资失败");
			accountLog.setOtherSubAccountId(otherSubAccount.getId());
			accountLog = accountLogRepository.save(accountLog);
			
			accountDoService.execute(accountLog.getId());
			
			if(auditPass) {//通过则继续转账
				accountLog = new AccountLog();
				accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
				accountLog.setType(AccountLogType.OUT.getCode());
				accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
				accountLog.setSubAccountId(subAccount.getId());
				accountLog.setBalance(accountLogFreeze.getBalance());
				accountLog.setAccountBalance(subAccount.getBalance());
				accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
				accountLog.setMemo("投资转出");
				accountLog.setOtherSubAccountId(otherSubAccount.getId());
				accountLog = accountLogRepository.save(accountLog);
				
				accountDoService.execute(accountLog.getId());
				
				accountLog = new AccountLog();
				accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
				accountLog.setType(AccountLogType.IN.getCode());
				accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
				accountLog.setSubAccountId(otherSubAccount.getId());
				accountLog.setBalance(accountLogFreeze.getBalance());
				accountLog.setAccountBalance(otherSubAccount.getBalance());
				accountLog.setAccountFreezeBalance(otherSubAccount.getFreezeBalance());
				accountLog.setMemo("投资转入");
				accountLog.setOtherSubAccountId(subAccount.getId());
				accountLog = accountLogRepository.save(accountLog);
				
				accountDoService.execute(accountLog.getId());
			}
			return Result.newSuccess(true);
		}else {
			QddAccountLog qddAccountLog = qddAccountLogRepository.findByLoanNo(LoanNo);
			if(qddAccountLog==null) return errors.newFailure("ACT0504", LoanNo);
			
			if(!qddAccountLog.success()) return Result.newFailure(-1, "投资状态不正确："+qddAccountLog.getMessage());
			if(qddAccountLog.getAuditPass() > 0) return Result.newSuccess(true);//已审核
			
			Result<QDDPaymentResponse> loanAudit = qddPaymentService.loanAudit(LoanNo, auditPass);
			if(loanAudit.success()) {
				QDDPaymentResponse qddPaymentResponse = loanAudit.getObject();
				if(qddPaymentResponse.success()) {
					logger.info("unfreeze ok: "+qddAccountLog.getPlatformId()+" -> "+qddAccountLog.getMoneyMoreMoreId()+" unfreeze "+qddAccountLog.getAmount()+" to "+qddAccountLog.getOtherMoneyMoreMoreId());
					return Result.newSuccess(true);
				}else {
					logger.warn("unfreeze fail: "+qddAccountLog.getId()+","+qddPaymentResponse.message());
					return Result.newFailure(-1, qddPaymentResponse.message());
				}
			}else {
				logger.warn("unfreeze fail: "+qddAccountLog.getId()+","+loanAudit.getMessage());
				return Result.newFailure(loanAudit);
			}
		}
	}

	@Override
	public Result<Boolean> freeze(Long subAccountId, BigDecimal balance) {
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0103", "", subAccountId);
		if(balance==null || balance.compareTo(BigDecimal.ZERO)<=0) return errors.newFailure("ACT0104", "", balance);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", "", subAccountId);
		
		if(subAccount.getBalance()==null || subAccount.getBalance().compareTo(balance)<0)
			return errors.newFailure("ACT0107", "", subAccountId, balance, subAccount.getBalance());
		
		AccountLog accountLog = new AccountLog();
		accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
		accountLog.setType(AccountLogType.FREEZE.getCode());
		accountLog.setSubAccountId(subAccountId);//冻结账户
		accountLog.setBalance(balance);//冻结金额
		accountLog.setAccountBalance(subAccount.getBalance());
		accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
		accountLog.setMemo("冻结");
		accountLog = accountLogRepository.save(accountLog);
		
		accountDoService.execute(accountLog.getId());
		return Result.newSuccess(true);
	}

	@Override
	public Result<Boolean> unfreeze(Long subAccountId, BigDecimal balance) {
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0103", "", subAccountId);
		if(balance==null || balance.compareTo(BigDecimal.ZERO)<=0) return errors.newFailure("ACT0104", "", balance);
		
		SubAccount subAccount = subAccountRepository.findOne(subAccountId);
		if(subAccount==null) return errors.newFailure("ACT0105", "", subAccountId);
		
		if(subAccount.getFreezeBalance()==null || subAccount.getFreezeBalance().compareTo(balance)<0)
			return errors.newFailure("ACT0108", "", subAccountId, balance, subAccount.getFreezeBalance());
		
		AccountLog accountLog = new AccountLog();
		accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
		accountLog.setType(AccountLogType.UNFREEZE.getCode());
		accountLog.setSubAccountId(subAccountId);
		accountLog.setBalance(balance);
		accountLog.setAccountBalance(subAccount.getBalance());
		accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
		accountLog.setMemo("解冻");
		accountLog = accountLogRepository.save(accountLog);
		
		accountDoService.execute(accountLog.getId());
		return Result.newSuccess(true);
	}
	
	private void notifyTrade(final AccountLog accountLog) {
		if(accountLog==null || accountLog.getTradeId()==null) return;
		
		TaskUtil.submit(new Runnable() {
			public void run() {
				logger.info("notify trade: "+accountLog.getTradeId()+","+accountLog.isExecuted()+","+accountLog.getMemo());
				Result<Boolean> callBack = dealService.callBack(accountLog.getTradeId(), accountLog.isExecuted(), accountLog.getMemo());
				logger.info("notify trade: "+callBack.toString());
			}
		});
	}
}
