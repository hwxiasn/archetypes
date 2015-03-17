package com.qingbo.ginkgo.ygb.account.service;

import java.math.BigDecimal;

import com.qingbo.ginkgo.ygb.common.result.Result;

/**
 * 支付服务
 * @author hongwei
 * @date 2014-12-11
 */
public interface PaymentService {
	/**
	 * @param tradeId 回调通知交易执行结果
	 * @param subAccountId 资金存入子账户
	 * @param balance 存款金额
	 * @return 跳转链接，错误信息
	 */
	Result<String> deposit(Long tradeId, Long subAccountId, BigDecimal balance);
	/**
	 * 提现会将BankType转换为乾多多支持的银行代码，会根据CardNo获取银行卡的省市信息传递给乾多多
	 * @param tradeId 回调通知交易执行结果
	 * @param subAccountId 资金提现子账户
	 * @param balance 提现金额
	 * @param BankCode 提现银行类型
	 * @param CardNo 提现银行卡号
	 * @return 提现链接，错误信息
	 */
	Result<String> withdraw(Long tradeId, Long subAccountId, BigDecimal balance, String BankCode, String CardNo);
	
	/**
	 * 乾多多转账冻结功能，先冻结子账户subAccountId的金额，并且这笔资金是打算要转账给otherSubAccountId的
	 * @param subAccountId 资金冻结子账户
	 * @param otherSubAccountId 资金预计转入子账户
	 * @param balance 转账冻结金额
	 * @return 转账冻结流水LoanNo，或错误消息
	 */
	Result<String> freeze(Long subAccountId, Long otherSubAccountId, BigDecimal balance);
	/**
	 * 审核转账冻结操作是否通过
	 * @param LoanNo 转账冻结流水
	 * @param auditPass true转账通过，资金会转入冻结时的otherSubAccountId子账户，false审核拒绝，资金依然在冻结时的subAccountId子账户
	 * @return 成功或失败
	 */
	Result<Boolean> unfreeze(String LoanNo, boolean auditPass);
	/**
	 * 直接转账汇款，用于分佣分润等
	 * @param subAccountId 转出子账户
	 * @param otherSubAccountId 转入子账户
	 * @param balance 转账金额
	 * @return 成功或失败
	 */
	Result<Boolean> transfer(Long subAccountId, Long otherSubAccountId, BigDecimal balance);
	
	/**
	 * 冻结资金余额
	 * @param subAccountId 冻结子账户
	 * @param balance 冻结金额
	 * @return 成功或失败
	 */
	Result<Boolean> freeze(Long subAccountId, BigDecimal balance);
	/**
	 * 解冻资金
	 * @param subAccountId 解冻子账户
	 * @param balance 解冻金额
	 * @return 成功或失败
	 */
	Result<Boolean> unfreeze(Long subAccountId, BigDecimal balance);
}
