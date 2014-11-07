package com.qingbo.gingko.repository;

import com.qingbo.gingko.entity.UserEnterpriseProfile;

public interface UserEnterpriseProfileRepository extends BaseRepository<UserEnterpriseProfile> {
	UserEnterpriseProfile findByUserId(Long userId);
}
