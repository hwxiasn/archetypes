package com.qingbo.ginkgo.ygb.account.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;

public interface SubAccountRepository extends BaseRepository<SubAccount> {
	List<SubAccount> findByAccountId(Long accountId);
	SubAccount findByAccountIdAndType(Long accountId, String type);
}
