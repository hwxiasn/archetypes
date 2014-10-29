package com.qingbo.gingko.web.service.impl;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.gingko.entity.User;
import com.qingbo.gingko.integration.shiro.common.ShiroTool;
import com.qingbo.gingko.web.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired private com.qingbo.gingko.domain.UserService userService;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Integer register(User user) {
		Integer userId = userService.saveUser(user);
		return userId;
	}

	@Override
	public User getUser(String userName) {
		User user = userService.getUser(userName);
		return user;
	}

	@Override
	public boolean[] login(String userName, String password) {
		boolean[] login = new boolean[2];
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(new UsernamePasswordToken(userName, password));
        } catch (UnknownAccountException e) {
        	logger.info("not exist userName: "+userName);
        } catch(DisabledAccountException e) {
        	logger.info("disabled userName: "+userName);
        } catch(IncorrectCredentialsException e) {
        	logger.info("incorrect password for userName: "+userName);
        } catch(ExcessiveAttemptsException e) {
        	logger.info("excessive attempts for userName: "+userName+", times="+e.getMessage());
        }
		
		if (subject.isAuthenticated()) {
			login[0] = true;
			login[1] = ShiroTool.isFrontAuthenticated();
			logger.info("successful login of userName:" + userName);
		}else {
			logger.info("fail to login of userName: "+userName);
		}
		return login;
	}

	@Override
	public void logout() {
		SecurityUtils.getSubject().logout();
	}

}
