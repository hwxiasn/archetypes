package com.qingbo.project.repository.search;

import java.util.Collection;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * select column from table where condition group by column having condition order by column limit from,length
 * @author hongwei
 * @date 2014-07-29
 */
public class SqlBuilder {
	private String select;
	private String from;
	private StringBuilder where = new StringBuilder();
	private String groupBy;
	private String having;
	private String orderBy;
	private String limit;
	
	public static SqlBuilder builder() {
		return new SqlBuilder();
	}
	
	/**
	 * @param select
	 * <li>*
	 * <li>column1,column2,...
	 * <li>count(column1),sum(column2),avg(column3),...
	 * <li>user.id,user.name,account.amount,...
	 */
	public SqlBuilder select(String select) {
		this.select = select;
		return this;
	}
	
	/**
	 * @param from
	 * <li>ts_user
	 * <li>ts_user u left join ts_account a on u.ts_user_id = a.ts_user_id
	 * <li>jpql: TsUser user left join user.tsAccount account
	 */
	public SqlBuilder from(String from) {
		this.from = from;
		return this;
	}
	
	/**
	 * @param where
	 * <li>null, to reset where
	 * <li>name like '%Jack%'
	 * <li>id in (1,2,3)
	 * <li>exists (select a.id from acount a where a.user_id=user.id)
	 * <li>name is empty
	 */
	public SqlBuilder where(String where) {
		if(StringUtils.isEmpty(where)) {
			this.where.setLength(0);
		}else {
			this.where.append(" and " + where);
		}
		return this;
	}
	
	/**
	 * @param columnOperator
	 * <li>column1 >=
	 * <li>column2 !=
	 */
	public SqlBuilder where(String columnOperator, Object value) {
		if(!StringUtils.isEmpty(value)) {
			where.append(" and " + columnOperator + " '" + sqlParam(value.toString()) + "'");
		}
		return this;
	}
	
	public SqlBuilder groupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}
	
	public SqlBuilder having(String having) {
		this.having = having;
		return this;
	}
	
	public SqlBuilder orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	/**
	 * @param limit
	 * <li>limit 0,10
	 */
	public SqlBuilder limit(String limit) {
		this.limit = limit;
		return this;
	}
	
	public SqlBuilder eq(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			where.append(" and " + column + " = '" + sqlParam(value.toString()) + "'");
		}
		return this;
	}
	
	public SqlBuilder ne(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			where.append(" and " + column + " != '" + sqlParam(value.toString()) + "'");
		}
		return this;
	}
	
	public SqlBuilder like(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			where.append(" and " + column + " like '%" + sqlParam(value.toString()) + "%'");
		}
		return this;
	}
	
	public SqlBuilder notLike(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			where.append(" and " + column + " not like '%" + sqlParam(value.toString()) + "%'");
		}
		return this;
	}
	
	public SqlBuilder in(String column, Collection<?> value) {
		if(!CollectionUtils.isEmpty(value)) {
			where.append(" and " + column + " in (" + StringUtils.collectionToDelimitedString(value, ",", "'", "'") + ")");
		}
		return this;
	}
	
	public SqlBuilder notIn(String column, Collection<?> value) {
		if(!CollectionUtils.isEmpty(value)) {
			where.append(" and " + column + " not in (" + StringUtils.collectionToDelimitedString(value, ",", "'", "'") + ")");
		}
		return this;
	}
	
	public SqlBuilder gt(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			where.append(" and " + column + " > '" + sqlParam(value.toString()) + "'");
		}
		return this;
	}
	
	public SqlBuilder gte(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			where.append(" and " + column + " >= '" + sqlParam(value.toString()) + "'");
		}
		return this;
	}
	
	public SqlBuilder lt(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			where.append(" and " + column + " < '" + sqlParam(value.toString()) + "'");
		}
		return this;
	}
	
	public SqlBuilder lte(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			where.append(" and " + column + " <= '" + sqlParam(value.toString()) + "'");
		}
		return this;
	}
	
	public SqlBuilder between(String column, Object from, Object to) {
		if(StringUtils.isEmpty(from) && StringUtils.isEmpty(to)) return this;
		if(!StringUtils.isEmpty(from)) where.append(" and " + column + " >= '" + sqlParam(from.toString()) + "'");
		else if(!StringUtils.isEmpty(to)) where.append(" and " + column + " <= '" + sqlParam(to.toString()) + "'");
		else where.append(" and " + column + " between '" + sqlParam(from.toString()) + "' and '" + sqlParam(to.toString()) + "'");
		return this;
	}
	
	public String sql() {
		if(select == null || from == null) //必须得有selece和from部分
			throw new RuntimeException("sql must contain select + from parts.");
		
		StringBuilder sql = new StringBuilder("select " + select+" from "+from);
		if(where.length() > 4) sql.append(" where " + where.substring(5));
		if(groupBy != null) sql.append(" group by " + groupBy);
		if(having != null) sql.append(" having " + having);
		if(orderBy != null) sql.append(" order by " + orderBy);
		if(limit != null) sql.append(" limit " + limit);
		return sql.toString();
	}
	
	public static String sqlParam(String sqlParam) {
		return sqlParam.replaceAll("([';]+|(--)+)", "");
	}
}
