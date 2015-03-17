package com.qingbo.ginkgo.ygb.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.repository.TongjiRepository;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;

@Service("tongjiService")
@SuppressWarnings("rawtypes")
public class TongjiServiceImpl implements TongjiService {
	@Autowired private TongjiRepository tongjiRepository;

	@Override
	public Result<Integer> count(SqlBuilder sqlBuilder) {
		try {
			int count = tongjiRepository.count(sqlBuilder.sql());
			return Result.newSuccess(count);
		}catch (Exception e) {
			return Result.newFailure(1, e.getMessage());
		}
	}

	@Override
	public Result<List> list(SqlBuilder sqlBuilder) {
		try {
			List list = tongjiRepository.list(sqlBuilder.sql());
			return Result.newSuccess(list);
		}catch (Exception e) {
			return Result.newFailure(1, e.getMessage());
		}
	}

	@Override
	public Result<Object[]> sums(SqlBuilder sqlBuilder) {
		try {
			Object[] sums = tongjiRepository.sums(sqlBuilder.sql());
			return Result.newSuccess(sums);
		}catch (Exception e) {
			return Result.newFailure(1, e.getMessage());
		}
	}

}
