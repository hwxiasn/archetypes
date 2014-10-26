package com.qingbo.gingko.integration.shiro.common;

import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.qingbo.gingko.entity.Role;

/** velocity toolbox shiro object */
public class ShiroTool {
	private static final String ROLE_NAMES_DELIMETER = ",";
	private static final String PERMISSION_NAMES_DELIMETER = ",";
	
	public static Integer userId() {
		if(SecurityUtils.getSubject().isAuthenticated()) {
			Session session = SecurityUtils.getSubject().getSession();
			Object userId = session.getAttribute("userId");
			if(userId!=null) return (Integer)userId;
		}
		return null;
	}
	
	public static String userName() {
		if(SecurityUtils.getSubject().isAuthenticated()) {
			Session session = SecurityUtils.getSubject().getSession();
			Object userName = session.getAttribute("userName");
			if(userName!=null) return (String)userName;
		}
		return null;
		
	}

	/** 已认证 */
	public static boolean isAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.isAuthenticated() == true;
	}
	
	public static boolean isNotAuthenticated() {
		return isAuthenticated() == false;
	}
	
	/** 后台认证 */
	public static boolean isAdminAuthenticated() {
		return isAuthenticated() && checkRole(false);
	}
	
	/** 前端认证 */
	public static boolean isFrontAuthenticated() {
		return isAuthenticated() && checkRole(true);
	}

	/** 角色判断 */
	public static boolean hasRole(String role) {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.hasRole(role) == true;
	}

	public static boolean lacksRole(String role) {
		return hasRole(role) != true;
	}

	/** 多个角色选一 */
	public static boolean hasAnyRoles(String roleNames, String delimeter) {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			if (delimeter == null || delimeter.length() == 0) {
				delimeter = ROLE_NAMES_DELIMETER;
			}

			for (String role : roleNames.split(delimeter)) {
				if (subject.hasRole(role.trim()) == true) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean hasAnyRoles(String roleNames) {
		return hasAnyRoles(roleNames, ROLE_NAMES_DELIMETER);
	}

	public static boolean hasAnyRoles(Collection<String> roleNames) {
		Subject subject = SecurityUtils.getSubject();

		if (subject != null && roleNames != null) {
			for (String role : roleNames) {
				if (role != null && subject.hasRole(role.trim())) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean hasAnyRoles(String[] roleNames) {
		Subject subject = SecurityUtils.getSubject();

		if (subject != null && roleNames != null) {
			for (int i = 0; i < roleNames.length; i++) {
				String role = roleNames[i];
				if (role != null && subject.hasRole(role.trim())) {
					return true;
				}
			}
		}

		return false;
	}

	/** 权限判断 */
	public static boolean hasPermission(String permission) {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.isPermitted(permission);
	}

	public static boolean lacksPermission(String permission) {
		return hasPermission(permission) != true;
	}

	/** 多个权限选一 */
	public static boolean hasAnyPermissions(String permissions, String delimeter) {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			if (delimeter == null || delimeter.length() == 0) {
				delimeter = PERMISSION_NAMES_DELIMETER;
			}

			for (String permission : permissions.split(delimeter)) {
				if (permission != null
						&& subject.isPermitted(permission.trim()) == true) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean hasAnyPermissions(String permissions) {
		return hasAnyPermissions(permissions, PERMISSION_NAMES_DELIMETER);
	}

	public static boolean hasAnyPermissions(Collection<String> permissions) {
		Subject subject = SecurityUtils.getSubject();

		if (subject != null && permissions != null) {
			for (String permission : permissions) {
				if (permission != null
						&& subject.isPermitted(permission.trim())) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean hasAnyPermissions(String[] permissions) {
		Subject subject = SecurityUtils.getSubject();

		if (subject != null && permissions != null) {
			for (int i = 0; i < permissions.length; i++) {
				String permission = permissions[i];
				if (permission != null
						&& subject.isPermitted(permission.trim())) {
					return true;
				}
			}
		}

		return false;
	}
	
	private static Role[] getRoles() {
		if(SecurityUtils.getSubject().isAuthenticated()) {
			Session session = SecurityUtils.getSubject().getSession();
			Object userRoles = session.getAttribute("userRoles");
			if(userRoles!=null) return (Role[])userRoles;
		}
		return null;
	}
	
	private static boolean checkRole(boolean front) {
		Role[] roles = getRoles();
		if(roles != null) {
			for(Role role:roles) {
				if(front && role.isFrontRole()) return true;
				if(!front && role.isAdminRole()) return true;
			}
		}
		return false;
	}
}