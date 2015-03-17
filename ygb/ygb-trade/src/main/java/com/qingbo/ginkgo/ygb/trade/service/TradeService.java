package com.qingbo.ginkgo.ygb.trade.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.entity.TradeLog;
import com.qingbo.ginkgo.ygb.trade.entity.TradeSchedule;
import com.qingbo.ginkgo.ygb.trade.lang.RestrictType;

/**
 * 交易域服务接口
 * @author xiejinjun
 *
 */
public interface TradeService {

    /**
     * 交易系统查询单个交易记录
     */
    Result<Trade> getTrade(Long id);
    
    /**
     * 创建一个交易
     */
    Result<Trade> createTrade(Trade trade);
    
    /**
     * 批量创建交易
     */
    Result<List<Trade>> createTrades(List<Trade> trades);
    
    /**
     * 交易更新
     */
    Result<Trade> updateTrade(Trade trade);
    
    /**
     * 钱多多充值交易接口
     * @param trade
     * @return
     */
    Result<String> depositTrade(Trade trade);
    
    /**
     * 钱多多提现交易接口 
     * @param trade
     * @return
     */
    Result<String> withdrawTrade(Trade trade);
    
    /**
     * 按条件查询交易
     */
    Result<PageObject<Trade>> listTradeBySpecAndPage(SpecParam<Trade> spec,Pager page);
    
    
}
