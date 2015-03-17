package com.qingbo.ginkgo.ygb.account.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
import com.qingbo.ginkgo.ygb.account.payment.PaymentUtil;
import com.qingbo.ginkgo.ygb.account.payment.qdd.MD5;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QDDPaymentResponse;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddApi;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddConfig;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddUtil;
import com.qingbo.ginkgo.ygb.account.payment.qdd.RsaHelper;
import com.qingbo.ginkgo.ygb.account.repository.AccountLogRepository;
import com.qingbo.ginkgo.ygb.account.repository.QddAccountLogRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubAccountRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubQddAccountRepository;
import com.qingbo.ginkgo.ygb.account.service.QddAccountLogService;
import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.OrderNoLock;
import com.qingbo.ginkgo.ygb.common.util.TaskUtil;
import com.qingbo.ginkgo.ygb.trade.service.DealService;

@Service("qddAccountLogService")
public class QddAccountLogServiceImpl implements QddAccountLogService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private ErrorMessage errors = new ErrorMessage("account-error.properties");
	
	@Autowired private AccountLogRepository accountLogRepository;
	@Autowired private QddAccountLogRepository qddAccountLogRepository;
	@Autowired private SubAccountRepository subAccountRepository;
	@Autowired private SubQddAccountRepository subQddAccountRepository;
	
	@Autowired private QueuingService queuingService;
	@Autowired private DealService dealService;
	@Autowired private AccountDoService accountDoService;

	@Override
	public Result<Boolean> checkNotify(Map<String, String> callbacks, String... orderedNames) {
		String signature = callbacks.get("SignInfo");
		if(StringUtils.isBlank(signature)) return Result.newFailure(-1, "checkNotify: false, empty signature");
		
		StringBuilder dataStrBuilder = new StringBuilder();
		for(String name : orderedNames) dataStrBuilder.append(StringUtils.trimToEmpty(callbacks.get(name)));
		String dataStr = dataStrBuilder.toString();
		
		String PlatformMoneymoremore = callbacks.get("PlatformMoneymoremore");
		QddApi qddApi = QddApi.getByPlatformId(PlatformMoneymoremore);
		if(qddApi == null) return Result.newFailure(-1, "checkNotify: bad PlatformMoneymoremore: "+PlatformMoneymoremore);
		
		QddConfig qddConfig = qddApi.getQddConfig();
		if(qddConfig.antistate() == 1) dataStr = MD5.getMD5Info(dataStr);
		
		boolean verifySignature = RsaHelper.getInstance().verifySignature(signature, dataStr, qddConfig.publicKey());
		if(verifySignature == false) return Result.newFailure(-1, "checkNotify: false, bad signature of request");
		return Result.newSuccess(true);
	}

	@Override
	public Result<Boolean> doRegister(Map<String, String> callbacks) {
		String ResultCode = callbacks.get("ResultCode");
		String Message = callbacks.get("Message");
		if("88".equals(ResultCode) || "16".equals(ResultCode)) {
			Long subAccountId = NumberUtil.parseLong(callbacks.get("LoanPlatformAccount"), null);
			if(subAccountId != null) {
				String PlatformMoneymoremore = callbacks.get("PlatformMoneymoremore");
				String MoneymoremoreId = callbacks.get("MoneymoremoreId");
				
				SubAccount subAccount = subAccountRepository.findOne(subAccountId);
				if(!subAccount.isQddAccount()) {
					String subAccountType = QddApi.subAccountTypeOf(PlatformMoneymoremore);
					subAccount.setType(subAccountType);
					subAccountRepository.save(subAccount);
				}
				
				SubQddAccount subQddAccount = subQddAccountRepository.findOne(subAccountId);
				if(subQddAccount==null) {
					subQddAccount = new SubQddAccount();
					subQddAccount.setId(subAccount.getId());
				}
				subQddAccount.setPlatformId(PlatformMoneymoremore);
				subQddAccount = subQddAccountRepository.save(subQddAccount);
				
				String OldMoneymoremoreId = subQddAccount.getMoneyMoreMoreId();
				if(StringUtils.isNotBlank(OldMoneymoremoreId) && (OldMoneymoremoreId.startsWith("m") || OldMoneymoremoreId.startsWith("p"))) {
					logger.info("sub_account_id: " + subAccountId + " has already registered. OldMoneymoremoreId= " + MoneymoremoreId);
				}else {
					boolean updatePaymentAccountId = true;
					if("16".equals(ResultCode)){
						SubQddAccount findByPaymentAccountId = subQddAccountRepository.findByMoneyMoreMoreId(MoneymoremoreId);
						String paymentAccountId = findByPaymentAccountId==null ? null : findByPaymentAccountId.getMoneyMoreMoreId();
						if(StringUtils.isNotBlank(paymentAccountId) && (paymentAccountId.startsWith("m") || paymentAccountId.startsWith("p"))){
							updatePaymentAccountId = false;
							logger.warn("sub_account_id: " + subAccountId + " could not register MoneymoremoreId= " + MoneymoremoreId + ", cause " + findByPaymentAccountId.getId() + "=" + findByPaymentAccountId.getMoneyMoreMoreId() + " has taken it.");
							return errors.newFailure("ACT0501", subAccountId, MoneymoremoreId, findByPaymentAccountId.getId());
						}
					}
					if(updatePaymentAccountId){
						subQddAccount.setMoneyMoreMoreId(MoneymoremoreId);
						subQddAccountRepository.save(subQddAccount);
						logger.info("sub_account_id: " + subAccountId + " has registered. MoneymoremoreId= " + MoneymoremoreId);
					}
				}
			}else {
				logger.warn("sub_account_id=LoanPlatformAccount: null");
				return errors.newFailure("ACT0301", subAccountId);
			}
		}else {
			logger.warn("ResultCode: "+ResultCode+", Message: "+Message);
			return errors.newFailure("ACT0502", ResultCode, Message);
		}
		return Result.newSuccess(true);
	}

	@Override
	public Result<Boolean> doAuthorise(Map<String, String> callbacks) {
		String ResultCode = callbacks.get("ResultCode");
		String Message = callbacks.get("Message");
		if("88".equals(ResultCode) || "08".equals(ResultCode)) {
			String moneyMoreMoreId = callbacks.get("MoneymoremoreId");
			if(moneyMoreMoreId != null) {
				SubQddAccount subQddAccount = subQddAccountRepository.findByMoneyMoreMoreId(moneyMoreMoreId);
				if(subQddAccount != null) {
					boolean authorised = subQddAccount.isAuthorised();
					if(authorised) {
						logger.info("moneyMoreMoreId: " + moneyMoreMoreId + " has already authorised sub_qdd_account: " + subQddAccount.getId());
					}else {
						subQddAccount.setAuthorised(true);
						subQddAccountRepository.save(subQddAccount);
						logger.info("moneyMoreMoreId: " + moneyMoreMoreId + " has authorised sub_qdd_account: " +subQddAccount.getId());
					}
				}else {
					logger.warn("moneyMoreMoreId: " + moneyMoreMoreId + " has no sub_qdd_account");
				}
			}else {
				logger.warn("moneyMoreMoreId=MoneymoremoreId: null");
			}
		}else {
			logger.warn("ResultCode: "+ResultCode+", Message: "+Message);
			return errors.newFailure("ACT0502", ResultCode, Message);
		}
		return Result.newSuccess(true);
	}
	
	private void notifyTrade(final QddAccountLog qddAccountLog) {
		if(qddAccountLog==null || qddAccountLog.getTradeId()==null) return;
		
		TaskUtil.submit(new Runnable() {
			public void run() {
				logger.info("notify trade: "+qddAccountLog.getTradeId()+","+qddAccountLog.success()+","+qddAccountLog.getMessage());
				Result<Boolean> callBack = dealService.callBack(qddAccountLog.getTradeId(), qddAccountLog.success(), qddAccountLog.getMessage());
				logger.info("notify trade: "+callBack.toString());
			}
		});
	}

	@Override
	public Result<Boolean> doRecharge(Map<String, String> callbacks) {
		String ResultCode = callbacks.get("ResultCode");
		String Message = callbacks.get("Message");
		String orderNo = callbacks.get("OrderNo");
		try {
		OrderNoLock.lock(orderNo);
		QddAccountLog qddAccountLog = qddAccountLogRepository.findByOrderNo(orderNo);
		if(qddAccountLog==null) return Result.newFailure("ACT0503", orderNo);
		if(StringUtils.isNotBlank(qddAccountLog.getResultCode())) return Result.newSuccess(true);
		
		qddAccountLog.setResultCode(ResultCode);
		qddAccountLog.setMessage(Message);
		qddAccountLog.setLoanNo(callbacks.get("LoanNo"));
		qddAccountLog.setCallback(JSON.toJSONString(callbacks));
		
		Result<Boolean> result = null;
		if("88".equals(ResultCode)) {
			qddAccountLog.setMemo("充值成功");
			result = Result.newSuccess(true);
		}else {
			qddAccountLog.setMemo("充值失败："+ResultCode+","+Message);
			logger.warn("ResultCode: "+ResultCode+", Message: "+Message);
			result = errors.newFailure("ACT0502", ResultCode, Message);
		}
		qddAccountLog = qddAccountLogRepository.save(qddAccountLog);
		notifyTrade(qddAccountLog);
		
		SubQddAccount subQddAccount = subQddAccountRepository.findByMoneyMoreMoreId(qddAccountLog.getMoneyMoreMoreId());
		SubAccount subAccount = subAccountRepository.findOne(subQddAccount.getId());
		
		AccountLog accountLog = new AccountLog();
		accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
		accountLog.setType(AccountLogType.IN.getCode());
		accountLog.setSubType(AccountLogSubType.DEPOSIT.getCode());
		accountLog.setTradeId(qddAccountLog.getTradeId());
		accountLog.setSubAccountId(subAccount.getId());
		accountLog.setBalance(qddAccountLog.getAmount());
		accountLog.setAccountBalance(subAccount.getBalance());
		accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
		if(qddAccountLog.success()) {
			accountLog.setMemo("充值成功");
			accountLog = accountLogRepository.save(accountLog);
			
			accountLogIds.offer(accountLog.getId());
		}else {
			accountLog.setMemo("充值失败："+Message);
//			accountLogRepository.save(accountLog);
		}
		
		return result;
		}finally {
			OrderNoLock.unlock(orderNo);
		}
	}

	@Override
	public Result<Boolean> doAudit(Map<String, String> callbacks) {
		String ResultCode = callbacks.get("ResultCode");
		String Message = callbacks.get("Message");
		String LoanNoList = callbacks.get("LoanNoList");
		String[] LoanNoArray = LoanNoList.split(",");
		String LoanNo = LoanNoArray[0];
		
		try {
		OrderNoLock.lock(LoanNo);
		QddAccountLog qddAccountLog = qddAccountLogRepository.findByLoanNo(LoanNo);
		if(qddAccountLog==null) return Result.newFailure("ACT0503", LoanNo);
		if(qddAccountLog.getAuditPass() > 0) return Result.newSuccess(true);//已审核
		
		if("88".equals(ResultCode)) {
			String AuditType = callbacks.get("AuditType");
			qddAccountLog.setAuditPass(NumberUtil.parseInt(AuditType, 0));
			qddAccountLogRepository.save(qddAccountLog);
			
			SubQddAccount subQddAccount = subQddAccountRepository.findByMoneyMoreMoreId(qddAccountLog.getMoneyMoreMoreId());
			SubAccount subAccount = subAccountRepository.findOne(subQddAccount.getId());
			SubQddAccount otherSubQddAccount = subQddAccountRepository.findByMoneyMoreMoreId(qddAccountLog.getOtherMoneyMoreMoreId());
			SubAccount otherSubAccount = subAccountRepository.findOne(otherSubQddAccount.getId());
			
			AccountLog accountLog = new AccountLog();
			accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			accountLog.setType(AccountLogType.UNFREEZE.getCode());
			accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
			accountLog.setSubAccountId(subAccount.getId());
			accountLog.setBalance(qddAccountLog.getAmount());
			accountLog.setAccountBalance(subAccount.getBalance());
			accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
			accountLog.setMemo(qddAccountLog.getAuditPass()==1?"投资通过解冻":"投资失败解冻");
			accountLog.setOtherSubAccountId(otherSubAccount.getId());
			accountLog = accountLogRepository.save(accountLog);
			
			accountLogIds.offer(accountLog.getId());
			
			if("1".equals(AuditType)) {//通过则继续转账
				accountLog = new AccountLog();
				accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
				accountLog.setType(AccountLogType.OUT.getCode());
				accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
				accountLog.setSubAccountId(subAccount.getId());
				accountLog.setBalance(qddAccountLog.getAmount());
				accountLog.setAccountBalance(subAccount.getBalance());
				accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
				accountLog.setMemo("投资转出");
				accountLog.setOtherSubAccountId(otherSubAccount.getId());
				accountLog = accountLogRepository.save(accountLog);
				
				accountLogIds.offer(accountLog.getId());
				
				accountLog = new AccountLog();
				accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
				accountLog.setType(AccountLogType.IN.getCode());
				accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
				accountLog.setSubAccountId(otherSubAccount.getId());
				accountLog.setBalance(qddAccountLog.getAmount());
				accountLog.setAccountBalance(subAccount.getBalance());
				accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
				accountLog.setMemo("投资转入");
				accountLog.setOtherSubAccountId(subAccount.getId());
				accountLog = accountLogRepository.save(accountLog);
				
				accountLogIds.offer(accountLog.getId());
			}
			return Result.newSuccess(true);
		}else {
			logger.warn("ResultCode: "+ResultCode+", Message: "+Message);
			return errors.newFailure("ACT0502", ResultCode, Message);
		}
		}finally {
			OrderNoLock.unlock(LoanNo);
		}
	}

	@Override
	public Result<Boolean> doTransfer(Map<String, String> callbacks) {
		String ResultCode = callbacks.get("ResultCode");
		String Message = callbacks.get("Message");
		
		String LoanJsonList = callbacks.get("LoanJsonList");
		@SuppressWarnings("unchecked")
		Map<String, String> LoanJsonMap = LoanJsonList.startsWith("[") ? JSON.parseObject(LoanJsonList, Map[].class)[0] : JSON.parseObject(LoanJsonList, Map.class);
		String orderNo = LoanJsonMap.get("OrderNo");
		
		try {
		OrderNoLock.lock(orderNo);
		QddAccountLog qddAccountLog = qddAccountLogRepository.findByOrderNo(orderNo);
		if(qddAccountLog==null) return Result.newFailure("ACT0503", orderNo);
		if(StringUtils.isNotBlank(qddAccountLog.getResultCode())) return Result.newSuccess(true);
		
		qddAccountLog.setResultCode(ResultCode);
		qddAccountLog.setMessage(Message);
		qddAccountLog.setLoanNo(LoanJsonMap.get("LoanNo"));
		qddAccountLog.setCallback(JSON.toJSONString(callbacks));
		
		Result<Boolean> result = null;
		if("88".equals(callbacks.get("ResultCode"))) {
			qddAccountLog.setMemo((qddAccountLog.getAuditPass()==-1?"投资冻结":"转账")+"成功");
			qddAccountLog = qddAccountLogRepository.save(qddAccountLog);
			result = Result.newSuccess(true);
			
			SubQddAccount subQddAccount = subQddAccountRepository.findByMoneyMoreMoreId(qddAccountLog.getMoneyMoreMoreId());
			SubAccount subAccount = subAccountRepository.findOne(subQddAccount.getId());
			SubQddAccount otherSubQddAccount = subQddAccountRepository.findByMoneyMoreMoreId(qddAccountLog.getOtherMoneyMoreMoreId());
			SubAccount otherSubAccount = subAccountRepository.findOne(otherSubQddAccount.getId());
			
			if(qddAccountLog.getAuditPass()==-1) {//冻结
				AccountLog accountLog = new AccountLog();
				accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
				accountLog.setType(AccountLogType.FREEZE.getCode());
				accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
				accountLog.setTradeId(qddAccountLog.getTradeId());
				accountLog.setSubAccountId(subAccount.getId());
				accountLog.setBalance(qddAccountLog.getAmount());
				accountLog.setAccountBalance(subAccount.getBalance());
				accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
				accountLog.setOtherSubAccountId(otherSubAccount.getId());
				accountLog.setMemo("投资冻结");
				accountLog = accountLogRepository.save(accountLog);
				
				accountLogIds.offer(accountLog.getId());
			}else {//转账
				AccountLog accountLog = new AccountLog();
				accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
				accountLog.setType(AccountLogType.OUT.getCode());
				accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
				accountLog.setSubAccountId(subAccount.getId());
				accountLog.setBalance(qddAccountLog.getAmount());
				accountLog.setAccountBalance(subAccount.getBalance());
				accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
				accountLog.setMemo("转出");
				accountLog.setOtherSubAccountId(otherSubAccount.getId());
				accountLog = accountLogRepository.save(accountLog);
				
				accountLogIds.offer(accountLog.getId());
				
				accountLog = new AccountLog();
				accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
				accountLog.setType(AccountLogType.IN.getCode());
				accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
				accountLog.setSubAccountId(otherSubAccount.getId());
				accountLog.setBalance(qddAccountLog.getAmount());
				accountLog.setAccountBalance(subAccount.getBalance());
				accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
				accountLog.setMemo("转入");
				accountLog.setOtherSubAccountId(subAccount.getId());
				accountLog = accountLogRepository.save(accountLog);
				
				accountLogIds.offer(accountLog.getId());
			}
		}else {
			qddAccountLog.setMemo((qddAccountLog.getAuditPass()==-1?"投资冻结":"转账")+"失败："+ResultCode+","+Message);
			logger.warn("ResultCode: "+ResultCode+", Message: "+Message);
			result = errors.newFailure("ACT0502", ResultCode, Message);
		}
		
		return result;
		}finally {
			OrderNoLock.unlock(orderNo);
		}
	}

	@Override
	public Result<Boolean> doRelease(Map<String, String> callbacks) {
		String ResultCode = callbacks.get("ResultCode");
		String Message = callbacks.get("Message");
		String orderNo = callbacks.get("OrderNo");
		
		try {
		OrderNoLock.lock(orderNo);
		QddAccountLog qddAccountLog = qddAccountLogRepository.findByOrderNo(orderNo);
		if(qddAccountLog==null) return Result.newFailure("ACT0503", orderNo);
		if(StringUtils.isNotBlank(qddAccountLog.getResultCode())) return Result.newSuccess(true);
		
		qddAccountLog.setResultCode(ResultCode);
		qddAccountLog.setMessage(Message);
		qddAccountLog.setLoanNo(callbacks.get("LoanNo"));
		qddAccountLog.setCallback(JSON.toJSONString(callbacks));
		if("88".equals(ResultCode)) {
			qddAccountLog.setMemo("资金释放成功");
			qddAccountLogRepository.save(qddAccountLog);
			
			return Result.newSuccess(true);
		}else {
			qddAccountLog.setMemo("资金释放失败："+ResultCode+","+Message);
			qddAccountLogRepository.save(qddAccountLog);
			
			logger.warn("ResultCode: "+ResultCode+", Message: "+Message);
			return errors.newFailure("ACT0502", ResultCode, Message);
		}
		}finally {
			OrderNoLock.unlock(orderNo);
		}
	}

	@Override
	public Result<Boolean> doWithdraw(Map<String, String> callbacks) {
		String ResultCode = callbacks.get("ResultCode");
		String Message = callbacks.get("Message");
		String orderNo = callbacks.get("OrderNo");
		
		try {
		OrderNoLock.lock(orderNo);
		QddAccountLog qddAccountLog = qddAccountLogRepository.findByOrderNo(orderNo);
		if(qddAccountLog==null) return Result.newFailure("ACT0503", orderNo);
		//提现成功88，之后可能提现退回89
		if(StringUtils.isNotBlank(qddAccountLog.getResultCode()) && ResultCode.equals(qddAccountLog.getResultCode())) return Result.newSuccess(true);
		
		qddAccountLog.setResultCode(ResultCode);
		qddAccountLog.setMessage(Message);
		qddAccountLog.setLoanNo(callbacks.get("LoanNo"));
		qddAccountLog.setCallback(JSON.toJSONString(callbacks));
		
		Result<Boolean> result = null;
		if("88".equals(ResultCode)) {
			qddAccountLog.setMemo("提现成功");
			result = Result.newSuccess(true);
		}else if("89".equals(ResultCode)) {
			qddAccountLog.setMemo("提现退回");
			logger.warn("ResultCode: "+ResultCode+", Message: "+Message);
			result = Result.newSuccess(true);
		}else {
			qddAccountLog.setMemo("提现失败："+ResultCode+","+Message);
			logger.warn("ResultCode: "+ResultCode+", Message: "+Message);
			result = errors.newFailure("ACT0502", ResultCode, Message);
		}
		BigDecimal Fee = NumberUtil.parseBigDecimal(callbacks.get("Fee"), BigDecimal.ZERO);
		BigDecimal FeeWithdraws = NumberUtil.parseBigDecimal(callbacks.get("FeeWithdraws"), BigDecimal.ZERO);
		if(Fee.compareTo(BigDecimal.ZERO) > 0) {
			qddAccountLog.setFee(Fee);
			qddAccountLog.setFeeMoneyMoreMoreId(GinkgoConfig.getProperty("com.qingbo.ginkgo.ygb.account.impl.QddAccountLogServiceImpl.platformFeeMoneyMoreMoreId", "p251"));
		}else if(FeeWithdraws.compareTo(BigDecimal.ZERO) > 0){
			qddAccountLog.setFee(FeeWithdraws);//自付手续费
		}
		qddAccountLog = qddAccountLogRepository.save(qddAccountLog);
		notifyTrade(qddAccountLog);
		
		SubQddAccount subQddAccount = subQddAccountRepository.findByMoneyMoreMoreId(qddAccountLog.getMoneyMoreMoreId());
		SubAccount subAccount = subAccountRepository.findOne(subQddAccount.getId());
		AccountLog accountLog = new AccountLog();
		accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
		accountLog.setType(AccountLogType.OUT.getCode());
		accountLog.setSubType(AccountLogSubType.WITHDRAW.getCode());
		accountLog.setTradeId(qddAccountLog.getTradeId());
		accountLog.setSubAccountId(subAccount.getId());
		accountLog.setBalance(qddAccountLog.getAmount());
		accountLog.setAccountBalance(subAccount.getBalance());
		accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
		if(Fee.compareTo(BigDecimal.ZERO) > 0) {
			accountLog.setFee(Fee);
			accountLog.setFeeSubAccountId(NumberUtil.parseLong(GinkgoConfig.getProperty("com.qingbo.ginkgo.ygb.account.impl.AccountLogServiceImpl.platformFeeSubAccount"), 4L));
		}else if(FeeWithdraws.compareTo(BigDecimal.ZERO) > 0){
			accountLog.setFee(FeeWithdraws);//自付手续费
		}
		if(qddAccountLog.success()) {
			accountLog.setMemo("提现成功");
			accountLog = accountLogRepository.save(accountLog);
			
			accountLogIds.offer(accountLog.getId());
			
			if(Fee.compareTo(BigDecimal.ZERO) > 0) {//垫付手续费
				SubAccount feeSubAccount = subAccountRepository.findOne(accountLog.getFeeSubAccountId());
				accountLog = new AccountLog();
				accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
				accountLog.setType(AccountLogType.OUT.getCode());
				accountLog.setSubType(AccountLogSubType.FEE.getCode());
				accountLog.setSubAccountId(feeSubAccount.getId());
				accountLog.setBalance(accountLog.getFee());//手续费
				accountLog.setAccountBalance(feeSubAccount.getBalance());
				accountLog.setAccountFreezeBalance(feeSubAccount.getFreezeBalance());
				accountLog.setOtherSubAccountId(subAccount.getId());
				accountLog.setTransAmount(qddAccountLog.getAmount());//提现金额
				accountLog.setMemo("垫付手续费");
				accountLog = accountLogRepository.save(accountLog);
				
				accountLogIds.offer(accountLog.getId());
			}
		}else if("89".equals(ResultCode)) {//提现退回
			accountLog = new AccountLog();
			accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			accountLog.setType(AccountLogType.IN.getCode());
			accountLog.setSubType(AccountLogSubType.WITHDRAW_BACK.getCode());
			accountLog.setTradeId(qddAccountLog.getTradeId());
			accountLog.setSubAccountId(subAccount.getId());
			accountLog.setBalance(qddAccountLog.getAmount());
			accountLog.setAccountBalance(subAccount.getBalance());
			accountLog.setAccountFreezeBalance(subAccount.getFreezeBalance());
			accountLog.setMemo("提现退回");
			accountLog = accountLogRepository.save(accountLog);
			
			accountLogIds.offer(accountLog.getId());
		} else {
			accountLog.setMemo("提现失败："+Message);
			accountLogRepository.save(accountLog);
		}
		return result;
		}finally {
			OrderNoLock.unlock(orderNo);
		}
	}

	DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	public Result<Boolean> orderquery(Map<String, String> params) {
		String PlatformMoneymoremore = params.get("PlatformMoneymoremore");
		String Action = StringUtils.trimToEmpty(params.get("Action"));
		String BeginTime = StringUtils.trimToEmpty(params.get("BeginTime"));
		String EndTime = StringUtils.trimToEmpty(params.get("EndTime"));
		if(StringUtils.isBlank(PlatformMoneymoremore)) return Result.newFailure("ACT0505", PlatformMoneymoremore);
		
		QddApi qddApi = QddApi.getByPlatformId(PlatformMoneymoremore);
		if(qddApi==null) return Result.newFailure("ACT0505", PlatformMoneymoremore);
		
		if(StringUtils.isNotBlank(BeginTime) && StringUtils.isNotBlank(EndTime)) {
			BeginTime = df.format(DateUtil.parse(BeginTime));
			EndTime = df.format(DateUtil.parse(EndTime));
		}
		
		params = new LinkedHashMap<>();
		params.put("PlatformMoneymoremore", PlatformMoneymoremore);
		params.put("Action", Action);//空-转账，1-充值，2-提现
		params.put("LoanNo", "");
		params.put("OrderNo", "");
		params.put("BatchNo", "");
		params.put("BeginTime", BeginTime);//yyyyMMddHHmmss
		params.put("EndTime", EndTime);
		QDDPaymentResponse post = qddApi.post(QddUtil.orderQuery, params);
		String rawText = post.rawText();
		logger.info(rawText);
		if(StringUtils.isBlank(rawText)) return Result.newSuccess(true);
		@SuppressWarnings("unchecked")
		Map<String,String>[] maps=post.rawText().startsWith("[") ? JSON.parseObject(post.rawText(), Map[].class) : new HashMap[]{ JSON.parseObject(post.rawText(), HashMap.class) };;
		for(Map<String, String> map:maps) {
			logger.info(map.toString());
			
			String LoanNo = map.get("LoanNo");
			String OrderNo = map.get("OrderNo");
			
			if(StringUtils.isBlank(LoanNo)) continue;
			QddAccountLog qddAccountLog = qddAccountLogRepository.findByLoanNo(LoanNo);
			if(qddAccountLog!=null) {
				qddAccountLog.setOrderquery(JSON.toJSONString(map));
				qddAccountLogRepository.save(qddAccountLog);
			}else {
				qddAccountLog = new QddAccountLog();
				qddAccountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
				qddAccountLog.setLoanNo(LoanNo);
				qddAccountLog.setOrderNo(OrderNo);
				qddAccountLog.setOrderquery(JSON.toJSONString(map));
				qddAccountLog.setPlatformId(map.get("PlatformMoneymoremore"));
				
				String createAt = null;
				AccountLog accountLog = new AccountLog();
				accountLog.setId(qddAccountLog.getId());
				accountLog.setSubAccountLogId(qddAccountLog.getId());
				if(StringUtils.isBlank(Action)) {//转账
					qddAccountLog.setMoneyMoreMoreId(map.get("LoanOutMoneymoremore"));
					qddAccountLog.setOtherMoneyMoreMoreId(map.get("LoanInMoneymoremore"));
					qddAccountLog.setAmount(new BigDecimal(map.get("Amount")));
					qddAccountLog.setMemo("转账补单");//空-未操作，1-已通过，2-已退回，3-自动通过
					qddAccountLog.setResultCode(map.get("ActState"));
					qddAccountLog.setMessage("空-未操作，1-已通过，2-已退回，3-自动通过");
					createAt = map.get("TransferTime");
					accountLog.setType(AccountLogType.OUT.getCode());
					accountLog.setSubType(AccountLogSubType.TRANSFER.getCode());
					accountLog.setOtherSubAccountId(subQddAccountRepository.findByMoneyMoreMoreId(qddAccountLog.getOtherMoneyMoreMoreId()).getId());
				}else if("1".equals(Action)) {//充值
					qddAccountLog.setMoneyMoreMoreId(map.get("RechargeMoneymoremore"));
					qddAccountLog.setAmount(new BigDecimal(map.get("Amount")));
					qddAccountLog.setMemo("充值补单");
					qddAccountLog.setResultCode(map.get("RechargeState"));//0-未充值，1-成功，2-失败
					qddAccountLog.setMessage("0-未充值，1-成功，2-失败");
					createAt = map.get("RechargeTime");
					accountLog.setType(AccountLogType.IN.getCode());
					accountLog.setSubType(AccountLogSubType.DEPOSIT.getCode());
				}else if("2".equals(Action)) {//提现
					qddAccountLog.setMoneyMoreMoreId(map.get("WithdrawMoneymoremore"));
					qddAccountLog.setAmount(new BigDecimal(map.get("Amount")));
					qddAccountLog.setMemo("提现补单");
					qddAccountLog.setResultCode(map.get("WithdrawsState"));//0-未充值，1-成功，2-失败
					qddAccountLog.setMessage("0-未充值，1-成功，2-失败");
					BigDecimal FeeWithdraws = NumberUtil.parseBigDecimal(map.get("FeeWithdraws"), BigDecimal.ZERO);
					BigDecimal Fee = NumberUtil.parseBigDecimal(map.get("Fee"), BigDecimal.ZERO);
					qddAccountLog.setFee(FeeWithdraws.add(Fee));
					createAt = map.get("WithdrawsTime");
					accountLog.setFee(qddAccountLog.getFee());
					accountLog.setType(AccountLogType.OUT.getCode());
					accountLog.setSubType(AccountLogSubType.WITHDRAW.getCode());
				}
				
				if(StringUtils.isNotBlank(createAt)) {
					try {
						Date parse = df.parse(createAt);
						qddAccountLog.setCreateAt(parse);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				
				qddAccountLog.setUpdateAt(new Date());
				qddAccountLog = qddAccountLogRepository.save(qddAccountLog);
				logger.info(JSON.toJSONString(qddAccountLog));
				
				if("1".equals(qddAccountLog.getResultCode())) {//成功则记账
					accountLog.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
					accountLog.setSubAccountId(subQddAccountRepository.findByMoneyMoreMoreId(qddAccountLog.getMoneyMoreMoreId()).getId());
					accountLog.setBalance(qddAccountLog.getAmount());
					accountLog.setExecuted(true);
					accountLog.setCreateAt(qddAccountLog.getCreateAt());
					accountLog.setMemo(qddAccountLog.getMemo());
					accountLogRepository.save(accountLog);
					
					if(StringUtils.isBlank(Action)) {//转账再记一笔
						accountLog.setSubAccountId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
						accountLog.setType(AccountLogType.IN.getCode());
						Long otherSubAccountId = accountLog.getOtherSubAccountId();
						accountLog.setOtherSubAccountId(accountLog.getSubAccountId());
						accountLog.setSubAccountId(otherSubAccountId);
						accountLogRepository.save(accountLog);
					}
				}
			}
		}
		return Result.newSuccess(true);
	}
	
	private BlockingQueue<Long> accountLogIds = new LinkedBlockingQueue<>();
	public QddAccountLogServiceImpl() {
		TaskUtil.submitKeepRunning(new Runnable() {
			public void run() {
				try {
					while(true) {
						Long accountLogId = accountLogIds.take();
						accountDoService.execute(accountLogId);
					}
				} catch (InterruptedException e) {
					logger.warn("accountLogIds blocking queue exception: ", e);
				}
			}
		});
	}
}
