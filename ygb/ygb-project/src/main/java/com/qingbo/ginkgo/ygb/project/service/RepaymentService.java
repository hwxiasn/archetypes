package com.qingbo.ginkgo.ygb.project.service;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;


public interface RepaymentService {
	Result<Repayment> getRepayment(Long id);
	Result<Repayment> createRepayment(Repayment repayment);
	Result<Boolean> updateRepayment(Repayment repayment);
	Result<PageObject<Repayment>> listRepaymentBySpecAndPage(SpecParam<Repayment> spec,Pager page);
	public Result<Repayment> getRepaymentByProjectId(Long projectId);
}
