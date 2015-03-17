package com.qingbo.ginkgo.ygb.customer.repository;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.customer.entity.UserGroup;

public interface UserGroupRepository extends BaseRepository<UserGroup>{

	public UserGroup findByChildUserId(Long childUserId);
	public List<UserGroup> findByParentUserId(Long  parentUserId);
	public List<UserGroup> findByRootIdAndDeletedFalse(Long  rootId);
}
