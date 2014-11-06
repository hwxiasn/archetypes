package com.qingbo.gingko.repository;

import java.util.List;

import com.qingbo.gingko.entity.SubAccount;

public interface SubAccountRepository extends BaseRepository<SubAccount, Integer> {
	List<SubAccount> findByAccountId(Integer accountId);
	SubAccount findByAccountIdAndType(Integer accountId, String type);
}
