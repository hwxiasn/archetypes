package com.qingbo.ginkgo.ygb.web.biz;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;

public interface RepaymentBizService {
	/**
	 * 查询还款信息
	 * @param userId
	 * @param id
	 * @return
	 */
	public Result<Repayment> detail(String userId,Long id);
	
	/**
	 * 列表还款信息
	 * @param userId
	 * @param search
	 * @param pager
	 * @return
	 */
	public Result<Pager> list(String userId,Repayment search,Pager pager);
	
	/**
	 * 更新还款记录状态
	 * @param userId
	 * 操作者ID
	 * @param id
	 * 还款记录ID
	 * @return
	 */
	Result<Boolean> update(String userId,Long id,String status);
}
