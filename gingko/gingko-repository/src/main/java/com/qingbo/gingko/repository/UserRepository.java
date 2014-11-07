package com.qingbo.gingko.repository;

import com.qingbo.gingko.entity.User;

public interface UserRepository extends BaseRepository<User> {
	User findByUserName(String userName);
}
