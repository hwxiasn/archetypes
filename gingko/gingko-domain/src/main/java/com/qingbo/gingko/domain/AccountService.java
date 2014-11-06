package com.qingbo.gingko.domain;

import java.util.Map;

import com.qingbo.gingko.common.result.PageObject;
import com.qingbo.gingko.common.result.Result;
import com.qingbo.gingko.common.util.Pager;
import com.qingbo.gingko.entity.Account;
import com.qingbo.gingko.entity.SubAccount;

/**
 * 账户服务
 * @author hongwei
 */
public interface AccountService {
	/**
	 * 获取账户和子账户信息
	 */
	Result<Account> getAccount(Integer userId);
	/**
	 * 获取子账户信息
	 * @param type {@link com.qingbo.ginkgo.entity.enums.SubAccountType SubAccountType.DEFAULT.getCode()}
	 */
	Result<SubAccount> getSubAccount(Integer userId, String type);
	/**
	 * 创建账户和默认子账户
	 */
	Result<Account> createAccount(Integer userId);
	/**
	 * 创建子账户
	 * @param type {@link com.qingbo.ginkgo.entity.enums.SubAccountType SubAccountType.DEFAULT.getCode()}
	 */
	Result<SubAccount> createSubAccount(Integer userId, String type);
	
	/**
	 * 校验账户密码
	 */
	Result<Boolean> validatePassword(Integer userId, String password);
	/**
	 * 更新账户密码
	 */
	Result<Boolean> updatePassword(Integer userId, String oldPassword, String newPassword);
	/**
	 * 重置账户密码
	 */
	Result<Boolean> resetPassword(Integer userId, String password);
	
	/**
	 * 用户充值记录，键值有："accountId", "subAccountId", "accountLogId", "balance", "accountBalance"
	 */
	Result<PageObject<Map<String, String>>> depositPage(Integer userId, Pager pager);
	/**
	 * 用户提现记录，键值有："accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "fee", "feeAccountId", "feeSubAccountId"
	 */
	Result<PageObject<Map<String, String>>> withdrawPage(Integer userId, Pager pager);
	/**
	 * 用户转账记录，涉及到转账双方，"accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "otherAccountId", "otherSubAccountId"
	 */
	Result<PageObject<Map<String, String>>> transferPage(Integer userId, Pager pager);
	/**
	 * 用户账户变化记录，显示用户账户的详细变化记录，"accountId", "subAccountId", "accountLogId", "balance", "accountBalance", "fee", "feeAccountId", "feeSubAccountId", "otherAccountId", "otherSubAccountId"
	 */
	Result<PageObject<Map<String, String>>> accountLogPage(Integer userId, Pager pager);
}
