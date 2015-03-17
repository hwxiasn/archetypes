package com.qingbo.ginkgo.ygb.base.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.entity.CodeList;

public interface CodeListRepository extends BaseRepository<CodeList> {
	List<CodeList> findByTypeAndDeletedFalse(String type);
	CodeList findByTypeAndCode(String type, String code);
}
