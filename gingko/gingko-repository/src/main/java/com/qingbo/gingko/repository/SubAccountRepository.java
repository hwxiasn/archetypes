package com.qingbo.gingko.repository;

import java.util.List;

import com.qingbo.gingko.entity.SubAccount;

public interface SubAccountRepository extends BaseRepository<SubAccount> {
	List<SubAccount> findByAccountId(Long accountId);
	SubAccount findByAccountIdAndType(Long accountId, String type);
}
