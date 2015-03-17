package com.qingbo.ginkgo.ygb.base.service;

import com.qingbo.ginkgo.ygb.common.result.Result;

/**
 *	系统流水号生成器，通过统一的服务调用来获取全部流水号。
 *	调用需要以调用方的系统编码为输入（为2位的数字，由系统规划设定）
 *	输出为符合yyMMddHHmmssNNKKKK样式的Long型对象。
 *  格式解释：yyMMddHHmmssNNKKKK
 *  yyMMddHHmmss为时间到秒的时间序列，不含年份的头两位。
 *  NN为来源代码，系统定义，可以支持00～99，00表示未知来源
 *  KKKK为该来源的消息队列定义，从0001～9999顺序递增，系统重启则从0001开始
 *  暂定为
 *    客户系统为01,
 *    交易系统为02，
 *    帐务系统为03,
 *    CMS系统为04
 *  流水号生产过程出错返回采用Result.getError()确定是否出错
 *  为空则成功，不为空则为失败
 */
public interface QueuingService {
	
	/**
	 *	输入调用方的系统代码，输出下一个流水号 
	 */
	public Result<Long> next(String src);
	
}
