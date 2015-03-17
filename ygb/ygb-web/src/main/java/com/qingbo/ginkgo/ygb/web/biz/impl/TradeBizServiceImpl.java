package com.qingbo.ginkgo.ygb.web.biz.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.service.UserBankCardService;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.trade.entity.Deal;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants.TradeStatusType;
import com.qingbo.ginkgo.ygb.trade.service.DealService;
import com.qingbo.ginkgo.ygb.trade.service.TradeService;
import com.qingbo.ginkgo.ygb.web.biz.TradeBizService;
import com.qingbo.ginkgo.ygb.web.pojo.StatInfo;

@Service("tradeBizService")
public class TradeBizServiceImpl implements TradeBizService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private TradeService tradeService;
	@Resource private DealService dealService;
	@Resource private AccountService accountService;
	@Resource private CustomerService customerService;
	@Resource private UserBankCardService userBankCardService;

	public Result<Trade> detail(String userId, Long id) {
		
		Result<Trade> trade = tradeService.getTrade(id);
		logger.info(SimpleLogFormater.formatResult(trade));
		return trade;
	}
	
	public Result<String> deposit(String userId, BigDecimal balance) {
		Result<SubAccount> account = accountService.getSubAccount(Long.valueOf(userId));
		if(!account.success()){
			return Result.newFailure(account.getError(),account.getMessage());
		}
		if(BigDecimal.ZERO.compareTo(balance)>=0){
			return Result.newFailure("","充值金额不能小于0");
		}
		
		Trade tradeFee = new Trade();
		//交易金额
		tradeFee.setTradeAmount(balance);
		//目标交易金额
		tradeFee.setAimTradeAmount(balance);
		//出借方交易账户ID
		tradeFee.setCreditAccount(String.valueOf(account.getObject().getId()));
		//贷入人交易账户ID
		tradeFee.setDebitAccount(String.valueOf(account.getObject().getId()));
		tradeFee.setTradeSubjectId("");
		tradeFee.setTradeSubjectInfo("充值交易");
		tradeFee.setTradeSource("");
		tradeFee.setTradeType(TradeConstants.TradeType.DEPOSIT.getCode());
		tradeFee.setTradeKind(TradeConstants.TradeKind.SINGLE.getCode());
		tradeFee.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
		tradeFee.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());	
		Result<String> trade = tradeService.depositTrade(tradeFee);
		logger.info(SimpleLogFormater.formatResult(trade));
		return trade;
	}

	
	public Result<String> withdraw(String userId, BigDecimal balance) {
		Result<SubAccount> account = accountService.getSubAccount(Long.valueOf(userId));
		logger.info(SimpleLogFormater.formatResult(account));
		if(!account.success()){
			return Result.newFailure(account.getError(),account.getMessage());
		}
		if(BigDecimal.ONE.compareTo(balance)>=0){
			return Result.newFailure("","提现金额不能小于1元");
		}
		if(account.getObject().getBalance().compareTo(balance)<0){
			return Result.newFailure("", "提现金额高于账户余额");
		}
		
		Result<List<UserBankCard>> userBankCard = userBankCardService.getBankCardByUserId(Long.valueOf(userId));
		logger.info(SimpleLogFormater.formatResult(userBankCard));
		if(!userBankCard.success() && userBankCard.hasObject()){
			return Result.newFailure(userBankCard.getError(),userBankCard.getMessage());
		}

		Trade tradeFee = new Trade();
		//交易金额
		tradeFee.setTradeAmount(balance);
		//目标交易金额
		tradeFee.setAimTradeAmount(balance);
		//出借方交易账户ID
		tradeFee.setCreditAccount(String.valueOf(account.getObject().getId()));
		//贷入人交易账户ID
		tradeFee.setDebitAccount(String.valueOf(account.getObject().getId()));
		//贷入方银行代码
		tradeFee.setDebitBankName(userBankCard.getObject().get(0).getBankCode());
		//银行卡号
		tradeFee.setDebitAccountNo(userBankCard.getObject().get(0).getBankCardNum());
		tradeFee.setTradeSubjectId("");
		tradeFee.setTradeSubjectInfo("提现交易");
		tradeFee.setTradeSource("");
		tradeFee.setTradeType(TradeConstants.TradeType.WITHDRAW.getCode());
		tradeFee.setTradeKind(TradeConstants.TradeKind.SINGLE.getCode());
		tradeFee.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
		tradeFee.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());	
		Result<String> trade = tradeService.withdrawTrade(tradeFee);
		logger.info(SimpleLogFormater.formatResult(trade));
		return trade;
	}

	public Result<Pager> listTrade(String userId, SpecParam<Trade> spec, Pager pager) {
		// 创建时间倒序
		pager.setProperties("id");
		pager.setDirection("desc");
		
		Result<PageObject<Trade>> list = tradeService.listTradeBySpecAndPage(spec, pager);
		logger.info("TradeBizService.listTrade Result:"+SimpleLogFormater.formatResult(list));
		if(list.success()){
			pager.setElements(list.getObject().getList());
			pager.init(list.getObject().getTotal());
			return Result.newSuccess(pager);
		}else{
			return Result.newFailure(list.getError(), list.getMessage());
		}
	}

	@Override
	public Result<Pager> detailBySourceAndDebitAccount(String userId,String source, String debitAccount,String tradeType) {
		SpecParam<Trade> spec = new SpecParam<Trade>();
		spec.eq("sourceTradeId", source);
		spec.eq("debitAccount", debitAccount);
		if(tradeType != null && "".equals(tradeType)){
			spec.eq("tradeType",tradeType);	
		}
		Pager pager = new Pager();
		pager.setPageSize(200);
		pager.init(200);
		pager.page(0);
		Result<Pager> result = listTrade(userId,spec,pager);
		return result;
	}


}
