package com.qingbo.ginkgo.ygb.account.inner.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.account.entity.AccountLog;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.enums.AccountLogType;
import com.qingbo.ginkgo.ygb.account.inner.AccountDoService;
import com.qingbo.ginkgo.ygb.account.repository.AccountLogRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubAccountRepository;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.ServiceRequester;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;

@Service("accountDoService")
public class AccountDoServiceImpl implements AccountDoService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private ErrorMessage errors = new ErrorMessage("account-error.properties");
	
	@Autowired private AccountLogRepository accountLogRepository;
	@Autowired private SubAccountRepository subAccountRepository;
	
	@Autowired private CustomerService customerService;

	@Override
	public synchronized Result<Boolean> execute(Long accountLogId) {
		if(accountLogId==null || accountLogId<1) return errors.newFailure("ACT0201", accountLogId);
		
		AccountLog accountLog = accountLogRepository.findOne(accountLogId);
		if(accountLog.isDeleted()) return errors.newFailure("ACT0202", accountLogId);
		if(accountLog.isExecuted()) return errors.newFailure("ACT0203", accountLogId);
		
		AccountLogType accountLogType = AccountLogType.getByCode(accountLog.getType());
		if(accountLogType==null) return errors.newFailure("ACT0213", accountLog, accountLog.getType());
		
		Long subAccountId = accountLog.getSubAccountId();
		if(subAccountId==null || subAccountId<1) return errors.newFailure("ACT0205", accountLogId, subAccountId);
		SubAccount subAccount = subAccountRepository.findOne(accountLog.getSubAccountId());
		
		BigDecimal balance = accountLog.getBalance();
		if(balance==null || balance.compareTo(BigDecimal.ZERO)<=0) return errors.newFailure("ACT0206", accountLogId, balance);
		
		BigDecimal newBalance = null, newFreezeBalance = null;
		switch(accountLogType) {
		case IN:
			newBalance = subAccount.getBalance()!=null ? subAccount.getBalance().add(balance) : balance;
			subAccount.setBalance(newBalance);
			subAccount = subAccountRepository.save(subAccount);
			
			logger.info("account_log.id="+accountLogId+", account_id="+subAccount.getAccountId()+", sub_account_id="+subAccountId+" deposit "+accountLog.getBalance());
			break;
		case OUT:
			if(subAccount.getBalance()==null || subAccount.getBalance().compareTo(balance)<0)
				return errors.newFailure("ACT0208", accountLogId, balance, subAccount.getBalance());
			
			newBalance = subAccount.getBalance().subtract(balance);
			subAccount.setBalance(newBalance);
			subAccount = subAccountRepository.save(subAccount);
			
			logger.info("account_log.id="+accountLogId+", account_id="+subAccount.getAccountId()+", sub_account_id="+subAccountId+" withdraw "+accountLog.getBalance());
			break;
		case FREEZE:
			if(subAccount.getBalance()==null || subAccount.getBalance().compareTo(balance)<0)
				return errors.newFailure("ACT0210", accountLogId, balance, subAccount.getBalance());
			
			newBalance = subAccount.getBalance().subtract(balance);
			newFreezeBalance = subAccount.getFreezeBalance().add(balance);
			
			subAccount.setBalance(newBalance);
			subAccount.setFreezeBalance(newFreezeBalance);
			subAccount = subAccountRepository.save(subAccount);
			
			logger.info("account_log.id="+accountLogId+", account_id="+subAccount.getAccountId()+", sub_account_id="+subAccountId+" freeze "+accountLog.getBalance());
			break;
		case UNFREEZE:
			if(subAccount.getFreezeBalance()==null || subAccount.getFreezeBalance().compareTo(balance)<0)
				return errors.newFailure("ACT0212", accountLogId, balance, subAccount.getFreezeBalance());
			
			newBalance = subAccount.getBalance().add(balance);
			newFreezeBalance = subAccount.getFreezeBalance().subtract(balance);
			
			subAccount.setBalance(newBalance);
			subAccount.setFreezeBalance(newFreezeBalance);
			subAccount = subAccountRepository.save(subAccount);
			
			logger.info("account_log.id="+accountLogId+", account_id="+subAccount.getAccountId()+", sub_account_id="+subAccountId+" unfreeze "+accountLog.getBalance());
			break;
		}
		
		accountLog.setExecuted(true);
		accountLog.setAccountBalance2(subAccount.getBalance());
		accountLog.setAccountFreezeBalance2(subAccount.getFreezeBalance());
		accountLog = accountLogRepository.save(accountLog);
		
		if(AccountLogType.IN.getCode().equals(accountLogType) || AccountLogType.OUT.getCode().equals(accountLogType)) {
			notifyAccountLog(subAccount, accountLog);//收支变化时通知日志和余额
		}
		return Result.newSuccess(true);
	}

	public void notifyAccountLog(SubAccount subAccount, AccountLog accountLog) {
		Result<Boolean> isWbossUser = customerService.isWbossUser(subAccount.getAccountId());
		if(isWbossUser.hasObject() && isWbossUser.getObject()) {
			Map<String, String> params = ServiceRequester.paramsWithSecret();
			params.put("method", "accountLog");
			Map<String, String> accountLogMap = ServiceRequester.convert(accountLog);
			params.putAll(accountLogMap);
			JSONObject request = ServiceRequester.request(ServiceRequester.serviceWboss+"account", params);
			logger.info("notify accountLog "+accountLog.getId()+": "+request);
			
			params = ServiceRequester.paramsWithSecret();
			params.put("method", "balance");
			params.put("userId", String.valueOf(subAccount.getAccountId()));
			params.put("subAccountId", String.valueOf(subAccount.getId()));
			params.put("balance", subAccount.getBalance().toPlainString());
			params.put("freezeBalance", subAccount.getFreezeBalance().toPlainString());
			ServiceRequester.request(ServiceRequester.serviceWboss+"account", params);
			logger.info("notify balance "+accountLog.getId()+": "+request);
			
		}
	}
}
