package com.qingbo.ginkgo.ygb.account.repository;

import com.qingbo.ginkgo.ygb.account.entity.QddAccountLog;
import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;

public interface QddAccountLogRepository extends BaseRepository<QddAccountLog> {
	QddAccountLog findByTradeId(Long tradeId);
	QddAccountLog findByOrderNo(String orderNo);
	QddAccountLog findByLoanNo(String loanNo);
}
