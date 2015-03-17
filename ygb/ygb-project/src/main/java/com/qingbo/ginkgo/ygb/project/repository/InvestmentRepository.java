package com.qingbo.ginkgo.ygb.project.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.project.entity.Investment;

public interface InvestmentRepository extends BaseRepository<Investment> {
	List<Investment> findByProjectIdAndDeletedFalse(Long id);
}
