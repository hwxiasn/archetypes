package com.qingbo.ginkgo.ygb.base.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.entity.StateCity;

public interface StateCityRepository extends BaseRepository<StateCity> {
	List<StateCity> findByParentCodeNullAndDeletedFalse();
	List<StateCity> findByParentCodeAndDeletedFalse(String state);
	StateCity findByCode(String code);
}
