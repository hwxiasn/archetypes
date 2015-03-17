package com.qingbo.ginkgo.ygb.account.inner;

import com.qingbo.ginkgo.ygb.common.result.Result;

/**
 * 执行账务日志，操作账户金额
 * @author hongwei
 * @date 2014-12-12
 */
public interface AccountDoService {
	Result<Boolean> execute(Long accountLogId);
}
