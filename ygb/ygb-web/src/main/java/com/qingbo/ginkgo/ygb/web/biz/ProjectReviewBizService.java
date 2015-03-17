package com.qingbo.ginkgo.ygb.web.biz;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.ProjectReview;

public interface ProjectReviewBizService {
	/**
	 * 创建一个项目审核记录
	 * @param userId
	 * @param review
	 * @return
	 */
	public Result<ProjectReview> create(String userId,ProjectReview review);
	
	public Result<ProjectReview> update(String userId,ProjectReview review);
	/**
	 * 列表一个项目审核记录
	 * @param userId
	 * @param projectId
	 * @return
	 */
	public Result<List<ProjectReview>>  list(String userId,Long projectId);
}
