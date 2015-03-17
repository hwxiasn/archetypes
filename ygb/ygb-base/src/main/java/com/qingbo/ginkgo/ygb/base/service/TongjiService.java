package com.qingbo.ginkgo.ygb.base.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;

@SuppressWarnings("rawtypes")
public interface TongjiService {
	Result<Integer> count(SqlBuilder sqlBuilder);
	Result<List> list(SqlBuilder sqlBuilder);
	Result<Object[]> sums(SqlBuilder sqlBuilder);
}
