package com.qingbo.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.project.domain.UserDomain;
import com.qingbo.project.entity.User;
import com.qingbo.project.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired private UserDomain userDomain;

	@Override
	public User getUser(String userName) {
		return userDomain.getUser(userName);
	}
	
}
