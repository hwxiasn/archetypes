package com.qingbo.project.domain.shiro.common;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** velocity toolbox shiro object */
public class ShiroTool {
	private static final String ROLE_NAMES_DELIMETER = ",";
	private static final String PERMISSION_NAMES_DELIMETER = ",";

	private static final Logger logger = LoggerFactory.getLogger(ShiroTool.class);

	/** 已认证 */
	public boolean isAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.isAuthenticated() == true;
	}
	
	/** 后台认证 */
	public boolean isAdminAuthenticated() {
		return isAuthenticated() && RoleUtil.isAdminRole();
	}
	
	/** 前端认证 */
	public boolean isFrontAuthenticated() {
		return isAuthenticated() && RoleUtil.isFrontRole();
	}

	public boolean isNotAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		return subject == null || subject.isAuthenticated() == false;
	}

	/** 访客 */
	public boolean isGuest() {
		Subject subject = SecurityUtils.getSubject();
		return subject == null || subject.getPrincipal() == null;
	}

	/** 用户 */
	public boolean isUser() {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.getPrincipal() != null;
	}
	
	public boolean isAdminUser() {
		return isUser() && RoleUtil.isAdminRole();
	}
	
	public boolean isFrontUser() {
		return isUser() && RoleUtil.isFrontRole();
	}

	public Object getPrincipal() {
		Subject subject = SecurityUtils.getSubject();
		return subject != null ? subject.getPrincipal() : null;
	}

	public Object getPrincipalProperty(String property) {
		Subject subject = SecurityUtils.getSubject();
		Object value = null;

		if (subject != null) {
			Object principal = subject.getPrincipal();
			try {
				BeanInfo bi = Introspector.getBeanInfo(principal.getClass());

				boolean foundProperty = false;
				for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
					if (pd.getName().equals(property)) {
						value = pd.getReadMethod().invoke(principal, (Object[]) null);
						foundProperty = true;
						break;
					}
				}

				if (!foundProperty) {
					final String message = "Property [" + property + "] not found in principal of type [" + principal.getClass().getName() + "]";
					logger.trace(message);
				}
			} catch (Exception e) {
				final String message = "Error reading property [" + property + "] from principal of type [" + principal.getClass().getName() + "]";
				logger.trace(message);
			}
		}

		return value;
	}

	/** 角色判断 */
	public boolean hasRole(String role) {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.hasRole(role) == true;
	}

	public boolean lacksRole(String role) {
		return hasRole(role) != true;
	}

	/** 多个角色选一 */
	public boolean hasAnyRoles(String roleNames, String delimeter) {
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

	public boolean hasAnyRoles(String roleNames) {
		return hasAnyRoles(roleNames, ROLE_NAMES_DELIMETER);
	}

	public boolean hasAnyRoles(Collection<String> roleNames) {
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

	public boolean hasAnyRoles(String[] roleNames) {
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
	public boolean hasPermission(String permission) {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.isPermitted(permission);
	}

	public boolean lacksPermission(String permission) {
		return hasPermission(permission) != true;
	}

	/** 多个权限选一 */
	public boolean hasAnyPermissions(String permissions, String delimeter) {
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

	public boolean hasAnyPermissions(String permissions) {
		return hasAnyPermissions(permissions, PERMISSION_NAMES_DELIMETER);
	}

	public boolean hasAnyPermissions(Collection<String> permissions) {
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

	public boolean hasAnyPermissions(String[] permissions) {
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

}