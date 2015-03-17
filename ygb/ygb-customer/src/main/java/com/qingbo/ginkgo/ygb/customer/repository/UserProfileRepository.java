package com.qingbo.ginkgo.ygb.customer.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;


public interface UserProfileRepository extends BaseRepository<UserProfile> {
	UserProfile findByUserId(Long userId);
	List<UserProfile> findByMobile(String mobile);
	List<UserProfile> findByEmail(String email);
	UserProfile findByCustomerNum(String customerNum);
}
