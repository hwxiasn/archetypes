package com.qingbo.gingko.domain.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.gingko.common.util.NumberUtil;
import com.qingbo.gingko.domain.RolePermissionService;
import com.qingbo.gingko.entity.Permission;
import com.qingbo.gingko.entity.Role;
import com.qingbo.gingko.entity.User;
import com.qingbo.gingko.repository.PermissionRepository;
import com.qingbo.gingko.repository.RoleRepository;
import com.qingbo.gingko.repository.UserRepository;

@Service("rolePermissionService")
public class RolePermissionServiceImpl implements RolePermissionService {
	@Autowired private UserRepository userRepository;
	@Autowired private RoleRepository roleRepository;
	@Autowired private PermissionRepository permissionRepository;
	
	@Override
	public Set<String> getRoles(String userName) {
		Set<String> roles = new HashSet<>();
		User user = userRepository.findByUserName(userName);
		String roleIds = user.getRoleIds();
		if(roleIds!=null && roleIds.length()>0) {
			for(String roleId : roleIds.split(",")) {
				Long id = NumberUtil.parseLong(roleId, null);
				if(id!=null) {
					Role role = roleRepository.findOne(id);
					if(role!=null) roles.add(role.getName());
				}
			}
		}
		return roles;
	}
	
	@Override
	public Role[] getRoles(Long userId) {
		User user = userRepository.findOne(userId);
		List<Role> roles = new ArrayList<Role>();
		String roleIds = user.getRoleIds();
		if(roleIds!=null && roleIds.length()>0) {
			for(String roleId : roleIds.split(",")) {
				Long id = NumberUtil.parseLong(roleId, null);
				if(id!=null) {
					Role role = roleRepository.findOne(id);
					if(role!=null) roles.add(role);
				}
			}
		}
		return roles.toArray(new Role[roles.size()]);
	}

	@Override
	public Set<String> getPermissions(String userName) {
		Set<String> permissions = new HashSet<>();
		User user = userRepository.findByUserName(userName);
		
		//get all permission ids
		String roleIds = user.getRoleIds();
		if(roleIds!=null && roleIds.length()>0) {
			for(String roleId : roleIds.split(",")) {
				Role role = roleRepository.findOne(Long.parseLong(roleId));
				String permissionIds = role.getPermissionIds();
				if(permissionIds!=null && permissionIds.length()>0) {
					for(String permissionId : permissionIds.split(",")) {
						permissions.add(permissionId);
					}
				}
			}
		}
		String permissionIds = user.getPermissionIds();
		if(permissionIds!=null && permissionIds.length()>0) {
			for(String permissionId : permissionIds.split(",")) {
				permissions.add(permissionId);
			}
		}
		
		//convert permission ids to permission names
		for(String permissionId : new HashSet<String>(permissions)) {
			permissions.remove(permissionId);
			Long id = NumberUtil.parseLong(permissionId, null);
			if(id!=null) {
				Permission permission = permissionRepository.findOne(id);
				if(permission!=null) permissions.add(permission.getName());
			}
		}
		return permissions;
	}
}
