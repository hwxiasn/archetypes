package com.qingbo.gingko.domain.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.qingbo.gingko.common.util.Pager;
import com.qingbo.gingko.domain.UserService;
import com.qingbo.gingko.entity.User;
import com.qingbo.gingko.repository.UserRepository;

@Service("userService")
public class UserServiceImpl implements UserService {
	@Autowired private UserRepository userRepository;
	@Autowired private PasswordServiceImpl passwordHelper;
	
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