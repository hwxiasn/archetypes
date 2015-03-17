package com.qingbo.ginkgo.ygb.account.repository;

import com.qingbo.ginkgo.ygb.account.entity.SubQddAccount;
import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;

public interface SubQddAccountRepository extends BaseRepository<SubQddAccount> {
	SubQddAccount findByMoneyMoreMoreId(String moneyMoreMoreId);
}
