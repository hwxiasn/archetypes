package com.qingbo.gingko.domain.inner.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qingbo.gingko.common.result.Result;
import com.qingbo.gingko.domain.inner.AccountServiceInner;
import com.qingbo.gingko.entity.AccountLog;
import com.qingbo.gingko.entity.SubAccount;
import com.qingbo.gingko.entity.enums.AccountLogType;
import com.qingbo.gingko.repository.AccountLogRepository;
import com.qingbo.gingko.repository.AccountRepository;
import com.qingbo.gingko.repository.SubAccountRepository;

@Service("accountServiceInnerImpl")
public class AccountServiceInnerImpl implements AccountServiceInner {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired private AccountLogRepository accountLogRepository;
	@Autowired private AccountRepository accountRepository;
	@Autowired private SubAccountRepository subAccountRepository;

	@Transactional
	public Result<Boolean> deposit(Long accountLogId) {
		try {
			AccountLog accountLog = accountLogRepository.findOne(accountLogId);
			if(accountLog.isExecuted() || accountLog.isDeleted()) {
				logger.info("account_log.id="+accountLogId+" already executed or deleted.");
				return Result.newSuccess(false);
			}
			
			if(!AccountLogType.IN.getCode().equals(accountLog.getType())) {
				return Result.newFailure(-1, "account_log.id="+accountLogId+" bad type: "+accountLog.getType());
			}
			
			SubAccount subAccount = subAccountRepository.findOne(accountLog.getSubAccountId());
			BigDecimal newBalance = subAccount.getBalance().add(accountLog.getBalance());
			subAccount.setBalance(newBalance);
			subAccountRepository.save(subAccount);
			
			accountLog.setExecuted(true);
			accountLogRepository.save(accountLog);
			
			logger.info("account_log.id="+accountLogId+" executed and user_id="+subAccount.getAccountId()+" deposit "+accountLog.getBalance());
			return Result.newSuccess(true);
		}catch(Exception e) {
			logger.info("account_log.id="+accountLogId+" want to deposit, but execute to exception: "+e.getMessage());
			return Result.newException(e);
		}
	}

	@Transactional
	public Result<Boolean> withdraw(Long accountLogId) {
		try {
			AccountLog accountLog = accountLogRepository.findOne(accountLogId);
			if(accountLog.isExecuted() || accountLog.isDeleted()) {
				logger.info("account_log.id="+accountLogId+" already executed or deleted.");
				return Result.newSuccess(false);
			}
			
			if(!AccountLogType.OUT.getCode().equals(accountLog.getType())) {
				return Result.newFailure(-1, "account_log.id="+accountLogId+" bad type: "+accountLog.getType());
			}
			
			SubAccount subAccount = subAccountRepository.findOne(accountLog.getSubAccountId());
			if(subAccount.getBalance().compareTo(accountLog.getBalance()) >= 0) {
				subAccount.setBalance(subAccount.getBalance().subtract(accountLog.getBalance()));
				subAccountRepository.save(subAccount);
				
				accountLog.setExecuted(true);
				accountLogRepository.save(accountLog);
				
				logger.info("account_log.id="+accountLogId+" executed and user_id="+subAccount.getAccountId()+" withdraw "+accountLog.getBalance());
				return Result.newSuccess(true);
			}else {
				logger.info("account_log.id="+accountLogId+" want to withdraw "+accountLog.getBalance()+", but balance is "+subAccount.getBalance());
				return Result.newSuccess(false);
			}
		}catch(Exception e) {
			logger.info("account_log.id="+accountLogId+" want to freeze, but execute to exception: "+e.getMessage());
			return Result.newException(e);
		}
	}

	@Transactional
	public Result<Boolean> freeze(Long accountLogId) {
		try {
			AccountLog accountLog = accountLogRepository.findOne(accountLogId);
			if(accountLog.isExecuted() || accountLog.isDeleted()) {
				logger.info("account_log.id="+accountLogId+" already executed or deleted.");
				return Result.newSuccess(false);
			}
			
			if(!AccountLogType.FREEZE.getCode().equals(accountLog.getType())) {
				return Result.newFailure(-1, "account_log.id="+accountLogId+" bad type: "+accountLog.getType());
			}
			
			SubAccount subAccount = subAccountRepository.findOne(accountLog.getSubAccountId());
			if(subAccount.getBalance().compareTo(accountLog.getBalance()) >= 0) {
				subAccount.setBalance(subAccount.getBalance().subtract(accountLog.getBalance()));
				subAccount.setFreezeBalance(subAccount.getFreezeBalance().add(accountLog.getBalance()));
				subAccountRepository.save(subAccount);
				
				accountLog.setExecuted(true);
				accountLogRepository.save(accountLog);
				
				logger.info("account_log.id="+accountLogId+" executed and user_id="+subAccount.getAccountId()+" freeze "+accountLog.getBalance());
				return Result.newSuccess(true);
			}else {
				logger.info("account_log.id="+accountLogId+" want to freeze "+accountLog.getBalance()+", but balance is "+subAccount.getBalance());
				return Result.newSuccess(false);
			}
		}catch(Exception e) {
			logger.info("account_log.id="+accountLogId+" want to freeze, but execute to exception: "+e.getMessage());
			return Result.newException(e);
		}
	}

	@Transactional
	public Result<Boolean> unfreeze(Long accountLogId) {
		try {
			AccountLog accountLog = accountLogRepository.findOne(accountLogId);
			if(accountLog.isExecuted() || accountLog.isDeleted()) {
				logger.info("account_log.id="+accountLogId+" already executed or deleted.");
				return Result.newSuccess(false);
			}
			
			if(!AccountLogType.UNFREEZE.getCode().equals(accountLog.getType())) {
				return Result.newFailure(-1, "account_log.id="+accountLogId+" bad type: "+accountLog.getType());
			}
			
			SubAccount subAccount = subAccountRepository.findOne(accountLog.getSubAccountId());
			if(subAccount.getFreezeBalance().compareTo(accountLog.getBalance()) >= 0) {
				subAccount.setBalance(subAccount.getBalance().add(accountLog.getBalance()));
				subAccount.setFreezeBalance(subAccount.getFreezeBalance().subtract(accountLog.getBalance()));
				subAccountRepository.save(subAccount);
				
				accountLog.setExecuted(true);
				accountLogRepository.save(accountLog);
				
				logger.info("account_log.id="+accountLogId+" executed and user_id="+subAccount.getAccountId()+" unfreeze "+accountLog.getBalance());
				return Result.newSuccess(true);
			}else {
				logger.info("account_log.id="+accountLogId+" want to unfreeze "+accountLog.getBalance()+", but freeze_balance is "+subAccount.getFreezeBalance());
				return Result.newSuccess(false);
			}
		}catch(Exception e) {
			logger.info("account_log.id="+accountLogId+" want to unfreeze, but execute to exception: "+e.getMessage());
			return Result.newException(e);
		}
	}

	@Override
	public Result<Boolean> handle(Long accountLogId) {
		try {
			AccountLog accountLog = accountLogRepository.findOne(accountLogId);
			if(accountLog.isExecuted() || accountLog.isDeleted()) {
				logger.info("account_log.id="+accountLogId+" already executed or deleted.");
				return Result.newSuccess(false);
			}
			
			if(AccountLogType.IN.getCode().equals(accountLog.getType())) {
				return deposit(accountLogId);
			}else if(AccountLogType.OUT.getCode().equals(accountLog.getType())) {
				return withdraw(accountLogId);
			}else if(AccountLogType.FREEZE.getCode().equals(accountLog.getType())) {
					return freeze(accountLogId);
			}else if(AccountLogType.UNFREEZE.getCode().equals(accountLog.getType())){
				return unfreeze(accountLogId);
			}else {
				return Result.newFailure(-1, "account_log.id="+accountLogId+" bad type: "+accountLog.getType());
			}
		}catch(Exception e) {
			logger.info("account_log.id="+accountLogId+" want to handle, but execute to exception: "+e.getMessage());
			return Result.newException(e);
		}
	}
}
