package com.qingbo.ginkgo.ygb.web.biz.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;
import com.qingbo.ginkgo.ygb.project.service.RepaymentService;
import com.qingbo.ginkgo.ygb.web.biz.RepaymentBizService;

@Service
public class RepaymentBizServiceImpl implements RepaymentBizService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource private RepaymentService repaymentService;

	public Result<Repayment> detail(String userId, Long projectId) {
		logger.info("用户ID:" + userId + ", 还款ID：" + projectId);
		Result<Repayment> repay = repaymentService.getRepaymentByProjectId(projectId);
		return repay;
	}

	public Result<Pager> list(String userId, Repayment search,Pager pager) {
		SpecParam<Repayment> spec = new SpecParam<Repayment>();
		if(search.getProjectId()>0L){
			spec.eq("projectId", search.getProjectId());
		}
		if(search.getLoaneeId()>0L){
			spec.eq("loaneeId", search.getLoaneeId());
		}
		spec.eq("status", search.getStatus());
		Result<PageObject<Repayment>> pageObject = repaymentService.listRepaymentBySpecAndPage(spec, pager);
		pager.setElements(pageObject.getObject().getList());
		pager.init(pageObject.getObject().getTotal());
		return Result.newSuccess(pager);
	}

	public Result<Boolean> update(String userId, Long id, String status) {
		Result<Repayment> result = repaymentService.getRepayment(id);
		Repayment repay = result.getObject();
		repay.setStatus(status);
		Result<Boolean> resultBoolean = repaymentService.updateRepayment(repay);
		return resultBoolean;
	}

}
