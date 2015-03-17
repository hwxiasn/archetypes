package com.qingbo.ginkgo.ygb.trade.impl.inner.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.trade.entity.Deal;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.entity.TradeLog;
import com.qingbo.ginkgo.ygb.trade.entity.TradeSchedule;
import com.qingbo.ginkgo.ygb.trade.impl.inner.TradeEntityInner;
import com.qingbo.ginkgo.ygb.trade.impl.util.TradeError;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.trade.repository.DealRepository;
import com.qingbo.ginkgo.ygb.trade.repository.TradeLogRepository;
import com.qingbo.ginkgo.ygb.trade.repository.TradeRepository;
import com.qingbo.ginkgo.ygb.trade.repository.TradeScheduleRepository;

@Service("tradeEntityInner")
public class TradeEntityInnerImpl implements TradeEntityInner {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource private TradeRepository trade;
	@Resource private TradeLogRepository tradeLog;
	@Resource private TradeScheduleRepository tradeSchedule;
	@Resource private QueuingService queuingService;
	@Resource private DealRepository dealRepository;
	

	@SuppressWarnings("unchecked")
	public <T> Result<T> save(T t) {
		if(t == null){
			return TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
		try {
			Result<Long> queuing = queuingService.next(TradeConstants.TRADE_QUEUING);
			if(queuing.getError() != null ){
				return TradeError.getMessage().newFailure(queuing.getError(), queuing.getMessage());
			}
			if (t instanceof Trade) {
				Trade tr = (Trade) t;
				if(tr.getId() == null){
					tr.setId(queuing.getObject());
				}
				Trade trSave = trade.save(tr);
				return Result.newSuccess((T) trSave);
			} else if (t instanceof TradeLog) {
				TradeLog tr = (TradeLog) t;
				if(tr.getId() == null){
					tr.setId(queuing.getObject());
				}
				TradeLog trSave = tradeLog.save(tr);
				return Result.newSuccess((T) trSave);
			} else if (t instanceof TradeSchedule) {
				TradeSchedule tr = (TradeSchedule) t;
				if(tr.getId() == null){
					tr.setId(queuing.getObject());
				}
				TradeSchedule trSave = tradeSchedule.save(tr);
				return Result.newSuccess((T) trSave);
			}else if (t instanceof Deal) {
				Deal tr = (Deal) t;
				if(tr.getId() == null){
					tr.setId(queuing.getObject());
				}
				Deal trSave = dealRepository.save(tr);
				return Result.newSuccess((T) trSave);
			}
		} catch (Exception e) {
			logger.info("Create/Update DB Error.Object:"+t);
//			logger.error(SimpleLogFormater.formatException(e.getMessage(), e));
		}
		return TradeError.getMessage().newFailure(TradeError.TradeSys0002);
	}
	

}
