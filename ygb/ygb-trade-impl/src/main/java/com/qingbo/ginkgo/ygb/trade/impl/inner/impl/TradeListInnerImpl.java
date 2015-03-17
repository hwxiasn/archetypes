package com.qingbo.ginkgo.ygb.trade.impl.inner.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.entity.TradeLog;
import com.qingbo.ginkgo.ygb.trade.entity.TradeSchedule;
import com.qingbo.ginkgo.ygb.trade.impl.inner.TradeListInner;
import com.qingbo.ginkgo.ygb.trade.impl.util.TradeError;
import com.qingbo.ginkgo.ygb.trade.repository.TradeLogRepository;
import com.qingbo.ginkgo.ygb.trade.repository.TradeRepository;
import com.qingbo.ginkgo.ygb.trade.repository.TradeScheduleRepository;

@Service("tradeListInner")
public class TradeListInnerImpl implements TradeListInner {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private TradeRepository trade;
	@Resource private TradeLogRepository tradeLog;
	@Resource private TradeScheduleRepository tradeSchedule;

	public Result<PageObject<Trade>> listTradeBySpecAndPage(SpecParam<Trade> spec, Pager page) {
		if(spec == null || page == null){
			return TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
		try{			
			spec.eq("deleted", false);// 未删除
			Page<Trade> resultSet = trade.findAll(SpecUtil.spec(spec), page(page));
			Result<PageObject<Trade>> result = Result.newSuccess(new PageObject<Trade>((int)resultSet.getTotalElements(), resultSet.getContent()));;
			return result;
		}catch(Exception e){
			logger.error(SimpleLogFormater.formatParams(spec, page)+"\t"+SimpleLogFormater.formatException(e.getMessage(), e));
			return TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
	}

	public Result<PageObject<TradeLog>> listTradeLogBySpecAndPage(SpecParam<TradeLog> spec, Pager page) {
		if(spec == null || page == null){
			return TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
		try{			
			spec.eq("deleted", false);// 未删除
			Page<TradeLog> resultSet = tradeLog.findAll(SpecUtil.spec(spec), page(page));
			Result<PageObject<TradeLog>> result = Result.newSuccess(new PageObject<TradeLog>((int)resultSet.getTotalElements(), resultSet.getContent()));;
			return result;
		}catch(Exception e){
			logger.error(SimpleLogFormater.formatParams(spec, page)+"\t"+SimpleLogFormater.formatException(e.getMessage(), e));
			return TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
	}

	public Result<PageObject<TradeSchedule>> listTradeScheduleBySpecAndPage(SpecParam<TradeSchedule> spec, Pager page) {
		if(spec == null || page == null){
			return TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
		try{			
			spec.eq("deleted", false);// 未删除
			Page<TradeSchedule> resultSet = tradeSchedule.findAll(SpecUtil.spec(spec), page(page));
			Result<PageObject<TradeSchedule>> result = Result.newSuccess(new PageObject<TradeSchedule>((int)resultSet.getTotalElements(), resultSet.getContent()));;
			return result;
		}catch(Exception e){
			logger.error(SimpleLogFormater.formatParams(spec, page)+"\t"+SimpleLogFormater.formatException(e.getMessage(), e));
			return TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
	}
	
	private Pageable page(Pager page){
		return page.getDirection() == null || page.getProperties() == null ? 
				new PageRequest(page.getCurrentPage() - 1, page.getPageSize()) :
				new PageRequest(page.getCurrentPage() - 1, page.getPageSize(),Direction.valueOf(page.getDirection()),page.getProperties().split(","));
	}

}
