package com.qingbo.project.repository;

import com.qingbo.project.entity.User;
import com.qingbo.project.repository.base.BaseRepository;

public interface UserRepository extends BaseRepository<User, Integer> {
	User findByUserName(String userName);
}
