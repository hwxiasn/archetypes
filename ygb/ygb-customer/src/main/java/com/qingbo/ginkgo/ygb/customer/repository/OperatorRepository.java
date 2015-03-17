package com.qingbo.ginkgo.ygb.customer.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.customer.entity.Operator;

public interface OperatorRepository extends BaseRepository<Operator>{

	List<Operator> findByOrganizationId(Long organizationId);
	
	Operator findByUserName(String userName);
	
	Operator findByUserId(Long userId);
	
}
