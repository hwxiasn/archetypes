package com.qingbo.project.domain.shiro.common;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;


/**
 * 前后台用户角色判断，用于控制登录权限
 * @author hongwei
 * @date 2014-08-11
 */
public class RoleUtil {
	private static final String[] adminRoles = {"AA","A","NO","AO"};
	private static final String[] frontRoles = {"I","BR","B","S","AG","G","IE","SE","O","P"};
	
	public static boolean isAdminRole(String nameEn) {
		for(String role : adminRoles) {
			if(role.equalsIgnoreCase(nameEn))
				return true;
		}
		return false;
	}
	
	public static boolean isFrontRole(String nameEn) {
		for(String role : frontRoles) {
			if(role.equalsIgnoreCase(nameEn))
				return true;
		}
		return false;
	}
	
	public static boolean isAdminRole() {
		Session session = SecurityUtils.getSubject().getSession(false);
		if(session == null) return false;
		Object userRole = session.getAttribute("userRole");
		if(userRole == null) return false;
		String role = ObjectUtils.toString(userRole);
		return isAdminRole(role);
	}
	
	public static boolean isFrontRole() {
		Session session = SecurityUtils.getSubject().getSession(false);
		if(session == null) return false;
		Object userRole = session.getAttribute("userRole");
		if(userRole == null) return false;
		String role = ObjectUtils.toString(userRole);
		return isFrontRole(role);
	}
}
