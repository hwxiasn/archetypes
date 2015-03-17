package com.qingbo.ginkgo.ygb.customer.repository;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.customer.entity.User;



public interface UserRepository extends BaseRepository<User> {
	User findByUserName(String userName);
	User findByActivateCode(String activatedCode);
}
