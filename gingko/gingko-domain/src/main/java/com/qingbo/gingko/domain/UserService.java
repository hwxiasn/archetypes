package com.qingbo.gingko.domain;

import org.springframework.data.jpa.domain.Specification;

import com.qingbo.gingko.common.util.Pager;
import com.qingbo.gingko.entity.User;

public interface UserService {
	/**
	 * 获取用户
	 */
	User getUser(String userName);
	/**
	 * 添加或更新用户
	 */
	Integer saveUser(User user);
	/**
	 * 分页查询用户
	 */
	void pageUser(Specification<User> spec, Pager pager);
	/**
	 * 校验用户密码
	 */
	boolean validatePassword(String userName, String password);
	/**
	 * 更新用户密码
	 */
	boolean updatePassword(String userName, String oldPassword, String newPassword);
	/**
	 * 重置用户密码
	 */
	boolean resetPassword(String userName, String password);
}
