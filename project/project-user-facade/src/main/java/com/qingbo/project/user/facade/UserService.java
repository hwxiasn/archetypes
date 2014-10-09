package com.qingbo.project.user.facade;

import com.qingbo.project.entity.User;

public interface UserService {
	String echo(String request);
	String echo(User user);
}
