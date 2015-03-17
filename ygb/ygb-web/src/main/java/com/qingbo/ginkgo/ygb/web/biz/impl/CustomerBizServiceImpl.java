package com.qingbo.ginkgo.ygb.web.biz.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.account.entity.Account;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.base.util.PagerUtil;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.entity.UserEnterpriseProfile;
import com.qingbo.ginkgo.ygb.customer.entity.UserGroup;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.entity.UserStatus;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.service.PasswordService;
import com.qingbo.ginkgo.ygb.customer.service.UserBankCardService;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.Broker;
import com.qingbo.ginkgo.ygb.web.pojo.Investor;
import com.qingbo.ginkgo.ygb.web.pojo.Operator;
import com.qingbo.ginkgo.ygb.web.pojo.UserActivate;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.UserRegister;
@Service
public class CustomerBizServiceImpl implements CustomerBizService{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired private CustomerService customerService;
	@Autowired private AccountService accountService;
	@Autowired private UserBizService userBizService;
	@Autowired private TongjiService tongjiService;
	@Autowired private UserBankCardService userBankCardService;
	@Autowired private PasswordService passwordService;
	/**
	 * 前台投资人注册
	 */
	@Override
	public Result<User> investorRegister(UserRegister userRegister) {
		// TODO Auto-generated method stub
		if(userRegister == null || ("").equals(userRegister)) {
			logger.info("注册参数为空");
			return Result.newFailure("", "注册参数为空");
		}
		boolean flag = this.checkRegister(userRegister.getUserName(), userRegister.getEmail(), userRegister.getMobile());
		if(flag==false) {
			logger.info("用户名、邮箱、手机存在重复");
			return Result.newFailure("", "用户名、邮箱、手机存在重复");
		}
		if(userRegister.getUserName()==null || ("").equals(userRegister.getUserName()) 
				||userRegister.getEmail()==null ||("").equals(userRegister.getEmail())
				||userRegister.getMobile()==null ||("").equals(userRegister.getMobile())
				||userRegister.getRealName()==null || ("").equals(userRegister.getRealName())
				||userRegister.getRegisterSource()==null||("").equals(userRegister.getRegisterSource())) {
			logger.info("注册参数提供不全");
			return Result.newFailure("", "注册参数提供不全");
		}
		if(userRegister.getRole()==null || ("").equals(userRegister.getRole())) {
			userRegister.setRole(CustomerConstants.Role.INVESTOR.getCode());
		}
		if(!userRegister.getRole().equals(CustomerConstants.Role.INVESTOR.getCode())) {
			logger.info("角色传入错误");
			return Result.newFailure("", "此方法只提供投资人注册，传入的角色有误");
		}
		if(userRegister.getRegisterType()==null || ("").equals(userRegister.getRegisterType())) {
			userRegister.setRegisterType(CustomerConstants.UserRegisterType.PERSONAL.getCode());
		}
		Long brokerUserId = null;
		Long marketingUserId = null;
		if(userRegister.getRefererNumber()==null || ("").equals(userRegister.getRefererNumber())) {
		//说明此前台注册的投资人没有填写推荐人编号，那么将此投资人归结到平台经纪人下	
			//平台默认经纪人的user信息
			Result<User> ParentUser = customerService.getOnlyUserByIdOrUserName(null, CustomerConstants.DEFAULT_BROKER_USERNAME);
			//平台默认营销机构的user信息
			Result<User> marktingUser = customerService.getOnlyUserByIdOrUserName(null, CustomerConstants.DEFAULT_AGENCY_USERNAME);
			if(ParentUser.success() && marktingUser.success()) {
				brokerUserId = ParentUser.getObject().getId();
				marketingUserId = marktingUser.getObject().getId();
			}
		}else {
		//前台注册的投资人填写了推荐人的编号	
			Result<User> brokerUser = customerService.getUserByCustomerNum(userRegister.getRefererNumber());
			//校验是否存在此经纪人，并且此编号是不是经纪人编号
			if(brokerUser.success() && brokerUser.getObject().getRole().equals(CustomerConstants.Role.BROKER.getCode())) {
				//取经纪人userId
				brokerUserId = brokerUser.getObject().getId();
				//取得营销机构的userId
				Result<UserGroup> userGroup = customerService.getParentUserByChildId(brokerUserId);
				if(userGroup.success()) {
					marketingUserId = userGroup.getObject().getParentUserId();
				}
			}else {
				logger.info("用户填写的推荐人编号不存在或者此编号不是经纪人的编号");
				return Result.newFailure("", "用户填写的推荐人编号"+userRegister.getRefererNumber()+"不存在或者此编号不是经纪人的编号");
			}
		}
		User user = userRegister.toUser();
		user.setActivateCode(passwordService.generateSalt());
		//业务判断完成，进入注册用户，调用统一的注册方法
		Result<User> result1 = this.GeneralRegister(user, brokerUserId, marketingUserId,null);
		if(result1.success()) {
			return Result.newSuccess(result1.getObject());
		}
		return Result.newSuccess(result1.getObject());
	}

	/**
	 * 经纪人给投资人开户
	 */
	@Override
	public Result<User> createInvestorByBroker(Investor investor) {
		// TODO Auto-generated method stub
		if(investor==null) {
			logger.info("经纪人给投资人开户参数为空");
			return Result.newFailure("", "经纪人给投资人开户参数为空");
		}
		boolean flag = this.checkRegister(investor.getUserName(), investor.getEmail(), investor.getMobile());
		if(flag==false) {
			logger.info("用户名、邮箱、手机存在重复");
			return Result.newFailure("", "用户名、邮箱、手机存在重复");
		}
		//User user1 = new User();
		if(investor.getUserName() == null || ("").equals(investor.getEmail())||
				investor.getMobile()==null || ("").equals(investor.getMobile())||
				investor.getUserName()==null || ("").equals(investor.getUserName())||
				investor.getRegisterSource()==null || ("").equals(investor.getRegisterSource())||
				investor.getRealName()==null ||("").equals(investor.getRealName()) ||
				investor.getBrokerUserId() ==null || ("").equals(investor.getBrokerUserId())) {
			logger.info("经纪人给投资人开户参数提供不全");
			return Result.newFailure("", "经纪人给投资人开户参数提供不全");
		}
		Long brokerUserId = investor.getBrokerUserId();
		Long marketingUserId = null;
		Result<UserGroup> result = customerService.getParentUserByChildId(brokerUserId);
		if(result.success()) {
			marketingUserId = result.getObject().getParentUserId();
		}
		User user = investor.toUser();
		user.setActivateCode(passwordService.generateSalt());
		Result<User> result1 = this.GeneralRegister(user, brokerUserId, marketingUserId,null);
//		if(result1.success()) {
//			user1 = result1.getObject();
//		}
		return result1;
	}

	/**
	 * 营销机构给经纪人开户
	 */
	@Override
	public Result<User> createBrokerByMarketing(Broker broker) {
		// TODO Auto-generated method stub
		if(broker==null) {
			logger.info("营销机构给经纪人开户参数为空");
			return Result.newFailure("", "营销机构给经纪人开户参数为空");
		}
		boolean flag = this.checkRegister(broker.getUserName(), broker.getEmail(), broker.getMobile());
		if(flag==false) {
			logger.info("用户名、邮箱、手机存在重复");
			return Result.newFailure("", "用户名、邮箱、手机存在重复");
		}
		if(broker.getUserName()==null || ("").equals(broker.getUserName())||
				broker.getMarketingUserId()==null || ("").equals(broker.getMarketingUserId())||
				broker.getEmail()==null||("").equals(broker.getEmail())||
				broker.getMobile()==null|| ("").equals(broker.getMobile())||
				broker.getRealName()==null ||("").equals(broker.getRealName())||
				broker.getRegisterSource()==null||("").equals(broker.getRegisterSource())) {
			logger.info("营销机构给经纪人开户参数不全");
			return Result.newFailure("", "营销机构给经纪人开户参数不全");
		}
		
		Long marketingUserId = broker.getMarketingUserId();
		User user = broker.toUser();
		user.setActivateCode(passwordService.generateSalt());
		Result<User> result1 = this.GeneralRegister(user, marketingUserId, marketingUserId,null);
		return result1;
	}

	/**
	 * 担保机构给操作员开户
	 */
	@Override
	public Result<User> createOperator(Operator operator) {
		// TODO Auto-generated method stub
		if(operator == null) {
			logger.info("操作员开户参数为空");
			return Result.newFailure("", "操作员开户参数为空");
		}
		boolean flag = this.checkRegister(operator.getUserName(), null, null);
		if(flag==false) {
			logger.info("用户名重复");
			return Result.newFailure("", "用户名重复");
		}
//		if(operator.getUserName()==null || ("").equals(operator.getUserName())) {
//			logger.info("操作员用户名为空");
//			return Result.newFailure("", "操作员用户名为空");
//		}
//		if(operator.getAuditPassword()==null || ("").equals(operator.getAuditPassword())) {
//			logger.info("操作员审核密码为空");
//			return Result.newFailure("", "操作员审核密码为空");
//		}
		User user = new User();
		user.setUserName(operator.getUserName());
		user.setRole(CustomerConstants.Role.OPERATOR.getCode());
		user.setRegisterType(CustomerConstants.UserRegisterType.PERSONAL.getCode());
		user.setRegSource(CustomerConstants.RegisterSource.BEIYING.getCode());
		user.setActivateCode(passwordService.generateSalt());
		user.setPassword(operator.getPassword());
		UserProfile userProfile = new UserProfile();
		userProfile.setRealName(CustomerConstants.OPERATOR_REALNAME);
		userProfile.setEmail(CustomerConstants.OPERATOR_REALNAME+"_operator@mail.com");
		userProfile.setMobile("00000000000");
		userProfile.setAuditPassword(operator.getAuditPassword());
		user.setUserProfile(userProfile);
		Long marketingUserId = operator.getGuaranteeId();
		String level = operator.getOperatorType();
		//marketingUserId 不需要编号
		Result<User> result1 = this.GeneralRegister(user, marketingUserId, marketingUserId,level);
		//回填审核密码和登录密码
		if(result1.success() && result1.getObject()!=null) {
			Result<Boolean> resu = customerService.resetLoginPassword(result1.getObject().getId(), operator.getPassword());
			Result<Boolean> resu1 = customerService.restAuditPassword(result1.getObject().getId(), operator.getAuditPassword());
			if(resu.success()==false){
				logger.error("登录密码回填失败");
			}
			if(resu1.success()==false) {
				logger.error("审核密码回填失败");
			}
		}
		return result1;
	}

	/**
	 * 校验激活码
	 */
	@Override
	public Result<UserRegister> checkUserActivatedCode(String activatedCode) {
		// TODO Auto-generated method stub
		if(activatedCode == null) {
			logger.info("激活码为空");
			return Result.newFailure("", "激活码为空");
		}
		UserRegister register = new UserRegister();
		Result<User> userRegister = customerService.getUserByActivatedCode(activatedCode);
		if(userRegister.success()) {
			register = UserRegister.formUser(userRegister.getObject());
		}
		return Result.newSuccess(register);
	}

	/**
	 * 用户激活
	 */
	@Override
	public Result<UserRegister> doActivate(UserActivate userActivate) {
		// TODO Auto-generated method stub
		if(userActivate==null) {
			logger.info("激活信息为空");
			return Result.newFailure("", "激活信息为空");
		}
		if(userActivate.getId() == null || ("").equals(userActivate.getId()) || 
				userActivate.getUsersnid()==null || ("").equals(userActivate.getUsersnid()) ||
				userActivate.getPassword() ==null||("").equals(userActivate.getPassword())||
				userActivate.getPayPassword() == null|| ("").equals(userActivate.getPayPassword())) {
			logger.info("用户激活参数不全");
			return Result.newFailure("", "用户激活参数不全");
		}
		UserRegister userRegister = new UserRegister();
		//1.激活用户
		Result<Boolean> result = customerService.doActivate(UserActivate.toUser(userActivate));
		//2.存平台交易密码
		accountService.createAccount(userActivate.getId());
		accountService.resetPassword(userActivate.getId(), userActivate.getPayPassword());
		//3.回填激活时填写的手机号码和身份证号码
		if(result.success()){
			Result<User> result1 = customerService.getUserByUserName(userActivate.getUserName());
			if(result1.success()){
				userRegister = UserRegister.formUser(result1.getObject());
				User user1 = new User();
				user1.setRegisterType(result1.getObject().getRegisterType());
				if((CustomerConstants.UserRegisterType.PERSONAL.getCode()).equals(user1.getRegisterType())){
					UserProfile userProfile = result1.getObject().getUserProfile();
					userProfile.setIdNum(userActivate.getUsersnid());
					userProfile.setMobile(userActivate.getMobile());
					//user1.setUserProfile(userProfile);
					customerService.updateUserProfile(userProfile);
				}else if((CustomerConstants.UserRegisterType.ENTERPRISE.getCode()).equals(user1.getRegisterType())) {
					UserEnterpriseProfile enter = result1.getObject().getEnterpriseProfile();
					enter.setLegalPersonIdNum(userActivate.getUsersnid());
					enter.setContactPhone(userActivate.getMobile());
					//user1.setEnterpriseProfile(enter);
					customerService.updateUserEnterPrise(enter);
				}
				
			}
			//4.更新用户的状态
			UserStatus userStatus = new UserStatus();
			userStatus.setUserId(result1.getObject().getId());
			userStatus.setActivated(CustomerConstants.SomethingBinding.BINDED.getCode());
			userStatus.setEmailbinding(CustomerConstants.SomethingBinding.BINDED.getCode());
			userStatus.setMobilebinding(CustomerConstants.SomethingBinding.BINDED.getCode());
			customerService.updateUserStatus(result1.getObject().getId(), userStatus);
		}
		return Result.newSuccess(userRegister);
	}

	/**
	 * 设置交易密码
	 */
	@Override
	public Result<Boolean> openAccount(Long userId, String password) {
		// TODO Auto-generated method stub
		if(userId == null || ("").equals(userId)||
				password==null || ("").equals(password)) {
			logger.info("设置交易密码参数不全");
			return Result.newFailure("", "设置交易密码参数不全");
		}
		Result<Account> result = accountService.createAccount(userId);
		if(result.success()){
			Result<Boolean> result1 = accountService.resetPassword(userId, password);
			if(result1.success()){
				return Result.newSuccess(result1.getObject());
			}
		}
		return Result.newSuccess(false);
	}

	/**
	 * 分页查询投资人
	 */
	@Override
	public Result<List<Investor>> investorPage(Pager pager,
			Investor investor, Long brokerUserId) {
		// TODO Auto-generated method stub
		List<Investor> userList = new ArrayList<Investor>();
		if(brokerUserId == null ||("").equals(brokerUserId)) {
			logger.info("经纪人userid为空");
			return Result.newFailure("", "经纪人userid为空");
		}
		SqlBuilder sqlBuilder = new SqlBuilder("count(u.id)","user_group g left join user u on g.child_user_id = u.id left join user_profile p on g.child_user_id = p.user_id");
		sqlBuilder.like("u.user_name", investor.getUserName());
		sqlBuilder.like("p.real_name", investor.getRealName());
		//sqlBuilder.like("pr.customer_num", userSearch.getCustomerNum());
		//sqlBuilder.like("qdd.money_more_more_id", userSearch.getMoneyMoreMoreId());
		sqlBuilder.eq("g.parent_user_id", investor.getBrokerUserId().toString());
		sqlBuilder.orderBy("u.create_at desc");
		sqlBuilder.limit(PagerUtil.limit(pager));
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(userList);
		sqlBuilder.select("u.user_name,p.real_name,p.customer_num,u.`status`,u.create_at,g.child_user_id");
		//sqlBuilder.groupBy("u.id");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
		List<Investor> content = new ArrayList<Investor>();
		if(list.size()>0) {
			for(Object obj:list) {
				Investor inves = new Investor();
				Object[] ss = (Object[])obj;
				int idx = 0;
				inves.setUserName(ObjectUtils.toString(ss[idx++]));
				inves.setRealName(ObjectUtils.toString(ss[idx++]));
				inves.setUserNum(ObjectUtils.toString(ss[idx++]));
				inves.setStatus(ObjectUtils.toString(ss[idx++]));
				inves.setCreateDate(ObjectUtils.toString(ss[idx++]));
				inves.setId(NumberUtil.parseLong(ObjectUtils.toString(ss[idx++]), null));
				content.add(inves);
			}
			pager.setElements(content);
			pager.init(count);
		}
		return Result.newSuccess(content);
	}

	/**
	 * 分页查询经纪人
	 */
	@Override
	public Result<List<Broker>> brokerPage(Pager pager, Broker broker,
			Long marketingId) {
		// TODO Auto-generated method stub
		List<Broker> userList = new ArrayList<Broker>();
		if(marketingId==null || ("").equals(marketingId)) {
			logger.info("营销机构userid为空");
			return Result.newFailure("", "营销机构userid为空");
		}
		SqlBuilder sqlBuilder = new SqlBuilder("count(u.id)","user_group g left join user u on g.child_user_id = u.id left join user_profile p on g.child_user_id = p.user_id");
		sqlBuilder.like("u.user_name", broker.getUserName());
		sqlBuilder.like("p.real_name", broker.getRealName());
		sqlBuilder.eq("g.parent_user_id", marketingId.toString());
		sqlBuilder.orderBy("u.create_at desc");
		sqlBuilder.limit(PagerUtil.limit(pager));
		int count = 0;
		if(pager==null || pager.notInitialized()) {
			count = tongjiService.count(sqlBuilder).getObject();
		}else{
			count = pager.getTotalRows();
		}
		if(count < 1) {
			return Result.newSuccess(userList);
		}
		sqlBuilder.select("u.user_name,p.real_name,p.customer_num,u.`status`,u.create_at,g.child_user_id");
		//sqlBuilder.groupBy("u.id");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
		List<Broker> content = new ArrayList<Broker>();
		if(list.size()>0) {
			for(Object obj:list) {
				Broker bro = new Broker();
				Object[] ss = (Object[]) obj;
				int idx = 0;
				bro.setUserName(ObjectUtils.toString(ss[idx++]));
				bro.setRealName(ObjectUtils.toString(ss[idx++]));
				bro.setUserNum(ObjectUtils.toString(ss[idx++]));
				bro.setStatus(ObjectUtils.toString(ss[idx++]));
				bro.setCreateDate(ObjectUtils.toString(ss[idx++]));
				bro.setId(NumberUtil.parseLong(ObjectUtils.toString(ss[idx++]), null));
				content.add(bro);
			}
			pager.setElements(content);
			pager.init(count);
		}
		return Result.newSuccess(content);
	}

	/**
	 * 分页查询操作员
	 */
	@Override
	public Result<List<Operator>> operatorPage(Pager pager,
			Operator operator, Long guaranteeId) {
		// TODO Auto-generated method stub
		List<Operator> userList = new ArrayList<Operator>();
		if(guaranteeId == null || ("").equals(guaranteeId)) {
			logger.info("担保机构userid为空");
			return Result.newFailure("", "担保机构userid为空");
		}
		SqlBuilder sqlBuilder = new SqlBuilder("count(g.child_user_id)","user_group g left join user u on g.child_user_id = u.id");
		sqlBuilder.eq("g.parent_user_id", operator.getGuaranteeId().toString());
		sqlBuilder.like("u.user_name", operator.getUserName());
		sqlBuilder.limit(PagerUtil.limit(pager));
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(userList);
		sqlBuilder.select("u.user_name,u.`status`,g.memo,u.create_at,g.child_user_id,g.level");
		//sqlBuilder.groupBy("u.id");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
		List<Operator> content = new ArrayList<Operator>();
		if(list.size()>0) {
			for(Object obj:list) {
				Operator op = new Operator();
				Object[] ss = (Object[]) obj;
				int idx = 0;
				op.setUserName(ObjectUtils.toString(ss[idx++]));
				op.setStatus(ObjectUtils.toString(ss[idx++]));
				op.setRemark(ObjectUtils.toString(ss[idx++]));
				op.setCreatDate(ObjectUtils.toString(ss[idx++]));
				op.setId(NumberUtil.parseLong(ObjectUtils.toString(ss[idx++]), null));
				op.setLevel(ObjectUtils.toString(ss[idx++]));
				content.add(op);
			}
			pager.setElements(content);
			pager.init(count);
		}
		return Result.newSuccess(content);
	}

	/**
	 * 根据用户userid查询用户的信息及详情
	 */
	@Override
	public Result<UserDetail> getUserDetailByUserId(Long userId) {
		// TODO Auto-generated method stub
		UserDetail userDetail = new UserDetail();;
		if(userId == null || ("").equals(userId)) {
			logger.info("userid为空");
			return Result.newFailure("", "userid为空");
		}
		Result<User> userResult= customerService.getUserByUserId(userId);
		if(userResult.success()) {
			userDetail = UserDetail.formUser(userResult.getObject());
		}
		return Result.newSuccess(userDetail);
	}

	/**
	 * 根据用户名查询用户的信息及详情
	 */
	@Override
	public Result<UserDetail> getUserDetailByUserName(String userName) {
		// TODO Auto-generated method stub
		UserDetail userDetail = null;
		if(userName==null|| ("").equals(userName)) {
			logger.info("userName为空");
			return Result.newFailure("", "userName为空");
		}
		Result<User> userResult= customerService.getUserByUserName(userName);
		if(userResult.success()) {
			userDetail = UserDetail.formUser(userResult.getObject());
		}
		return Result.newSuccess(userDetail);
	}

	/**
	 * 修改登录密码
	 */
	@Override
	public Result<Boolean> modifyLoginPassword(Long userId, String oldPassword,
			String newPassword) {
		// TODO Auto-generated method stub
		if(userId == null ||("").equals(userId)||oldPassword == null ||("").equals(oldPassword)||newPassword==null||("").equals(newPassword)) {
			logger.info("修改登录密码参数不全");
			return Result.newFailure("", "修改登录密码参数不全");
		}
		//1.验证旧的登录密码
		Result<Boolean> flag = customerService.validateLoginPassword(userId, oldPassword);
		if(flag.success() && flag.getObject()==true) {
			//2.修改密码
			Result<Boolean> result = customerService.updateLoginPassword(userId, oldPassword, newPassword);
			if(result.success()) {
				return Result.newSuccess(true);
			}
		}else {
			 return Result.newSuccess(false);
		}
		
		return Result.newSuccess(true);
	}

	/**
	 * 修改交易密码
	 */
	@Override
	public Result<Boolean> modifyTradePassword(Long userId, String oldPassword,
			String newPassword) {
		// TODO Auto-generated method stub
		if(userId == null ||("").equals(userId)||oldPassword == null ||("").equals(oldPassword)||newPassword==null||("").equals(newPassword)) {
			logger.info("修改交易密码参数不全");
			return Result.newFailure("", "修改交易密码参数不全");
		}
		//1.验证旧的登录密码
		Result<Boolean> flag = accountService.validatePassword(userId, oldPassword);
		if(flag.success()) {
			//2.修改密码
			return  accountService.updatePassword(userId, oldPassword, newPassword);
		}else {
			return Result.newSuccess(false);
		}
	}

	/**
	 * 重置登录密码
	 */
	@Override
	public Result<Boolean> resetLoginPassword(Long userId, String password) {
		// TODO Auto-generated method stub
		if(userId ==null || ("").equals(userId) || password==null || ("").equals(password)) {
			logger.info("getBindingStatusByUserId方法的参数不全");
			return Result.newFailure("", "getBindingStatusByUserId方法的参数不全");
		}
		
		return customerService.resetLoginPassword(userId, password);
	}
	
	/**
	 * 重置操作员的审核密码
	 */
	@Override
	public Result<Boolean> resetAuditPassword(Long userId, String password) {
		// TODO Auto-generated method stub
		if(userId == null ||("").equals(userId)|| password ==null||("").equals(password) ) {
			logger.info("重置操作员密码参数不全");
			return Result.newFailure("", "重置操作员密码参数不全");
		}
		return customerService.restAuditPassword(userId, password);
	}

	/**
	 * 修改交易密码
	 */
	@Override
	public Result<Boolean> resetTradePassword(Long userId, String password) {
		// TODO Auto-generated method stub
		if(userId == null ||("").equals(userId)|| password ==null||("").equals(password) ) {
			logger.info("重置操作员密码参数不全");
			return Result.newFailure("", "重置操作员密码参数不全");
		}
		return accountService.resetPassword(userId, password);
	}
	
	/**
	 * 添加银行卡
	 */
	@Override
	public Result<UserBankCard> createBankCard(UserBankCard userBankCard) {
		// TODO Auto-generated method stub
		if(userBankCard.getUserId() == null || ("").equals(userBankCard.getUserId())||
				userBankCard.getBankCardAccountName() == null || ("").equals(userBankCard.getBankCardAccountName())||
				userBankCard.getBankCardNum() == null || ("").equals(userBankCard.getBankCardNum())||
				userBankCard.getBankCode() == null || ("").equals(userBankCard.getBankCode())) {
			logger.info("添加银行卡参数不全");
			return Result.newFailure("", "添加银行卡参数不全");
		}
		if(userBankCard.getBankCardType()==null||("").equals(userBankCard.getBankCardType())) {
			userBankCard.setBankCardType(CustomerConstants.bangCardType.DEBIT_CARD.getCode());
		}
		userBankCard.setIdType(CustomerConstants.IdType.IDENTITY_CARD.getCode());
		Result<UserBankCard> result = userBankCardService.createBankCard(userBankCard);
		//更新状态
		if(result.success()) {
			UserStatus userStatus = new UserStatus();
			userStatus.setBankbinding(CustomerConstants.SomethingBinding.BINDED.getCode());
			Result<Boolean> result1 = customerService.updateUserStatus(userBankCard.getUserId(), userStatus);
			return  Result.newSuccess(result.getObject());
		}
		return Result.newSuccess(result.getObject());
	}

	/**
	 * 修改银行卡
	 */
	@Override
	public Result<UserBankCard> modifyBankCard(UserBankCard userBankCard, Long userId) {
		// TODO Auto-generated method stub
		userBankCard.setUserId(userId);
		return  userBankCardService.updateBankCard(userBankCard);
	}

	
	
	/**
	 * 根据userId查询用户的银行卡相关信息
	 */
	@Override
	public Result<UserBankCard> getUserBankCard(Long userId) {
		// TODO Auto-generated method stub
		if(userId==null || ("").equals(userId)) {
			logger.info("userId为空");
			return Result.newFailure("", "userId为空");
		}
		UserBankCard userBankCard = null;
		Result<List<UserBankCard>> userBankList = userBankCardService.getBankCardByUserId(userId);
		if(userBankList.success() && userBankList.getObject().size()>0) {
			userBankCard = userBankList.getObject().get(0);
		}
		return Result.newSuccess(userBankCard);
	}
	
	/**
	 * 开户校验用户名、手机、邮箱是否唯一
	 */
	public boolean checkRegister(String username,String email,String mobile) {
		Result<Boolean> result = customerService.checkRepeat(username, mobile, email);
		if(result.success() && result.getObject()==true) {
			return true;
		}else {
			return false;
		}
	}
	

	/**
	 * 校验用户名是否已经存在
	 */
	@Override
	public Result<Boolean> checkUserName(String username) {
		// TODO Auto-generated method stub
		if(username == null || ("").equals(username)) {
			logger.info("用户名为空");
			return  Result.newFailure("", "用户名为空");
		}
		Result<Boolean> result = customerService.checkRepeat(username, null, null);
		if(result.success()){
			return Result.newSuccess(result.getObject());
		}
		return Result.newSuccess(false);
	}

	/**
	 * 检查邮箱是否重复
	 */
	@Override
	public Result<Boolean> checkUserEmail(String email) {
		// TODO Auto-generated method stub
		if(email == null || ("").equals(email)) {
			logger.info("邮箱为空");
			return Result.newFailure("", "邮箱为空");
		}
		Result<Boolean> result = customerService.checkRepeat(null, null, email);
		if(result.success()){
			return Result.newSuccess(result.getObject());
		}
		return Result.newSuccess(false);
	}

	/**
	 * 检查手机是否重复
	 */
	@Override
	public Result<Boolean> checkUserMobile(String mobile) {
		// TODO Auto-generated method stub
		if(mobile == null || ("").equals(mobile)) {
			logger.info("手机号码为空");
			return Result.newFailure("", "手机号码为空");
		}
		Result<Boolean> result = customerService.checkRepeat(null, mobile, null);
		if(result.getCode() == 0){
			return Result.newSuccess(result.getObject());
		}
		return Result.newSuccess(false);
	}

	/**
	 * 检查是够存在此推荐人的编号（注:是客户编号是经纪人编号）
	 */
	@Override
	public Result<Boolean> checkUserCustomerNum(String customerNum) {
		// TODO Auto-generated method stub
		if(customerNum == null || ("").equals(customerNum)) {
			logger.info("客户编号为空");
			return Result.newFailure("", "客户编号为空");
		}
		Result<User> result= customerService.getUserByCustomerNum(customerNum);
		if(result.success()) {
			User user = result.getObject();
			if(user.getRole().equals(CustomerConstants.Role.BROKER.getCode())) {
				return Result.newSuccess(true);
			}
		}
		return Result.newSuccess(false);
	}
	

	/**
	 * 公用方法：上边所有的注册均调用此处提供的公用的注册方法,完成1.注册，2.建立上下级关系，3.取得客户编号
	* @Description:  parentUserId 关系中的上级，agencyId：所属营销机构的userId
	* @param      
	* @return 
	* @throws
	 */
	public Result<User> GeneralRegister(User user,Long parentUserId,Long agencyId,String level){
		if(user == null ||parentUserId==null || agencyId == null ||user.getUserProfile()==null) {
			logger.info("注册提供的参数不全");
			return Result.newFailure("", "注册提供的参数不全");
		}
		Result<User> userResult = customerService.register(user);
		User user1 = new User();
		//1.注册成功
		if(userResult.success()) {
			//2.建立上下级联系
			UserGroup userGroup = new UserGroup();
			user1 = userResult.getObject();
			userGroup.setChildUserId(user1.getId());
			userGroup.setParentUserId(parentUserId);
			//如果是投资人：relationship为B-I,级别是2，如果是经纪人：relationship为A-B，级别是1
			if(user1.getRole().equals(CustomerConstants.Role.INVESTOR.getCode())) {
				userGroup.setRelationship(CustomerConstants.UserRelationship.BROKER_INVESTOR.getCode());
				userGroup.setLevel(CustomerConstants.Level.L2.getCode());
			}else if(user1.getRole().equals(CustomerConstants.Role.BROKER.getCode())) {
				userGroup.setRelationship(CustomerConstants.UserRelationship.AGENCY_BROKER.getCode());
				userGroup.setLevel(CustomerConstants.Level.L1.getCode());
			}else {
			//操作员开户	
				userGroup.setRelationship(CustomerConstants.UserRelationship.ENTERPRISE_OPERATOR.getCode());
				userGroup.setLevel(level);
			}
			userGroup.setRootId(agencyId);
			Result<Boolean> result = customerService.setUpUserRelationship(userGroup, null);
			//2.创建关系成功
			if(result.success()) {
				if(!(CustomerConstants.Role.OPERATOR.getCode()).equals(user.getRole())) {
					//3.取得客户编号进行回填
					Result<String> result2 = userBizService.getCustomerNum(agencyId);
					if(result2.success()) {
						Result<User> user2 = customerService.getUserByUserId(user1.getId());
						if(user2.success()) {
							UserProfile userProfile = user2.getObject().getUserProfile();
							userProfile.setCustomerNum(result2.getObject());
							customerService.updateUserProfile(userProfile);
						}
						
					}
				}
			}
		}else {
			return Result.newFailure("", "注册用户时出错");
		}
		return Result.newSuccess(user1);
	}

	/**
	 * 根据用户id查询用户绑定信息
	 */
	@Override
	public Result<AccountDetail> getBindingStatusByUserId(Long userId) {
		// TODO Auto-generated method stub
		AccountDetail accountDetail = new AccountDetail();
		if(userId==null || ("").equals(userId)) {
			logger.info("getBindingStatusByUserId方法的参数不全");
			return Result.newFailure("", "getBindingStatusByUserId方法的参数不全");
		}
		Result<UserStatus> result = customerService.getBindingStatus(userId);
		if(result.success() && result.hasObject()) {
			UserStatus   userStatus = result.getObject();
			accountDetail.setIsMobile(userStatus.getMobilebinding());
			accountDetail.setIsBinding(userStatus.getBankbinding());
			accountDetail.setIsEmail(userStatus.getEmailbinding());
		}
		return Result.newSuccess(accountDetail);
	}

	/**
	 *  根据用户的userId重置用户的activateCode
	 */
	@Override
	public String restActivateCode(Long userId) {
		// TODO Auto-generated method stub
		String activateCode = null;
		if(userId!=null && !("").equals(userId)) {
			Result<User> result = customerService.getOnlyUserByIdOrUserName(userId, null);
			if(result.success()) {
				User user = result.getObject();
				activateCode =passwordService.generateSalt(); 
				user.setActivateCode(activateCode);
				Result<User> userResult = customerService.updateOnlyUser(user);
				if(userResult.success() && userResult.hasObject()) {
					return activateCode;
				}
			}
		}
		return null;
	}


}
