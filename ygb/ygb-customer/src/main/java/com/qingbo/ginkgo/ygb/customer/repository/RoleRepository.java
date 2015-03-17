package com.qingbo.ginkgo.ygb.customer.repository;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.customer.entity.Role;

public interface RoleRepository extends BaseRepository<Role> {
	Role findByName(String name);
}
