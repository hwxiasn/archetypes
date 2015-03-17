package com.qingbo.ginkgo.ygb.trade.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.trade.entity.Deal;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.lang.RestrictType;

/**
 * 交易执行服务
 * @author Administrator
 *
 */
public interface DealService {
	
	public Result<Trade> execute(Trade trade); 
	
	public Result<Deal> getDeal(Long id);
	
	public Result<List<Deal>> listAll(SpecParam<Deal> spec);
	
	/**
	 * 回调接口
	 * @param id
	 * Deal的ID
	 * @param status
	 * 成功 true  失败 false
	 * @param msg
	 * 错误信息
	 * @return
	 */
	public Result<Boolean> callBack(Long id,boolean status,String msg);
	
	/**
	 * 补交易执行
	 * @param id
	 * Deal ID
	 * @return
	 */
	public Result<Boolean> dealExecute(Long id);


}
