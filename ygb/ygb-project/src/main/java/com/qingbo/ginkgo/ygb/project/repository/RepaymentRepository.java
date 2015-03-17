package com.qingbo.ginkgo.ygb.project.repository;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;

public interface RepaymentRepository extends BaseRepository<Repayment> {
	Repayment findByProjectIdAndDeletedFalse(Long projectId);
}
