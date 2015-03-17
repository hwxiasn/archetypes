package com.qingbo.ginkgo.ygb.project.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.project.entity.LoaneeInfo;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.project.repository.LoaneeInfoRepository;
import com.qingbo.ginkgo.ygb.project.service.LoaneeInfoService;

@Service("loaneeInfoService")
public class LoaneeInfoServiceImpl implements LoaneeInfoService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private QueuingService queuingService;
	@Resource private LoaneeInfoRepository loaneeInfoRepository;

	public Result<LoaneeInfo> create(LoaneeInfo info) {
		Result<Long> queuing = queuingService.next(ProjectConstants.PROJECT_QUEUING);
		if(queuing.getError() != null ){
			return Result.newFailure(queuing.getError(), queuing.getMessage());
		}
		info.setId(queuing.getObject());
		logger.info("LoaneeInfo Create Id:"+info.getId()+" ProjectId:"+info.getProjectId());
		LoaneeInfo save = loaneeInfoRepository.save(info);
		return Result.newSuccess(save);
	}

	public Result<LoaneeInfo> detail(Long projectId) {
		logger.info("LoaneeInfo Load ProjectId:"+projectId);
		LoaneeInfo save = loaneeInfoRepository.findByProjectIdAndDeletedFalse(projectId);
		return Result.newSuccess(save);
	}

}
