package com.qingbo.ginkgo.ygb.project.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.project.entity.ProjectReview;

public interface ProjectReviewRepository extends BaseRepository<ProjectReview> {
	List<ProjectReview> findByProjectIdAndDeletedFalse(Long id);
}
