package com.qingbo.ginkgo.ygb.customer.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.customer.entity.UserEnterpriseProfile;



public interface UserEnterpriseProfileRepository extends BaseRepository<UserEnterpriseProfile> {
	UserEnterpriseProfile findByUserId(Long userId);
	List<UserEnterpriseProfile> findByContactPhone(String contactPhone);
	List<UserEnterpriseProfile> findByContactEmail(String contactEmail);
	
}
