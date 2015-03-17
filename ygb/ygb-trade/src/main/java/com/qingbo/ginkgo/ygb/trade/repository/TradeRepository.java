package com.qingbo.ginkgo.ygb.trade.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;

public interface TradeRepository extends BaseRepository<Trade> {

	List<Trade> findByParentTradeIdAndDeletedFalse(Long id);
}
