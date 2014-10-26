package com.qingbo.gingko.domain;

import java.util.Set;

import com.qingbo.gingko.entity.Role;

public interface RolePermissionService {
	/**
	 * 获取用户角色，user.role_ids
	 */
	Set<String> getRoles(String userName);
	/**
	 * 获取用户角色，用于判断是否前台用户或后台用户
	 */
	Role[] getRoles(Integer userId);
	/**
	 * 获取用户权限，user.permission_ids+user.role_ids
	 */
	Set<String> getPermissions(String userName);
}
