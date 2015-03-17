package com.qingbo.ginkgo.ygb.trade.impl.inner;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.entity.TradeLog;
import com.qingbo.ginkgo.ygb.trade.entity.TradeSchedule;

public interface TradeListInner {
	
	/**
	 * 列表查询Trade服务，查询通配的实体信息
	 */
	Result<PageObject<Trade>> listTradeBySpecAndPage(SpecParam<Trade> spec, Pager page);


	/**
	 * 列表查询TradeLog服务，查询通配的实体信息
	 */
	Result<PageObject<TradeLog>> listTradeLogBySpecAndPage(SpecParam<TradeLog> spec, Pager page);

	/**
	 * 列表查询TradeSchedule服务，查询通配的实体信息
	 */
	Result<PageObject<TradeSchedule>> listTradeScheduleBySpecAndPage(SpecParam<TradeSchedule> spec, Pager page);

}
