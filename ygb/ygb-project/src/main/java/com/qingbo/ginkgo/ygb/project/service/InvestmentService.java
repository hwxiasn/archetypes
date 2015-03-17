package com.qingbo.ginkgo.ygb.project.service;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.Investment;

public interface InvestmentService {
	/**
	 * 按ID获取投资凭证
	 * @param id
	 * @return
	 */
	Result<Investment> getInvestment(Long id);
	/**
	 * 创建一个投资凭证
	 * @param investment
	 * @return
	 */
	Result<Investment> createInvestment(Investment investment);
	/**
	 * 更新投资凭证
	 * @param investment
	 * @return
	 */
	Result<Boolean> updateInvestment(Investment investment);
	/**
	 * 列表投资凭证
	 * @param spec
	 * @param page
	 * @return
	 */
	Result<PageObject<Investment>> listInvestmentBySpecAndPage(SpecParam<Investment> spec,Pager page);

}
