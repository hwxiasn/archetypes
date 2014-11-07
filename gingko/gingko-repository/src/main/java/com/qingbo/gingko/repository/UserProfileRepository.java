package com.qingbo.gingko.repository;

import java.util.List;

import com.qingbo.gingko.entity.UserProfile;

public interface UserProfileRepository extends BaseRepository<UserProfile> {
	UserProfile findByUserId(Integer userId);
	List<UserProfile> findByMobile(String mobile);
	List<UserProfile> findByEmail(String email);
}
