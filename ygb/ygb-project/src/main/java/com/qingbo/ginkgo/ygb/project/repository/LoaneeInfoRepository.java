package com.qingbo.ginkgo.ygb.project.repository;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.project.entity.LoaneeInfo;

public interface LoaneeInfoRepository extends BaseRepository<LoaneeInfo> {
	LoaneeInfo findByProjectIdAndDeletedFalse(Long projectId);
}
