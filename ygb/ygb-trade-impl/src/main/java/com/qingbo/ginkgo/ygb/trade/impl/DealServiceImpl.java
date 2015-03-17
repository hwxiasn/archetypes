package com.qingbo.ginkgo.ygb.trade.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.trade.entity.Deal;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.impl.inner.TradeExecuteInner;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.trade.repository.DealRepository;
import com.qingbo.ginkgo.ygb.trade.repository.TradeRepository;
import com.qingbo.ginkgo.ygb.trade.service.DealService;

@Service("dealService")
public class DealServiceImpl implements DealService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private TradeExecuteInner tradeExecuteInner;
	@Resource private DealRepository dealRepository;
	@Resource private TradeRepository tradeRepository;

	public Result<Trade> execute(Trade trade) {
		try{
			// 转换输入的交易为交易执行，并驱动执行
			List<Deal> singleTradeDeal = this.transferDeal(trade,null);
			logger.info("DealService Build Trade to Deal,List Size:"+singleTradeDeal.size());
			if(singleTradeDeal != null && singleTradeDeal.size()>0){
				Result<List<Deal>> resultList = tradeExecuteInner.execute(singleTradeDeal, null);
				logger.info("DealService execute Trade "+SimpleLogFormater.formatResult(resultList));
				if(!resultList.success()){
					Result result = Result.newFailure(resultList.getError(),resultList.getMessage());
					result.setObject(trade);
					return result;
				}
			}
			return Result.newSuccess(trade);
		}catch(Exception e){
			logger.info("DealService Execute Trade Exception");
			logger.debug("DealService Execute Trade Exception",e);
		}
		return Result.newFailure("", "DealService Execute Trade Fail.");
	}
	
	
	/**
	 * 将交易转换为交易执行 交易定义： 符合本系统定义的交易为：
	 * 投资父交易：融资人获得投资的一笔虚拟交易，由多个投资人向一个接收人发起投资，不处理；
	 * 状态机变化：
	 * 父交易 待成立 ，子交易 成立中；父交易 成立中，子交易 已成立；
	 * 父交易 已成立 ，子交易 已成立；父交易 成立失败，子交易 成立失败；
	 * 投资子交易：投资人做出的投资交易，需要立即冻结该投资人资金到接收人，有一条冻结记录 
	 * 投资单笔交易：目前未有
	 * 还款父交易：融资人偿还融资的交易，一对一交易，转入平台中间账户，系统需处理 
	 * 还款子交易：目前未有 
	 * 还款单笔交易：目前未有 
	 * 分佣父交易：目前未有
	 * 分佣子交易：目前未有 
	 * 分佣单笔交易：由平台中间账户向经营人员分佣金 
	 * 充值父交易：目前未有 
	 * 充值子交易：目前未有 
	 * 充值单笔交易：由用户向自己账户存款
	 * 提现父交易：目前未有 
	 * 提现子交易：目前未有 
	 * 提现单笔交易：由用户从自己账户转账到银行账户
	 * 转账父交易：目前未有
	 * 转账子交易：目前未有
	 * 转账单笔交易：由单个用户向另外用户账户转账 
	 * 分润父交易：目前未有 
	 * 分润子交易：目前未有 
	 * 分润单笔交易：由平台中间账户向投资人账户转投资收益及本金
	 */
	private List<Deal> transferDeal(Trade trade,Trade father) {
		List<Deal> list = new ArrayList<Deal>();
		Deal deal = new Deal();
		deal.setBatchId(trade.getBatchId());
		deal.setTradeId(trade.getId());
		deal.setCreditAccount(trade.getCreditAccount());
		deal.setDebitAccount(trade.getDebitAccount());
		deal.setTradeAmount(trade.getTradeAmount());
		deal.setTradeType(trade.getTradeType());
		deal.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());
		
		/** 投资 */
		if (TradeConstants.TradeType.INVEST.getCode().equalsIgnoreCase(trade.getTradeType())) {
			// 子交易
			if (TradeConstants.TradeKind.CHILDREN.getCode().equalsIgnoreCase(trade.getTradeKind())) {
				//获得父交易情况
				Trade parent =father == null ? tradeRepository.findOne(trade.getParentTradeId()):father;
				
				if(TradeConstants.TradeStatusType.PENDING.getCode().equalsIgnoreCase(trade.getTradeStatus())){
					deal.setTradeAction(TradeConstants.TradeActionType.FREEZE.getCode());
				}else if(TradeConstants.TradeStatusType.EXECUTING.getCode().equalsIgnoreCase(trade.getTradeStatus())){
					//投资交易为成立中，则必定有冻结交易
					List<Deal> source = dealRepository.findByTradeIdAndDeletedFalse(trade.getId());
					//新的交易增加前关联交易的ID
					deal.setSourceDealId(source.get(0).getId());
					//写入前关联交易的支付接口的序列号
					deal.setTradeParam(source.get(0).getTradeParam());
					if(TradeConstants.TradeStatusType.EXECUTING.getCode().equalsIgnoreCase(parent.getTradeStatus())){
						//父交易为成立中，则子交易转账
						deal.setTradeAction(TradeConstants.TradeActionType.TRANSFER.getCode());
					}else if(TradeConstants.TradeStatusType.FAILURE.getCode().equalsIgnoreCase(parent.getTradeStatus())){
						//父交易为成立失败，则子交易为解冻回退
						deal.setTradeAction(TradeConstants.TradeActionType.UNFREEZE.getCode());
					} 
				}else if(TradeConstants.TradeStatusType.EXECUTED.getCode().equalsIgnoreCase(trade.getTradeStatus())){
					//最终操作结果，无操作
				}else if(TradeConstants.TradeStatusType.FAILURE.getCode().equalsIgnoreCase(trade.getTradeStatus())){
					//最终操作结果，无操作
				}
			}else if (TradeConstants.TradeKind.PARENT.getCode().equalsIgnoreCase(trade.getTradeKind())) {
				List<Trade> listTrade = tradeRepository.findByParentTradeIdAndDeletedFalse(trade.getId());
				List<Deal> listDeal = new ArrayList<Deal>();
				for(Trade tr:listTrade){
					//	父交易成立中或者失败 ，成立中的交易状态转为成立通过定时JOB来判定
					if(TradeConstants.TradeStatusType.EXECUTING.getCode().equalsIgnoreCase(trade.getTradeStatus())
							||TradeConstants.TradeStatusType.FAILURE.getCode().equalsIgnoreCase(trade.getTradeStatus())){
							tr.setTradeStatus(TradeConstants.TradeStatusType.EXECUTING.getCode());
							List<Deal> deals = this.transferDeal(tr,trade);
							listDeal.addAll(deals);
					}
				}
				return listDeal;
			}
			
			
			/** 还款 */
		} else if (TradeConstants.TradeType.REPAY.getCode().equalsIgnoreCase(trade.getTradeType())) {
			// 父交易
			if (TradeConstants.TradeKind.PARENT.getCode().equalsIgnoreCase(trade.getTradeKind())) {
				deal.setTradeAction(TradeConstants.TradeActionType.TRANSFER.getCode());
			}
			/** 分佣 */
		} else if (TradeConstants.TradeType.COMMISSION.getCode().equalsIgnoreCase(trade.getTradeType())) {
			// 单笔交易
			if (TradeConstants.TradeKind.SINGLE.getCode().equalsIgnoreCase(trade.getTradeKind())) {
				deal.setTradeAction(TradeConstants.TradeActionType.TRANSFER.getCode());
			}
			/** 充值 */
		} else if (TradeConstants.TradeType.DEPOSIT.getCode().equalsIgnoreCase(trade.getTradeType())) {
			if (TradeConstants.TradeKind.SINGLE.getCode().equalsIgnoreCase(trade.getTradeKind())) {
				// 单笔交易
				deal.setTradeAction(TradeConstants.TradeActionType.NO_ACTION.getCode());
			}
			/** 提现 */
		} else if (TradeConstants.TradeType.WITHDRAW.getCode().equalsIgnoreCase(trade.getTradeType())) {
			if (TradeConstants.TradeKind.SINGLE.getCode().equalsIgnoreCase(trade.getTradeKind())) {
				// 单笔交易
				deal.setTradeAction(TradeConstants.TradeActionType.NO_ACTION.getCode());
			}
			/** 转账 */
		} else if (TradeConstants.TradeType.TRANSFER.getCode().equalsIgnoreCase(trade.getTradeType())) {
			if (TradeConstants.TradeKind.SINGLE.getCode().equalsIgnoreCase(trade.getTradeKind())) {
				// 单笔交易
				deal.setTradeAction(TradeConstants.TradeActionType.TRANSFER.getCode());
			}
			/** 分润 */
		} else if (TradeConstants.TradeType.SPLIT.getCode().equalsIgnoreCase(trade.getTradeType())) {
			// 单笔交易
			if (TradeConstants.TradeKind.SINGLE.getCode().equalsIgnoreCase(trade.getTradeKind())) {
				deal.setTradeAction(TradeConstants.TradeActionType.TRANSFER.getCode());
			}
		} else if (TradeConstants.TradeType.OUT_INVEST.getCode().equalsIgnoreCase(trade.getTradeType())) {
			// 单笔交易
			if (TradeConstants.TradeKind.SINGLE.getCode().equalsIgnoreCase(trade.getTradeKind())) {
				deal.setTradeAction(TradeConstants.TradeActionType.TRANSFER.getCode());
			}
		}
		if(deal.getTradeAction() != null){
			list.add(deal);
		}
		return list;
	}

	public Result<Deal> getDeal(Long id) {
		try{
			Deal d = dealRepository.findOne(id);
			if(d == null){
				return Result.newFailure("", "交易不存在");
			}
			return Result.newSuccess(d);
		}catch(Exception e){
			logger.info("Deal CallBack getDeal DB Exception");
			logger.debug("Deal CallBack getDeal DB Exception", e);
		}
		return Result.newFailure("","Deal CallBack Detail Fail.");
	}

	public Result<Boolean> callBack(Long id, boolean status, String msg) {
		logger.info("Deal CallBack Id:"+id+" Auth:"+status+" Message:"+msg);
		try{
			Deal d = dealRepository.findOne(id);
			if(d == null){
				logger.info("Deal CallBack Id:"+id+" Deal Info Is not exists.");	
				return Result.newFailure("", "info line is not exists.");
			}
			Trade trade = tradeRepository.findOne(d.getTradeId());
			if(trade == null){
				logger.info("Deal CallBack Id:"+id+" Trade Info Is not exists.TradeId:"+d.getTradeId());	
				return Result.newFailure("", "info line is not exists.");
			}
			String step = "";
			try{
				if(status){
					if(TradeConstants.TradeStatusType.EXECUTED.getCode().equalsIgnoreCase(d.getTradeStatus())){
						logger.info("Deal CallBack Id:"+id+" Trade Info Is already Executed.TradeId:"+d.getTradeId());	
						return Result.newSuccess(true);
					}else{
						step = "TRADE";
						d.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
						trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
						try{
							Trade update = tradeRepository.save(trade);
							logger.info("Deal CallBack Id:"+id+" Trade Info Is changed Executed.TradeId:"+d.getTradeId()+" Status:"+update.getTradeStatus());	
						}catch(Exception e){
							logger.info("Deal CallBack Id:"+id+" Trade Info Is changed Executed Error.TradeId:"+d.getTradeId()+" By:"+e.getMessage());
						}
					}
				}else{
					if(TradeConstants.TradeStatusType.FAILURE.getCode().equalsIgnoreCase(d.getTradeStatus())){
						logger.info("Deal CallBack Id:"+id+" Deal Info Is already FAILURE.");	
						return Result.newSuccess(true);
					}else{
						step = "DEAL";
						d.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						trade.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						d.setMessage(msg);
						logger.info("Deal CallBack Id:"+id+" Trade Info Is changed FAILURE.");	
						dealRepository.save(d);
					}
				}
				return Result.newSuccess(true);
			}catch(Exception e){
				logger.info("Deal CallBack Update DB Exception ID:"+id+" Step:"+step);
				logger.debug("Deal CallBack Update DB Exception", e);
			}
		}catch(Exception e){
			logger.info("Deal CallBack Update Status Exception ID:"+id);
			logger.debug("Deal CallBack Update Status Exception", e);
		}
		return Result.newFailure("", "CallBack Status Fail.");
	}

	public Result<List<Deal>> listAll(SpecParam<Deal> spec) {
		try{
			spec.eq("deleted", false);// 未删除
			List<Deal> listDeal = dealRepository.findAll(SpecUtil.spec(spec));
			return Result.newSuccess(listDeal);
		}catch(Exception e){
			logger.info("DealService ListAll Exception");
			logger.debug("DealService ListAll Exception", e);
		}
		return Result.newFailure("", "DealService ListAll Info Fail.");
	}


	public Result<Boolean> dealExecute(Long id) {
		Result<Deal> resultDeal = this.getDeal(id);
		if(!resultDeal.success()){
			return Result.newFailure("", "未发现该Deal");
		}
		List<Deal> list = new ArrayList<Deal>();
		list.add(resultDeal.getObject());
		Result<List<Deal>> resultList = tradeExecuteInner.accountExecute(list, null);
		if(resultList.success()){
			return Result.newSuccess();
		}
		return Result.newFailure(resultList.getError(), resultList.getMessage());
	}

}
