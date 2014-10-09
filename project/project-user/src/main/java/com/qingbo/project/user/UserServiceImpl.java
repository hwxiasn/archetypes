package com.qingbo.project.user;

import com.alibaba.fastjson.JSON;
import com.qingbo.project.entity.User;
import com.qingbo.project.user.facade.UserService;

public class UserServiceImpl implements UserService {

	@Override
	public String echo(String request) {
		return "echo: "+request;
	}

	@Override
	public String echo(User user) {
		return JSON.toJSONString(user);
	}

}
