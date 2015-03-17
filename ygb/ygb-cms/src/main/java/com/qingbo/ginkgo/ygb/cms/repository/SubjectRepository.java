package com.qingbo.ginkgo.ygb.cms.repository;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.cms.entity.Subject;

public interface SubjectRepository extends BaseRepository<Subject> {
	/**
	 * 通过专题编码获取专题
	 */
	Subject findByCode(String code);
}
