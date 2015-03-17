package com.qingbo.ginkgo.ygb.project.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.project.entity.CommissionDetail;

public interface CommissionDetailRepository extends BaseRepository<CommissionDetail> {
	List<CommissionDetail> findByTemplateIdAndDeletedFalse(Long id);
}
