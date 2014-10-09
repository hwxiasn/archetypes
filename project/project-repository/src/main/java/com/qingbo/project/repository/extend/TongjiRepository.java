package com.qingbo.project.repository.extend;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.qingbo.project.repository.search.JpqlBuilder;
import com.qingbo.project.repository.search.SqlBuilder;

/**
 * 统计服务
 * @author hongwei
 * @Date 2014-07-28
 */
public interface TongjiRepository {
	//Criteria API
	/** 获取查询构建器 */
	CriteriaBuilder builder();
	/** 计算查询结果总数 */
	long count(CriteriaQuery<Long> query);
	/** 计算多个统计值，对应总计行 */
	Object[] sums(CriteriaQuery<Tuple> query);
	/** 获取查询结果分页，maxResults<=0时获取全部结果 */
	List<Tuple> list(CriteriaQuery<Tuple> query, int firstResult, int maxResults);
	
	//Java Persistence Query Language
	/** 计算总数 */
	int count(JpqlBuilder jpqlBuilder);
	/** 计算多个统计值 */
	Object[] sums(JpqlBuilder jpqlBuilder);
	/** 获得统计列表 */
	List<?> list(JpqlBuilder jpqlBuilder);
	
	//SQL
	/** 计算总数 */
	int count(SqlBuilder sqlBuilder);
	/** 计算多个统计值 */
	Object[] sums(SqlBuilder sqlBuilder);
	/** 获得统计列表 */
	List<?> list(SqlBuilder sqlBuilder);
}
