package com.qingbo.ginkgo.ygb.web.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.project.entity.ProjectReview;
import com.qingbo.ginkgo.ygb.project.service.ProjectReviewService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectReviewBizService;

@Service("projectReviewBizService")
public class ProjectReviewBizServiceImpl implements ProjectReviewBizService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private ProjectReviewService projectReviewService;

	public ProjectReviewBizServiceImpl() {
	}

	public Result<ProjectReview> create(String userId, ProjectReview review) {
		logger.info("Create ProjectReview ");
		Result<ProjectReview> result = projectReviewService.create(review);
		return result;
	}

	public Result<List<ProjectReview>> list(String userId, Long projectId) {
		logger.info("Query Project Review Info.");
		Result<List<ProjectReview>> result = projectReviewService.list(projectId);
		return result;
	}

	@Override
	public Result<ProjectReview> update(String userId, ProjectReview review) {
		Result<ProjectReview> result = projectReviewService.update(review);
		return result;
	}

}
