package com.qingbo.ginkgo.ygb.account.repository;

import com.qingbo.ginkgo.ygb.account.entity.AccountDaily;
import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;

public interface AccountDailyRespository extends BaseRepository<AccountDaily> {
	AccountDaily findByAccountIdAndDaily(Long accountId, String daily);
}
