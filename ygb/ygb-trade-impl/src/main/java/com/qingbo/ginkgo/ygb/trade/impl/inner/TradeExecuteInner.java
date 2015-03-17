package com.qingbo.ginkgo.ygb.trade.impl.inner;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.trade.entity.Deal;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.lang.RestrictType;

/**
 * 在系统内部建立内部接口，分解上一级服务的接口，降低上一级服务实现的复杂度
 * 服务定义域上一级接口完全一致
 * @author xiejinjun
 *
 */
public interface TradeExecuteInner {
	
	/**
	 * 交易执行,单个交易同样纳入列表执行，约束条件为对整个列表的约束
	 */
	Result<List<Deal>> execute(List<Deal> trades, RestrictType restrict);
	
	/**
	 * 交易执行驱动帐务执行
	 * @param trades
	 * List<Deal>
	 * @param restrict
	 * RestrictType 可以为空
	 * @return
	 */
	Result<List<Deal>> accountExecute(List<Deal> trades, RestrictType restrict);
}
