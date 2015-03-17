package com.qingbo.ginkgo.ygb.customer.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.entity.Operator;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.repository.OperatorRepository;
import com.qingbo.ginkgo.ygb.customer.repository.UserRepository;
import com.qingbo.ginkgo.ygb.customer.service.OperatorService;
import com.qingbo.ginkgo.ygb.customer.service.PasswordService;

@Service("operatorService")
public class OperatorServiceImpl implements OperatorService{
	@Autowired
	private QueuingService queuingService;
	
	@Autowired
	private PasswordService passwordService;
	
	@Autowired
	private UserRepository  userRepository;
	
	@Autowired
	private OperatorRepository operatorRepository;
	
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	ErrorMessage errors = new ErrorMessage("errorcode-cst.properties");
	/**
	 * 创建操作员                      用户名         登录密码         注册来源     角色        状态   注册类型
	 */
	@Override
	public Result<User> operatorRegister(User user) {
		// TODO Auto-generated method stub
		Result<User> result;
		logger.info(SimpleLogFormater.formatParams(user));
		//校验参数
		if(user==null) {
			result = errors.newFailure("");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(user.getUserName()==null || ("").equals(user.getUserName()) || user.getPassword()==null || ("").equals(user.getPassword())||
				user.getRegSource()==null || ("").equals(user.getRegSource())||user.getRole()==null || ("").equals(user.getRole())||
			    user.getRegisterType()==null || ("").equals(user.getRegisterType())) {
			result = errors.newFailure("");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		try {
			String userName = user.getUserName().trim().toLowerCase();
			user.setUserName(userName);
			user.setId(queuingService.next(userName).getObject());
			//操作员注册后状态变为已激活
			user.setStatus(CustomerConstants.Status.ACTIVE.getCode());
			//加密登录密码
			String loginSalt = passwordService.generateSalt();
			String encryptPassword = passwordService.encryptPassword(user.getPassword(), user.getUserName(), loginSalt);
			user.setSalt(loginSalt);
			user.setPassword(encryptPassword);
//			//加密审核密码
//			String auditSalt = passwordService.generateSalt();
//			String auditPassword = passwordService.encryptPassword(specialUser.getAuditPassword(), specialUser.getUserName(), auditSalt);
//			specialUser.setAuditSalt(auditSalt);
//			specialUser.setAuditPassword(auditPassword);
			userRepository.save(user);
		}catch(Exception e) {
			result = errors.newFailure("");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(user);
	}

	/**
	 * 保存操作员的信息
	 */
	@Override
	public Result<Operator> createOperator(Operator operator) {
		// TODO Auto-generated method stub
		Result<Operator> result;
		logger.info(SimpleLogFormater.formatParams(operator));
		if(operator.getLevel()==null && ("").equals(operator.getLevel())||
				operator.getUserId()==null && ("").equals(operator.getUserId()) || operator.getOrganizationId()==null || ("").equals(operator.getOrganizationId())||
				operator.getDisplayOrgName()==null && ("").equals(operator.getDisplayOrgName()) || operator.getUserName()==null || ("").equals(operator.getUserName())){
			result = errors.newFailure("");
			logger.info("创建操作员参数不全");
			return result;
		}
		try {
				operator.setId(queuingService.next(operator.getUserName()).getObject());
				//加密审核密码
				if(!operator.getLevel().equals(CustomerConstants.operatorLevel.LEVEL3.getCode())) {
					String auditSalt = passwordService.generateSalt();
					String auditPassword = passwordService.encryptPassword(operator.getAuditPassword(),operator.getUserName(), auditSalt);
					operator.setAuditSalt(auditSalt);
					operator.setAuditPassword(auditPassword);
				}
				operatorRepository.save(operator);
		}catch(Exception e) {
			result = errors.newFailure("");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(operator);
	}

	
	/**
	 * 查询机构下的操作员
	 */
	@Override
	public Result<PageObject<Operator>> listOperatorByOrganizationId(
			Long organizationId, SpecParam<Operator> specs,Pager pager) {
		// TODO Auto-generated method stub
		Result<PageObject<Operator>> result;
		logger.info(SimpleLogFormater.formatParams(organizationId,specs));
		if(organizationId==null ||("").equals(organizationId)) {
			logger.info("参数企业id"+organizationId+"为空");
			return errors.newFailure("");
		}
		try {
			specs.eq("deleted", false);
			specs.eq("organizationId", organizationId);
			Page<Operator> resultSet = operatorRepository.findAll(SpecUtil.spec(specs), page(pager));
			Result<PageObject<Operator>> result1 = Result.newSuccess(new PageObject<Operator>((int)resultSet.getTotalElements(), resultSet.getContent()));;
			return result1;
		}catch(Exception e) {
			result = errors.newFailure("");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
	}

	/**
	 * 根据用户名或者id查询操作员   此id为操作员表中的主键
	 */
	@Override
	public Result<Operator> listOperator(Long id, String userName) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(id,userName));
		Result<Operator> result;
		if(id==null && userName==null) {
			logger.info("listOperator方法的参数为空");
			return errors.newFailure("");
		}
		Operator operator = null;
		try {
			if(id==null && userName!=null) {
				operator = operatorRepository.findByUserName(userName);
			}else if(id!=null && userName==null) {
				operator = operatorRepository.findOne(id);
			}
		}catch(Exception e) {
			result = errors.newFailure("");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(operator);
	}
	

	
	
	/**
	 * 重置操作员审核密码
	 */
	@Override
	public Result<Boolean> reseatAuditPassword(Long id, String auditPassword) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(id,auditPassword));
		Result<Boolean> result;
		// TODO Auto-generated method stub
		if(id==null || ("").equals(id)) {
			result = errors.newFailure("");
			logger.info("the param id for updateAuditPassword is null");
			return result;
		}
		if(auditPassword==null || ("").equals(auditPassword)) {
			result = errors.newFailure("");
			logger.info("the param auditPassword  for updateAuditPassword is null");
			return result;
		}
		try {
			Operator operator = null;
			operator = operatorRepository.findOne(id);
			if(operator==null) {
				result = errors.newFailure("");
				logger.info("the poperator not exist for id="+id);
				return result;
			}else {
				String salt = passwordService.generateSalt();
				String newAuditPassword = passwordService.encryptPassword(auditPassword, operator.getUserName(), salt);
				operator.setAuditPassword(newAuditPassword);
				operatorRepository.save(operator);
			}
			
		}catch(Exception e) {
			result = errors.newFailure("");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}
	
	private Pageable page(Pager page){
		return page.getDirection() == null || page.getProperties() == null ? 
				new PageRequest(page.getCurrentPage() - 1, page.getPageSize()) :
				new PageRequest(page.getCurrentPage() - 1, page.getPageSize(),Direction.valueOf(page.getDirection()),page.getProperties().split(","));
	}

	
	/**
	 * 校验操作员的审核密码
	 */
	@Override
	public Result<Boolean> validateAuditPassword(Long id, String auditPassword) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(id,auditPassword));
		Result<Boolean> result;
		if(id==null || ("").equals(id)) {
			result = errors.newFailure("");
			logger.info("the param id for validateAuditPassword is null");
			return result;
		}
		if(auditPassword==null || ("").equals(auditPassword)) {
			result = errors.newFailure("");
			logger.info("the param auditPassword for validateAuditPassword is null");
			return result;
		}
		try {
			Operator operator = null;
			operator = operatorRepository.findOne(id);
			if(operator!=null) {
				String auditPassword1 = passwordService.encryptPassword(auditPassword, operator.getUserName(), operator.getAuditPassword());
				if(auditPassword1.equals(operator.getAuditPassword())) {
					return Result.newSuccess(true);
				}else {
					return Result.newSuccess(false);
				}
			}else {
				result = errors.newFailure("");
				logger.info("the poperator not exist for id="+id);
				return result;
			}
		}catch(Exception e) {
			result = errors.newFailure("");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
	}

	/**
	 * 根据userid查询操作员的详细信息
	 */
	@Override
	public Result<Operator> listOperatorByUserId(Long userId) {
		// TODO Auto-generated method stub
		Result<Operator> result;
		logger.info(SimpleLogFormater.formatParams(userId));
		if(userId==null || ("").equals(userId)) {
			result = errors.newFailure("");
			logger.info("the param userId for listByUserId is null");
			return result;
		}
		Operator operator = null;
		try {
			operator = operatorRepository.findByUserId(userId);
		}catch(Exception e) {
			result = errors.newFailure("");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(operator);
	}

	
}
