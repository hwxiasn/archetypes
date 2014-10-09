package com.qingbo.project.domain.impl;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.qingbo.project.common.util.Pager;
import com.qingbo.project.domain.UserDomain;
import com.qingbo.project.domain.shiro.PasswordHelper;
import com.qingbo.project.entity.Permission;
import com.qingbo.project.entity.Role;
import com.qingbo.project.entity.User;
import com.qingbo.project.repository.PermissionRepository;
import com.qingbo.project.repository.RoleRepository;
import com.qingbo.project.repository.UserRepository;

@Service
public class UserDomainImpl implements UserDomain {
	@Autowired private UserRepository userRepository;
	@Autowired private RoleRepository roleRepository;
	@Autowired private PermissionRepository permissionRepository;
	@Autowired private PasswordHelper passwordHelper;
	
	@Override
	public Set<String> getRoles(String userName) {
		Set<String> roles = new HashSet<>();
		User user = getUser(userName);
		String roleIds = user.getRoleIds();
		if(roleIds!=null && roleIds.length()>0) {
			for(String roleId : roleIds.split(",")) {
				Role role = roleRepository.findOne(Integer.parseInt(roleId));
				if(role!=null) roles.add(role.getName());
			}
		}
		return roles;
	}
	
	@Override
	public Set<String> getPermissions(String userName) {
		Set<String> permissions = new HashSet<>();
		User user = getUser(userName);
		
		//get all permission ids
		String roleIds = user.getRoleIds();
		if(roleIds!=null && roleIds.length()>0) {
			for(String roleId : roleIds.split(",")) {
				Role role = roleRepository.findOne(Integer.parseInt(roleId));
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
			Permission permission = permissionRepository.findOne(Integer.parseInt(permissionId));
			if(permission!=null) permissions.add(permission.getName());
			
		}
		return permissions;
	}

	@Override
	public User getUser(String userName) {
		return userRepository.findByUserName(userName);
	}

	@Override
	public void saveUser(User user) {
		userRepository.save(user);
	}

	@Override
	public void pageUser(Specification<User> spec, Pager pager) {
		Pageable pageable = pager.getDirection()==null || pager.getProperties()!=null ? 
				new PageRequest(pager.getCurrentPage()-1, pager.getPageSize()) :
					new PageRequest(pager.getCurrentPage()-1, pager.getPageSize(), Direction.valueOf(pager.getDirection()), pager.getProperties().split(","));
		Page<User> page = userRepository.findAll(spec, pageable);
		if(pager.notInitialized()) pager.init((int)page.getTotalElements());
		pager.setElements(page.getContent());
	}

	@Override
	public boolean validatePassword(String userName, String password) {
		User user = getUser(userName);
		if(user == null) return false;
		String encryptPassword = passwordHelper.encryptPassword(password, userName, user.getSalt());
		return encryptPassword.equals(user.getPassword());
	}

	@Override
	@Transactional
	public boolean updatePassword(String userName, String oldPassword, String newPassword) {
		if(validatePassword(userName, oldPassword)) {
			User user = getUser(userName);
			String generateSalt = passwordHelper.generateSalt();
			String encryptPassword = passwordHelper.encryptPassword(newPassword, userName, generateSalt);
			user.setSalt(generateSalt);
			user.setPassword(encryptPassword);
			userRepository.save(user);
		}
		return false;
	}

	@Override
	@Transactional
	public boolean resetPassword(String userName, String password) {
		User user = getUser(userName);
		if(user == null) return false;
		String generateSalt = passwordHelper.generateSalt();
		String encryptPassword = passwordHelper.encryptPassword(password, userName, generateSalt);
		user.setSalt(generateSalt);
		user.setPassword(encryptPassword);
		userRepository.save(user);
		return true;
	}

}
