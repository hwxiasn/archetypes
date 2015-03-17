package com.qingbo.ginkgo.ygb.project.service;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.project.entity.LoaneeInfo;

public interface LoaneeInfoService {
	/**
	 * 创建一个借款人信息披露条目
	 * @param info
	 * @return
	 */
	Result<LoaneeInfo> create(LoaneeInfo info);
	
	/**
	 * 按照项目ID调阅一个借款人信息
	 * @param projectId
	 * @return
	 */
	Result<LoaneeInfo> detail(Long projectId);

}
