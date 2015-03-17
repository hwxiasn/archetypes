package com.qingbo.ginkgo.ygb.project.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.project.entity.ProjectReview;

public interface ProjectReviewService {
	/**
	 * 创建一个项目审核信息
	 */
	public Result<ProjectReview> create(ProjectReview review);
	/**
	 * 更新一个审核记录
	 */
	public Result<ProjectReview> update(ProjectReview review);
	/**
	 * 依据项目ID查询全部的项目审批信息
	 */
	public Result<List<ProjectReview>> list(Long projectId);
	
}
