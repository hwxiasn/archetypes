package com.qingbo.ginkgo.ygb.customer.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.customer.entity.Permission;
import com.qingbo.ginkgo.ygb.customer.entity.Role;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.repository.PermissionRepository;
import com.qingbo.ginkgo.ygb.customer.repository.RoleRepository;
import com.qingbo.ginkgo.ygb.customer.repository.UserRepository;
import com.qingbo.ginkgo.ygb.customer.service.RolePermissionService;

@Service("rolePermissionService")
public class RolePermissionServiceImpl implements RolePermissionService {
	@Autowired private UserRepository userRepository;
	@Autowired private RoleRepository roleRepository;
	@Autowired private PermissionRepository permissionRepository;
	
	@Override
	public Set<String> getRoles(String userName) {
		Set<String> roles = new HashSet<>();
		User user = userRepository.findByUserName(userName);
		if(user!=null && StringUtils.isNotBlank(user.getRole()))
			roles.add(user.getRole());
		return roles;
	}
	
	@Override
	public Role[] getRoles(Long userId) {
		User user = userRepository.findOne(userId);
		List<Role> roles = new ArrayList<Role>();
		if(user!=null && StringUtils.isNotBlank(user.getRole())) {
			Role role = roleRepository.findByName(user.getRole());
			if(role!=null) roles.add(role);
		}
		return roles.toArray(new Role[roles.size()]);
	}

	@Override
	public Set<String> getPermissions(String userName) {
		Set<String> permissions = new HashSet<>();
		User user = userRepository.findByUserName(userName);
		if(user!=null && StringUtils.isNotBlank(user.getRole())) {
			Role role = roleRepository.findByName(user.getRole());
			if(role!=null && StringUtils.isNotBlank(role.getPermissionIds())) {
				for(String permissionId : role.getPermissionIds().split(",")) {
					Long id = NumberUtil.parseLong(permissionId, null);
					if(id!=null && id>0) {
						Permission permission = permissionRepository.findOne(id);
						if(permission!=null && StringUtils.isNotBlank(permission.getName()))
							permissions.add(permission.getName());
					}
				}
			}
		}
		return permissions;
	}
}
