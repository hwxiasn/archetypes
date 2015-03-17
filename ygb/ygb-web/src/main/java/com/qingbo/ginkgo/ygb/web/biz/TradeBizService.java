package com.qingbo.ginkgo.ygb.web.biz;

import java.math.BigDecimal;
import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.web.pojo.StatInfo;

public interface TradeBizService {
	/**
	 *按照交易ID查询交易信息 
	 * @param userId
	 * 用户ID
	 * @param id
	 * 交易ID
	 * @return
	 */
	public Result<Trade> detail(String userId,Long id);
	
	/**
	 * 钱多多充值交易
	 * @param userId
	 * 用户ID
	 * @param balance
	 * 充值金额
	 * @return
	 */
	public Result<String> deposit(String userId,BigDecimal balance);
	
	/**
	 * 钱多多提现交易
	 * @param userId
	 * 用户ID
	 * @param balance
	 * 提现金额
	 * @return
	 */
	public Result<String> withdraw(String userId,BigDecimal balance);
	
	
	/**
	 * 
	 * @param userId
	 * 调用者用户ID
	 * @param spec
	 * Trade 查询条件
	 * 必要条件，trade的借出方、贷入方账户ID
	 * 不支持：按时间查询
	 * setTradeType（）；
	 * 可选枚举 TradeType
	 * 		DEPOSIT("D", "存款"),
	 * 		WITHDRAW("W", "提现"),
	 * 支持状态查询，状态枚举
	 * TradeStatusType
	 * @param pager
	 * @return
	 */
	public Result<Pager> listTrade(String userId,SpecParam<Trade> spec,Pager pager);
	
	/**
	 * 按照源交易ID和贷入方账户ID查询交易，返回LIST
	 * @param userId
	 * @param source
	 * @param debitAccount
	 * @return
	 */
	public Result<Pager> detailBySourceAndDebitAccount(String userId,String source,String debitAccount,String tradeType);
	
}
