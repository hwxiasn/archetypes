package com.qingbo.gingko.domain.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qingbo.gingko.common.result.PageObject;
import com.qingbo.gingko.common.result.Result;
import com.qingbo.gingko.common.util.Pager;
import com.qingbo.gingko.common.util.SqlBuilder;
import com.qingbo.gingko.domain.AccountService;
import com.qingbo.gingko.domain.util.PagerUtil;
import com.qingbo.gingko.entity.Account;
import com.qingbo.gingko.entity.SubAccount;
import com.qingbo.gingko.entity.enums.AccountLogSubType;
import com.qingbo.gingko.entity.enums.AccountLogType;
import com.qingbo.gingko.entity.enums.SubAccountType;
import com.qingbo.gingko.repository.AccountLogRepository;
import com.qingbo.gingko.repository.AccountRepository;
import com.qingbo.gingko.repository.SubAccountRepository;
import com.qingbo.gingko.repository.TongjiRepository;

@Service("accountServiceImpl")
public class AccountServiceImpl implements AccountService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired private AccountRepository accountRepository;
	@Autowired private SubAccountRepository subAccountRepository;
	@Autowired private AccountLogRepository accountLogRepository;
	@Autowired private TongjiRepository tongjiRepository;
	@Autowired private PasswordServiceImpl passwordHelper;

	@Override
	public Result<Account> getAccount(Integer userId) {
		try {
			Account account = accountRepository.findOne(userId);
			if(account == null) {
				logger.info("account not exist for "+userId);
				return Result.newFailure(-1, "account not exist for "+userId);
			}
			
			List<SubAccount> list = subAccountRepository.findByAccountId(userId);
			if(list==null || list.size()==0) {//create default sub account if it not exist
				createSubAccount(userId, SubAccountType.DEFAULT.getCode());
				list = subAccountRepository.findByAccountId(userId);
			}
			account.setSubAccounts(list);
			BigDecimal balance = new BigDecimal("0"), freezeBalance = new BigDecimal("0");
			for(SubAccount subAccount : list) {
				balance = balance.add(subAccount.getBalance());
				freezeBalance = freezeBalance.add(subAccount.getFreezeBalance());
			}
			account.setBalance(balance);
			account.setFreezeBalance(freezeBalance);
			return Result.newSuccess(account);
		}catch(Exception e) {
			logger.info("getAccount failed for "+userId);
			return Result.newException(e);
		}
	}

	@Override
	public Result<SubAccount> getSubAccount(Integer userId, String type) {
		try {
			SubAccountType subAccountType = SubAccountType.getByCode(type);
			if(subAccountType==null) {
				logger.info("bad sub account type: "+type);
				return Result.newFailure(-1, "bad sub account type: "+type+", please use SubAccountType.DEFAULT etc.");
			}
			
			SubAccount subAccount = subAccountRepository.findByAccountIdAndType(userId, type);
			if(subAccount == null) {
				logger.info("account not exist for "+userId+", type: "+type);
				return Result.newFailure(-1, "account not exist for "+userId+", type: "+type);
			}
			return Result.newSuccess(subAccount);
		}catch(Exception e) {
			logger.info("getAccount failed for "+userId);
			return Result.newException(e);
		}
	}

	@Transactional
	public Result<Account> createAccount(Integer userId) {
		try {
			Account account = accountRepository.findOne(userId);
			if(account != null) {//账户已存在
				logger.info("createAccount already exist for "+userId);
				return getAccount(userId);
			}
			
			account = new Account();
			account.setId(userId);
			String generateSalt = passwordHelper.generateSalt();
			String encryptPassword = passwordHelper.encryptPassword("12345678", "", generateSalt);
			account.setSalt(generateSalt);
			account.setPassword(encryptPassword);
			accountRepository.save(account);
			
			SubAccount subAccount = subAccountRepository.findByAccountIdAndType(userId, SubAccountType.DEFAULT.getCode());
			if(subAccount == null) {
				subAccount = new SubAccount();
				subAccount.setAccountId(userId);
				subAccount.setType(SubAccountType.DEFAULT.getCode());
				subAccountRepository.save(subAccount);
			}else {
				logger.info("createAccount subAccount of type DEFAULT already exist for "+userId+", it is sub_account.id="+subAccount.getId());
			}
			
			return getAccount(userId);
		}catch(Exception e) {
			logger.info("createAccount failed for "+userId);
			return Result.newException(e);
		}
	}

	@Transactional
	public Result<SubAccount> createSubAccount(Integer userId, String type) {
		try {
			SubAccountType subAccountType = SubAccountType.getByCode(type);
			if(subAccountType==null) {
				logger.info("bad sub account type: "+type);
				return Result.newFailure(-1, "bad sub account type: "+type+", please use SubAccountType.DEFAULT etc.");
			}
			
			SubAccount subAccount = subAccountRepository.findByAccountIdAndType(userId, type);
			if(subAccount != null) {
				logger.info("createAccount subAccount of type "+type+" already exist for "+userId);
				return Result.newSuccess(subAccount);
			}
			
			subAccount = new SubAccount();
			subAccount.setAccountId(userId);
			subAccount.setType(type);
			subAccountRepository.save(subAccount);
			
			return getSubAccount(userId, type);
		}catch(Exception e) {
			logger.info("createAccount failed for "+userId);
			return Result.newException(e);
		}
	}

	@Override
	public Result<Boolean> validatePassword(Integer userId, String password) {
		try {
			Account account = accountRepository.findOne(userId);
			if(account == null) {
				logger.info("account not exist for "+userId);
				return Result.newFailure(-1, "account not exist for "+userId);
			}
			
			String encryptPassword = passwordHelper.encryptPassword(password, "", account.getSalt());
			return Result.newSuccess(encryptPassword.equals(account.getPassword()));
		}catch(Exception e) {
			logger.info("validatePassword failed for "+userId);
			return Result.newException(e);
		}
	}

	@Transactional
	public Result<Boolean> updatePassword(Integer userId, String oldPassword, String newPassword) {
		try {
			Account account = accountRepository.findOne(userId);
			if(account == null) {
				logger.info("account not exist for "+userId);
				return Result.newFailure(-1, "account not exist for "+userId);
			}
			
			String encryptPassword = passwordHelper.encryptPassword(oldPassword, "", account.getSalt());
			if(encryptPassword.equals(account.getPassword())) {
				String generateSalt = passwordHelper.generateSalt();
				encryptPassword = passwordHelper.encryptPassword(newPassword, "", generateSalt);
				account.setSalt(generateSalt);
				account.setPassword(encryptPassword);
				accountRepository.save(account);
				return Result.newSuccess(true);
			}else {
				return Result.newFailure(-1, "updatePassword failed cause of old password wrong");
			}
		}catch(Exception e) {
			logger.info("updatePassword failed for "+userId);
			return Result.newException(e);
		}
	}

	@Transactional
	public Result<Boolean> resetPassword(Integer userId, String password) {
		try {
			Account account = accountRepository.findOne(userId);
			if(account == null) {
				logger.info("account not exist for "+userId);
				return Result.newFailure(-1, "account not exist for "+userId);
			}
			
			String generateSalt = passwordHelper.generateSalt();
			String encryptPassword = passwordHelper.encryptPassword(password, "", generateSalt);
			account.setSalt(generateSalt);
			account.setPassword(encryptPassword);
			accountRepository.save(account);
			return Result.newSuccess(true);
		}catch(Exception e) {
			logger.info("updatePassword failed for "+userId);
			return Result.newException(e);
		}
	}

	@Override
	public Result<PageObject<Map<String, String>>> depositPage(Integer userId, Pager pager) {
		try {
			String[] names = {"accountId", "subAccountId", "accountLogId", "balance", "accountBalance"};//names与select次序一致
			//这里select多个id需要提供别名，不然会报错，javax.persistence.PersistenceException: org.hibernate.loader.custom.NonUniqueDiscoveredSqlAliasException: Encountered a duplicated sql alias [id] during auto-discovery of a native-sql query
			SqlBuilder sqlBuilder = new SqlBuilder("ac.id acId,sub.id subId,log.id logId,log.balance,log.account_balance", "account_log log left join sub_account sub on log.sub_account_id=sub.id left join account ac on sub.account_id=ac.id");
			sqlBuilder.eq("executed", "1");
			sqlBuilder.eq("ac.id", userId.toString());
			sqlBuilder.eq("log.type", AccountLogType.IN.getCode());
			sqlBuilder.eq("log.sub_type", AccountLogSubType.DEPOSIT.getCode());
			sqlBuilder.limit(PagerUtil.limit(pager));
			
			List<?> list = tongjiRepository.list(sqlBuilder.sql());
			List<Map<String, String>> content = new ArrayList<>();
			for(Object item : list) {
				Object[] arr = (Object[])item;
				Map<String, String> map = new HashMap<String, String>();
				int idx = 0;
				for(String name:names) {
					map.put(name, ObjectUtils.toString(arr[idx++]));
				}
				content.add(map);
			}
			return Result.newSuccess(new PageObject<Map<String, String>>(10, content));
		}catch(Exception e) {
			logger.info("depositPage failed for "+userId);
			return Result.newException(e);
		}
	}

	@Override
	public Result<PageObject<Map<String, String>>> withdrawPage(Integer userId, Pager pager) {
		try {
			String[] names = {"accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "fee", "feeAccountId", "feeSubAccountId"};//names与select次序一致
			SqlBuilder sqlBuilder = new SqlBuilder("ac.id acId,sub.id subId,log.id logId,log.balance,log.account_balance,log.fee,ac2.id feeAccountId,sub2.id feeSubAccountId", "account_log log left join sub_account sub on log.sub_account_id=sub.id left join account ac on sub.account_id=ac.id" +
					" left join sub_account sub2 on log.fee_sub_account_id=sub2.id left join account ac2 on sub2.account_id=ac2.id");
			sqlBuilder.eq("executed", "1");
			sqlBuilder.eq("ac.id", userId.toString());
			sqlBuilder.eq("log.type", AccountLogType.OUT.getCode());
			sqlBuilder.eq("log.sub_type", AccountLogSubType.WITHDRAW.getCode());
			sqlBuilder.limit(PagerUtil.limit(pager));
			
			List<?> list = tongjiRepository.list(sqlBuilder.sql());
			List<Map<String, String>> content = new ArrayList<>();
			for(Object item : list) {
				Object[] arr = (Object[])item;
				Map<String, String> map = new HashMap<String, String>();
				int idx = 0;
				for(String name:names) {
					map.put(name, ObjectUtils.toString(arr[idx++]));
				}
				content.add(map);
			}
			return Result.newSuccess(new PageObject<Map<String, String>>(10, content));
		}catch(Exception e) {
			logger.info("depositPage failed for "+userId);
			return Result.newException(e);
		}
	}

	@Override
	public Result<PageObject<Map<String, String>>> transferPage(Integer userId, Pager pager) {
		try {
			String[] names = {"accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "otherAccountId", "otherSubAccountId"};//names与select次序一致
			SqlBuilder sqlBuilder = new SqlBuilder("ac.id acId,sub.id subId,log.id logId,log.balance,log.account_balance,ac2.id acId2,sub2.id subId2", "account_log log left join sub_account sub on log.sub_account_id=sub.id left join account ac on sub.account_id=ac.id" +
					" left join sub_account sub2 on log.other_sub_account_id=sub2.id left join account ac2 on sub2.account_id=ac2.id");
			sqlBuilder.eq("ac.id", userId.toString());
			sqlBuilder.in("log.sub_type", AccountLogSubType.TRANSFER.getCode(), AccountLogSubType.INVESTMENT.getCode(), AccountLogSubType.COMMISSION.getCode()
					, AccountLogSubType.REPAY.getCode(), AccountLogSubType.FEE.getCode(), AccountLogSubType.PRIZE.getCode());
			sqlBuilder.limit(PagerUtil.limit(pager));
			
			List<?> list = tongjiRepository.list(sqlBuilder.sql());
			List<Map<String, String>> content = new ArrayList<>();
			for(Object item : list) {
				Object[] arr = (Object[])item;
				Map<String, String> map = new HashMap<String, String>();
				int idx = 0;
				for(String name:names) {
					map.put(name, ObjectUtils.toString(arr[idx++]));
				}
				content.add(map);
			}
			return Result.newSuccess(new PageObject<Map<String, String>>(10, content));
		}catch(Exception e) {
			logger.info("depositPage failed for "+userId);
			return Result.newException(e);
		}
	}

	@Override
	public Result<PageObject<Map<String, String>>> accountLogPage(Integer userId, Pager pager) {
		try {
			String[] names = {"accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "fee", "feeAccountId", "feeSubAccountId", "otherAccountId", "otherSubAccountId"};//names与select次序一致
			SqlBuilder sqlBuilder = new SqlBuilder("ac.id acId,sub.id subId,log.id logId,log.balance,log.account_balance,log.fee,ac2.id feeAccountId,sub2.id feeSubAccountId,ac3.id otherAccountId,sub3.id otherSubAccountId", "account_log log left join sub_account sub on log.sub_account_id=sub.id left join account ac on sub.account_id=ac.id" +
					" left join sub_account sub2 on log.fee_sub_account_id=sub2.id left join account ac2 on sub2.account_id=ac2.id" +
					" left join sub_account sub3 on log.other_sub_account_id=sub3.id left join account ac3 on sub3.account_id=ac3.id");
			sqlBuilder.eq("ac.id", userId.toString());
			sqlBuilder.notIn("log.sub_type", AccountLogSubType.FREEZE.getCode(), AccountLogSubType.UNFREEZE.getCode());
			sqlBuilder.limit(PagerUtil.limit(pager));
			
			List<?> list = tongjiRepository.list(sqlBuilder.sql());
			List<Map<String, String>> content = new ArrayList<>();
			for(Object item : list) {
				Object[] arr = (Object[])item;
				Map<String, String> map = new HashMap<String, String>();
				int idx = 0;
				for(String name:names) {
					map.put(name, ObjectUtils.toString(arr[idx++]));
				}
				content.add(map);
			}
			return Result.newSuccess(new PageObject<Map<String, String>>(10, content));
		}catch(Exception e) {
			logger.info("accountLogPage failed for "+userId);
			return Result.newException(e);
		}
	}
}
