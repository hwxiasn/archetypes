package com.qingbo.ginkgo.ygb.web.biz.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.base.enums.CodeListType;
import com.qingbo.ginkgo.ygb.base.service.CodeListService;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.base.util.PagerUtil;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.DateUtil.FormatType;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.entity.UserEnterpriseProfile;
import com.qingbo.ginkgo.ygb.customer.entity.UserGroup;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.service.PasswordService;
import com.qingbo.ginkgo.ygb.customer.service.UserBankCardService;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.AgencyDetail;
import com.qingbo.ginkgo.ygb.web.pojo.AgencyItem;
import com.qingbo.ginkgo.ygb.web.pojo.AgencySearch;
import com.qingbo.ginkgo.ygb.web.pojo.BrokerAdd;
import com.qingbo.ginkgo.ygb.web.pojo.MarketingItem;
import com.qingbo.ginkgo.ygb.web.pojo.MarketingSearch;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.UserItem;
import com.qingbo.ginkgo.ygb.web.pojo.UserSearch;


@Service
public class UserBizServiceImpl implements UserBizService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired private CustomerService customerService;
	@Autowired private TongjiService tongjiService;
	@Autowired private CodeListService codeListService;
	@Autowired private UserBankCardService  bankCardService;
	@Autowired private PasswordService passwordService;
	@Autowired private AccountService  accountService;
	
	@Override
	public boolean[] login(String userName, String password) {
		boolean[] login = new boolean[3];
		if(StringUtils.isBlank(userName) || StringUtils.isBlank(password))
			return login;
		
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(new UsernamePasswordToken(userName, password));
			if(StringUtils.isBlank(ShiroTool.userRole()))
			subject.login(new UsernamePasswordToken(userName, password));
        } catch (UnknownAccountException e) {
        	logger.info("not exist userName: "+userName);
        } catch(DisabledAccountException e) {
        	logger.info("disabled userName: "+userName);
        } catch(IncorrectCredentialsException e) {
        	logger.info("incorrect password for userName: "+userName);
        } catch(ExcessiveAttemptsException e) {
        	logger.info("excessive attempts for userName: "+userName+", times="+e.getMessage());
        }
		
		if (subject.isAuthenticated()) {
			login[0] = true;
			login[1] = ShiroTool.isFrontAuthenticated();
			login[2] = ShiroTool.isAdminAuthenticated();
			logger.info("successful login of userName:" + userName);
		}else {
			logger.info("fail to login of userName: "+userName);
		}
		return login;
	}

	@Override
	public void logout() {
		SecurityUtils.getSubject().logout();
	}

	/**
	 * 分页查询个人用户
	 */
	@Override
	public Result<List<UserItem>> userPage(UserSearch userSearch, Pager pager) {
		List<UserItem> userList = new ArrayList<UserItem>();
		SqlBuilder sqlBuilder = new SqlBuilder("count(distinct u.id)","user u left join user_profile pr on u.id = pr.user_id left join user_group gr on gr.child_user_id = u.id");
		sqlBuilder.like("u.user_name", userSearch.getUserName());
		sqlBuilder.like("pr.real_name", userSearch.getRealName());
		sqlBuilder.like("pr.customer_num", userSearch.getCustomerNum());
		//sqlBuilder.like("qdd.money_more_more_id", userSearch.getMoneyMoreMoreId());
		sqlBuilder.eq("u.role", userSearch.getRole());
		sqlBuilder.eq("u.status", userSearch.getStatus());
		sqlBuilder.eq("u.register_type",CustomerConstants.UserRegisterType.PERSONAL.getCode());
		sqlBuilder.orderBy("u.create_at desc");
		sqlBuilder.limit(PagerUtil.limit(pager));
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(userList);
		
		//String[] names = {"qdd.money_more_more_id","u.user_name","pro.customer_num","pro.real_name","u.create_at","s.status","gro.parent_user_id as parentUserId","u.role","u.id"};//names与select次序一致
		sqlBuilder.select("u.user_name,u.`status`,u.role,u.create_at,pr.real_name,pr.customer_num,gr.parent_user_id,u.id");
		//sqlBuilder.groupBy("u.id");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
		List<UserItem> content = new ArrayList<UserItem>();
		if(list.size()>0) {
			for(Object obj:list) {
				UserItem userItem = new UserItem();
				Object[] ss = (Object[])obj;
//				int idx = 0;
				//userItem.setMoneyMoreMoreId(ObjectUtils.toString(ss[idx++]));
				userItem.setUserName(ObjectUtils.toString(ss[0]));
				userItem.setStatus(ObjectUtils.toString(ss[1]));
				userItem.setRole(ObjectUtils.toString(ss[2]));
				userItem.setCreateTime(ObjectUtils.toString(ss[3]));
				userItem.setRealName(ObjectUtils.toString(ss[4]));
				userItem.setCustomerNum(ObjectUtils.toString(ss[5]));
				Long parentUserId = NumberUtil.parseLong(ObjectUtils.toString(ss[6]), null);
				if(parentUserId!=null) {
					Result<User> userResult = customerService.getUserByUserId(parentUserId);
					if(userResult.success() && userResult.getObject().getUserProfile()!=null) {
						userItem.setRefererNumber(userResult.getObject().getUserProfile().getCustomerNum());
					}
				}
				userItem.setId(NumberUtil.parseLong(ObjectUtils.toString(ss[7]), null));
				Result<CodeList> codeList1 = codeListService.item(CodeListType.USER_STATUS.getCode(), ObjectUtils.toString(ss[1]));
				Result<CodeList> codeList2 = codeListService.item(CodeListType.USER_ROLE.getCode(), ObjectUtils.toString(ss[2]));
				if(codeList1.success()) {
					userItem.setStatusName(codeList1.getObject().getName());
				}
				if(codeList2.success()) {
					userItem.setRoleName(codeList2.getObject().getName());
				}
				
				content.add(userItem);
			}
			pager.setElements(content);
			pager.init(count);
		}
		return Result.newSuccess(content);
	}

	/**
	 * 新增用户
	 */
	@Override
	public Result<User> saveAddUser(UserDetail userDetail) {
		//userDetail.toUser()将userDetail中的信息转化为user和userProfile
		Result<Boolean> result1 = customerService.checkRepeat(userDetail.getUserName(), userDetail.getMobile(), userDetail.getEmail());
		if(result1.success() && result1.getObject()==false) {
			return Result.newFailure("", "用户名、手机号或者邮箱重复");
		}
		User user = userDetail.toUser();
		user.setActivateCode(passwordService.generateSalt());
		Result<User> result= customerService.register(user);
		//保存用户成功,在保存银行卡相关信息
		if(result.success()) {
				userDetail.setUserId(result.getObject().getId());
				userDetail.setId(result.getObject().getId());
				Result<UserBankCard> resultBank = bankCardService.createBankCard(userDetail.toUserBank());
				//后台用户如果是投资人，要跟平台默认的经纪人建立联系 此处
				if(userDetail.getRole().equals(CustomerConstants.Role.INVESTOR.getCode())) {
					//平台默认的经纪人userName
					String broker = CustomerConstants.DEFAULT_BROKER_USERNAME;
					//平台默认的营销机构
					String markting = CustomerConstants.DEFAULT_AGENCY_USERNAME;
					//新增用户的userid
					Long childId = result.getObject().getId();
					//平台默认经纪人的user信息
					Result<User> ParentUser = customerService.getOnlyUserByIdOrUserName(null, broker);
					//平台默认营销机构的user信息
					Result<User> marktingUser = customerService.getOnlyUserByIdOrUserName(null, markting);
					if(ParentUser.success() && marktingUser.success()) {
						//平台默认经纪人的userid
						Long parentId = ParentUser.getObject().getId();
						//平台默认营销机构的userid
						Long marktingId= marktingUser.getObject().getId();
						UserGroup group = new UserGroup();
						group.setChildUserId(childId);
						group.setParentUserId(parentId);
						group.setLevel("2");
						group.setRootId(marktingId);
						group.setRelationship(CustomerConstants.UserRelationship.BROKER_INVESTOR.getCode());
						customerService.setUpUserRelationship(group, null);
						
					}
					//建立关系后，要计算出customernum进行回填
					Result<String> customerNumResult = this.getCustomerNum(marktingUser.getObject().getId());
					if(customerNumResult.success()) {
						String customerNum = customerNumResult.getObject();
						Result<User> result2 = customerService.getUserByUserId(result.getObject().getId());
						//User user1 =  result2.getObject();
						UserProfile userProfile1 = result2.getObject().getUserProfile();
						userProfile1.setCustomerNum(customerNum);
						//user1.setUserProfile(userProfile1);
						customerService.updateUserProfile(userProfile1);
					}
				}
				if(resultBank.success()) {
					return Result.newSuccess(user);
				}else {
					return Result.newFailure("", "银行卡相关信息保存失败");
				}
			}
		return Result.newSuccess(result.getObject());
	}

	/**
	 * 根据id取得用户信息
	 */
	@Override
	public Result<UserDetail> getUser(Long UserId) {
		Result<User> userResult = customerService.getUserByUserId(UserId);
		UserDetail userDetail = new UserDetail();
		if(userResult.success() && userResult.getObject()!=null) {
			User user =  userResult.getObject();
			userDetail.setId(user.getId());
			userDetail.setUserName(user.getUserName());
			userDetail.setRole(user.getRole());
			userDetail.setStatus(user.getStatus());
			if(user.getUserProfile()!=null) {
				UserProfile userPro = user.getUserProfile();
				userDetail.setRealName(userPro.getRealName());
				userDetail.setEmail(userPro.getEmail());
				userDetail.setMobile(userPro.getMobile());
				userDetail.setIdCopyBank(userPro.getIdCopyBank());
				userDetail.setIdCopyFront(userPro.getIdCopyFront());
				userDetail.setIdNum(userPro.getIdNum());
				userDetail.setIdValidTo(DateUtil.format(userPro.getIdValidTo(), FormatType.DAYTIME));
				userDetail.setCustomerNum(userPro.getCustomerNum());
				userDetail.setMemo(userPro.getMemo());
			}
		}else {
			return Result.newFailure("", "user is not exist for"+UserId);
		}
		Result<List<UserBankCard>> bankList =bankCardService.getBankCardByUserId(UserId);
		if(bankList.success() && bankList.getObject()!=null) {
			 if(bankList.getObject().size()>0) {
			    	UserBankCard bankCard = bankList.getObject().get(0);
			    	userDetail.setBankCode(bankCard.getBankCode());
			    	userDetail.setBankCardAccountName(bankCard.getBankCardAccountName());
			    	userDetail.setBankCardNum(bankCard.getBankCardNum());
			    	userDetail.setProvince(bankCard.getProvince());
			    	userDetail.setCity(bankCard.getCity());
			    	userDetail.setAddress(bankCard.getAddress());
			    }
		}
	   
		return Result.newSuccess(userDetail);
	}

	/**
	 * 修改保存后的用户信息
	 */
	@Override
	public Result<Boolean>  saveEditUser(UserDetail userDetail) {
		if(userDetail!=null) {
			Result<User> user = customerService.getUserByUserId(userDetail.getId());
			if(user.success()&& user.getObject().getUserProfile()!=null) 
			{
				UserProfile userPro = user.getObject().getUserProfile();
				userPro.setEmail((userDetail.getEmail()==null ||("").equals(userDetail.getEmail()))?userPro.getEmail():userDetail.getEmail());
				userPro.setMobile((userDetail.getMobile()==null ||("").equals(userDetail.getMobile()))?userPro.getMobile():userDetail.getMobile());
				userPro.setIdNum((userDetail.getIdNum()==null || ("").equals(userDetail.getIdNum()))?userPro.getIdNum():userDetail.getIdNum());
				userPro.setIdCopyBank((userDetail.getIdCopyBank()==null || ("").equals(userDetail.getIdCopyBank()))?userPro.getIdCopyBank():userDetail.getIdCopyBank());
				userPro.setIdCopyFront((userDetail.getIdCopyFront()==null|| ("").equals(userDetail.getIdCopyFront()))?userPro.getIdCopyFront():userDetail.getIdCopyFront());
				userPro.setIdValidTo(DateUtil.parse(userDetail.getIdValidTo()));
				Result<Boolean> result = customerService.updateUserProfile(userPro);
				if(!result.success()) {
					return Result.newFailure("","修改用户信息时出错 id "+userDetail.getUserId());
				}
			}
			Result<List<UserBankCard>> bankResult = bankCardService.getBankCardByUserId(user.getObject().getId());
			if(bankResult.success() && bankResult.getObject().size()>0) 
			{
				UserBankCard bankCard= bankResult.getObject().get(0);
				bankCard.setBankCardAccountName(userDetail.getRealName());
				bankCard.setBankCardNum(userDetail.getBankCardNum());
				bankCard.setProvince(userDetail.getProvince());
				bankCard.setCity(userDetail.getCity());
				bankCard.setBankCode(userDetail.getBankCode());
				bankCard.setIdNum(userDetail.getIdNum());
				bankCard.setAddress(userDetail.getAddress());
				bankCard.setUserId(user.getObject().getId());
				Result<UserBankCard> bankRes =  bankCardService.updateBankCard(bankCard);
				if(!bankRes.success()) {
					return Result.newFailure("","用户id为 "+userDetail.getUserId()+"的用户修改银行卡相关信息失败");
				}
			}
			//如果用户的状态有改变
			if(userDetail.getStatus()!=null && !("").equals(userDetail.getStatus()) && !(userDetail.getStatus().equals(user.getObject().getStatus()))) {
				User user1 = new User();
				user1 = user.getObject();
				user1.setStatus(userDetail.getStatus());
				Result<User> resultUser = customerService.updateOnlyUser(user1);
				if(!resultUser.success()) {
					return Result.newFailure("","用户id为 "+userDetail.getUserId()+"的用户修改用户状态失败");
				}
			}
		}
		return Result.newSuccess(true);
	}

	/**
	 * 企业用户分页
	 */
	@Override
	public Result<List<AgencyItem>> agencyPage(AgencySearch agencySearch, Pager pager) {
		List<AgencyItem> agencyList = new ArrayList<AgencyItem>();
		SqlBuilder sqlBuilder = new SqlBuilder("count(pro.id) ","user_enterprise_profile pro "+
				"left  join user cus on cus.id = pro.user_id ");
				//"left join user_status st on st.user_id = pro.user_id");
		sqlBuilder.like("cus.user_name", agencySearch.getUserName());
		sqlBuilder.like("pro.enterprise_name", agencySearch.getEnterpriseName());
		sqlBuilder.eq("cus.role", agencySearch.getRole());
		sqlBuilder.eq("cus.status", agencySearch.getStatus());
		sqlBuilder.orderBy("cus.create_at desc");
		sqlBuilder.limit(PagerUtil.limit(pager));
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(agencyList);
		sqlBuilder.select("cus.id,cus.user_name,cus.role,cus.create_at,pro.id as userId,pro.enterprise_name,pro.contact_name,cus.status");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
		List<AgencyItem> content = new ArrayList<AgencyItem>();
		if(list!=null && !("").equals(list)) {
			if(list.size()>0) {
				for(Object obj:list) {
					AgencyItem agencyItem = new AgencyItem();
					Object[] ss = (Object[])obj;
					int idx = 0;
					Long userId = NumberUtil.parseLong(ObjectUtils.toString(ss[idx++]),null);
					agencyItem.setUserId(userId);
					agencyItem.setUserName(ObjectUtils.toString(ss[idx++]));
					agencyItem.setRole(ObjectUtils.toString(ss[idx++]));
					agencyItem.setCreateTime(ObjectUtils.toString(ss[idx++]));
					agencyItem.setUserProfileId(NumberUtil.parseLong(ObjectUtils.toString(ss[idx++]),null));
					agencyItem.setEnterpriseName(ObjectUtils.toString(ss[idx++]));
					agencyItem.setContactName(ObjectUtils.toString(ss[idx++]));
					agencyItem.setStatus(ObjectUtils.toString(ss[idx++]));
					//名称需要转化
					//agencyItem.setStatusName(null);
					//agencyItem.setRoleName(null);
					//此处根据userId查询支付账户id
					//agencyItem.setMoneyMoreMoreId(null);
					content.add(agencyItem);
				}	
				pager.setElements(content);	
				pager.init(count);
			}
		}
		
		return Result.newSuccess(content);
	}

	
	
	/**
	 * 企业用户的新增
	 */
	@Override
	public Result<User> saveAddAgency(AgencyDetail agencyDetail) {
		User user = agencyDetail.toUser();
		user.setActivateCode(passwordService.generateSalt());
		Result<User> result= customerService.register(user);
		if(result.success()) {
			Long userId = result.getObject().getId();
			agencyDetail.setUserId(userId);
			Result<UserBankCard> result1 = bankCardService.createBankCard(agencyDetail.toUserBank());
			if(result1.success()) {
				return Result.newSuccess(result.getObject());
			}else {
				return Result.newFailure("", "保存用户名为"+agencyDetail.getUserName() +"的银行信息时出错");
			}
		}
		return null;
	}

	/**
	 * 根据用户id获取企业相关信息
	 */
	@Override
	public Result<AgencyDetail> getAgency(Long userId) {
		AgencyDetail agencyDetail = new AgencyDetail();
		Result<User> result = customerService.getUserByUserId(userId);
		if(result.success()&&result.getObject().getEnterpriseProfile()!=null) {
			 User user = result.getObject();
			 UserEnterpriseProfile prise = result.getObject().getEnterpriseProfile();
			// Copier.cope();
			 agencyDetail.setUserId(userId);
			 agencyDetail.setUserName(user.getUserName());
			 agencyDetail.setRole(user.getRole());
			 agencyDetail.setEnterpriseName(prise.getEnterpriseName());
			 agencyDetail.setEnterpriseBriefCode(prise.getEnterpriseBriefCode());
			 agencyDetail.setEnterpriseSubcodeFrom(prise.getEnterpriseSubcodeFrom());
			 agencyDetail.setEnterpriseSubcodeTo(prise.getEnterpriseSubcodeTo());
			 agencyDetail.setAgencyId(prise.getId());
			 agencyDetail.setContactName(prise.getContactName());
			 agencyDetail.setContactEmail(prise.getContactEmail());
			 agencyDetail.setContactPhone(prise.getContactPhone());
			 agencyDetail.setLegalPersonIdCopyBack(prise.getLegalPersonIdCopyBack());
			 agencyDetail.setLegalPersonIdCopyFont(prise.getLegalPersonIdCopyFont());
			 agencyDetail.setLegalPersonIdNum(prise.getLegalPersonIdNum());
			 agencyDetail.setLegalPersonName(prise.getLegalPersonName());
			 agencyDetail.setLicenseCachetPath(prise.getLicenseCachetPath());
			 agencyDetail.setLicenseNum(prise.getLicenseNum());
			 agencyDetail.setLicensePath(prise.getLicenseCachetPath());
			 agencyDetail.setLicenseValidPeriod(prise.getLicenseValidPeriod());
			 agencyDetail.setOpenningLicensePath(prise.getOpenningLicensePath());
			 agencyDetail.setOrganizationCode(prise.getOrganizationCode());
			 agencyDetail.setPhoneNum(prise.getPhoneNum());
			 agencyDetail.setRegisterAddress(prise.getRegisterAddress());
			 agencyDetail.setRegisterCity(prise.getRegisterCity());
			 agencyDetail.setRegisterProvince(prise.getRegisterProvince());
			 agencyDetail.setTaxRegistrationNo(prise.getTaxRegistrationNo());
			 agencyDetail.setMemo(prise.getMemo());
		}else {
			return Result.newFailure("", "用户ID为 "+userId+"的用户不存在");
		}
		Result<List<UserBankCard>> bankList =bankCardService.getBankCardByUserId(userId);
		if(bankList.success() && bankList.getObject()!=null) {
			 if(bankList.getObject().size()>0) {
			    	UserBankCard bankCard = bankList.getObject().get(0);
			    	agencyDetail.setBankCode(bankCard.getBankCode());
			    	agencyDetail.setBankCardAccountName(agencyDetail.getEnterpriseName());
			    	agencyDetail.setBankCardNum(bankCard.getBankCardNum());
			    	agencyDetail.setProvince(bankCard.getProvince());
			    	agencyDetail.setCity(bankCard.getCity());
			    	agencyDetail.setAddress(bankCard.getAddress());
			    }
		}
		return Result.newSuccess(agencyDetail);
	}

	/**
	 *  保存修改后的企业信息
	 */
	@Override
	public Result<Boolean> saveEditAgency(AgencyDetail agencyDetail) {
		if(agencyDetail == null ) {
			return Result.newFailure("", "parements is null for saveEditAgency");
		}
		Long userId = agencyDetail.getUserId();
		Result<User> user = customerService.getUserByUserId(userId);
		if(user.success() && user.getObject().getEnterpriseProfile()!=null) {
			UserEnterpriseProfile enterPrise = user.getObject().getEnterpriseProfile();
			enterPrise.setContactEmail(agencyDetail.getContactEmail());
			enterPrise.setContactName(agencyDetail.getContactName());
			enterPrise.setContactPhone(agencyDetail.getContactPhone());
			enterPrise.setEnterpriseBriefCode(agencyDetail.getEnterpriseBriefCode());
			enterPrise.setEnterpriseName(agencyDetail.getEnterpriseName());
			enterPrise.setEnterpriseSubcodeFrom(agencyDetail.getEnterpriseSubcodeFrom());
			enterPrise.setEnterpriseSubcodeTo(agencyDetail.getEnterpriseSubcodeTo());
			enterPrise.setLegalPersonIdCopyBack(agencyDetail.getLegalPersonIdCopyBack());
			enterPrise.setLegalPersonIdCopyFont(agencyDetail.getLegalPersonIdCopyFont());
			enterPrise.setLegalPersonIdNum(agencyDetail.getLegalPersonIdNum());
			enterPrise.setLegalPersonName(agencyDetail.getLegalPersonName());
			enterPrise.setLicenseCachetPath(agencyDetail.getLicenseCachetPath());
			enterPrise.setLicenseNum(agencyDetail.getLicenseNum());
			enterPrise.setLicensePath(agencyDetail.getLicensePath());
			enterPrise.setLicenseValidPeriod(agencyDetail.getLicenseValidPeriod());
			enterPrise.setOpenningLicensePath(agencyDetail.getOpenningLicensePath());
			enterPrise.setOrganizationCode(agencyDetail.getOrganizationCode());
			enterPrise.setTaxRegistrationNo(agencyDetail.getTaxRegistrationNo());
			enterPrise.setRegisterAddress(agencyDetail.getRegisterAddress());
			enterPrise.setRegisterCity(agencyDetail.getRegisterCity());
			enterPrise.setRegisterProvince(agencyDetail.getRegisterProvince());
			enterPrise.setPhoneNum(agencyDetail.getPhoneNum());
			enterPrise.setMemo(agencyDetail.getMemo());
			customerService.updateUserEnterPrise(enterPrise);
			Result<List<UserBankCard>> bankResult = bankCardService.getBankCardByUserId(user.getObject().getId());
			if(bankResult.success() && bankResult.getObject().size()>0) 
			{
				UserBankCard bankCard= bankResult.getObject().get(0);
				bankCard.setBankCardAccountName(agencyDetail.getBankCardAccountName());
				bankCard.setBankCardNum(agencyDetail.getBankCardNum());
				bankCard.setProvince(agencyDetail.getProvince());
				bankCard.setCity(agencyDetail.getCity());
				bankCard.setBankCode(agencyDetail.getBankCode());
				bankCard.setIdNum(agencyDetail.getLegalPersonIdNum());
				bankCard.setAddress(agencyDetail.getAddress());
				bankCard.setUserId(agencyDetail.getUserId());
				Result<UserBankCard> bankRes =  bankCardService.updateBankCard(bankCard);
				if(bankRes.success()) {
					return Result.newSuccess(true);
				}else {
					return Result.newFailure("", "用户id为"+user.getObject().getId()+"的用户没有银行卡相关信息");
				}
				
			}
		}else {
			return Result.newFailure("", "用户id为 "+userId+"的用户不存在");
		}
		return Result.newSuccess(true);
	}

	/**
	 * 带参数分页查询系统中所有的营销机构
	 */
	@Override
	public Result<List<MarketingItem>> marktingPage(Pager pager,
			MarketingSearch marketingSearch) {
		List<MarketingItem> marktingList = new ArrayList<MarketingItem>();
		SqlBuilder sqlBuilder = new SqlBuilder("count(enter.id)","user_enterprise_profile enter " +
				"left join user user on enter.user_id = `user`.id ") ;
				//"left join user_status st on `user`.id = st.user_id");
		
		sqlBuilder.like("`user`.user_name", marketingSearch.getUserName());
		sqlBuilder.like("enter.enterprise_name", marketingSearch.getEnterpriseName());
		sqlBuilder.eq("user.role", CustomerConstants.Role.AGENCY.getCode());
		sqlBuilder.limit(PagerUtil.limit(pager));
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(marktingList);
		sqlBuilder.select("enter.user_id,enter.enterprise_name,enter.enterprise_brief_code,`user`.user_name,`user`.create_at,user.`status`");
		//sqlBuilder.limit(pager.getPageSize());
		List<?> list = tongjiService.list(sqlBuilder).getObject();
		List<MarketingItem> content = new ArrayList<MarketingItem>();
		if(list.size()>0) {
			for(Object obj:list) {
				MarketingItem marktingItem = new MarketingItem();
				Object[] array = (Object[]) obj;
				int idx = 0;
				//机构用户表中的userid
				marktingItem.setMarketingUserId(NumberUtil.parseLong(ObjectUtils.toString(array[idx++]),null));
				marktingItem.setEnterpriseName(ObjectUtils.toString(array[idx++]));
				marktingItem.setEnterpriseBriefCode(ObjectUtils.toString(array[idx++]));
				marktingItem.setUserName(ObjectUtils.toString(array[idx++]));
				marktingItem.setCreateTime(ObjectUtils.toString(array[idx++]));
				//将状态转化为statusname在页面展示
				marktingItem.setStatus(ObjectUtils.toString(array[idx++]));
				content.add(marktingItem);
			}
			pager.setElements(content);
			pager.init(count);
		}
		return Result.newSuccess(content);
	}

	
	/**
	 * 取得客户编号 
	 */
	@Override
	public Result<String> getCustomerNum(Long agencyId) {
		String customerNum=null;
		Result<User> result = customerService.getUserByUserId(agencyId);
		if(result.success() && result.getObject().getEnterpriseProfile()!=null) {
			UserEnterpriseProfile enterPrise = result.getObject().getEnterpriseProfile();
			String code = enterPrise.getEnterpriseBriefCode();
			String subForm = enterPrise.getEnterpriseSubcodeFrom();
			String subTo = enterPrise.getEnterpriseSubcodeTo();
			SqlBuilder sqlBuilder = new SqlBuilder();
			sqlBuilder.select("pro.customer_num ");
			sqlBuilder.from("user_profile pro ");
			sqlBuilder.where("pro.customer_num LIKE'"+code+"%'");
			sqlBuilder.orderBy("pro.customer_num DESC");
			Result<List> resultList = tongjiService.list(sqlBuilder);
			if(resultList.success()) {
				//说明数据库中此机构用户已经存在客户了
				if(resultList.getObject().size()>0) {
					String oldCode = resultList.getObject().get(0).toString();
					//机构简码
					int index = code.length();
					int temp = Integer.parseInt(oldCode.substring(index));
					//简码开始的位数formLength
					customerNum = code+preString(temp+1,subForm.length());
				}else {
				//说明数据库中此机构下不存在客户
					customerNum = code+subForm;
				}
			}
			
		}else {
			return Result.newFailure("", "没有找到用户id为 "+agencyId+"的用户");
		}
		return Result.newSuccess(customerNum);
	}

	//按照传进去的leng长度来补位 此处掺入的是enterPrise.getEnterpriseSubcodeFrom();的长度的位数
	private String preString(int num,int leng) {
		String s = String.valueOf(num);
		int i=s.length();
		while(i<leng) {
			s = "0"+s;
			i++;
		}
		return s;
	}

	/**
	 * 根据用户名称取得用户
	 */
	@Override
	public Result<User> getUserByUserName(String userName) {
		Result<User> user = customerService.getUserByUserName(userName);
		if(user.success()) {
			 return Result.newSuccess(user.getObject());
		}
		return null;
	}

	/**
	 * 修改用户的状态(正常、冻结等)
	 */
	@Override
	public UserDetail updateUserStatus(Long id, String status) {
		// TODO Auto-generated method stub
		UserDetail userDetail = null;
		if(id==null || ("").equals(id) || status==null || ("").equals(status)) {
			Result.newFailure(null, "updateUserStatus参数为空");
			logger.info("the parameter for updateUserStatus is null");
		}
		User user = new User(); 
		Result<User> result = customerService.getUserByUserId(id);
		if(result.success()) {
			user = result.getObject();
			user.setStatus(status);
			 Result<User> result1 = customerService.updateOnlyUser(user);
			 if(result1.success()) {
				 userDetail = userDetail.formUser(user);
				 userDetail.setStatusName(codeListService.item(CodeListType.USER_STATUS.getCode(), status).getObject().getName());
			 }
		}
		return userDetail;
	}
	
	
	/**
	 * 检查用户是否已经建立联系
	 */
	@Override
	public Result<Boolean> checkSetUpRelationship(String userName) {
		
		Result<User> user = customerService.getUserByUserName(userName);
		if(user.success()) {
			Result<UserGroup> result = customerService.getParentUserByChildId(user.getObject().getId());
			if(result.success() && result.getObject()!=null) {
				return Result.newSuccess(true);
			}else {
				return Result.newSuccess(false);
			}
		}else {
			return Result.newFailure("", "不存在用户名为"+userName+" 的用户");
		}
	}

	
	
	/**
	 * 营销机构添加经纪人
	 */
	@Override
	public Result<Boolean> brokerAddSave(BrokerAdd brokerAdd) {
		Result<Boolean> result1;
		String userName = brokerAdd.getUserName();
		Result<User> user = customerService.getUserByUserName(userName);
		if(user.success()) {
			
			UserGroup userGroup = new UserGroup();
			//营销机构userId
			userGroup.setParentUserId(brokerAdd.getMarketingUserId());
			
			//经纪人userId
			userGroup.setChildUserId(user.getObject().getId());
			userGroup.setLevel("1");
			userGroup.setRootId(brokerAdd.getMarketingUserId());
			userGroup.setRelationship(CustomerConstants.UserRelationship.AGENCY_BROKER.getCode());
			Result<Boolean> resultl = customerService.setUpUserRelationship(userGroup, null);
			//保存客户编号
			UserProfile userProfile = user.getObject().getUserProfile();
			userProfile.setCustomerNum(brokerAdd.getCustomerNum());
			//User user1 = new User();
			//user1.setRegisterType(CustomerConstants.UserRegisterType.PERSONAL.getCode());
			//user1.setId(user.getObject().getId());
			//user1.setUserProfile(userProfile);
			Result<Boolean> result = customerService.updateUserProfile(userProfile);
			if(result.success()&& resultl.success()) {
				result1 = Result.newSuccess(true);
			}else {
				result1 = Result.newSuccess(false);
			}
			
		}else {
			result1 = Result.newFailure("", "不存在用户名为:"+userName+"的用户");
		}
		return result1;
	}

	/**
	 * 分页带参数查询营销机构下的经纪人
	 */
	@Override
	public Result<List<MarketingItem>> userPageForBroker(
			MarketingSearch marketingSearch, Pager pager) {
		List<MarketingItem> marktingList = new ArrayList<MarketingItem>();
		//取出营销机构的用户id
		if(marketingSearch == null) {
			Result.newFailure(null, "参数marketingSearch为空");
			logger.info("参数marketingSearch为空");
		}
		if(marketingSearch.getMarketingUserId() == null) {
			Result.newFailure(null, "营销机构userid为空");
			logger.info("营销机构userid为空");
		}
		Long marketingUserId = marketingSearch.getMarketingUserId();
		SqlBuilder sqlBuilder = new SqlBuilder("count(gr.child_user_id)","user_group gr left join user u on gr.child_user_id = u.id left join user_profile pr on gr.child_user_id = pr.user_id");
		sqlBuilder.like("u.user_name", marketingSearch.getUserName());
		sqlBuilder.like("pr.real_name", marketingSearch.getRealName());
		//sqlBuilder.like("pr.customer_num", marketingSearch.getCustomerNum());
		sqlBuilder.eq("gr.parent_user_id", marketingUserId.toString());
		sqlBuilder.limit(PagerUtil.limit(pager));
		//sqlBuilder.eq("u.role",CustomerConstants.Role.BROKER.getCode());
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(marktingList);
		sqlBuilder.select("u.user_name,u.create_at,pr.real_name,pr.customer_num,u.`status`,gr.child_user_id,gr.parent_user_id");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
		List<MarketingItem> content = new ArrayList<MarketingItem>();
		if(list.size()>0) {
			for(Object obj:list) {
				MarketingItem marktingItem = new MarketingItem();
				Object[] array = (Object[]) obj;
				int idx = 0;
				marktingItem.setUserName(ObjectUtils.toString(array[idx++]));
				marktingItem.setCreateTime(ObjectUtils.toString(array[idx++]));
				marktingItem.setRealName(ObjectUtils.toString(array[idx++]));
				marktingItem.setCustomerNum(ObjectUtils.toString(array[idx++]));
				marktingItem.setStatus(ObjectUtils.toString(array[idx++]));
				marktingItem.setBrokerUserId(NumberUtil.parseLong(ObjectUtils.toString(array[idx++]),null));
				marktingItem.setMarketingUserId(NumberUtil.parseLong(ObjectUtils.toString(array[idx++]),null));
				content.add(marktingItem);
			}
			pager.setElements(content);
			pager.init(count);
		}
		
		return Result.newSuccess(content);
	}

	/**
	 * 分页带参数查询经纪人下的投资人 
	 */
	@Override
	public Result<List<MarketingItem>> userPageForInvestor(
			MarketingSearch marketingSearch, Pager pager) {
		List<MarketingItem> marktingList = new ArrayList<MarketingItem>();
		//取出营销机构的用户id
		if(marketingSearch == null) {
			Result.newFailure(null, "参数marketingSearch为空");
			logger.info("参数marketingSearch为空");
		}
		if(marketingSearch.getBrokerUserId() == null) {
			Result.newFailure(null, "经纪人用户id为空");
			logger.info("参数经纪人用户id为空");
		}
		Long brokerUserId = marketingSearch.getBrokerUserId();
		SqlBuilder sqlBuilder = new SqlBuilder("count(gr.child_user_id)","user_group gr left join user u on gr.child_user_id = u.id left join user_profile pr on gr.child_user_id = pr.user_id");
		sqlBuilder.like("u.user_name", marketingSearch.getUserName());
		sqlBuilder.like("pr.real_name", marketingSearch.getRealName());
		sqlBuilder.like("pr.customer_num", marketingSearch.getCustomerNum());
		sqlBuilder.eq("gr.parent_user_id", brokerUserId.toString());
		sqlBuilder.limit(PagerUtil.limit(pager));
		//sqlBuilder.eq("u.role",CustomerConstants.Role.INVESTOR.getCode());
		int count = 0;
		if(pager==null || pager.notInitialized()) count = tongjiService.count(sqlBuilder).getObject();
		else count = pager.getTotalRows();
		if(count < 1) return Result.newSuccess(marktingList);
		sqlBuilder.select("u.user_name,u.create_at,pr.real_name,pr.customer_num,u.`status`,gr.child_user_id,gr.parent_user_id");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
		List<MarketingItem> content = new ArrayList<MarketingItem>();
		if(list.size()>0) {
			for(Object obj:list) {
				MarketingItem marktingItem = new MarketingItem();
				Object[] array = (Object[]) obj;
				int idx = 0;
				marktingItem.setUserName(ObjectUtils.toString(array[idx++]));
				marktingItem.setCreateTime(ObjectUtils.toString(array[idx++]));
				marktingItem.setRealName(ObjectUtils.toString(array[idx++]));
				marktingItem.setCustomerNum(ObjectUtils.toString(array[idx++]));
				marktingItem.setStatus(ObjectUtils.toString(array[idx++]));
				marktingItem.setInvestUsetId(NumberUtil.parseLong(ObjectUtils.toString(array[idx++]),null));
				marktingItem.setBrokerUserId(NumberUtil.parseLong(ObjectUtils.toString(array[idx++]),null));
				content.add(marktingItem);
			}
			pager.setElements(content);
			pager.init(count);
		}
		
		return Result.newSuccess(content);
	}

	@Override
	public String getUserActivatedCode(Long id) {
		if(id==null||("").equals(id)) {
			Result.newFailure(null, "参数id为空");
			logger.info("参数id为空");
		}
		String activateCode="";
		Result<User> user = customerService.getUserByUserId(id);
		if(user.success()) {
			activateCode = user.getObject().getActivateCode();
		}
		return activateCode;
	}

	@Override
	public AccountDetail getAccountDetail(Long userId) {
		// 账户信息
		AccountDetail accountDetail = new AccountDetail();
		Result<SubAccount> result1 = accountService.getSubAccount(userId);
		if(result1.success() && result1.getObject()!=null){
			accountDetail.setBalance(result1.getObject().getBalance().toPlainString());
			accountDetail.setLockBalance(result1.getObject().getFreezeBalance().toPlainString());
		}
		
		// 用户信息
		Result<User> result2 = customerService.getUserByUserId(userId);
		if(result2.success() && result2.getObject()!=null) {
			User user =  result2.getObject();
			accountDetail.setUserName(user.getUserName());
			if(user.getUserProfile() != null) {
				UserProfile userPro = user.getUserProfile();
				accountDetail.setRealName(userPro.getRealName());
				accountDetail.setEmail(userPro.getEmail());
				accountDetail.setTelephone(userPro.getMobile());
				accountDetail.setCardNum(userPro.getIdNum());
				accountDetail.setUserNum(userPro.getCustomerNum());
			}
			if(user.getEnterpriseProfile() != null){
				UserEnterpriseProfile eProfile = user.getEnterpriseProfile();
				accountDetail.setRealName(eProfile.getEnterpriseName());
				accountDetail.setEmail(eProfile.getContactEmail());
				accountDetail.setTelephone(eProfile.getContactPhone());
				accountDetail.setCardNum(eProfile.getLegalPersonIdNum());
			}
		}
		
		// 银行卡信息
		Result<List<UserBankCard>> result3 =bankCardService.getBankCardByUserId(userId);
		if(result3.success() && result3.getObject() != null) {
			 if(result3.getObject().size() > 0) {
			    	UserBankCard bankCard = result3.getObject().get(0);
			    	accountDetail.setBank(bankCard.getBankCode());
			    	accountDetail.setBankCardNum(bankCard.getBankCardNum());
			    	accountDetail.setProvince(bankCard.getProvince());
			    	accountDetail.setCity(bankCard.getCity());
			    }
		}
		
		return accountDetail;
	}

	
}
