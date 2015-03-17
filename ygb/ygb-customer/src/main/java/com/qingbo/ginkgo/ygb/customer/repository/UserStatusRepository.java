package com.qingbo.ginkgo.ygb.customer.repository;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.customer.entity.UserStatus;

public interface UserStatusRepository extends BaseRepository<UserStatus>{
	UserStatus findByUserId(Long userId);
	
}
