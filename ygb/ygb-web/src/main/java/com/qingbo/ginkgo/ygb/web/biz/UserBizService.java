package com.qingbo.ginkgo.ygb.web.biz;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.customer.entity.User;
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


public interface UserBizService {
	boolean[] login(String userName, String password);
	void logout();
	
	/**
	 * 
	* @Description:  分页查询个人用户
	* @param      
	* @return 
	* @throws
	 */
	public Result<List<UserItem>> userPage(UserSearch userSearch, Pager pager);
	
	/**
	 * 
	* @Description:  新增用户
	* @param      
	* @return 
	* @throws
	 */
	public Result<User> saveAddUser(UserDetail userDetail);
	
	/**
	 * 
	* @Description:  根据用户的id查询用户的所有信息
	* @param      
	* @return 
	* @throws
	 */
	public Result<UserDetail> getUser(Long UserId);
	
	/**
	 * 
	* @Description:  保存修改后的用户信息
	* @param      
	* @return 
	* @throws
	 */
	public Result<Boolean> saveEditUser(UserDetail userDetail);
	
	
	/**
	 * 
	* @Description:  分页查询企业用户
	* @param      
	* @return 
	* @throws
	 */
	Result<List<AgencyItem>> agencyPage(AgencySearch agencySearch, Pager pager);
	
	
	
	/**
	 * 
	* @Description:  企业用户的新增
	* @param      
	* @return 
	* @throws
	 */
	Result<User> saveAddAgency(AgencyDetail agencyDetail);
	
	
	
	/**
	 * 
	* @Description:  根据用户id获取企业相关信息
	* @param      
	* @return 
	* @throws
	 */
	Result<AgencyDetail> getAgency(Long UserId);
	
	/**
	 * 
	* @Description: 保存修改后的企业信息
	* @param      
	* @return 
	* @throws
	 */
	Result<Boolean> saveEditAgency(AgencyDetail agencyDetail);
	
	/**
	 * 
	* @Description:  查找系统中所有的营销机构
	* @param      
	* @return 
	* @throws
	 */
	Result<List<MarketingItem>> marktingPage(Pager pager,MarketingSearch marketingSearch);
	
	
	//Result<MarketingItem> 
	
	/**
	 * 
	* @Description:  取客户编号(此方法只负责计算客户编号，取客户编号的判断需在业务逻辑中自行判断)
	* @param      agencyId   投资人或者经纪人所在的营销机构的userId
	* @return 	客户编号
	* @throws
	 */
	Result<String> getCustomerNum(Long agencyId);
	
	
	/**
	 * 
	* @Description: 根据用户名查找用户
	* @param      
	* @return 
	* @throws
	 */
	Result<User>getUserByUserName(String userName);
	
	
	/**
	 * 
	* @Description:  检查用户是否已经建立关系
	* @param      
	* @return 
	* @throws
	 */
	Result<Boolean> checkSetUpRelationship(String userName);
	
	/**
	 * 
	* @Description:  营销机构添加经纪人
	* @param      
	* @return 
	* @throws
	 */
	Result<Boolean> brokerAddSave(BrokerAdd brokerAdd);
	
	/**
	 * 
	* @Description:  分页带参数查询营销机构下的经纪人
	* @param      
	* @return 
	* @throws
	 */
	Result<List<MarketingItem>> userPageForBroker(
			MarketingSearch marketingSearch, Pager pager);
	
	
	/**
	 * 
	* @Description: 分页带参数查询经纪人下的投资人 
	* @param      
	* @return 
	* @throws
	 */
	Result<List<MarketingItem>> userPageForInvestor(
			MarketingSearch marketingSearch, Pager pager);
	
	
	/**
	 * 获取用户及其账户的详细信息
	 * @param userId	用户ID
	 * @return
	 */
	AccountDetail getAccountDetail(Long userId);
	
	
	/**
	 * 
	* @Description:  根据用户id查找activate
	* @param      
	* @return 
	* @throws
	 */
	String getUserActivatedCode(Long id);
	/**
	 * 
	* @Description:  
	* @param      
	* @return 
	* @throws
	 */
	UserDetail updateUserStatus(Long id, String status);
}
