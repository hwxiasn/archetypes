package com.qingbo.gingko.web.service;

import com.qingbo.gingko.entity.User;

public interface UserService {
	Integer register(User user);
	User getUser(String userName);
	boolean[] login(String userName, String password);
	void logout();
}
