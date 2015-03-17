package com.qingbo.ginkgo.ygb.account.service;

import java.util.Map;

import com.qingbo.ginkgo.ygb.account.entity.Account;
import com.qingbo.ginkgo.ygb.account.entity.AccountLog;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;

/**
 * 账户服务
 * @author hongwei
 */
public interface AccountService {
	/**
	 * 获取账户和子账户信息
	 */
	Result<Account> getAccount(Long userId);
	/** 获取默认子账户 */
	Result<SubAccount> getSubAccount(Long userId);
	/**
	 * 创建账户和默认子账户
	 */
	Result<Account> createAccount(Long userId);
	/**
	 * 获取子账户信息
	 * @param type {@link com.qingbo.ginkgo.ygb.entity.enums.SubAccountType SubAccountType.DEFAULT.getCode()}
	 */
	Result<SubAccount> getSubAccount(Long userId, String type);
	/**
	 * 创建子账户
	 * @param type {@link com.qingbo.ginkgo.ygb.entity.enums.SubAccountType SubAccountType.DEFAULT.getCode()}
	 */
	Result<SubAccount> createSubAccount(Long userId, String type);
	/**
	 * 校验账户密码
	 */
	Result<Boolean> validatePassword(Long userId, String password);
	/**
	 * 更新账户密码
	 */
	Result<Boolean> updatePassword(Long userId, String oldPassword, String newPassword);
	/**
	 * 重置账户密码
	 */
	Result<Boolean> resetPassword(Long userId, String password);
	
	/**
	 * 用户充值记录，键值有："accountId", "subAccountId", "accountLogId", "balance", "accountBalance"
	 */
	Result<PageObject<Map<String, String>>> depositPage(Long userId, Pager pager);
	/**
	 * 用户提现记录，键值有："accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "fee", "feeAccountId", "feeSubAccountId"
	 */
	Result<PageObject<Map<String, String>>> withdrawPage(Long userId, Pager pager);
	/**
	 * 用户转账记录，涉及到转账双方，"accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "otherAccountId", "otherSubAccountId"
	 */
	Result<PageObject<Map<String, String>>> transferPage(Long userId, Pager pager);
	/**
	 * 用户账户变化记录，显示用户账户的详细变化记录，"accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "fee", "feeAccountId", "feeSubAccountId", "otherAccountId", "otherSubAccountId"
	 */
	Result<PageObject<Map<String, String>>> accountLogPage(Long userId, Pager pager);
	
	Result<AccountLog> accountLog(Long accountLogId);
}
