package com.qingbo.ginkgo.ygb.account.service;

import java.util.Map;

import com.qingbo.ginkgo.ygb.common.result.Result;

public interface QddAccountLogService {
	/** 检查回调结果 */
	Result<Boolean> checkNotify(Map<String, String> callbacks, String... orderedNames);
	/** 注册回调接口，前端执行checkNotify之后 */
	Result<Boolean> doRegister(Map<String, String> callbacks);
	/** 授权回调 */
	Result<Boolean> doAuthorise(Map<String, String> callbacks);
	/** 充值回调 */
	Result<Boolean> doRecharge(Map<String, String> callbacks);
	/** 审核回调 */
	Result<Boolean> doAudit(Map<String, String> callbacks);
	/** 转账回调 */
	Result<Boolean> doTransfer(Map<String, String> callbacks);
	/** 释放回调 */
	Result<Boolean> doRelease(Map<String, String> callbacks);
	/** 提现回调 */
	Result<Boolean> doWithdraw(Map<String, String> callbacks);
	/** 对账查询 */
	Result<Boolean> orderquery(Map<String, String> params);
}
