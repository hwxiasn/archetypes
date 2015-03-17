package com.qingbo.ginkgo.ygb.trade.impl.inner;

import com.qingbo.ginkgo.ygb.common.result.Result;

public interface TradeEntityInner {
	/**
	 * 执行数据库操作，负责新增和更新的操作
	 * 若过程失败则采用 Result.getError()返回设定错误，Result.hasException()返回运行期异常
	 * 若两种方法均为无错误则表明执行成功
	 */
	<T> Result<T> save(T t) ;
}
