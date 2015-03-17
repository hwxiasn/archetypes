package com.qingbo.ginkgo.ygb.account.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qingbo.ginkgo.ygb.account.entity.Account;
import com.qingbo.ginkgo.ygb.account.entity.AccountLog;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.entity.SubQddAccount;
import com.qingbo.ginkgo.ygb.account.enums.AccountLogSubType;
import com.qingbo.ginkgo.ygb.account.enums.AccountLogType;
import com.qingbo.ginkgo.ygb.account.enums.SubAccountType;
import com.qingbo.ginkgo.ygb.account.payment.PaymentUtil;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QddConfig;
import com.qingbo.ginkgo.ygb.account.repository.AccountLogRepository;
import com.qingbo.ginkgo.ygb.account.repository.AccountRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubAccountRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubQddAccountRepository;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.base.util.PagerUtil;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.ServiceRequester;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.service.PasswordService;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private List<Map<String, String>> emptyList = new ArrayList<>();
	private ErrorMessage errors = new ErrorMessage("account-error.properties");
	private Pattern pwd = Pattern.compile("[a-zA-Z0-9!@#$%^&*\\(\\)_+-=`~\\[\\]\\{\\}\\\\|;\':\",.<>/?]{4,16}");
	
	@Autowired private TongjiService tongjiService;
	@Autowired private PasswordService passwordService;
	@Autowired private QueuingService queuingService;
	@Autowired private CustomerService customerService;
	
	@Autowired private AccountRepository accountRepository;
	@Autowired private SubAccountRepository subAccountRepository;
	@Autowired private AccountLogRepository accountLogRepository;
	@Autowired private SubQddAccountRepository subQddAccountRepository;

	@Override
	public Result<Account> getAccount(Long userId) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		
		Account account = accountRepository.findOne(userId);
		if(account == null) return errors.newFailure("ACT0002", userId);
		
		List<SubAccount> list = subAccountRepository.findByAccountId(userId);
		if(list==null || list.size()==0) {//create default sub account if it not exist
			createSubAccount(userId, SubAccountType.DEFAULT.getCode());
			list = subAccountRepository.findByAccountId(userId);
		}
		account.setSubAccounts(list);
		BigDecimal balance = BigDecimal.ZERO, freezeBalance = BigDecimal.ZERO;
		for(SubAccount subAccount : list) {
			balance = balance.add(subAccount.getBalance());
			freezeBalance = freezeBalance.add(subAccount.getFreezeBalance());
		}
		account.setBalance(balance);
		account.setFreezeBalance(freezeBalance);
		return Result.newSuccess(account);
	}

	@Override
	public Result<SubAccount> getSubAccount(Long userId) {
		List<SubAccount> list = subAccountRepository.findByAccountId(userId);
		if(list==null || list.size()==0) return errors.newFailure("ACT0004", userId, "");
		SubAccount subAccount = list.get(0);
		return Result.newSuccess(subAccount);
	}

	@Override
	public Result<SubAccount> getSubAccount(Long userId, String type) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		
		SubAccountType subAccountType = SubAccountType.getByCode(type);
		if(subAccountType==null) return errors.newFailure("ACT0003", type);
		
		SubAccount subAccount = subAccountRepository.findByAccountIdAndType(userId, type);
		if(subAccount == null) return errors.newFailure("ACT0004", userId, type);
		return Result.newSuccess(subAccount);
	}

	@Transactional
	public Result<Account> createAccount(Long userId) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		
		Account account = accountRepository.findOne(userId);
		if(account != null) {//账户已存在
			logger.info("createAccount already exist for "+userId);
			return getAccount(userId);
		}
		
		Result<User> userResult = customerService.getUserByUserId(userId);
		if(!userResult.success()) return errors.newFailure("ACT0001", userId);
		User user = userResult.getObject();
		String userName = user.getUserName();
		
		account = new Account();
		account.setId(userId);
		String generateSalt = passwordService.generateSalt();
		String initPaymentPassword = GinkgoConfig.getProperty("com.qingbo.ginkgo.ygb.account.impl.AccountServiceImpl.initPaymentPassword", "12345678");
		String encryptPassword = passwordService.encryptPassword(initPaymentPassword, userName, generateSalt);
		account.setSalt(generateSalt);
		account.setPassword(encryptPassword);
		account = accountRepository.save(account);
		
		Result<Boolean> isWbossUser = customerService.isWbossUser(userId);
		if(isWbossUser.hasObject() && isWbossUser.getObject()) {
			Map<String, String> paramsWithSecret = ServiceRequester.paramsWithSecret();
			paramsWithSecret.put("method", "account");
			Map<String, String> convert = ServiceRequester.convert(account);
			paramsWithSecret.putAll(convert);
			ServiceRequester.request(ServiceRequester.serviceWboss+"account", paramsWithSecret);
		}
		
		SubAccount subAccount = subAccountRepository.findByAccountIdAndType(userId, SubAccountType.DEFAULT.getCode());
		if(subAccount == null) {
			subAccount = new SubAccount();
//			subAccount.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
			subAccount.setId(userId);//user.id=account.id=sub_account.id=sub_qdd_account.id，默认子账户和用户ID一致
			subAccount.setAccountId(userId);
			subAccount.setType(SubAccountType.DEFAULT.getCode());
			subAccount = subAccountRepository.save(subAccount);
			
			if(isWbossUser.hasObject() && isWbossUser.getObject()) {
				Map<String, String> paramsWithSecret = ServiceRequester.paramsWithSecret();
				paramsWithSecret.put("method", "subAccount");
				Map<String, String> convert = ServiceRequester.convert(subAccount);
				paramsWithSecret.putAll(convert);
				ServiceRequester.request(ServiceRequester.serviceWboss+"account", paramsWithSecret);
			}
		}else {
			logger.info("createAccount subAccount of type DEFAULT already exist for "+userId+", it is sub_account.id="+subAccount.getId());
		}
		
		return getAccount(userId);
	}

	@Transactional
	public Result<SubAccount> createSubAccount(Long userId, String type) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		
		SubAccountType subAccountType = SubAccountType.getByCode(type);
		if(subAccountType==null) return errors.newFailure("ACT0003", type);
		
		SubAccount subAccount = subAccountRepository.findByAccountIdAndType(userId, type);
		if(subAccount != null) {
			logger.info("createAccount subAccount of type "+type+" already exist for "+userId);
			return Result.newSuccess(subAccount);
		}
		
		Result<User> userResult = customerService.getUserByUserId(userId);
		if(!userResult.success()) return errors.newFailure("ACT0001", userId);
		
		subAccount = new SubAccount();
		subAccount.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
		subAccount.setAccountId(userId);
		subAccount.setType(subAccountType.getCode());
		subAccount = subAccountRepository.save(subAccount);
		
		Result<Boolean> isWbossUser = customerService.isWbossUser(userId);
		if(isWbossUser.hasObject() && isWbossUser.getObject()) {
			Map<String, String> paramsWithSecret = ServiceRequester.paramsWithSecret();
			paramsWithSecret.put("method", "subAccount");
			Map<String, String> convert = ServiceRequester.convert(subAccount);
			paramsWithSecret.putAll(convert);
			ServiceRequester.request(ServiceRequester.serviceWboss+"account", paramsWithSecret);
		}
		
		if(subAccount.isQddAccount()) {
			SubQddAccount subQddAccount = subQddAccountRepository.findOne(subAccount.getId());
			if(subQddAccount==null) {
				QddConfig qddConfig = QddConfig.get(subAccount.getType());
				subQddAccount = new SubQddAccount();
//				subQddAccount.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
				subQddAccount.setId(subAccount.getId());
				subQddAccount.setPlatformId(qddConfig.PlatformMoneymoremore());
				subQddAccountRepository.save(subQddAccount);
			}
		}
		
		return getSubAccount(userId, type);
	}

	@Override
	public Result<Boolean> validatePassword(Long userId, String password) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		if(!pwd.matcher(password).matches()) return errors.newFailure("ACT0006", password);
		
		Result<User> userResult = customerService.getUserByUserId(userId);
		if(!userResult.success()) return errors.newFailure("ACT0001", userId);
		User user = userResult.getObject();
		String userName = user.getUserName();
		
		Account account = accountRepository.findOne(userId);
		if(account == null) return errors.newFailure("ACT0002", userId);
		
		if(StringUtils.isBlank(account.getSalt())) return Result.newSuccess(false);
		String encryptPassword = passwordService.encryptPassword(password, userName, account.getSalt());
		return Result.newSuccess(encryptPassword.equals(account.getPassword()));
	}

	@Transactional
	public Result<Boolean> updatePassword(Long userId, String oldPassword, String newPassword) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		if(!pwd.matcher(newPassword).matches()) return errors.newFailure("ACT0006", newPassword);
		
		Result<User> userResult = customerService.getUserByUserId(userId);
		if(!userResult.success()) return errors.newFailure("ACT0001", userId);
		User user = userResult.getObject();
		String userName = user.getUserName();
		
		Account account = accountRepository.findOne(userId);
		if(account == null) return errors.newFailure("ACT0002", userId);
		
		String encryptPassword = passwordService.encryptPassword(oldPassword, userName, account.getSalt());
		if(encryptPassword.equals(account.getPassword())) {
			String generateSalt = passwordService.generateSalt();
			encryptPassword = passwordService.encryptPassword(newPassword, userName, generateSalt);
			account.setSalt(generateSalt);
			account.setPassword(encryptPassword);
			accountRepository.save(account);
			return Result.newSuccess(true);
		}else {
			return errors.newFailure("ACT0004");
		}
	}

	@Transactional
	public Result<Boolean> resetPassword(Long userId, String password) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		if(!pwd.matcher(password).matches()) return errors.newFailure("ACT0006", password);
		
		Result<User> userResult = customerService.getUserByUserId(userId);
		if(!userResult.success()) return errors.newFailure("ACT0001", userId);
		User user = userResult.getObject();
		String userName = user.getUserName();
		
		Account account = accountRepository.findOne(userId);
		if(account == null) return errors.newFailure("ACT0002", userId);
		
		String generateSalt = passwordService.generateSalt();
		String encryptPassword = passwordService.encryptPassword(password, userName, generateSalt);
		account.setSalt(generateSalt);
		account.setPassword(encryptPassword);
		accountRepository.save(account);
		return Result.newSuccess(true);
	}

	@Override
	public Result<PageObject<Map<String, String>>> depositPage(Long userId, Pager pager) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		
		SqlBuilder sqlBuilder = new SqlBuilder("count(log.id)", "account_log log left join sub_account sub on log.sub_account_id=sub.id left join account ac on sub.account_id=ac.id");
		sqlBuilder.eq("log.deleted", "0");
		sqlBuilder.eq("log.executed", "1");
		sqlBuilder.eq("ac.id", userId.toString());
		sqlBuilder.eq("log.type", AccountLogType.IN.getCode());
		sqlBuilder.eq("log.sub_type", AccountLogSubType.DEPOSIT.getCode());
		sqlBuilder.limit(PagerUtil.limit(pager));
		
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(new PageObject<Map<String, String>>(0, emptyList));
		
		String[] names = {"accountId", "subAccountId", "accountLogId", "balance", "accountBalance"};//names与select次序一致
		sqlBuilder.select("ac.id accountId,sub.id subAccountId,log.id accountLogId,log.balance balance,log.account_balance accountBalance");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
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
		return Result.newSuccess(new PageObject<Map<String, String>>(count, content));
	}

	@Override
	public Result<PageObject<Map<String, String>>> withdrawPage(Long userId, Pager pager) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		
		SqlBuilder sqlBuilder = new SqlBuilder("count(log.id)", "account_log log left join sub_account sub on log.sub_account_id=sub.id left join account ac on sub.account_id=ac.id" +
				" left join sub_account sub2 on log.fee_sub_account_id=sub2.id left join account ac2 on sub2.account_id=ac2.id");
		sqlBuilder.eq("log.deleted", "0");
		sqlBuilder.eq("log.executed", "1");
		sqlBuilder.eq("ac.id", userId.toString());
		sqlBuilder.eq("log.type", AccountLogType.OUT.getCode());
		sqlBuilder.eq("log.sub_type", AccountLogSubType.WITHDRAW.getCode());
		sqlBuilder.limit(PagerUtil.limit(pager));
		
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(new PageObject<Map<String, String>>(0, emptyList));
		
		String[] names = {"accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "fee", "feeAccountId", "feeSubAccountId"};//names与select次序一致
		sqlBuilder.select("ac.id accountId,sub.id subAccountId,log.id accountLogId,log.balance balance,log.account_balance accountBalance,log.fee fee,ac2.id feeAccountId,sub2.id feeSubAccountId");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
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
		return Result.newSuccess(new PageObject<Map<String, String>>(count, content));
	}

	@Override
	public Result<PageObject<Map<String, String>>> transferPage(Long userId, Pager pager) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		
		SqlBuilder sqlBuilder = new SqlBuilder("count(log.id)", "account_log log left join sub_account sub on log.sub_account_id=sub.id left join account ac on sub.account_id=ac.id" +
				" left join sub_account sub2 on log.other_sub_account_id=sub2.id left join account ac2 on sub2.account_id=ac2.id");
		sqlBuilder.eq("log.deleted", "0");
		sqlBuilder.eq("log.executed", "1");
		sqlBuilder.eq("ac.id", userId.toString());
		sqlBuilder.in("log.type", AccountLogType.IN.getCode(), AccountLogType.OUT.getCode());
		sqlBuilder.eq("log.sub_type", AccountLogSubType.TRANSFER.getCode());
		sqlBuilder.limit(PagerUtil.limit(pager));
		
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(new PageObject<Map<String, String>>(0, emptyList));
		
		String[] names = {"accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "transferType", "otherAccountId", "otherSubAccountId"};//names与select次序一致
		sqlBuilder.select("ac.id accountId,sub.id subAccountId,log.id accountLogId,log.balance balance,log.account_balance accountBalance,log.transfer_type,ac2.id otherAccountId,sub2.id otherSubAccountId");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
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
		return Result.newSuccess(new PageObject<Map<String, String>>(count, content));
	}

	@Override
	public Result<PageObject<Map<String, String>>> accountLogPage(Long userId, Pager pager) {
		if(userId == null || userId < 1) return errors.newFailure("ACT0001", userId);
		
		SqlBuilder sqlBuilder = new SqlBuilder("count(log.id)", "account_log log left join sub_account sub on log.sub_account_id=sub.id left join account ac on sub.account_id=ac.id" +
				" left join sub_account sub2 on log.fee_sub_account_id=sub2.id left join account ac2 on sub2.account_id=ac2.id" +
				" left join sub_account sub3 on log.other_sub_account_id=sub3.id left join account ac3 on sub3.account_id=ac3.id");
		sqlBuilder.eq("log.deleted", "0");
		sqlBuilder.eq("log.executed", "1");
		sqlBuilder.eq("ac.id", userId.toString());
		sqlBuilder.notIn("log.type", AccountLogType.FREEZE.getCode(), AccountLogType.UNFREEZE.getCode());
		sqlBuilder.limit(PagerUtil.limit(pager));
		
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(new PageObject<Map<String, String>>(0, emptyList));
		
		String[] names = {"accountId", "subAccountId", "accountLogId", "balance", "accountBalance","transferType", "fee", "feeAccountId", "feeSubAccountId", "otherAccountId", "otherSubAccountId"};//names与select次序一致
		sqlBuilder.select("ac.id acId,sub.id subId,log.id logId,log.balance,log.account_balance,log.transfer_type,log.fee,ac2.id feeAccountId,sub2.id feeSubAccountId,ac3.id otherAccountId,sub3.id otherSubAccountId");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
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
		return Result.newSuccess(new PageObject<Map<String, String>>(count, content));
	}

	@Override
	public Result<AccountLog> accountLog(Long accountLogId) {
		AccountLog findOne = accountLogRepository.findOne(accountLogId);
		return Result.newSuccess(findOne);
	}
	
}
