package com.qingbo.project.domain;

import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import com.qingbo.project.common.util.Pager;
import com.qingbo.project.entity.User;

public interface UserDomain {
	/**
	 * 获取用户角色，user.role_ids
	 */
	Set<String> getRoles(String userName);
	/**
	 * 获取用户权限，user.permission_ids+user.role_ids
	 */
	Set<String> getPermissions(String userName);
	/**
	 * 获取用户
	 */
	User getUser(String userName);
	/**
	 * 添加或更新用户
	 */
	void saveUser(User user);
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
