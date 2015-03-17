package com.qingbo.ginkgo.ygb.trade.impl.inner;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.trade.entity.TradeSchedule;

public interface TradeScheduleInner {
	Result<TradeSchedule> schedule(TradeSchedule schedule) ;
}
