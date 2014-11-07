package com.qingbo.gingko.domain;

import com.qingbo.gingko.common.result.PageObject;
import com.qingbo.gingko.common.result.Result;
import com.qingbo.gingko.common.result.SpecParam;
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
	Long saveUser(User user);
	/**
	 * 分页查询用户
	 */
	Result<PageObject<User>> page(SpecParam<User> specs, Pager pager);
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
