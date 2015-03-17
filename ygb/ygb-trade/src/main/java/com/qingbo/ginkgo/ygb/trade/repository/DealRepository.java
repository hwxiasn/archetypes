package com.qingbo.ginkgo.ygb.trade.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.trade.entity.Deal;

public interface DealRepository extends BaseRepository<Deal> {
	
	List<Deal> findByTradeIdAndDeletedFalse(Long id);
}
