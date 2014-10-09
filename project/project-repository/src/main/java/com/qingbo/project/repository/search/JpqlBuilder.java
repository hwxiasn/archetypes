package com.qingbo.project.repository.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * select column from table where condition group by column having condition order by column limit from,length
 * @author hongwei
 * @date 2014-07-31
 */
public class JpqlBuilder {
	private String select;
	private String from;
	private StringBuilder where = new StringBuilder();
	private List<Object> args = new ArrayList<Object>();
	private String groupBy;
	private String having;
	private String orderBy;
	private String limit;
	
	public static JpqlBuilder builder() {
		return new JpqlBuilder();
	}
	
	/**
	 * @param select
	 * <li>*
	 * <li>user.id,user.name,account.amount,...
	 */
	public JpqlBuilder select(String select) {
		this.select = select;
		return this;
	}
	
	/**
	 * @param from
	 * <li>jpql: TsUser user left join user.tsAccount account
	 */
	public JpqlBuilder from(String from) {
		this.from = from;
		return this;
	}
	
	/**
	 * @param where
	 * <li>null, to reset where
	 * <li>name like '%Jack%'
	 */
	public JpqlBuilder where(String where) {
		if(StringUtils.isEmpty(where)) {
			this.where.setLength(0);
			args.clear();
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
	public JpqlBuilder where(String columnOperator, Object value) {
		if(!StringUtils.isEmpty(value)) {
			args.add(value);
			where.append(" and " + columnOperator + " ?" + args.size());
		}
		return this;
	}
	
	public JpqlBuilder groupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}
	
	public JpqlBuilder having(String having) {
		this.having = having;
		return this;
	}
	
	public JpqlBuilder orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	/**
	 * @param limit
	 * <li>limit 0,10
	 */
	public JpqlBuilder limit(String limit) {
		this.limit = limit;
		return this;
	}
	
	public JpqlBuilder eq(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			args.add(value);
			where.append(" and " + column + " = ?" + args.size());
		}
		return this;
	}
	
	public JpqlBuilder ne(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			args.add(value);
			where.append(" and " + column + " != ?" + args.size());
		}
		return this;
	}
	
	public JpqlBuilder like(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			args.add(value);
			where.append(" and " + column + " like ?" + args.size());
		}
		return this;
	}
	
	public JpqlBuilder notLike(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			args.add(value);
			where.append(" and " + column + " not like ?" + args.size());
		}
		return this;
	}
	
	public JpqlBuilder in(String column, Collection<?> value) {
		if(!CollectionUtils.isEmpty(value)) {
			where.append(" and " + column + " in (");
			boolean first = true;
			for(Object obj : value) {
				args.add(obj);
				if(first) {
					first = false;
					where.append("?"+args.size());
				}else {
					where.append(" , ?"+args.size());
				}
			}
			where.append(")");
		}
		return this;
	}
	
	public JpqlBuilder notIn(String column, Collection<?> value) {
		if(!CollectionUtils.isEmpty(value)) {
			where.append(" and " + column + " not in (");
			boolean first = true;
			for(Object obj : value) {
				args.add(obj);
				if(first) {
					first = false;
					where.append("?"+args.size());
				}else {
					where.append(" , ?"+args.size());
				}
			}
			where.append(")");
		}
		return this;
	}
	
	public JpqlBuilder gt(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			args.add(value);
			where.append(" and " + column + " > ?" + args.size());
		}
		return this;
	}
	
	public JpqlBuilder gte(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			args.add(value);
			where.append(" and " + column + " >= ?" + args.size());
		}
		return this;
	}
	
	public JpqlBuilder lt(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			args.add(value);
			where.append(" and " + column + " < ?" + args.size());
		}
		return this;
	}
	
	public JpqlBuilder lte(String column, Object value) {
		if(!StringUtils.isEmpty(value)) {
			args.add(value);
			where.append(" and " + column + " <= ?" + args.size());
		}
		return this;
	}
	
	public JpqlBuilder between(String column, Object from, Object to) {
		if(StringUtils.isEmpty(from) && StringUtils.isEmpty(to)) return this;
		if(!StringUtils.isEmpty(from)) {
			args.add(from);
			where.append(" and " + column + " >= ?" + args.size());
		}else if(!StringUtils.isEmpty(to)) {
			args.add(to);
			where.append(" and " + column + " <= ?" + args.size());
		}else {
			args.add(from);
			args.add(to);
			where.append(" and " + column + " between ?" + (args.size()-1) + " and ?" + args.size());
		}
		return this;
	}
	
	public String jpql() {
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
	
	public Object[] args() {
		return args.toArray();
	}
}
