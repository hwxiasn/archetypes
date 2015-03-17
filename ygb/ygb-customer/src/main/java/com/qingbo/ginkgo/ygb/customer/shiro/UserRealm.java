package com.qingbo.ginkgo.ygb.customer.shiro;

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

import ch.qos.logback.classic.Logger;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.customer.entity.Operator;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserEnterpriseProfile;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants.Status;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.service.OperatorService;
import com.qingbo.ginkgo.ygb.customer.service.RolePermissionService;

public class UserRealm extends AuthorizingRealm {
	@Autowired private CustomerService customerService;
	@Autowired private RolePermissionService rolePermissionService;
	@Autowired private OperatorService operatorService;
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
        Result<User> userResult = customerService.getUserByUserName(username);
        User user = userResult.success() ? userResult.getObject() : null;

        if (user == null || user.isDeleted()) {
            throw new UnknownAccountException();//没找到帐号或账号已删除，通常还需要检查用户状态阻止禁用的用户登录
        } else if (Status.DISABLED.getCode().equals(user.getStatus())) {
        	throw new DisabledAccountException();//账号禁用
        }
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("userId", user.getId());
    	session.setAttribute("userName", username);
    	session.setAttribute("userRoles", rolePermissionService.getRoles(user.getId()));
        //如果角色是操作员，真实姓名在operator表中取,如果不是，就用userResult中的真实姓名即可
        if((CustomerConstants.Role.OPERATOR.getCode()).equals(user.getRole())) {
        	try {
        		Result<Operator> operatorResult = operatorService.listOperatorByUserId(user.getId());
            	Operator operator = operatorResult.success() ?operatorResult.getObject() :null;
            	if(operator!=null) {
            		session.setAttribute("realName", operator.getDisplayOrgName());
            	}
        	}catch(Exception e) {
        		System.out.println(user.getId()+"的操作员不存在");
        	}
        }else {
        	if(CustomerConstants.UserRegisterType.PERSONAL.getCode().equals(user.getRegisterType())) {
        		UserProfile userProfile = user.getUserProfile();
        		if(userProfile!=null) {
	        		session.setAttribute("realName", userProfile.getRealName());
	            	session.setAttribute("customerNum", userProfile.getCustomerNum());
        		}
        	}else {
        		UserEnterpriseProfile enterpriseProfile = user.getEnterpriseProfile();
        		if(enterpriseProfile!=null) {
	        		session.setAttribute("realName", enterpriseProfile.getEnterpriseName());
	            	session.setAttribute("customerNum", null);
        		}
        	}
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUserName(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getUserName()+user.getSalt()),//salt=username+salt, 与PasswordHelper.encryptPassword对应
                getName()  //realm name
        );
        return authenticationInfo;
    }
}
