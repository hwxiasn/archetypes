package com.qingbo.ginkgo.ygb.trade.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.trade.entity.Deal;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.impl.inner.TradeEntityInner;
import com.qingbo.ginkgo.ygb.trade.impl.inner.TradeExecuteInner;
import com.qingbo.ginkgo.ygb.trade.impl.inner.TradeListInner;
import com.qingbo.ginkgo.ygb.trade.impl.inner.TradeScheduleInner;
import com.qingbo.ginkgo.ygb.trade.impl.util.TradeError;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.trade.repository.TradeRepository;
import com.qingbo.ginkgo.ygb.trade.service.DealService;
import com.qingbo.ginkgo.ygb.trade.service.TradeService;

@Service("tradeService")
public class TradeServiceImpl implements TradeService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource private TradeExecuteInner tradeExecuteInner;
	@Resource private TradeListInner tradeListInner;
	@Resource private TradeScheduleInner tradeScheduleInner;	
	@Resource private TradeRepository tradeRepository;
	@Resource private QueuingService queuingService;
	@Resource private TradeEntityInner tradeEntityInner;
	@Resource private DealService dealService;

	public Result<Trade> getTrade(Long id) {
		if(id == null ){
			return  TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
		try{
			Trade tr = tradeRepository.findOne(id);
			return Result.newSuccess(tr);
		}catch(Exception e){
			logger.info("Trade Detail Exception.Id:"+id);
			logger.debug("Trade Detail Exception", e);
		}
		return  TradeError.getMessage().newFailure(TradeError.TradeSys0002);
	}
	
	public Result<Trade> createTrade(Trade trade) {
		if(trade == null){
			return  TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
		logger.info("Trade CreateTrade CreditAccount:"+trade.getCreditAccount()+" DebitAccount:" + trade.getDebitAccount() 
				+" Type:"+trade.getTradeType()+" Amount:" + trade.getTradeAmount() 
				+" SubjectId:"+ trade.getTradeSubjectId()+" Kind:"+trade.getTradeKind());
		try{
			//如果交易目标ID不为空且有目标号码，且交易类型为投资
			if(trade.getTradeSubjectId() != null && !"".equals(trade.getTradeSubjectId()) 
					&& trade.getTradeType() != null && TradeConstants.TradeType.INVEST.getCode().equalsIgnoreCase(trade.getTradeType())){
				SpecParam<Trade> spec = new SpecParam<Trade>();
				spec.eq("tradeSubjectId", trade.getTradeSubjectId());
				Pager pager = new Pager();
				pager.init(1);
				pager.setPageSize(1);
				pager.page(0);
				Result<PageObject<Trade>> resultObject = this.listTradeBySpecAndPage(spec, pager);
				logger.info("CreateTrade Check Repeat Result:"+SimpleLogFormater.formatResult(resultObject));
				if(resultObject.success() && resultObject.hasObject()){
					if(resultObject.getObject().getList() !=null && resultObject.getObject().getList().size()>0){
						return Result.newSuccess(resultObject.getObject().getList().get(0));
					}
				}
			}
		}catch(Exception e){
			logger.info("CreateTrade Check Repeat Error.By:"+e.getMessage());
		}
		try{
			//获取批次号
			Result<Long> queuing = queuingService.next(TradeConstants.TRADE_QUEUING);
			if(queuing.getError() != null ){
				return TradeError.getMessage().newFailure(queuing.getError(), queuing.getMessage());
			}
			trade.setBatchId(queuing.getObject());
			Result<Trade> save = tradeEntityInner.save(trade);
			logger.info("TradeService CreateTrade Save Trade:"+SimpleLogFormater.formatResult(save));
			//入库过程报错则返回错误信息并返回
			if(!save.success()){
				return save;
			}
	        Result<Trade>  resultList = dealService.execute(save.getObject());
	        logger.info(SimpleLogFormater.formatResult(resultList));
			if(!resultList.success()){
				return Result.newFailure(resultList.getError(), resultList.getMessage());
			}
			return Result.newSuccess(save.getObject());
		}catch(Exception e){
			logger.info("Trade Create Exception");
			logger.debug("Trade Create Exception",e);
		}
		return Result.newFailure("", "Create Trade Fail.");
	}

	public Result<Trade> updateTrade(Trade trade) {
//		Trade oldTrade = tradeRepository.findOne(trade.getId());
//		if(trade.getTradeStatus() != null && !"".equals(trade.getTradeStatus())){
//			oldTrade.setTradeStatus(trade.getTradeStatus());
//		}
//		if(trade.getDealDate() != null ){
//			oldTrade.setTradeStatus(trade.getTradeStatus());
//		}
//		if(trade.getTradeStatus() != null && !"".equals(trade.getTradeStatus())){
//			oldTrade.setTradeStatus(trade.getTradeStatus());
//		}
		try{
			Result<Trade> save = tradeEntityInner.save(trade);
			logger.info(SimpleLogFormater.formatResult(save));
			//入库过程报错则返回错误信息并返回
			if(!save.success()){
				return save;
			}
	        Result<Trade>  resultList = dealService.execute(trade);
			logger.info(SimpleLogFormater.formatResult(resultList));
			if(!resultList.success()){
				return Result.newFailure(resultList.getError(), resultList.getMessage());
			}
			return Result.newSuccess(trade);
		}catch(Exception e){
			logger.info("Trade updateTrade Exception.ID:"+trade.getId());
			logger.debug("Trade updateTrade Exception",e);
		}
		return Result.newFailure("", "Update Trade Fail.");
	}

	public Result<PageObject<Trade>> listTradeBySpecAndPage(SpecParam<Trade> spec, Pager page) {
		try{
			if(spec == null || page == null){
				return TradeError.getMessage().newFailure(TradeError.TradeSys1001);
			}
			logger.info(SimpleLogFormater.formatParams(spec, page));
			Result<PageObject<Trade>> result = tradeListInner.listTradeBySpecAndPage(spec, page);
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}catch(Exception e){
			logger.info("Trade listTradeBySpecAndPage Exception");
			logger.debug("Trade listTradeBySpecAndPage Exception",e);
		}
		return Result.newFailure("", "List Trade Fail.");
	}
	
	public Result<List<Trade>> createTrades(List<Trade> trades) {
		List<Trade> list = new ArrayList<Trade>();
		for(Trade tr:trades){
			Result<Trade> result = this.createTrade(tr);
			list.add(result.getObject());
		}
		return Result.newSuccess(list);
	}


	public Result<String> depositTrade(Trade trade) {
		if(trade == null){
			return  TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
		try{
			logger.info("depositTrade Info:"+trade.getCreditAccount());
			Result<Trade> create = this.createTrade(trade);
			if(!create.success()){
				Result.newFailure(create.getError(), create.getMessage());
			}
			SpecParam<Deal> spec = new SpecParam<Deal>();
			spec.eq("tradeId", create.getObject().getId());
			spec.eq("tradeType", TradeConstants.TradeType.DEPOSIT.getCode());
			spec.eq("creditAccount", trade.getCreditAccount());
			spec.eq("debitAccount", trade.getDebitAccount());
			Result<List<Deal>> resultList = dealService.listAll(spec);
			if (resultList.success()) {
				if(resultList.getObject().size()<1){
					return Result.newSuccess("");
				}else{
					return Result.newSuccess(resultList.getObject().get(0).getTradeDispatch());
				}
			}
			return Result.newFailure(resultList.getError(), resultList.getMessage());
		}catch(Exception e){
			logger.info("Trade depositTrade Exception");
			logger.debug("Trade depositTrade Exception",e);
		}
		return Result.newFailure("", "depositTrade fail.");
	}


	public Result<String> withdrawTrade(Trade trade) {
		if(trade == null){
			return  TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
		try{
			logger.info("withdrawTrade Info:"+trade.getCreditAccount());
			Result<Trade> create = this.createTrade(trade);
			logger.info("withdrawTrade CreateTrade Result:"+SimpleLogFormater.formatResult(create));
			if(!create.success() || !create.hasObject()){
				return Result.newFailure(create.getError(), create.getMessage());
			}
			if(create.getObject().getId() == null){
				logger.info("withdrawTrade CreateTrade Result: id is null "+trade.getCreditAccount());
				return Result.newFailure("", "Create Trade Fail.");
			}
			SpecParam<Deal> spec = new SpecParam<Deal>();
			spec.eq("tradeId", create.getObject().getId());
			spec.eq("tradeType", TradeConstants.TradeType.WITHDRAW.getCode());
			spec.eq("creditAccount", trade.getCreditAccount());
			spec.eq("debitAccount", trade.getDebitAccount());
			Result<List<Deal>> resultList = dealService.listAll(spec);
			logger.info(SimpleLogFormater.formatResult(resultList));
			if (resultList.success() && resultList.hasObject() && resultList.getObject().size()>0) {
				return Result.newSuccess(resultList.getObject().get(0).getTradeDispatch());
			}
			return Result.newFailure(resultList.getError(), resultList.getMessage());		
		}catch(Exception e){
			logger.info("Trade withdrawTrade Exception");
			logger.debug("Trade withdrawTrade Exception",e);
		}
		return Result.newFailure("", "withdrawTrade fail.");
	}


}
