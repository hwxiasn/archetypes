package com.qingbo.ginkgo.ygb.account.inner;

import com.qingbo.ginkgo.ygb.account.payment.qdd.QDDPaymentResponse;
import com.qingbo.ginkgo.ygb.common.result.Result;

/**
 * 乾多多支付接口
 */
public interface QddPaymentService {
	/**
	 * 跳转充值
	 */
	Result<String> loanRecharge(Long qddAccountLogId);
	/**
	 * 转账（有冻结转账和直接到帐两种）
	 * @param qddAccountLogId 转账交易
	 * @param freeze true 冻结转账 false 直接到帐
	 */
	Result<QDDPaymentResponse> loanTransfer(Long qddAccountLogId, boolean freeze);
	/**
	 * 无密码审核（联系乾多多技术人员开启）
	 * @param freezeQddAccountLogId 投资冻结交易
	 * @param qddAccountLogId 审核交易
	 * @param auditPass 审核通过或拒绝
	 */
	Result<QDDPaymentResponse> loanAudit(String loanNo, boolean auditPass);
	/**
	 * 跳转提现
	 */
	Result<String> loanWithdraw(Long qddAccountLogId);
}
