package com.qingbo.gingko.integration.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.gingko.domain.RolePermissionService;
import com.qingbo.gingko.domain.UserService;
import com.qingbo.gingko.entity.User;
import com.qingbo.gingko.entity.enums.UserStatus;

public class UserRealm extends AuthorizingRealm {
	@Autowired UserService userService;
	@Autowired RolePermissionService rolePermissionService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(rolePermissionService.getRoles(username));
        authorizationInfo.setStringPermissions(rolePermissionService.getPermissions(username));
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        User user = userService.getUser(username);

        if (user == null || user.isDeleted()) {
            throw new UnknownAccountException();//没找到帐号或账号已删除，通常还需要检查用户状态阻止禁用的用户登录
        } else if (UserStatus.DISABLED.getCode().equals(user.getStatus())) {
        	throw new DisabledAccountException();//账号禁用
        }

        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("userId", user.getId());
    	session.setAttribute("userName", username);
    	session.setAttribute("userRoles", rolePermissionService.getRoles(user.getId()));
        
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUserName(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getUserName()+user.getSalt()),//salt=username+salt, 与PasswordHelper.encryptPassword对应
                getName()  //realm name
        );
        return authenticationInfo;
    }
}
