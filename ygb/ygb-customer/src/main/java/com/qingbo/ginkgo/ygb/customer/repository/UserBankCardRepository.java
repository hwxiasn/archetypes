package com.qingbo.ginkgo.ygb.customer.repository;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;

public interface UserBankCardRepository extends BaseRepository<UserBankCard>{
	UserBankCard findByUserId(Long userId);
	
}
