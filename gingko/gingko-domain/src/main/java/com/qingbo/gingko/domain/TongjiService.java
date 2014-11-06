package com.qingbo.gingko.domain;

import java.util.List;

import com.qingbo.gingko.common.result.Result;
import com.qingbo.gingko.common.util.SqlBuilder;

@SuppressWarnings("rawtypes")
public interface TongjiService {
	Result<Integer> count(SqlBuilder sqlBuilder);
	Result<List> list(SqlBuilder sqlBuilder);
	Result<Object[]> sums(SqlBuilder sqlBuilder);
}
