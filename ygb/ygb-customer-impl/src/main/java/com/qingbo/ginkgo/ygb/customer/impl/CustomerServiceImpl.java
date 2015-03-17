package com.qingbo.ginkgo.ygb.customer.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.util.GinkgoUtil;
import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.entity.UserEnterpriseProfile;
import com.qingbo.ginkgo.ygb.customer.entity.UserGroup;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.entity.UserStatus;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.repository.UserBankCardRepository;
import com.qingbo.ginkgo.ygb.customer.repository.UserEnterpriseProfileRepository;
import com.qingbo.ginkgo.ygb.customer.repository.UserGroupRepository;
import com.qingbo.ginkgo.ygb.customer.repository.UserProfileRepository;
import com.qingbo.ginkgo.ygb.customer.repository.UserRepository;
import com.qingbo.ginkgo.ygb.customer.repository.UserStatusRepository;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.service.PasswordService;


@Service("customerService")
public class CustomerServiceImpl implements CustomerService{

	@Autowired
	private QueuingService queuingService;
	@Autowired
	private UserRepository  userRepository;
	@Autowired
	private PasswordService passwordService;
	@Autowired 
	private UserProfileRepository userProfileRepository;
	@Autowired
	private UserStatusRepository userStatusRepository;
	@Autowired 
	private UserEnterpriseProfileRepository  enterpriseProfileRepository;
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private UserBankCardRepository userBankCardRepository;
	@Autowired
	private  UserEnterpriseProfileRepository userEnterpriseProfileRepository;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	ErrorMessage errors = new ErrorMessage("errorcode-cst.properties");
	
	/**
	 * 用户注册
	 */
	@Override
	@Transactional
	public Result<User> register(User user) {
		// TODO 参数校验
		if(user==null) {
			logger.info("user must not be null");
			return errors.newFailure("CST1001");
		}
		if(user.getRegisterType()==null) {
			return errors.newFailure("CST1002");
		}
		//个人用户注册
		if((CustomerConstants.UserRegisterType.PERSONAL.getCode()).equals(user.getRegisterType())) {
			UserProfile userPrf =  user.getUserProfile();
			if(userPrf == null) 
			{
				logger.info("userProfile must not be null");
				return errors.newFailure("CST1003");
			}
			//2015.1.26修改 底层方法去掉检测手机号码的判断，手机客户端不需要手机号码的判断，网站需要的话自行在业务层判断
			if(user.getRegSource()==null || user.getRole() == null || user.getUserName()== null ||userPrf.getRealName()==null || userPrf.getEmail()==null) 
			{
				logger.info("missing param for the method register");
				return errors.newFailure("CST1004");
			}
			if(user.getRegDate()==null) 
			{
				user.setRegDate(new Date());
			}
			//String activatedCode = passwordService.generateSalt();
			user.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
			//user.setActivateCode(activatedCode);
			try {
				//userName去掉空格和全部转化为小写
				String userName = user.getUserName().trim().toLowerCase();
				user.setUserName(userName);
				if((CustomerConstants.Role.OPERATOR.getCode()).equals(user.getRole())) {
					user.setStatus(CustomerConstants.Status.ACTIVE.getCode());
				}else {
					user.setStatus(CustomerConstants.Status.INACTIVE.getCode());
				}
				//保存用户
				User userEntity = userRepository.save(user);
				userPrf.setUserId(user.getId());
				userPrf.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
				//保存用户详细信息
				userPrf.setCustomerNum(null);
				userProfileRepository.save(userPrf);
				//保存用户状态信息
				UserStatus userStatus = new UserStatus();
				userStatus.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
				userStatus.setUserId(user.getId());
				userStatus.setActivated(CustomerConstants.IsActivated.ISNOTACTIVATED.getCode());
				userStatus.setBankbinding(CustomerConstants.SomethingBinding.UNBIND.getCode());
				userStatus.setEmailbinding(CustomerConstants.SomethingBinding.UNBIND.getCode());
				userStatus.setMobilebinding(CustomerConstants.SomethingBinding.UNBIND.getCode());
				userStatus.setRealNameAuthStatus(CustomerConstants.RealNameAuthStatus.UNREALNAME.getCode());
				userStatusRepository.save(userStatus);
				//将userProfile添加进user中返回
				user.setUserProfile(userPrf);
			}catch(Exception e) 
			{
				logger.info(SimpleLogFormater.formatResult(errors.newFailure("CST0001")));
				logger.error(SimpleLogFormater.formatException(errors.newFailure("CST0001").getMessage(), e));
				return errors.newFailure("CST0001");
			}
		}else if ((CustomerConstants.UserRegisterType.ENTERPRISE.getCode()).equals(user.getRegisterType())) {
		//企业用户开户
			UserEnterpriseProfile entPrise = user.getEnterpriseProfile();
			if(entPrise == null) 
			{
				logger.info("userEnterpriseProfile must not be null");
				return errors.newFailure("CST1005");
			}
			if(user.getRegSource()==null || user.getRole() == null || user.getUserName() == null ||entPrise.getEnterpriseName()==null || entPrise.getContactEmail()==null) 
			{
				logger.info("missing param for the method register");
				return errors.newFailure("CST1004");
			}
			if(user.getRegDate()==null) 
			{
				user.setRegDate(new Date());
			}
			//String activatedCode = GinkgoUtil.generateActivatedCode(user.getUserName());
			user.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
			//user.setActivateCode(activatedCode);
			try {
				//userName去掉空格和全部转化为小写
				String userName = user.getUserName().trim().toLowerCase();
				user.setUserName(userName);
				user.setStatus(CustomerConstants.Status.INACTIVE.getCode());
				//保存用户
				User userEntity = userRepository.save(user);
				entPrise.setUserId(user.getId());
				//保存企业详细信息
				entPrise.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
				enterpriseProfileRepository.save(entPrise);
				//保存企业状态信息
				UserStatus userStatus = new UserStatus();
				userStatus.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
				userStatus.setUserId(user.getId());
				//userStatus.setStatus(CustomerConstants.Status.INACTIVE.getCode());
				userStatus.setActivated(CustomerConstants.IsActivated.ISNOTACTIVATED.getCode());
				userStatus.setBankbinding(CustomerConstants.SomethingBinding.UNBIND.getCode());
				userStatus.setEmailbinding(CustomerConstants.SomethingBinding.UNBIND.getCode());
				userStatus.setMobilebinding(CustomerConstants.SomethingBinding.UNBIND.getCode());
				userStatus.setRealNameAuthStatus(CustomerConstants.RealNameAuthStatus.UNREALNAME.getCode());
				userStatusRepository.save(userStatus);
				user.setEnterpriseProfile(entPrise);
			}catch(Exception e) {
				logger.error(SimpleLogFormater.formatException(errors.newFailure("CST0001").getMessage(), e));
				return errors.newFailure("CST0001");
			}
		}else {
			logger.info("registerType is not P or E");
			return errors.newFailure("registerType is not P or E");
		}
		return Result.newSuccess(user);
	}

	/**
	 * 根据客户编号查询客户的所有信息
	 */
	@Override
	public Result<User> getUserByCustomerNum(String customerNum) {
		// TODO Auto-generated method stub
		User user = new User();
		//校验参数
		if(customerNum == null || ("").equals(customerNum)) 
		{
			logger.info("customerNum must not be null");
			return errors.newFailure("customerNum must not be null");
		}
		try {
			UserProfile userProfile = userProfileRepository.findByCustomerNum(customerNum);
			if(userProfile == null) {
				logger.info("userProfile not exist for customerNum is"+customerNum);
				return errors.newFailure("CST1021");
			}
			user = userRepository.findOne(userProfile.getUserId());
			user.setUserProfile(userProfile);
		}catch(Exception e) {
			logger.error(SimpleLogFormater.formatException(errors.newFailure("CST0002").getMessage(), e));
			return errors.newFailure("CST0002");
		}
		return Result.newSuccess(user);
	}
	
	/**
	 * 建立上下级关系
	 */
	@Override
	public Result<Boolean> setUpUserRelationship(UserGroup userGroup,String parentCustomerNum) {
		// TODO Auto-generated method stub
		//校验参数
		if(userGroup == null || ("").equals(userGroup)) 
		{
			logger.info("userGroup must not be null");
			return errors.newFailure("userGroup must not be null");
		}
		if(userGroup.getChildUserId()==null) 
		{
			logger.info("missing param for the method setUpUserRelationship");
			return errors.newFailure("CST1011");
		}
		try {
			//参数为父客户编号
			if(parentCustomerNum!= null) {
				Result<User> user = this.getUserByCustomerNum(parentCustomerNum);
				if(user == null) {
					logger.info("user not exist for parentCustomerNum is"+parentCustomerNum);
					return errors.newFailure("CST1012");
				}
				Long parentUserId = user.getObject().getId();
				userGroup.setParentUserId(parentUserId);
				userGroup.setChildUserId(userGroup.getChildUserId());
				userGroup.setDeleted(true);
				userGroup.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
				userGroupRepository.save(userGroup);
			}else {//参数为父userid
				if(userGroup.getParentUserId()!=null && !("").equals(userGroup.getParentUserId())) {
					userGroup.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
					userGroupRepository.save(userGroup);
				}
			}
			
		}catch(Exception e) {
			logger.error(SimpleLogFormater.formatException(errors.newFailure("CST0003").getMessage(), e));
			return errors.newFailure("CST0003");
		}
		return Result.newSuccess(true);
	}



	/**
	 * 根据用户名查询用户及其详细信息
	 */
	@Override
	public Result<User> getUserByUserName(String userName) {
		// TODO Auto-generated method stub
		Result<User> result;
		if(userName == null || ("").equals(userName)) 
		{
			logger.info("userName must not be null");
			return errors.newFailure("CST1031");
		}
		User user = new User();
		try {
			user = userRepository.findByUserName(userName.toLowerCase());
			if(user == null) {
				logger.info("user not exist for "+userName);
				return errors.newFailure("CST1032");
			}
			Long userId = user.getId();
			if((CustomerConstants.UserRegisterType.PERSONAL.getCode()).equals(user.getRegisterType())) 
			{
				UserProfile userProfile= userProfileRepository.findByUserId(userId);
				if(userProfile!=null) {
					user.setUserProfile(userProfile);
				}
			}else {
				UserEnterpriseProfile enterProf = enterpriseProfileRepository.findByUserId(userId);
				if(enterProf!=null) 
				{
					user.setEnterpriseProfile(enterProf);
				}
			}
			
		}catch(Exception e) {
			result = errors.newFailure("CST0004");
	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		
		return Result.newSuccess(user);
	}

	/**
	 * 根据用户id查询用户详情
	 */
	@Override
	public Result<User> getUserByUserId(Long userId) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userId));
		Result<User> result;
		if(userId == null || ("").equals(userId)) 
		{
			logger.info("userId must not be null");
			logger.info(SimpleLogFormater.formatResult(errors.newFailure("CST1041")));
			return errors.newFailure("CST1041");
		}
		User user = null;
		try {
			user = userRepository.findOne(userId);
			if(user == null) {
				logger.info("user not exist for"+String.valueOf(userId));
				logger.info(SimpleLogFormater.formatResult(errors.newFailure("CST1042")));
				return errors.newFailure("CST1042");
			}
			if((CustomerConstants.UserRegisterType.PERSONAL.getCode()).equals(user.getRegisterType())) 
			{
				UserProfile userProfile = userProfileRepository.findByUserId(userId);
				if(userProfile!=null) 
				{
					user.setUserProfile(userProfile);
				}
			}else {
				UserEnterpriseProfile enterProf = enterpriseProfileRepository.findByUserId(userId);
				if(enterProf!=null) 
				{
					user.setEnterpriseProfile(enterProf);
				}
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0005");
	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		
		logger.info(SimpleLogFormater.formatResult(user));
		return Result.newSuccess(user);
	}

	/**
	 * 根据激活码查询用户
	 */
	@Override
	public Result<User> getUserByActivatedCode(String activatedCode) {
		// TODO Auto-generated method stub
		Result<User> result; 
		if(activatedCode==null && ("").equals(activatedCode)) 
		{
			logger.info("activatedCode must not be null");
			logger.info(SimpleLogFormater.formatResult(errors.newFailure("CST1051")));
			return errors.newFailure("CST1051");
		}
		User user = null;
		try {
			user = userRepository.findByActivateCode(activatedCode);
			if(user==null) 
			{
				logger.info("user not exist for"+String.valueOf(activatedCode));
				logger.info(SimpleLogFormater.formatResult(errors.newFailure("CST1052")));
				return errors.newFailure("CST1052"); 
			}
			if((CustomerConstants.UserRegisterType.PERSONAL.getCode()).equals(user.getRegisterType())) 
			{
				UserProfile userProfile = userProfileRepository.findByUserId(user.getId());
				if(userProfile!=null) 
				{
					user.setUserProfile(userProfile);
				}
			}else {
				UserEnterpriseProfile enterProf = enterpriseProfileRepository.findByUserId(user.getId());
				if(enterProf!=null) 
				{
					user.setEnterpriseProfile(enterProf);
				}
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0006");
	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		
		logger.info(SimpleLogFormater.formatResult(user));
		return Result.newSuccess(user);
	}

	/**
	 * 根据用户名或者id查询用户基本信息
	 */
	@Override
	public Result<User> getOnlyUserByIdOrUserName(Long id, String userName) {
		// TODO Auto-generated method stub
		Result<User> result;
		if(id==null && userName == null) {
			logger.info("id and userName is null");
			logger.info(SimpleLogFormater.formatResult(errors.newFailure("CST1061")));
			return errors.newFailure("CST1061");
		}
		User user = null;
		try {
			if(id!=null) 
			{
				user = userRepository.findOne(id);
			}
			if(userName!=null) {
				user = userRepository.findByUserName(userName.toLowerCase());
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0007");
	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		logger.info(SimpleLogFormater.formatResult(user));
		return Result.newSuccess(user);
	}
	
	/**
	 * 分页查询用户及详细信息
	 */
	@Override
	public Result<PageObject<User>> page(SpecParam<User> specs, Pager pager) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(specs, pager));
		Result<PageObject<User>> result;
		// 参数校验
		if(specs == null || pager == null){
			result = errors.newFailure("CST1071");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(pager.getPageSize() <= 0){
			result = errors.newFailure("CMS1072");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		// 准备分页参数
		Pageable pageable = pager.getDirection() == null || pager.getProperties() == null ? 
								new PageRequest(pager.getCurrentPage() - 1, pager.getPageSize()) :
								new PageRequest(pager.getCurrentPage() - 1, pager.getPageSize(), 
												Direction.valueOf(pager.getDirection()), 
												pager.getProperties().split(","));
		// 准备查询参数
		specs.eq("deleted", false);								// 未删除	
		// 执行查询
	    Page<User> resultSet = null;
	    try {
	    	resultSet = userRepository.findAll(SpecUtil.spec(specs), pageable);
	    	
	    	
	    	
	    	
	    }catch(Exception e) {
	    	result = errors.newFailure("CST0008");
	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
	    }
	    // 返回结果
	    result = Result.newSuccess(new PageObject<User>((int)resultSet.getTotalElements(), resultSet.getContent()));
	    logger.info(SimpleLogFormater.formatResult(result));
	    return result;
		
	}

	/**
	 * 用户激活
	 */
	@Override
	public Result<Boolean> doActivate(User user) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(user));
		Result<Boolean> result;
		if(user == null) {
			result = errors.newFailure("CST1081");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(user.getId()==null || user.getPassword() == null) 
		{
			result = errors.newFailure("CST1082");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		try {
			User user1 = userRepository.findOne(user.getId());
			String salt= passwordService.generateSalt();
			//加密
			String encryptPassword = passwordService.encryptPassword(user.getPassword(), user1.getUserName(), salt);
			user1.setSalt(salt);
			user1.setStatus(CustomerConstants.Status.ACTIVE.getCode());
			user1.setPassword(encryptPassword);
			//logger.info("activateCode = "+user1.getActivateCode()+"for userId= "+user1.getId());
			//user1.setActivateCode(null);
			userRepository.save(user1);
			//如果是企业的操作员
//			if(user.getRole().equals(CustomerConstants.Role.OPERATOR.getCode())) {
//				if(user.getUserProfile()!=null) {
//					if(user.getUserProfile().getAuditPassword()!=null) {
//						String salt1 =  passwordService.generateSalt();
//						//审核密码加密
//						String encryptPassword1 = passwordService.encryptPassword(user.getUserProfile().getAuditPassword(), user.getUserName(), salt1);
//						UserProfile pro = userProfileRepository.findByUserId(user.getId());
//						if(pro!=null) {
//							pro.setAuditPassword(encryptPassword1);
//							pro.setAuditSalt(salt1);
//							userProfileRepository.save(pro);
//						}else {
//							
//						}
//					}
//				}
//				
//			}
			UserStatus userStatus = userStatusRepository.findByUserId(user.getId());
			userStatus.setActivated(CustomerConstants.IsActivated.ISACTIVATED.getCode());
			userStatusRepository.save(userStatus);
		}catch(Exception e) {
			result = errors.newFailure("CST0009");
	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}

	/**
	 * 用户登录
	 */
	@Override
	public Result<Boolean> doLogin(String userName, String password) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userName,password));
		Result<Boolean> result;
		if(userName==null || password==null) {
			logger.info("用户名或者密码为空");
			errors.newFailure("CST1091");
			return Result.newFailure("CST1091", "用户名或者密码为空");
		}
		try {
			String userName1 = userName.trim().toLowerCase();
			User user = userRepository.findByUserName(userName1);
			if(user == null) {
				result = errors.newFailure("CST1092");
				logger.info(SimpleLogFormater.formatResult(result));
				return Result.newFailure("CST1092", "用户名不存在");
			}
			//登录密码加密
			String encryptPassword = passwordService.encryptPassword(password, userName1, user.getSalt());
			if(!(user.getPassword()).equals(encryptPassword)) {
				result = errors.newFailure("CST1093");
				logger.info(SimpleLogFormater.formatResult(result));
				return Result.newFailure("CST1093", "密码不正确");
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0010");
	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}

	/**
	 * 更新用户状态
	 */
	@Override
	public Result<Boolean> updateUserStatus(Long userId, UserStatus userStatus) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userId, userStatus));
		Result<Boolean> result;
		if(userId==null || userStatus==null) {
			result = errors.newFailure("CST1101");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		try {
			UserStatus status = userStatusRepository.findByUserId(userId);
			if(status==null) {
				logger.info("userStatus not exist for"+String.valueOf(userId));
				result = errors.newFailure("CST1102");
				logger.info(SimpleLogFormater.formatResult(result));
				return result;
			}
			if(userStatus.getActivated()!=null && !("").equals(status.getActivated())) {
				status.setActivated(userStatus.getActivated());
			}
			if(userStatus.getBankbinding()!=null && !("").equals(status.getBankbinding())) {
				status.setBankbinding(userStatus.getBankbinding());
			}
			if(userStatus.getEmailbinding()!=null && !("").equals(status.getEmailbinding())) {
				status.setEmailbinding(userStatus.getEmailbinding());
			}
			if(userStatus.getRealNameAuthStatus()!=null && !("").equals(userStatus.getRealNameAuthStatus())) {
				status.setRealNameAuthStatus(userStatus.getRealNameAuthStatus());
			}
			if(userStatus.getMobilebinding()!=null && !("").equals(userStatus.getMobilebinding())) 
			{
				status.setMobilebinding(userStatus.getMobilebinding());
			}
			userStatusRepository.save(status);
		}catch(Exception e) {
			result = errors.newFailure("CST0011");
	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		logger.info(SimpleLogFormater.formatResult(Result.newSuccess(true)));
		return Result.newSuccess(true);
	}

	/**
	 *修改用户详细信息表
	 */
	@Override
	public Result<Boolean> updateUserProfile(UserProfile userProfile) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userProfile));
		Result<Boolean> result;
		if(userProfile == null ||("").equals(userProfile)) {
			result = errors.newFailure("CST1111");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(userProfile.getId()==null || userProfile.getUserId()==null || ("").equals(userProfile.getId())|| ("").equals(userProfile.getUserId())) {
			result = errors.newFailure("");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		try {
			//修改个人用户信息
			userProfileRepository.save(userProfile);	
		}catch(Exception e) {
			result = errors.newFailure("CST0012");
	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		logger.info(SimpleLogFormater.formatResult(Result.newSuccess(true)));
		return Result.newSuccess(true);
	}

	/**
	 * 
	* @Description:  
	* @param      
	* @return 
	* @throws
	 */
	public Result<Boolean> updateUserEnterPrise(UserEnterpriseProfile userEnterpriseProfile){
		logger.info(SimpleLogFormater.formatParams(userEnterpriseProfile));
		Result<Boolean> result;
		if(userEnterpriseProfile==null || ("").equals(userEnterpriseProfile)) {
			result = errors.newFailure("userEnterpriseProfile为空");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(userEnterpriseProfile.getId()==null || ("").equals(userEnterpriseProfile.getId()) || userEnterpriseProfile.getUserId() ==null || ("").equals(userEnterpriseProfile.getUserId())) {
			result = errors.newFailure("id为空");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		try {
			userEnterpriseProfileRepository.save(userEnterpriseProfile);
		}catch(Exception e) {
			result = errors.newFailure("CST0012");
	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		logger.info(SimpleLogFormater.formatResult(Result.newSuccess(true)));
		return Result.newSuccess(true);
	}
	

	/**
	 * 实名认证
	 */
//	@Override
//	public Result<Boolean> realNameAuth(Long userId, User user) {
//		// TODO Auto-generated method stub
//		logger.info(SimpleLogFormater.formatParams(userId,user));
//		Result<Boolean> result;
//		if(userId == null) {
//			result = errors.newFailure("CST1131");
//			logger.info(SimpleLogFormater.formatResult(result));
//			return result;
//		}
//		if(user.getRegisterType()==null) {
//			result = errors.newFailure("CST1132");
//			logger.info(SimpleLogFormater.formatResult(result));
//			return result;
//		}
//		try {//个人用户的实名认证
//			if(user.getRegisterType().equals(CustomerConstants.UserRegisterType.PERSONAL.getCode())) {
//				UserProfile pro = user.getUserProfile();
//				if(pro == null) {
//					result = errors.newFailure("CST1133");
//					logger.info(SimpleLogFormater.formatResult(result));
//					return result;
//				}
////				if(pro.getRealName()==null || pro.getEmail() ==null || pro.getIdCopyBank()==null ||pro.getIdCopyFront()==null 
////						||pro.getIdNumber()==null ||pro.getIdType()==null||pro.getMobile()==null ||pro.getIdValidTo()==null) {
////					result = errors.newFailure("CST1134","个人实名认证参数传递不全");
////					logger.info(SimpleLogFormater.formatResult(result));
////					return result;
////				}
//				UserProfile userProfile = userProfileRepository.findByUserId(userId);
//				//将实名认证的信息存入UserProfile中
//				if(userProfile != null) {
//					userProfile.setRealName(pro.getRealName());
//					userProfile.setEmail(pro.getEmail());
//					userProfile.setIdCopyBank(pro.getIdCopyBank());
//					userProfile.setIdCopyFront(pro.getIdCopyFront());
//					userProfile.setIdNum(pro.getIdNum());
//					userProfile.setIdType(pro.getIdType());
//					userProfile.setMobile(pro.getMobile());
//					userProfile.setIdValidTo(pro.getIdValidTo());
//					userProfileRepository.save(userProfile);
//				}
//			}else {
//			//企业用户的实名认证
//				UserEnterpriseProfile enterPro = user.getEnterpriseProfile();
//				if(enterPro == null) {
//					result = errors.newFailure("CST1134");
//					logger.info(SimpleLogFormater.formatResult(result));
//					return result;
//				}
//				//校验企业实名认证的参数
////				if(enterPro.getEnterpriseName() == null || enterPro.getLegalPersonName() == null || enterPro.getLegalPersonIdNum()==null ||enterPro.getOrganizationCode()==null
////				   ||enterPro.getTaxRegistrationNo() == null || enterPro.getLicenseNum() == null || enterPro.getRegisterProvince() == null ||enterPro.getRegisterCity()==null ||
////				   enterPro.getLicenseValidPeriod() == null || enterPro.getLicensePath()==null || enterPro.getLicenseCachetPath()==null || enterPro.getLegalPersonIdCopyBack()==null
////				   ||enterPro.getLegalPersonIdCopyFont() == null || enterPro.getOpenningLicensePath()==null) {
////				}
//				UserEnterpriseProfile priseProfile = enterpriseProfileRepository.findByUserId(userId);
//				if(priseProfile != null ) 
//				{
//					priseProfile.setEnterpriseName(enterPro.getEnterpriseName());
//					priseProfile.setLegalPersonName(enterPro.getLegalPersonName());
//					priseProfile.setLegalPersonIdNum(enterPro.getLegalPersonIdNum());
//					priseProfile.setOrganizationCode(enterPro.getOrganizationCode());
//					priseProfile.setTaxRegistrationNo(enterPro.getTaxRegistrationNo());
//					priseProfile.setLicenseNum(enterPro.getLicenseNum());
//					priseProfile.setRegisterProvince(enterPro.getRegisterProvince());
//					priseProfile.setRegisterCity(enterPro.getRegisterCity());
//					priseProfile.setLicenseValidPeriod(enterPro.getLicenseValidPeriod());
//					priseProfile.setLicensePath(enterPro.getLicensePath());
//					priseProfile.setLicenseCachetPath(enterPro.getLicenseCachetPath());
//					priseProfile.setLegalPersonIdCopyBack(enterPro.getLegalPersonIdCopyBack());
//					priseProfile.setLegalPersonIdCopyFont(enterPro.getLegalPersonIdCopyFont());
//					priseProfile.setOpenningLicensePath(enterPro.getOpenningLicensePath());
//					enterpriseProfileRepository.save(priseProfile);
//				}
//			}
//			//保存实名认证的状态为已认证
//			UserStatus userStatus = userStatusRepository.findByUserId(userId);
//			if(userStatus!=null) {
//				userStatus.setRealNameAuthStatus(CustomerConstants.RealNameAuthStatus.REALNAME.getCode());
//				userStatusRepository.save(userStatus);
//			}
//		}catch(Exception e) {
//			result = errors.newFailure("CST0014");
//	    	logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
//	    	logger.info(SimpleLogFormater.formatResult(result));
//	    	return result;
//		}
//		logger.info(SimpleLogFormater.formatResult(Result.newSuccess(true)));
//		return Result.newSuccess(true);
//	}

	/**
	 * 重新绑定手机和邮箱
	 */
	@Override
	public Result<Boolean> updateUserBinding(Long usersId, String mobile,
			String email) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(usersId,mobile,email));
		Result<Boolean> result;
		if(usersId == null) {
			return errors.newFailure("CST1141");
		}
		try {
			User user = userRepository.findOne(usersId);
			if(user==null) {
				logger.info("user not exist for "+usersId);
				return errors.newFailure("CST1142");
			}
			UserProfile userProfile = userProfileRepository.findByUserId(usersId);
			//个人绑定手机
			if(mobile!=null && !("").equals(mobile)) {
				userProfile.setMobile(mobile);
				userProfileRepository.save(userProfile);
			}else if(email !=null && !("").equals(email)) {
				userProfile.setEmail(email);
				userProfileRepository.save(userProfile);
			}
					
		}catch(Exception e) {
			result = errors.newFailure("CST0015");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}
	
	/**
	 * 修改密码
	 */
	@Override
	public Result<Boolean> updateLoginPassword(Long userId, String oldPassword,
			String newPassword) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userId,oldPassword,newPassword));
		Result<Boolean> result;
		if(userId==null || oldPassword==null ||newPassword == null) {
			return errors.newFailure("CST1151");
		}
		try {
			User user = null;
			user = userRepository.findOne(userId);
			if(user == null) {
				logger.info("user not exist for "+userId);
				return errors.newFailure("CST1152");
			}
			String encryptPassword1 = passwordService.encryptPassword(oldPassword, user.getUserName(), user.getSalt());
			if((encryptPassword1).equals(user.getPassword())) {
				String salt= passwordService.generateSalt();
				//加密
				String encryptPassword = passwordService.encryptPassword(newPassword, user.getUserName(), salt);
				user.setSalt(salt);
				user.setPassword(encryptPassword);
				userRepository.save(user);
			}else {
				logger.info("oldPassword is error for "+userId);
				return errors.newFailure("CST1153");
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0016");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}

	/**
	 * 重置密码
	 */
	@Override
	public Result<Boolean> resetLoginPassword(Long userId, String password) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userId,password));
		Result<Boolean> result;
		if(userId == null || password == null) {
			return errors.newFailure("CST1161");
		}
		try {
			User user = userRepository.findOne(userId);
			if(user == null) 
			{
				logger.info("user not exist for "+userId);
				return errors.newFailure("CST1162");
			}else {
				String salt = passwordService.generateSalt();
				String encryptPassword = passwordService.encryptPassword(password, user.getUserName(), salt);
				user.setSalt(salt);
				user.setPassword(encryptPassword);
				userRepository.save(user);
			}
			
		}catch(Exception e) {
			result = errors.newFailure("CST0017");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}

	/**
	 * 验证登录密码
	 */
	@Override
	public Result<Boolean> validateLoginPassword(Long userId, String password) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userId,password));
		Result<Boolean> result;
		if(userId == null || password == null) {
			logger.info("userId or password is null when validateLoginPassword");
			return errors.newFailure("CST1171");
		}
		try {
			User user = userRepository.findOne(userId);
			if(user == null) {
				logger.info("user not exist for "+userId);
				return errors.newFailure("CST1172");
			}else {
				String encryptPassword = passwordService.encryptPassword(password, user.getUserName(), user.getSalt());
				if(!(encryptPassword).equals(user.getPassword())) {
					logger.info("password is error for "+userId);
					return errors.newFailure("CST1173");
				}
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0018");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}

	
	/**
	 * 验证邮箱、手机、用户名等是否重复
	 */
	@Override
	public Result<Boolean> checkRepeat(String userName,String mobile,String email) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userName,mobile,email));
		Result<Boolean> result;
		if(userName==null && mobile==null && email==null) {
			result = errors.newFailure("CST1181");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		boolean flag = false;
		try {
			if(userName!=null && !("").equals(userName)) {
				User user = userRepository.findByUserName(userName.toLowerCase().trim());
				//不存在相同的用户名
				if(user==null) {
					flag = true;
				}else {
					return Result.newSuccess(false);
				}
			}
			if(mobile != null && !("").equals(mobile)) {
				List<UserProfile> userProfile = userProfileRepository.findByMobile(mobile);
				List<UserEnterpriseProfile> enterPrise = enterpriseProfileRepository.findByContactPhone(mobile);
				//数据库中不存在此手机号码
				if(userProfile.size()==0 && enterPrise.size()==0) {
					flag = true;
				}else {
					return Result.newSuccess(false);
				}
			}
			if(email!=null && !("").equals(email)) {
				List<UserProfile> userProfile = userProfileRepository.findByEmail(email);
				List<UserEnterpriseProfile> enterPrise = enterpriseProfileRepository.findByContactEmail(email);
				//数据库中不存在此邮箱
				if(userProfile.size()==0 && enterPrise.size()==0) {
					flag = true;
				}else {
					return Result.newSuccess(false);
				}
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0019");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(flag);
		
	}

	/**
	 * 校验审核密码
	 */
	@Override
	public Result<Boolean> validateAuditPassword(Long userId, String auditPassword) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userId,auditPassword));
		Result<Boolean> result;
		if(userId == null || auditPassword == null) {
			result = errors.newFailure("CST1191");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		try {
			User user = userRepository.findOne(userId);
			if(user!=null) 
			{
				UserProfile userProfile = userProfileRepository.findByUserId(userId);
				if(userProfile!=null) {
					String encryptPassword = passwordService.encryptPassword(auditPassword, user.getUserName(), userProfile.getAuditSalt());
					if(!encryptPassword.equals(userProfile.getAuditPassword())) {
						result = errors.newFailure("CST1194");
						logger.info("auditPassword is wrong for "+userId);
						return Result.newSuccess(false);
					}
				}else {
					result = errors.newFailure("CST1192");
					logger.info("userProfile is null for "+userId);
					return result;
				}
			}else {
				result = errors.newFailure("CST1193");
				logger.info("user is null for "+userId);
				return result;
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0020");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}

	/**
	 * 修改审核密码
	 */
	@Override
	public Result<Boolean> updateAuditPassword(Long userId, String oldPassword,
			String newPassword) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userId,oldPassword,newPassword));
		Result<Boolean> result;
		if(userId == null || oldPassword==null || newPassword==null) 
		{
			result = errors.newFailure("CST1201");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		try {
			Result<Boolean> flag =this.validateAuditPassword(userId, oldPassword);
			if(flag.success() && flag.getObject()==true) {
				User user = userRepository.findOne(userId);
				UserProfile userProfile = userProfileRepository.findByUserId(userId);
				//新密码加密
				String salt = passwordService.generateSalt();
				String encryptPassword = passwordService.encryptPassword(newPassword, user.getUserName(), salt);
				userProfile.setAuditPassword(encryptPassword);
				userProfile.setAuditSalt(salt);
				userProfileRepository.save(userProfile);
			}else {
				result = errors.newFailure("CST1202");
				logger.info("auditPassword is wrong for "+userId);
				return result;
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0021");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}

	/**
	 * 重置审核密码
	 */
	@Override
	public Result<Boolean> restAuditPassword(Long userId, String password) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userId,password));
		Result<Boolean> result;
		if(userId==null || password==null) {
			result = errors.newFailure("CST1211");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		try {
			User user = userRepository.findOne(userId);
			if(user == null) 
			{
				result = errors.newFailure("CST1212");
				logger.info("user not exist for "+userId);
				return result;
			}else {
				UserProfile pro = userProfileRepository.findByUserId(userId);
				String salt = passwordService.generateSalt();
				String encryptPassword = passwordService.encryptPassword(password, user.getUserName(), salt);
				pro.setAuditSalt(salt);
				pro.setAuditPassword(encryptPassword);
				userProfileRepository.save(pro);
			}
			
		}catch(Exception e) {
			result = errors.newFailure("CST0022");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}

	/**
	 * 解除上下级关系
	 */
	@Override
	public Result<Boolean> removeUserRelationship(Long childUserId) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(childUserId));
		Result<Boolean> result;
		if(childUserId == null) 
		{
			result = errors.newFailure("CST1221");
			logger.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		try {
			UserGroup userGroup = userGroupRepository.findByChildUserId(childUserId);
			if(userGroup == null ) {
				result = errors.newFailure("CST1222");
				logger.info("userGroup not exist for childUserId is "+childUserId);
				return result;
			}
			userGroup.setDeleted(true);
			userGroupRepository.save(userGroup);
		}catch(Exception e ) {
			result = errors.newFailure("CST0023");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
	    	logger.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return Result.newSuccess(true);
	}

	/**
	 * 根据子元素查询父元素
	 */
	@Override
	public Result<UserGroup> getParentUserByChildId(Long childUserId) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(childUserId));
		Result<UserGroup> result;
		if(childUserId == null || childUserId<1) {
			result = errors.newFailure("CST1231");
			return result;
		}
		try {
			UserGroup userGroup = userGroupRepository.findByChildUserId(childUserId);
			if(userGroup != null ) 
			{
				return Result.newSuccess(userGroup);
			}else {
				return Result.newSuccess(null);
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0024");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
	}

	/**
	 * 根据父元素查询子元素
	 */
	@Override
	public Result<List<UserGroup>> getChildrenUserByParentId(Long parentUserId) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(parentUserId));
		Result<List<UserGroup>> result;
		if(parentUserId == null || parentUserId<1) {
			result = errors.newFailure("CST1241");
			return result;
		}
		try {
			List<UserGroup>  userGroupList = userGroupRepository.findByParentUserId(parentUserId);
			if(userGroupList !=null || userGroupList.size()>0 ) {
				return Result.newSuccess(userGroupList);
			}else {
				result = errors.newFailure("CST1242", parentUserId);
				return result;
			}
			
		}catch(Exception e) {
			result = errors.newFailure("CST0025");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
	}
	
	public Result<List<UserGroup>> getChildrenUserByRootId(Long rootId) {
		logger.info("CustomService getChildrenUserByRootId rootId:"+rootId);
		Result<List<UserGroup>> result;
		if(rootId == null || rootId<1) {
			result = errors.newFailure("CST1241");
			return result;
		}
		try {
			List<UserGroup>  userGroupList = userGroupRepository.findByRootIdAndDeletedFalse(rootId);
			logger.info("CustomService getChildrenUserByRootId rootId:"+rootId+" List Size is "+(userGroupList==null?0:userGroupList.size()));
			return Result.newSuccess(userGroupList);
		}catch(Exception e) {
			result = errors.newFailure("CST0025");
			logger.info("CustomService getChildrenUserByRootId rootId:"+rootId+" Error.By:"+e.getMessage());
			return result;
		}
	}

	/**
	 * 添加用户的审核密码
	 */
	@Override
	public Result<Boolean> createAuditPassword(Long id, String userName,
			String password) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(id,userName,password));
		Result<Boolean> result;
		if(id==null || userName==null ||password==null) {
			result = errors.newFailure("CST1251");
			return result;
		}
		try {
			String auditSalt = passwordService.generateSalt();
			//审核密码加密
			String auditPassword = passwordService.encryptPassword(password,userName.toLowerCase().trim(),auditSalt);
			UserProfile userProfile = userProfileRepository.findOne(id);
			userProfile.setAuditPassword(auditPassword);
			userProfile.setAuditSalt(auditSalt);
			userProfileRepository.save(userProfile);
		}catch(Exception e) {
			result = errors.newFailure("CST0026");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		return Result.newSuccess(true);
	}

	/**
	 * 根据userId取得用户绑定信息
	 */
	@Override
	public Result<UserStatus> getBindingStatus(Long userId) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userId));
		Result<UserStatus> result;
		UserStatus userStatus;
		if(userId == null || ("").equals(userId)) {
			result = errors.newFailure("CST1261");
			return result;
		}
		try {
			userStatus = userStatusRepository.findByUserId(userId);
		}catch(Exception e) {
			result = errors.newFailure("CST0027");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		return Result.newSuccess(userStatus);
	}

	/**
	 * 修改主表user信息
	 */
	@Override
	public Result<User> updateOnlyUser(User user) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(user));
		Result<User> result;
		if(user==null || ("").equals(user)) {
			result = errors.newFailure("CST1271");
			return result;
		}
		try {
			if(user.getId()!=null && !("").equals(user.getId())) {
				userRepository.save(user);
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0028");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		return Result.newSuccess(user);
	}

	/**
	 * 
	* @Description:  根据userId查询此用户是否是吴掌柜的客户
	* @param      userId
	* @return     true:是吴掌柜的客户      false:不是吴掌柜的客户
	* @throws
	 */
	@Override
	public Result<Boolean> isWbossUser(Long userId) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(userId));
		Result<User> result;
		User user = new User();
		if(userId==null || ("").equals(userId)) {
			logger.info("isWbossUser方法参数不全");
			return Result.newFailure("", "isWbossUser方法参数不全");
		}
		try {
			user = userRepository.findOne(userId);
			String registerSource = user.getRegSource();
			if(registerSource.equals(CustomerConstants.RegisterSource.QINGBO.getCode())) {
				return Result.newSuccess(true);
			}else {
				return Result.newSuccess(false);
			}
		}catch(Exception e) {
			result = errors.newFailure("CST0030");
			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return Result.newFailure(result);
		}
	}

	/**
	 * 创建操作员
	 */
//	@Override
//	public Result<User> createOperator(User user) {
//		// TODO Auto-generated method stub
//		Result<User> result;
//		logger.info(SimpleLogFormater.formatParams(user));
//		if(user==null) {
//			result = errors.newFailure("CST1261");
//			return Result.newFailure("", "user is null for createOperator");
//		}
//		if(user.getUserName()==null ||("").equals(user.getUserName())) {
//			result = errors.newFailure("CST1262");
//			return Result.newFailure("", "userName is null for createOperator");
//		}
//		if(user.getRegDate()==null || ("").equals(user.getRegDate())) {
//			user.setRegDate(new Date());
//		}
//		try {
//			user.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
//			//userName去掉空格和全部转化为小写
//			String userName = user.getUserName().trim().toLowerCase();
//			user.setUserName(userName);
//			user.setStatus(CustomerConstants.Status.INACTIVE.getCode());
//			//保存用户
//			User userEntity = userRepository.save(user);
//			UserProfile userProfile = new UserProfile();
//			userProfile.setUserId(user.getId());
//			userProfile.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
//			//保存用户详细信息
//			userProfile.setCustomerNum(null);
//			userProfileRepository.save(userProfile);
//			user.setUserProfile(userProfile);
//			Result.newSuccess(user);
//		}catch(Exception e) {
//			result = errors.newFailure("CST0027");
//			logger.error(SimpleLogFormater.formatException(result.getMessage(), e));
//			return result;
//		}
//		return null;
//	}

	


}
