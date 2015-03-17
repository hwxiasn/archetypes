package com.qingbo.ginkgo.ygb.account.service;

import java.util.Map;

import com.qingbo.ginkgo.ygb.account.entity.AccountDaily;
import com.qingbo.ginkgo.ygb.common.result.Result;

public interface AccountDailyService {
	/** 获得用户某一天的账户状态 */
	Result<AccountDaily> findOne(Long userId, String daily);
	/** 获得一段时间内的账务变化数据 */
	Result<Map<String, AccountDaily>> findData(Long userId, String from, String to);
	/** 处理当天的账务数据，不重复处理 */
	Result<Boolean> handleDaily();
}
