package com.qingbo.gingko.domain.inner.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qingbo.gingko.common.result.Result;
import com.qingbo.gingko.common.result.SpecParam;
import com.qingbo.gingko.common.util.TaskUtil;
import com.qingbo.gingko.domain.inner.AccountLogServiceInner;
import com.qingbo.gingko.domain.inner.AccountServiceInner;
import com.qingbo.gingko.domain.util.SpecUtil;
import com.qingbo.gingko.entity.AccountLog;
import com.qingbo.gingko.entity.SubAccount;
import com.qingbo.gingko.entity.enums.AccountLogSubType;
import com.qingbo.gingko.entity.enums.AccountLogType;
import com.qingbo.gingko.repository.AccountLogRepository;
import com.qingbo.gingko.repository.SubAccountRepository;

@Service("accountLogServiceInnerImpl")
public class AccountLogServiceInnerImpl implements AccountLogServiceInner {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired private AccountLogRepository accountLogRepository;
	@Autowired private SubAccountRepository subAccountRepository;
	@Autowired private AccountServiceInner accountServiceInner;
	
	private BlockingQueue<Long> accountLogIds = new LinkedBlockingQueue<>();
	public AccountLogServiceInnerImpl() {
		TaskUtil.submitKeepRunning(new Runnable() {
			public void run() {
				try {
					Long accountLogId = accountLogIds.take();
					accountServiceInner.handle(accountLogId);
				} catch (InterruptedException e) {
					logger.warn("accountLogIds blocking queue exception: ", e);
				}
			}
		});
	}

	@Transactional
	public Result<Boolean> deposit(Long transactionId) {
		try {
			//TODO 检查交易是否已执行
			
			SpecParam<AccountLog> specs = new SpecParam<>();
			specs.eq("type", AccountLogType.IN.getCode());
			specs.eq("transactionId", transactionId);
			List<AccountLog> list = accountLogRepository.findAll(SpecUtil.spec(specs));
			if(list!=null && list.size()>0) {
				return Result.newSuccess(true);
			}
			
			Long subAccountId = 1L;
			BigDecimal balance = new BigDecimal("10");//充值
			String subType = "TRANSFER";//转账子类型
			Long otherSubAccountId=2L;//转出方
			
			SubAccount subAccount = subAccountRepository.findOne(subAccountId);
			if(subAccount==null) {
				logger.info("deposit not exist sub account="+subAccountId+", transaction="+transactionId);
				return Result.newFailure(-1, "deposit not exist sub account="+subAccountId+", transaction="+transactionId);
			}else {
				AccountLog accountLog = new AccountLog();
				accountLog.setType(AccountLogType.IN.getCode());
				accountLog.setSubType(subType);
				accountLog.setTransactionId(transactionId);
				accountLog.setSubAccountId(subAccountId);//转入账户
				accountLog.setBalance(balance);//转入金额
				accountLog.setOtherSubAccountId(otherSubAccountId);//转出方
				accountLog = accountLogRepository.save(accountLog);
				
				final Long accountLogId = accountLog.getId();
//				TaskUtil.submit(new Runnable() {
//					public void run() {
//						accountServiceInner.deposit(accountLogId);
//					}
//				});
				accountLogIds.offer(accountLogId);
				
				return Result.newSuccess(true);
			}
		}catch(Exception e) {
			return Result.newException(e);
		}
	}

	@Transactional
	public synchronized Result<Boolean> withdraw(Long transactionId) {
		try {
			//TODO 检查交易是否已执行
			
			SpecParam<AccountLog> specs = new SpecParam<>();
			specs.eq("type", AccountLogType.OUT.getCode());
			specs.eq("transactionId", transactionId);
			List<AccountLog> list = accountLogRepository.findAll(SpecUtil.spec(specs));
			if(list!=null && list.size()>0) {
				return Result.newSuccess(true);
			}
			
			Long subAccountId = 1L;
			BigDecimal balance = new BigDecimal("10");//充值
			String subType = "TRANSFER";//转账子类型
			Long otherSubAccountId=2L;//转入方
			BigDecimal fee = new BigDecimal("1");//手续费
			Long feeSubAccountId = 2L;//手续费支付方
			
			SubAccount subAccount = subAccountRepository.findOne(subAccountId);
			if(subAccount==null) {
				logger.info("deposit not exist sub account="+subAccountId+", transaction="+transactionId);
				return Result.newFailure(-1, "deposit not exist sub account="+subAccountId+", transaction="+transactionId);
			}else {
				AccountLog accountLog = new AccountLog();
				accountLog.setType(AccountLogType.OUT.getCode());
				accountLog.setSubType(subType);
				accountLog.setTransactionId(transactionId);
				accountLog.setSubAccountId(subAccountId);//转入账户
				accountLog.setBalance(balance);//转入金额
				accountLog.setOtherSubAccountId(otherSubAccountId);//转出方
				accountLog = accountLogRepository.save(accountLog);
				
				accountLogIds.offer(accountLog.getId());
				
				//他人支付手续费
				if(fee!=null && fee.compareTo(BigDecimal.ZERO)>0 && feeSubAccountId!=subAccountId) {
					accountLog = new AccountLog();
					accountLog.setType(AccountLogType.OUT.getCode());
					accountLog.setSubType(AccountLogSubType.FEE.getCode());
					accountLog.setTransactionId(transactionId);
					accountLog.setSubAccountId(feeSubAccountId);//手续费支付账户
					accountLog.setBalance(fee);//手续费
					accountLog.setOtherSubAccountId(subAccountId);//转出方
					accountLog = accountLogRepository.save(accountLog);
					
					accountLogIds.offer(accountLog.getId());
				}
				
				return Result.newSuccess(true);
			}
		}catch(Exception e) {
			return Result.newException(e);
		}
	}

	@Transactional
	public Result<Boolean> transfer(Long transactionId) {
		Result<Boolean> withdraw = withdraw(transactionId);
		if(withdraw.success() && withdraw.getObject()) {
			Result<Boolean> deposit = deposit(transactionId);
			if(!deposit.success() || !deposit.getObject()) {
				logger.info("transfer failed cause of deposit failed");
			}
			return deposit;
		}else {
			logger.info("transfer failed cause of withdraw failed");
			return withdraw;
		}
	}

	@Transactional
	public synchronized Result<Boolean> freeze(Long transactionId) {
		try {
			//TODO 检查交易是否已执行
			
			SpecParam<AccountLog> specs = new SpecParam<>();
			specs.eq("type", AccountLogType.FREEZE.getCode());
			specs.eq("transactionId", transactionId);
			List<AccountLog> list = accountLogRepository.findAll(SpecUtil.spec(specs));
			if(list!=null && list.size()>0) {
				return Result.newSuccess(true);
			}
			
			Long subAccountId = 1L;
			BigDecimal balance = new BigDecimal("10");
			
			SubAccount subAccount = subAccountRepository.findOne(subAccountId);
			if(subAccount==null) {
				logger.info("freeze not exist sub account="+subAccountId+", transaction="+transactionId);
				return Result.newFailure(-1, "freeze not exist sub account="+subAccountId+", transaction="+transactionId);
			}else if(subAccount.getBalance().compareTo(balance) >= 0) {
				AccountLog accountLog = new AccountLog();
				accountLog.setType(AccountLogType.FREEZE.getCode());
//				accountLog.setSubType(AccountLogSubType.FREEZE.getCode());
				accountLog.setTransactionId(transactionId);
				accountLog.setSubAccountId(subAccountId);//冻结账户
				accountLog.setBalance(balance);//冻结金额
				accountLog = accountLogRepository.save(accountLog);
				
				accountLogIds.offer(accountLog.getId());
				
				return Result.newSuccess(true);
			}else {
				logger.info("transaction.id="+transactionId+" want to freeze "+balance+", but balance is "+subAccount.getBalance());
				return Result.newSuccess(false);
			}
		}catch(Exception e) {
			return Result.newException(e);
		}
	}

	@Transactional
	public synchronized Result<Boolean> unfreeze(Long transactionId) {
		try {
			//TODO 检查交易是否已执行
			
			SpecParam<AccountLog> specs = new SpecParam<>();
			specs.eq("type", AccountLogType.UNFREEZE.getCode());
			specs.eq("transactionId", transactionId);
			List<AccountLog> list = accountLogRepository.findAll(SpecUtil.spec(specs));
			if(list!=null && list.size()>0) {
				return Result.newSuccess(true);
			}
			
			Long subAccountId = 1L;
			BigDecimal balance = new BigDecimal("10");
			
			SubAccount subAccount = subAccountRepository.findOne(subAccountId);
			if(subAccount==null) {
				logger.info("unfreeze not exist sub account="+subAccountId+", transaction="+transactionId);
				return Result.newFailure(-1, "unfreeze not exist sub account="+subAccountId+", transaction="+transactionId);
			}else if(subAccount.getFreezeBalance().compareTo(balance) >= 0) {
				AccountLog accountLog = new AccountLog();
				accountLog.setType(AccountLogType.FREEZE.getCode());
//				accountLog.setSubType(AccountLogSubType.UNFREEZE.getCode());
				accountLog.setTransactionId(transactionId);
				accountLog.setSubAccountId(subAccountId);//冻结账户
				accountLog.setBalance(balance);//冻结金额
				accountLog = accountLogRepository.save(accountLog);
				
				accountLogIds.offer(accountLog.getId());
				
				return Result.newSuccess(true);
			}else {
				logger.info("transaction.id="+transactionId+" want to unfreeze "+balance+", but freeze_balance is "+subAccount.getFreezeBalance());
				return Result.newSuccess(false);
			}
		}catch(Exception e) {
			return Result.newException(e);
		}
	}
}
