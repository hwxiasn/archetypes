package com.qingbo.ginkgo.ygb.web.biz;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.web.pojo.StatInfo;


public interface InvestmentBizService {
	
	/**
	 * 详细投资信息
	 * @param userId
	 * @param id
	 * @return
	 */
	public Result<Investment> detail(String userId,Long id);
	
	public Result<Investment> create(String userId,Investment investment );
	
	/**
	 * 列表投资凭证
	 * @param userId
	 * @param investment
	 * @param pager
	 * @return
	 */
	public Result<Pager> list(String userId,Investment investment,Pager pager);
	
	
	public Result<Boolean> update(String userId, Investment investment);
	
	/**
	 * 投资统计 
	 * @param userId
	 * 按照用户ID
	 * @return
	 */
	public Result<StatInfo> sumUser(String userId);

	/**
	 * 经济人或者营销机构查看投资人投资项目列表
	 * 按照投资时间倒序排列
	 * @param userId
	 * 操作者ID
	 * @param agencyId
	 * 营销机构ID或者经纪人ID
	 * @param pager
	 * @return
	 */
	Result<Pager> listByAgencyOrBroker(String userId, Long agencyId, Pager pager);
}
