package com.qingbo.ginkgo.ygb.account.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.account.entity.AccountLog;
import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;

public interface AccountLogRepository extends BaseRepository<AccountLog> {
	List<AccountLog> findByTypeAndTradeIdAndDeletedFalse(String type, Long tradeId);
	AccountLog findByTypeAndSubAccountLogId(String type, Long subAccountLogId);
}
