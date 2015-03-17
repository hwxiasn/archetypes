package com.qingbo.ginkgo.ygb.project.service;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.BorrowerInfo;

public interface BorrowerInfoService {

	/**
	 * 分页查询借款人信息
	 * @param spec 查询参数
	 * @param page 分页参数
	 * @return
	 */
	Result<PageObject<BorrowerInfo>> pageBorrowerInfos(SpecParam<BorrowerInfo>spec, Pager page);
	
	/**
	 * 添加借款人信息
	 * @param info 借款人信息
	 * @return
	 */
	Result<Boolean> addBorrowerInfo(BorrowerInfo info);
	
	/**
	 * 删除借款人信息
	 * @param id 流水号
	 * @return
	 */
	Result<Boolean> deleteBorrowerInfo(Long id);
	
	/**
	 * 查找借款人信息
	 * @param id 流水号
	 * @return
	 */
	Result<BorrowerInfo> getBorrowerInfo(Long id);
}
