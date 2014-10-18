package com.hwxiasn.dubbo.repository;

import com.hwxiasn.contentserver.entity.User;


public interface UserRepository extends BaseRepository<User, Integer> {
	User findByUserName(String userName);
}
