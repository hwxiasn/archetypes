package com.qingbo.ginkgo.ygb.project.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.project.entity.ProjectReview;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.project.repository.ProjectReviewRepository;
import com.qingbo.ginkgo.ygb.project.service.ProjectReviewService;

@Service("projectReviewService")
public class ProjectReviewServiceImpl implements ProjectReviewService {
	@Resource private ProjectReviewRepository projectReviewRepository;
	@Resource private QueuingService queuingService;

	public ProjectReviewServiceImpl() {
	}

	public Result<ProjectReview> create(ProjectReview review) {
		Result<Long> queuing = queuingService.next(ProjectConstants.PROJECT_QUEUING);
		if(queuing.getError() != null ){
			return Result.newFailure(queuing.getError(), queuing.getMessage());
		}
		review.setId(queuing.getObject());
		review = projectReviewRepository.save(review);
		return Result.newSuccess(review);
	}

	public Result<List<ProjectReview>> list(Long projectId) {
		try{
			List<ProjectReview> list = projectReviewRepository.findByProjectIdAndDeletedFalse(projectId);
			if(list == null){
				list = new ArrayList<ProjectReview>();
			}
			return Result.newSuccess(list);
		}catch(Exception e){
			return Result.newFailure("","数据库操作失败");
		}
	}
	
	public Result<ProjectReview> update(ProjectReview review) {
		ProjectReview result = projectReviewRepository.save(review);
		return Result.newSuccess(result);
	}

}
