package com.qingbo.ginkgo.ygb.project.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.project.repository.RepaymentRepository;
import com.qingbo.ginkgo.ygb.project.service.RepaymentService;


@Service("repaymentService")
public class RepaymentServiceImpl implements RepaymentService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private RepaymentRepository repaymentRepository;
	@Resource private QueuingService queuingService;

	@Override
	public Result<Repayment> getRepayment(Long id) {
		try {
			Repayment repay = repaymentRepository.findOne(id);
			if (repay != null) {
				return Result.newSuccess(repay);
			}
		} catch (Exception e) {
			logger.info("Load RepaymentById Error Id:"+id);
		}
		return Result.newFailure("", "Has No Repayment");
	}
	public Result<Repayment> getRepaymentByProjectId(Long projectId) {
		try{
			Repayment repay = repaymentRepository.findByProjectIdAndDeletedFalse(projectId);
			if(repay != null){
				return Result.newSuccess(repay);
			}
		}catch(Exception e){
			logger.info("Load RepaymentByProjectId Error ProjectId:"+projectId);
		}
		return Result.newFailure("", "Has No Repayment");
	}

	@Override
	public Result<Repayment> createRepayment(Repayment repayment) {
		Result<Long> queuing = queuingService.next(ProjectConstants.PROJECT_QUEUING);
		if(queuing.getError() != null ){
			return Result.newFailure(queuing.getError(), queuing.getMessage());
		}
		repayment.setId(queuing.getObject());
		try{
			repayment = repaymentRepository.save(repayment);
			return Result.newSuccess(repayment);
		}catch(Exception e){
			logger.info("CreateRepayment Error DB");
		}
		return Result.newFailure("", "Create Repayment Failed.");
	}

	@Override
	public Result<Boolean> updateRepayment(Repayment repayment) {
		repayment = repaymentRepository.save(repayment);
		return Result.newSuccess(true);
	}

	@Override
	public Result<PageObject<Repayment>> listRepaymentBySpecAndPage(
			SpecParam<Repayment> spec, Pager page) {
		spec.eq("deleted", false);// 未删除
		Pageable pageable = page.getDirection() == null || page.getProperties() == null ? new PageRequest(page.getCurrentPage() - 1, page.getPageSize()) :new PageRequest(page.getCurrentPage() - 1, page.getPageSize(),Direction.valueOf(page.getDirection()),page.getProperties().split(","));
		Page<Repayment> resultSet = repaymentRepository.findAll(SpecUtil.spec(spec), pageable);
		Result<PageObject<Repayment>> result = Result.newSuccess(new PageObject<Repayment>((int)resultSet.getTotalElements(), resultSet.getContent()));
		return result;
	}

}
