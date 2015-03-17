package com.qingbo.ginkgo.ygb.base.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.entity.BankType;

public interface BankTypeRepository extends BaseRepository<BankType> {
	List<BankType> findByDeletedFalse();
	BankType findByCode(String code);
}
