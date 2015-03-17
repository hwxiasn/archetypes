package com.qingbo.ginkgo.ygb.trade.impl.inner.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.account.payment.qdd.QDDPaymentResponse;
import com.qingbo.ginkgo.ygb.account.service.PaymentService;
import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.trade.entity.Deal;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.entity.TradeLog;
import com.qingbo.ginkgo.ygb.trade.impl.inner.TradeEntityInner;
import com.qingbo.ginkgo.ygb.trade.impl.inner.TradeExecuteInner;
import com.qingbo.ginkgo.ygb.trade.impl.util.TradeError;
import com.qingbo.ginkgo.ygb.trade.lang.RestrictType;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants.TradeActionType;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants.TradeCheckType;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants.TradeEngineType;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants.TradePkgType;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants.TradeRestrictType;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants.TradeStatusType;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants.TradeType;
import com.qingbo.ginkgo.ygb.trade.repository.DealRepository;
import com.qingbo.ginkgo.ygb.trade.repository.TradeLogRepository;
import com.qingbo.ginkgo.ygb.trade.repository.TradeRepository;

@Service("tradeExecuteInner")
public class TradeExecuteInnerImpl implements TradeExecuteInner {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource private TradeRepository tradeRepository;
	@Resource private TradeLogRepository tradeLogRepository;
	@Resource private DealRepository dealRepository;
	@Resource private TradeEntityInner tradeEntityInner;
	@Resource private QueuingService queuingService;
	@Resource private PaymentService paymentService;

	public Result<List<Deal>> execute(List<Deal> trades, RestrictType restrict) {
		//校验输入参数是否非法
		if(trades == null || trades.size()<1){
			return TradeError.getMessage().newFailure(TradeError.TradeSys1001);
		}
		//记录下输入参数基本情况
		logger.info("TradeExecuteInner execute List size:"+trades.size());//+" RestrictType pkgType:"+restrict.getTradePkgType() +" restrictType:"+restrict.getRestrictType());
//		Long tradeId = 0L;
		//交易入库
		for(Deal tr:trades){
			Result<Deal> save = tradeEntityInner.save(tr);
			//入库过程报错则返回错误信息并返回
			if(!save.success()){
				logger.info(SimpleLogFormater.formatResult(save));
				return Result.newFailure(save.getError(), save.getMessage());
			}else{
				tr.setId(save.getObject().getId());
			}
		}
		
//		//更新交易的状态
//		Trade trade = tradeRepository.findOne(tradeId);
//		trade.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());
//		tradeRepository.save(trade);
		
		//交易检验
		/**
		Result<List<Deal>> check = this.check(trades, restrict);
		if(!check.success()){
			logger.info(SimpleLogFormater.formatResult(check));
			return check;
		}
		//*/
		
		//执行操作
		Result<List<Deal>> account = this.accountExecute(trades, restrict);
		logger.info(SimpleLogFormater.formatResult(account));
		/**
		//失败回退操作
		if(account.getError()!=null && restrict.getRestrictType().equalsIgnoreCase(TradeRestrictType.ROLL.getCode())){
			Result<List<Deal>> roll = rollback(trades, restrict);
			//回退失败
			if(roll.getError() != null){
				logger.info("TradeExecuteInner Rollback error for "+roll.getError()+roll.getMessage());
				return roll;
			}else{
				RestrictType rollRestrict = new RestrictType();
				rollRestrict.setTradePkgType(TradePkgType.ROLL.getCode());
				rollRestrict.setRestrictType(TradeRestrictType.ALL.getCode());
				Result<List<Deal>> rollback = this.execute(roll.getObject(), rollRestrict);
				logger.info(SimpleLogFormater.formatResult(rollback));
				return rollback;
			}
		}
		//*/
		return account;
	}
	
	
	/**
	 * 交易驱动帐务系统并返回操作结果，如果返回失败则视约束是否触发后续的回退操作 
	 */
	public Result<List<Deal>> accountExecute(List<Deal> trades, RestrictType restrict){
		String error = null;
		String message = null;
		for(Deal deal:trades){
			TradeLog log = new TradeLog();
			log.setId(queuingService.next(TradeConstants.TRADE_QUEUING).getObject());
			log.setTradeId(deal.getId());
			log.setTradeParam(deal.getTradeParam());
			log.setExeEngine(TradeEngineType.AUTO.getCode());
			log.setExeUser(TradeEngineType.AUTO.getCode());
			log.setExeTime(new Date());
			Result<TradeLog> save = tradeEntityInner.save(log);
			if(!save.success()){
				logger.info(SimpleLogFormater.formatResult(save));
			}else{
				log = save.getObject();
			}
			
			if(TradeConstants.TradeType.INVEST.getCode().equalsIgnoreCase(deal.getTradeType())){//投资
				if(TradeConstants.TradeActionType.TRANSFER.getCode().equalsIgnoreCase(deal.getTradeAction())){// 投资成功
					logger.info("PaymentService Log :INVEST OK Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" Balance:"+deal.getTradeAmount()+" TradeParam:"+deal.getTradeParam());
					Result<Boolean> result= paymentService.unfreeze(deal.getTradeParam(), true);// 审核通过
					log.setExeResult(result.getError()==null?"":result.getError());
					log.setExeMsg(result.getMessage()==null?"":result.getMessage());
					logger.info("PaymentService InterFace Result:"+SimpleLogFormater.formatResult(result));
					if(result.success()){//成功则修改交易状态，不必要再退回上一层处理 true){//
						try{
							Trade trade = tradeRepository.findOne(deal.getTradeId());
							trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
							trade = tradeRepository.save(trade);
						}catch(Exception e){
							logger.info("AccountExecute TRANSFER OK,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
						}
						try{
							Deal saveDeal = dealRepository.findOne(deal.getId());
							saveDeal.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
							Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
							if(!saveReturn.success()){
								logger.info(SimpleLogFormater.formatResult(saveReturn));		
							}
						}catch(Exception e){
							logger.info("AccountExecute TRANSFER OK,Update Deal Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
						}
					}else{
						try{
							error = result.getError();
							message = result.getMessage();

							Deal saveDeal = dealRepository.findOne(deal.getId());
							saveDeal.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
							Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
							if(!saveReturn.success()){
								logger.info(SimpleLogFormater.formatResult(saveReturn));		
							}
						}catch(Exception e){
							logger.info("AccountExecute TRANSFER FAIL,Update Deal Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
						}
					}
				}else if(TradeConstants.TradeActionType.UNFREEZE.getCode().equalsIgnoreCase(deal.getTradeAction())){//投资失败
					logger.info("PaymentService Log :INVEST Fail Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" Balance:"+deal.getTradeAmount()+" TradeParam:"+deal.getTradeParam());
					Result<Boolean> result = paymentService.unfreeze(deal.getTradeParam(), false);//审核不通过
					log.setExeResult(result.getError()==null?"":result.getError());
					log.setExeMsg(result.getMessage()==null?"":result.getMessage());
					logger.info("PaymentService InterFace Result:"+SimpleLogFormater.formatResult(result));
					if(result.success()){//成功则修改交易状态，不必要再退回上一层处理 true){//
						try{
							Trade trade = tradeRepository.findOne(deal.getTradeId());
							trade.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
							trade = tradeRepository.save(trade);
						}catch(Exception e){
							logger.info("AccountExecute UNFREEZE OK,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
						}
						try{
							Deal saveDeal = dealRepository.findOne(deal.getId());
							saveDeal.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
							Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
							if(!saveReturn.success()){
								logger.info(SimpleLogFormater.formatResult(saveReturn));		
							}
						}catch(Exception e){
							logger.info("AccountExecute UNFREEZE OK,Update Deal Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
						}
					}else{
						try{
							error = result.getError();
							message = result.getMessage();
							
							Deal saveDeal = dealRepository.findOne(deal.getId());
							saveDeal.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
							Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
							if(!saveReturn.success()){
								logger.info(SimpleLogFormater.formatResult(saveReturn));		
							}
						}catch(Exception e){
							logger.info("AccountExecute UNFREEZE FAIL,Update Deal Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
						}
					}
				}else if(TradeConstants.TradeActionType.FREEZE.getCode().equalsIgnoreCase(deal.getTradeAction())){//投资冻结
					logger.info("PaymentService Log :INVEST Freeze Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" CreditAccount:"+deal.getCreditAccount()+" DebitAccount"+deal.getDebitAccount()+" Balance:"+deal.getTradeAmount());
					Result<String>	result = paymentService.freeze(Long.valueOf(deal.getCreditAccount()), Long.valueOf(deal.getDebitAccount()),deal.getTradeAmount());
					log.setExeResult(result.getError()==null?"":result.getError());
					log.setExeMsg(result.getMessage()==null?"":result.getMessage());
					logger.info("PaymentService InterFace Result:"+SimpleLogFormater.formatResult(result));
					try{
						if(result.success()){//保存返回的冻结流水号，在解冻或者通过时调用 true){//
							try{
								Deal saveDeal = dealRepository.findOne(deal.getId());
								saveDeal.setTradeParam(result.getObject()==null?"":result.getObject());
								saveDeal.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
								Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
								logger.info("AccountExecute Freeze OK Update Deal Status.DealId:"+deal.getId()+" ResultObject:"+SimpleLogFormater.formatResult(saveReturn));
								if(!saveReturn.success()){
									logger.info(SimpleLogFormater.formatResult(saveReturn));		
								}
							}catch(Exception e){
								logger.info("AccountExecute Freeze OK,Update Deal Error.DealId:"+deal.getId()+" By:"+e.getMessage());
							}
						}else{
							error = result.getError();
							message = result.getMessage();
							Deal saveDeal = dealRepository.findOne(deal.getId());
							saveDeal.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
							Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
							logger.info("AccountExecute Freeze Fail Update Deal Status.DealId:"+deal.getId()+" ResultObject:"+SimpleLogFormater.formatResult(saveReturn));
							if(!saveReturn.success()){
								logger.info(SimpleLogFormater.formatResult(saveReturn));		
							}
						}
					}catch(Exception e){
						logger.info("INVEST Freeze Deal Error ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" Balance:"+deal.getTradeAmount());
						logger.trace("INVEST Freeze Deal Error ",e);
					}
				}  
			}else if(TradeConstants.TradeType.REPAY.getCode().equalsIgnoreCase(deal.getTradeType())){//还款
				logger.info("PaymentService Log :Repay Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" CreditAccount:"+deal.getCreditAccount()+" DebitAccount"+deal.getDebitAccount()+" Balance:"+deal.getTradeAmount());
				Result<Boolean> result= paymentService.transfer(Long.valueOf(deal.getCreditAccount()), Long.valueOf(deal.getDebitAccount()),deal.getTradeAmount());// 审核通过
				log.setExeResult(result.getError()==null?"":result.getError());
				log.setExeMsg(result.getMessage()==null?"":result.getMessage());
				logger.info("PaymentService InterFace Result:"+SimpleLogFormater.formatResult(result));
				Trade trade = tradeRepository.findOne(deal.getTradeId());
				if(result.success()){//true){//
					try{
						trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute REPAY OK,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						logger.info("AccountExecute REPAY OK Update Deal Status.DealId:"+deal.getId()+" ResultObject:"+SimpleLogFormater.formatResult(saveReturn));
						if(!saveReturn.success()){
							logger.info(SimpleLogFormater.formatResult(saveReturn));		
						}
					}catch(Exception e){
						logger.info("AccountExecute REPAY OK,Update Deal Error.DealId:"+deal.getId()+" By:"+e.getMessage());
					}
				}else{
					error = result.getError();
					message = result.getMessage();
					try{
						trade.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute REPAY FAIL,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info(SimpleLogFormater.formatResult(saveReturn));		
						}
					}catch(Exception e){
						logger.info("AccountExecute REPAY FAIL,Update Deal Error.DealId:"+deal.getId()+" By:"+e.getMessage());
					}
				}
			}else if(TradeConstants.TradeType.COMMISSION.getCode().equalsIgnoreCase(deal.getTradeType())){//分佣
				logger.info("PaymentService Log :Commission Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" CreditAccount:"+deal.getCreditAccount()+" DebitAccount"+deal.getDebitAccount()+" Balance:"+deal.getTradeAmount());
				Result<Boolean> result= paymentService.transfer(Long.valueOf(deal.getCreditAccount()), Long.valueOf(deal.getDebitAccount()),deal.getTradeAmount());// 审核通过
				log.setExeResult(result.getError()==null?"":result.getError());
				log.setExeMsg(result.getMessage()==null?"":result.getMessage());
				logger.info("PaymentService InterFace Result:"+SimpleLogFormater.formatResult(result));
				Trade trade = tradeRepository.findOne(deal.getTradeId());
				if(result.success()){//true){//
					try{
						trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute COMMISSION OK,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info(SimpleLogFormater.formatResult(saveReturn));		
						}
					}catch(Exception e){
						logger.info("AccountExecute COMMISSION OK,Update Deal Error.DealId:"+deal.getId()+" By:"+e.getMessage());
					}
				}else{
					try{
						error = result.getError();
						message = result.getMessage();
						trade.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute COMMISSION FAIL,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info(SimpleLogFormater.formatResult(saveReturn));		
						}
					}catch(Exception e){
						logger.info("AccountExecute COMMISSION FAIL,Update Deal Error.DealId:"+deal.getId()+" By:"+e.getMessage());
					}
				}
			}else if(TradeConstants.TradeType.SPLIT.getCode().equalsIgnoreCase(deal.getTradeType())){//分润
				logger.info("PaymentService Log :Split Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" CreditAccount:"+deal.getCreditAccount()+" DebitAccount"+deal.getDebitAccount()+" Balance:"+deal.getTradeAmount());
				Result<Boolean> result= paymentService.transfer(Long.valueOf(deal.getCreditAccount()), Long.valueOf(deal.getDebitAccount()),deal.getTradeAmount());// 审核通过
				log.setExeResult(result.getError()==null?"":result.getError());
				log.setExeMsg(result.getMessage()==null?"":result.getMessage());
				logger.info("PaymentService InterFace Result:"+SimpleLogFormater.formatResult(result));
				Trade trade = tradeRepository.findOne(deal.getTradeId());
				if(result.success()){//true){//
					try{
						trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute SPLIT OK,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info(SimpleLogFormater.formatResult(saveReturn));		
						}
					}catch(Exception e){
						logger.info("AccountExecute SPLIT OK,Update Deal Error.DealId:"+deal.getId()+" By:"+e.getMessage());
					}
				}else{
					try{
						error = result.getError();
						message = result.getMessage();
						trade.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute SPLIT FAIL,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info(SimpleLogFormater.formatResult(saveReturn));		
						}
					}catch(Exception e){
						logger.info("AccountExecute SPLIT FAIL,Update Deal Error.DealId:"+deal.getId()+" By:"+e.getMessage());
					}
				}
			}else if(TradeConstants.TradeType.WITHDRAW.getCode().equalsIgnoreCase(deal.getTradeType())){//提现
				logger.info("PaymentService Log :Withdraw Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" CreditAccount"+deal.getCreditAccount()+" Balance:"+deal.getTradeAmount());
				Trade trade = tradeRepository.findOne(deal.getTradeId());
				logger.info("PaymentService Log :Withdraw Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" CreditAccount"+deal.getCreditAccount()+" DebitBankName"+trade.getDebitBankName()+" DebitAccountNo"+trade.getDebitAccountNo()+" Balance:"+deal.getTradeAmount());
				Result<String> result= paymentService.withdraw(deal.getId(), Long.valueOf(deal.getCreditAccount()), deal.getTradeAmount(), trade.getDebitBankName(), trade.getDebitAccountNo());// 审核通过
				log.setExeResult(result.getError()==null?"":result.getError());
				log.setExeMsg(result.getMessage()==null?"":result.getMessage());
				logger.info("PaymentService InterFace Result:"+SimpleLogFormater.formatResult(result));
				if(result.success()){//true){//
					try{
						trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTING.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute WITHDRAW OK,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeDispatch(result.getObject());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.EXECUTING.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info(SimpleLogFormater.formatResult(saveReturn));		
						}
					}catch(Exception e){
						logger.info("AccountExecute WITHDRAW OK,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
				}else{
					try{
						error = result.getError();
						message = result.getMessage();
						trade.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute WITHDRAW FAIL,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info(SimpleLogFormater.formatResult(saveReturn));		
						}
					}catch(Exception e){
						logger.info("AccountExecute WITHDRAW FAIL,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
				}
			}else if(TradeConstants.TradeType.DEPOSIT.getCode().equalsIgnoreCase(deal.getTradeType())){//存款
				logger.info("PaymentService Log :Deposit Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" CreditAccount"+deal.getCreditAccount()+" Balance:"+deal.getTradeAmount());
				Result<String> result= paymentService.deposit(deal.getId(), Long.valueOf(deal.getCreditAccount()), deal.getTradeAmount());//
				log.setExeResult(result.getError()==null?"":result.getError());
				log.setExeMsg(result.getMessage()==null?"":result.getMessage());
				logger.info("PaymentService InterFace Result:"+SimpleLogFormater.formatResult(result));
				Trade trade = tradeRepository.findOne(deal.getTradeId());
				if(result.success()){//true){//
					try{
						trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTING.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute DEPOSIT OK,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeDispatch(result.getObject());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.EXECUTING.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info(SimpleLogFormater.formatResult(saveReturn));		
						}
					}catch(Exception e){
						logger.info("AccountExecute DEPOSIT OK,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
				}else{
					try{
						error = result.getError();
						message = result.getMessage();
						trade.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute DEPOSIT FAIL,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info(SimpleLogFormater.formatResult(saveReturn));		
						}
					}catch(Exception e){
						logger.info("AccountExecute DEPOSIT FAIL,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
				}
				//对外投资
			}else if(TradeConstants.TradeType.OUT_INVEST.getCode().equalsIgnoreCase(deal.getTradeType())){//存款
				logger.info("PaymentService Log :OutInvest Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" CreditAccount:"+deal.getCreditAccount()+" DebitAccount"+deal.getDebitAccount()+" Balance:"+deal.getTradeAmount());
				Result<Boolean> result= paymentService.transfer(Long.valueOf(deal.getCreditAccount()), Long.valueOf(deal.getDebitAccount()),deal.getTradeAmount());// 审核通过
				log.setExeResult(result.getError()==null?"":result.getError());
				log.setExeMsg(result.getMessage()==null?"":result.getMessage());
				logger.info("PaymentService InterFace Result:"+SimpleLogFormater.formatResult(result));
				Trade trade = tradeRepository.findOne(deal.getTradeId());
				if(true){//result.success()){//true){//
					trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
					trade = tradeRepository.save(trade);
					Deal saveDeal = dealRepository.findOne(deal.getId());
					saveDeal.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
					Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
					if(!saveReturn.success()){
						logger.info(SimpleLogFormater.formatResult(saveReturn));		
					}
				}else{
					error = result.getError();
					message = result.getMessage();
					trade.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
					trade = tradeRepository.save(trade);
					Deal saveDeal = dealRepository.findOne(deal.getId());
					saveDeal.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
					Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
					if(!saveReturn.success()){
						logger.info(SimpleLogFormater.formatResult(saveReturn));		
					}
				}
				//转账操作
			}else if(TradeConstants.TradeType.TRANSFER.getCode().equalsIgnoreCase(deal.getTradeType())){//分佣
				logger.info("PaymentService Log :TRANSFER Deal ID:"+deal.getId()+" Action:"+deal.getTradeAction()+" Type:"+deal.getTradeType()+" CreditAccount:"+deal.getCreditAccount()+" DebitAccount"+deal.getDebitAccount()+" Balance:"+deal.getTradeAmount());
				Result<Boolean> result= paymentService.transfer(Long.valueOf(deal.getCreditAccount()), Long.valueOf(deal.getDebitAccount()),deal.getTradeAmount());// 审核通过
				logger.info("PaymentService InterFace Result:"+result.success());
				log.setExeResult(result.getError()==null?"":result.getError());
				log.setExeMsg(result.getMessage()==null?"":result.getMessage());
				Trade trade = tradeRepository.findOne(deal.getTradeId());
				if(result.success()){//true){//
					try{
						trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute TRANSFER OK,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info("TradeEntityInner Update Deal Id:"+saveDeal.getId()+" Result:"+saveReturn.success());		
						}
					}catch(Exception e){
						logger.info("AccountExecute TRANSFER OK,Update Deal Error.DealId:"+deal.getId()+" By:"+e.getMessage());
					}
				}else{
					try{
						error = result.getError();
						message = result.getMessage();
						trade.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						trade = tradeRepository.save(trade);
					}catch(Exception e){
						logger.info("AccountExecute TRANSFER FAIL,Update Trade Error.TradeId:"+deal.getTradeId()+" By:"+e.getMessage());
					}
					try{
						Deal saveDeal = dealRepository.findOne(deal.getId());
						saveDeal.setTradeStatus(TradeConstants.TradeStatusType.FAILURE.getCode());
						Result<Deal> saveReturn = tradeEntityInner.save(saveDeal);
						if(!saveReturn.success()){
							logger.info("TradeEntityInner Update Deal Id:"+saveDeal.getId()+" Result:"+saveReturn.success());		
						}
					}catch(Exception e){
						logger.info("AccountExecute TRANSFER FAIL,Update Deal Error.DealId:"+deal.getId()+" By:"+e.getMessage());
					}
				}
			} 			
			
			/**
			if(tr.getTradeAction()().equalsIgnoreCase(TradeConstants.TradeActionType.TRANSFER.DEPOSIT.getCode())){
				//存款交易
				Result<String>  result =qddPaymentService.loanRecharge(tr.getId());
				if(result.success()){
					accountLog = accountLogService.deposit(tr.getId());
					tr.setTradeDispatch((String)result.getObject());
					tradeEntityInner.save(tr);
					log.setExeMsg((String)result.getObject());
					tradeEntityInner.save(log);
				}
//			}else if(tr.getTradeType().equalsIgnoreCase(TradeType.FREEZE.getCode())){
//				//冻结交易 
//				result = qddPaymentService.loanAudit(tr.getId());
//				if(result.getError() == null){
//					accountLog = accountLogService.freeze(tr.getId());
//				}
			}else if(tr.getTradeType().equalsIgnoreCase(TradeType.INVEST.getCode())){
				//转账投资
				Result<QDDPaymentResponse> result = qddPaymentService.loanAudit(tr.getId(), tradeId, false);
				if(result.success()){
					accountLog = accountLogService.freeze(tr.getId());
					accountLog = accountLogService.transfer(tr.getId());
					log.setExeMsg(((QDDPaymentResponse)result.getObject()).rawText());
				}
			}else if(tr.getTradeType().equalsIgnoreCase(TradeType.UNFREEZE.getCode())){
				//解冻交易，或者解冻投资，或者转账投资
				result = qddPaymentService.loanAudit(tr.getId(),false);
				if(result.getError() == null){
					if(tr.getTradeType().equalsIgnoreCase(TradeType.TRANSFER_I.getCode())){
						accountLog = accountLogService.transfer(tr.getId());
					}else{
						accountLog = accountLogService.unfreeze(tr.getId());
					}
					log.setExeMsg(((QDDPaymentResponse)result.getObject()).rawText());
				}
			}else if(tr.getTradeType().equalsIgnoreCase(TradeType.WITHDRAW.getCode())){
				//取款交易
				result = qddPaymentService.loanWithdraw(tr.getId());
				if(result.getError() == null){
					accountLog = accountLogService.withdraw(tr.getId());
					tr.setTradeDispatch((String)result.getObject());
					tradeEntityInner.save(tr);
					log.setExeMsg((String)result.getObject());
				}
			} 
			//*/
			TradeLog saveLog = tradeLogRepository.findOne(log.getId());
			saveLog.setExeResult(log.getExeResult()==null?"":log.getExeResult());
			saveLog.setExeMsg(log.getExeMsg()==null?"":log.getExeMsg());
			Result<TradeLog> resultLog = tradeEntityInner.save(saveLog);
			if(!resultLog.success()){
				logger.info(SimpleLogFormater.formatResult(resultLog));		
			}
		}
		if(error == null && message == null){
			return Result.newSuccess(trades);
		}
		//执行环节出错
		Result<List<Deal>> back = Result.newFailure(error, message);
		back.setObject(trades);
		return back;
	}
	
	/**
	 * 校验输入的交易是否合法
	 */
	private Result<List<Deal>> check(List<Deal> trades, RestrictType restrict){
		//约束中为默认成功的则不做检验
		if(restrict.getRestrictType().equalsIgnoreCase(TradeRestrictType.ALWAYS.getCode())){
			for(Deal tr:trades){
				tr.setTradeCheck(TradeCheckType.TRUE.getCode());
				Result<Deal> save = tradeEntityInner.save(tr);
				//存储过程失败
				if(save.hasException() || save.getError() != null){
					return this.errorDesc(trades, save, tr);
				}
			}
			return Result.newSuccess(trades);
		}
		
		//充值--验证借出方与贷入方为同一账号即可
		if(restrict.getTradePkgType().equalsIgnoreCase(TradePkgType.DN.getCode())){
			for(Deal tr:trades){
				boolean failed = tr.getCreditAccount() == null || tr.getDebitAccount() == null 
						|| !tr.getCreditAccount().equalsIgnoreCase(tr.getDebitAccount()) || tr.getTradeAmount() == null
						|| tr.getTradeAmount().compareTo(BigDecimal.ZERO)<1;
				tr.setTradeCheck(failed ? TradeCheckType.FAILED.getCode():TradeCheckType.TRUE.getCode());
				Result<Deal> save = tradeEntityInner.save(tr);
				//存储过程失败
				if(save.hasException() || save.getError() != null){
					return this.errorDesc(trades, save, tr);
				}
				if(failed){
					Result<List<Deal>> r =TradeError.getMessage().newFailure(TradeError.TradeSys1002);
					r.setObject(trades);
					return r;
				}
			}
			return Result.newSuccess(trades);
		//募集--无需验证，仅生成一个无需操作的交易流水，金额不能为小于0.
		}else if(restrict.getTradePkgType().equalsIgnoreCase(TradePkgType.F.getCode())){
			for(Deal tr:trades){
				boolean failed = tr.getCreditAccount() == null || tr.getDebitAccount() == null 
						|| tr.getCreditAccount().equalsIgnoreCase(tr.getDebitAccount()) || tr.getTradeAmount() == null
						|| tr.getTradeAmount().compareTo(BigDecimal.ZERO)<1;
				tr.setTradeCheck(failed ? TradeCheckType.FAILED.getCode():TradeCheckType.TRUE.getCode());
				Result<Deal> save = tradeEntityInner.save(tr);
				//存储过程失败
				if(save.hasException() || save.getError() != null){
					return this.errorDesc(trades, save, tr);
				}
				if(failed){
					Result<List<Deal>> r =TradeError.getMessage().newFailure(TradeError.TradeSys1003);
					r.setObject(trades);
					return r;
				}
			}
			return Result.newSuccess(trades);
		//还款--需验证借出方金额是否足够
		}else if(restrict.getTradePkgType().equalsIgnoreCase(TradePkgType.P.getCode())){
			for(Deal tr:trades){
				Trade tradeInfo = tradeRepository.findOne(tr.getTradeId());
				boolean failed = tr.getCreditAccount() == null || tr.getDebitAccount() == null|| tr.getCreditAccount().equalsIgnoreCase(tr.getDebitAccount()) 
						|| tr.getTradeAmount() == null|| tr.getTradeAmount().compareTo(BigDecimal.ZERO)<1
						|| tradeInfo.getCreditBalance() == null || tradeInfo.getCreditBalance().compareTo(tr.getTradeAmount())<0;
				tr.setTradeCheck(failed ? TradeCheckType.FAILED.getCode():TradeCheckType.TRUE.getCode());
				Result<Deal> save = tradeEntityInner.save(tr);
				//存储过程失败
				if(save.hasException() || save.getError() != null){
					return this.errorDesc(trades, save, tr);
				}
				if(failed){
					Result<List<Deal>> r =TradeError.getMessage().newFailure(TradeError.TradeSys1004);
					r.setObject(trades);
					return r;
				}
			}
			return Result.newSuccess(trades);
		//投资--需验证上下条关联的金额匹配以及操作是否配对
		}else if(restrict.getTradePkgType().equalsIgnoreCase(TradePkgType.I.getCode())){
			boolean failed = false;
			int size = trades.size();
//			if(size <3 || !trades.get(size-1).getTradeType().equalsIgnoreCase("")){//待补充投资冻结的交易类型
//				failed = true;
//			}
			//如果粗检不合格
			if(failed){
				Result<List<Deal>> r =TradeError.getMessage().newFailure(TradeError.TradeSys1005);
				r.setObject(trades);
				return r;
			}
			int sit = size/2;
			BigDecimal total = BigDecimal.ZERO;
			Deal tr0 = null;
			Deal tr1 = null;
			for(int i=0;i<sit;i++){
				tr0 = trades.get(2*i);
				tr1 = trades.get(2*i+1);
				Trade tradeInfo = tradeRepository.findOne(tr0.getTradeId());
				//第一条的借出方与贷入方须一致，第二条的借出方须与第一条一直，与贷入方需不一致
				boolean singleFailed = tr0.getCreditAccount() == null || tr0.getDebitAccount() == null || tr1.getCreditAccount() == null || tr1.getDebitAccount() == null
						|| !tr0.getCreditAccount().equalsIgnoreCase(tr0.getDebitAccount()) || !tr0.getCreditAccount().equalsIgnoreCase(tr1.getCreditAccount())|| tr0.getCreditAccount().equalsIgnoreCase(tr1.getDebitAccount())
						//第一条的冻结金额不能低于交易金额,两条的交易金额需要一致
						||tradeInfo.getCreditFreezeBalance() == null || tr0.getTradeAmount() == null || tradeInfo.getCreditFreezeBalance().compareTo(tr0.getTradeAmount())<0
						||tr1.getTradeAmount() == null || tr1.getTradeAmount().compareTo(tr0.getTradeAmount())!=0;
				tr0.setTradeCheck(singleFailed ? TradeCheckType.FAILED.getCode():TradeCheckType.TRUE.getCode());
				Result<Deal> save = tradeEntityInner.save(tr0);
				//存储过程失败
				if(save.hasException() || save.getError() != null){
					return this.errorDesc(trades, save, tr0);
				}
				tr1.setTradeCheck(singleFailed ? TradeCheckType.FAILED.getCode():TradeCheckType.TRUE.getCode());
				save = tradeEntityInner.save(tr1);
				//存储过程失败
				if(save.hasException() || save.getError() != null){
					return this.errorDesc(trades, save, tr1);
				}
				if(singleFailed){
					Result<List<Deal>> r =TradeError.getMessage().newFailure(TradeError.TradeSys1005);
					r.setObject(trades);
					return r;
				}
				total = total.add(tr1.getTradeAmount());
			}
			Deal tr = trades.get(size-1);
			boolean finalFail = tr.getCreditAccount() == null || tr.getDebitAccount() == null || !tr.getCreditAccount().equalsIgnoreCase(tr.getDebitAccount())||!tr.getCreditAccount().equalsIgnoreCase(tr1.getDebitAccount())
					||tr.getTradeAmount() == null || tr.getTradeAmount().compareTo(total)!=0;
			tr.setTradeCheck(finalFail ? TradeCheckType.FAILED.getCode():TradeCheckType.TRUE.getCode());
			Result<Deal> save = tradeEntityInner.save(tr);
			//存储过程失败
			if(save.hasException() || save.getError() != null){
				return this.errorDesc(trades, save, tr);
			}
			if(finalFail){
				Result<List<Deal>> r =TradeError.getMessage().newFailure(TradeError.TradeSys1005);
				r.setObject(trades);
				return r;
			}
			return Result.newSuccess(trades);
			
		//分佣--若为单一记录需要验证余额是否足够，多条记录则仅需验证借出方是否为同一账号
		}else if(restrict.getTradePkgType().equalsIgnoreCase(TradePkgType.C.getCode())){
			if(trades.size() == 1){
				Deal tr=trades.get(0);
				Trade tradeInfo = tradeRepository.findOne(tr.getTradeId());
				boolean failed = tr.getCreditAccount() == null || tr.getDebitAccount() == null|| tr.getCreditAccount().equalsIgnoreCase(tr.getDebitAccount()) 
						|| tr.getTradeAmount() == null|| tr.getTradeAmount().compareTo(BigDecimal.ZERO)<1
						|| tradeInfo.getCreditBalance() == null || tradeInfo.getCreditBalance().compareTo(tr.getTradeAmount())<0;
				tr.setTradeCheck(failed ? TradeCheckType.FAILED.getCode():TradeCheckType.TRUE.getCode());
				Result<Deal> save = tradeEntityInner.save(tr);
				//存储过程失败
				if(save.hasException() || save.getError() != null){
					return this.errorDesc(trades, save, tr);
				}
				if(failed){
					Result<List<Deal>> r =TradeError.getMessage().newFailure(TradeError.TradeSys1006);
					r.setObject(trades);
					return r;
				}
			}else{
				String credit = null;
				for(Deal tr:trades){
					if(credit == null){
						credit = tr.getCreditAccount();
					}
					boolean failed = tr.getCreditAccount() == null || tr.getDebitAccount() == null|| tr.getCreditAccount().equalsIgnoreCase(tr.getDebitAccount()) 
							|| tr.getTradeAmount() == null|| tr.getTradeAmount().compareTo(BigDecimal.ZERO)<1
							|| !credit.equalsIgnoreCase(tr.getCreditAccount());
					tr.setTradeCheck(failed ? TradeCheckType.FAILED.getCode():TradeCheckType.TRUE.getCode());
					Result<Deal> save = tradeEntityInner.save(tr);
					//存储过程失败
					if(save.hasException() || save.getError() != null){
						return this.errorDesc(trades, save, tr);
					}
					if(failed){
						Result<List<Deal>> r =TradeError.getMessage().newFailure(TradeError.TradeSys1006);
						r.setObject(trades);
						return r;
					}
				}
			}
			return Result.newSuccess(trades);
		//投资冻结--验证余额是否足够
		}else if(restrict.getTradePkgType().equalsIgnoreCase(TradePkgType.IL.getCode())){
			for(Deal tr:trades){
				Trade tradeInfo = tradeRepository.findOne(tr.getTradeId());
				boolean failed = tr.getCreditAccount() == null || tr.getDebitAccount() == null|| tr.getCreditAccount().equalsIgnoreCase(tr.getDebitAccount()) 
						|| tr.getTradeAmount() == null|| tr.getTradeAmount().compareTo(BigDecimal.ZERO)<1
						|| tradeInfo.getCreditBalance() == null || tradeInfo.getCreditBalance().compareTo(tr.getTradeAmount())<0;
				tr.setTradeCheck(failed ? TradeCheckType.FAILED.getCode():TradeCheckType.TRUE.getCode());
				Result<Deal> save = tradeEntityInner.save(tr);
				//存储过程失败
				if(save.hasException() || save.getError() != null){
					return this.errorDesc(trades, save, tr);
				}
				if(failed){
					Result<List<Deal>> r =TradeError.getMessage().newFailure(TradeError.TradeSys1007);
					r.setObject(trades);
					return r;
				}
			}
			return Result.newSuccess(trades);
		//提现--验证余额是否足够，且借出方与贷入方一致，需要有银行信息
		}else if(restrict.getTradePkgType().equalsIgnoreCase(TradePkgType.W.getCode())){
			for(Deal tr:trades){
				Trade tradeInfo = tradeRepository.findOne(tr.getTradeId());
				boolean failed = tr.getCreditAccount() == null || tr.getDebitAccount() == null|| !tr.getCreditAccount().equalsIgnoreCase(tr.getDebitAccount()) 
						|| tr.getTradeAmount() == null|| tr.getTradeAmount().compareTo(BigDecimal.ZERO)<1
						|| tradeInfo.getDebitBankName() == null || tradeInfo.getDebitAccountNo() == null
						|| tradeInfo.getCreditBalance() == null || tradeInfo.getCreditBalance().compareTo(tr.getTradeAmount())<0;
				tr.setTradeCheck(failed ? TradeCheckType.FAILED.getCode():TradeCheckType.TRUE.getCode());
				Result<Deal> save = tradeEntityInner.save(tr);
				//存储过程失败
				if(save.hasException() || save.getError() != null){
					return this.errorDesc(trades, save, tr);
				}
				if(failed){
					Result<List<Deal>> r =TradeError.getMessage().newFailure(TradeError.TradeSys1008);
					r.setObject(trades);
					return r;
				}
			}
			return Result.newSuccess(trades);
		}
		return null;
	}
	
	/**
	 * 错误信息描述
	 */
	private Result<List<Deal>> errorDesc(List<Deal> trades,Result<Deal> save,Deal tr){
		Result<List<Deal>> r = TradeError.getMessage().newFailure(save.getError(),save.getMessage());
		r.setObject(trades);
		logger.info(SimpleLogFormater.formatResult(r));
		StringBuffer msg = new StringBuffer();
		msg.append("TradeExecuteInner save Trade failed at Subject:");
		msg.append(tr.getTradeId()).append(" creditAccount:").append(tr.getCreditAccount());
		msg.append(" debitAccount:").append(tr.getDebitAccount()).append(" tradeType:").append(tr.getTradeAction());
		msg.append(" tradeAmount:").append(tr.getTradeAmount());
		logger.info(msg.toString());
		return r;
	}
	
	/**
	 *	生成回退列表 
	 */
	private Result<List<Deal>> rollback(List<Deal> trades, RestrictType restrict){
		int end = 0;
		for(end=0;end<trades.size();end++){
			Deal tr = trades.get(end);
			if(tr.getMemo()!=null){
				break;
			}
		}
		List<Deal> rollList = new ArrayList<Deal>();
		//目前仅限冻结、解冻、转账交易有可能存在回滚的操作，主要业务场景存在于项目成立过程
		while(end>0){
			Deal tr = trades.get(end);
			end--;
			Deal rollTrade = new Deal();
			rollTrade.setBatchId(tr.getBatchId());
			rollTrade.setTradeId(tr.getTradeId());
			rollTrade.setTradeAmount(tr.getTradeAmount());
			rollTrade.setCreditAccount(tr.getDebitAccount());
			rollTrade.setDebitAccount(tr.getCreditAccount());
			
			rollList.add(rollTrade);
		}
		return Result.newSuccess(rollList);
	}
	

}
