package com.qingbo.ginkgo.ygb.project.impl;

import java.util.Date;

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
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.project.repository.InvestmentRepository;
import com.qingbo.ginkgo.ygb.project.service.InvestmentService;
import com.qingbo.ginkgo.ygb.project.enums.*;

@Service("investmentService")
public class InvestmentServiceImpl implements InvestmentService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private InvestmentRepository investmentRepository;
	@Resource private QueuingService queuingService;

	public Result<Investment> getInvestment(Long id) {
		Investment inv = investmentRepository.findOne(id);
		return Result.newSuccess(inv);
	}

	public Result<Investment> createInvestment(Investment investment) {
		Result<Long> queuing = queuingService.next(ProjectConstants.PROJECT_QUEUING);
		if(queuing.getError() != null ){
			return Result.newFailure(queuing.getError(), queuing.getMessage());
		}
		investment.setId(queuing.getObject());
		investment = investmentRepository.save(investment);
		return  Result.newSuccess(investment);
	}

	public Result<Boolean> updateInvestment(Investment investment) {
//		Investment inv = investmentRepository.findOne(investment.getId());
//		if(inv == null){
//			return Result.newFailure("", "不存在该投资凭证");
//		}
//		if(investment.getStatus() != null){
//			inv.setStatus(investment.getStatus());
//		}
//		if(investment.getInvestNo() != null){
//			inv.setInvestNo(investment.getInvestNo());
//		}
//		if(investment.getInvestAccNo() != null){
//			inv.setInvestAccNo(investment.getInvestAccNo());
//		}
//		if(investment.getSettledDate() != null){
//			inv.setSettledDate(investment.getSettledDate());
//		}
//		if(investment.getDueDate() != null){
//			inv.setDueDate(investment.getDueDate());
//		}
		try{
			investment = investmentRepository.save(investment);
			return Result.newSuccess(true);
		}catch(Exception e){
			return Result.newFailure("", "Investment Update DB error.ID:"+investment.getId());
		}
	}

	public Result<PageObject<Investment>> listInvestmentBySpecAndPage(SpecParam<Investment> spec, Pager page) {
		spec.eq("deleted", false);// 未删除
		Pageable pageable = page.getDirection() == null || page.getProperties() == null ? new PageRequest(page.getCurrentPage() - 1, page.getPageSize()) :new PageRequest(page.getCurrentPage() - 1, page.getPageSize(),Direction.valueOf(page.getDirection()),page.getProperties().split(","));
		Page<Investment> resultSet = investmentRepository.findAll(SpecUtil.spec(spec), pageable);
		Result<PageObject<Investment>> result = Result.newSuccess(new PageObject<Investment>((int)resultSet.getTotalElements(), resultSet.getContent()));
		return result;
	}

}
