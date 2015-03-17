package com.qingbo.ginkgo.ygb.web.biz;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.Broker;
import com.qingbo.ginkgo.ygb.web.pojo.Investor;
import com.qingbo.ginkgo.ygb.web.pojo.Operator;
import com.qingbo.ginkgo.ygb.web.pojo.UserActivate;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.UserRegister;
import com.qingbo.ginkgo.ygb.web.pojo.UserSearch;

public interface CustomerBizService {

	/**
	 * 
	* @Description: 投资人注册(包括1.注册，2.与经纪人或者平台建立联系3.分配客户编号) 
	* @param      userRegister 注册信息(1.如果是前台投资人自己注册用户，用investorRegister(userRegister,null),如果有介绍人，传入userRegister.refererNumber
	* @param	  必须传递的参数：userName,realName,email,mobile,registerSource,role
	* @return 
	* @throws
	 */
	Result<User> investorRegister(UserRegister userRegister);
	
	/**
	 * 
	* @Description: 经纪人给投资人开户 (包括1.注册，2.与经纪人建立联系3.分配客户编号)
	* @param      broker 经纪人给投资人开户需要的信息   investor.id 表示经纪人的userId
	* @param      必须传递的参数：经纪人的userId,userName,realName,email,mobile,registerSource
	* @return 
	* @throws
	 */
	Result<User> createInvestorByBroker(Investor investor);
	
	/**
	 * 
	* @Description:  营销机构给经纪人开户(包括1.注册，2.与经纪人建立联系3.分配客户编号)
	* @param      必须传递的参数：营销机构的userId,userName,realName,email
	* @return 
	* @throws
	 */
	Result<User> createBrokerByMarketing(Broker broker);
	
	/**
	 * 
	* @Description:  担保机构给操作员开户(包括1.注册2.与担保机构建立关系3.设置登录密码和审核密码操作)
	* @param      
	* @return 
	* @throws
	 */
	Result<User> createOperator(Operator operator);
	
	/**
	 * 验证用户激活码
	 * @param activatedCode 激活码
	 * @return
	 */
	Result<UserRegister> checkUserActivatedCode(String activatedCode);
	
	/**
	 * 
	* @Description:  用户激活
	* @param      
	* @return 
	* @throws
	 */
	Result<UserRegister> doActivate(UserActivate userActivate);
	
	/**
	 * 开设平台账户
	 * @param userId	用户ID
	 * @param password	交易密码
	 * @return
	 */
	Result<Boolean> openAccount(Long userId, String password);
	
	
	/**
	 * 
	* @Description:  分页查询经纪人管理下的投资人
	* @param      brokerUserId   经纪人userId
	* @return 
	* @throws
	 */
	Result<List<Investor>> investorPage(Pager pager,Investor investor,Long brokerUserId);
	
	/**
	 * 
	* @Description:  分页查询营销机构管理下的投资人
	* @param      marketingId 营销机构userID
	* @return 
	* @throws
	 */
	Result<List<Broker>> brokerPage(Pager pager,Broker broker,Long marketingId);
	
	/**
	 * 
	* @Description:  分页查询担保机构下的操作员
	* @param      guaranteeId 担保机构userId
	* @return 
	* @throws
	 */
	Result<List<Operator>> operatorPage(Pager pager,Operator operator,Long guaranteeId);
	
	/**
	 * 
	* @Description:  根据userId查询用户的信息及详情
	* @param      
	* @return 
	* @throws
	 */
	Result<UserDetail> getUserDetailByUserId(Long userId);
	
	/**
	 * 
	* @Description:  根据用户名查询用户的信息机详情
	* @param      
	* @return 
	* @throws
	 */
	Result<UserDetail> getUserDetailByUserName(String userName);
	
	
	/**
	 * 
	* @Description: 修改登录密码 
	* @param      
	* @return 
	* @throws
	 */
	Result<Boolean> modifyLoginPassword(Long userId,String oldPassword,String newPassword);
	
	/**
	 * 
	* @Description:  修改交易密码
	* @param      
	* @return 
	* @throws
	 */
	Result<Boolean> modifyTradePassword(Long userId,String oldPassword,String newPassword);
	
	/**
	 * 
	* @Description:  重置操作员审核密码
	* @param      
	* @return 
	* @throws
	 */
	Result<Boolean> resetAuditPassword(Long userId,String password);
	
	/**
	 * 
	* @Description:  重置登录密码
	* @param      
	* @return 
	* @throws
	 */
	
	Result<Boolean> resetLoginPassword(Long userId,String password);
	
	/**
	 * 
	* @Description:  重置交易密码
	* @param      
	* @return 
	* @throws
	 */
	Result<Boolean> resetTradePassword(Long userId,String password);
	
	/**
	 * 
	* @Description:  用户添加银行卡
	* @param      
	* @return 
	* @throws
	 */
	Result<UserBankCard> createBankCard(UserBankCard userBankCard);
	
	/**
	 * 
	* @Description:  用户修改银行卡
	* @param      
	* @return 
	* @throws
	 */
	Result<UserBankCard> modifyBankCard(UserBankCard userBankCard,Long userId);
	
	/**
	 * 
	* @Description:  根据userId查询用户的银行卡信息
	* @param      
	* @return 
	* @throws
	 */
	Result<UserBankCard> getUserBankCard(Long userId);
	
	/**
	 * 验证用户名是否存在
	 * @param 用户名
	 * @return
	 */
	Result<Boolean> checkUserName(String username);
	
	/**
	 * 验证电子邮箱是否存在
	 * @param 电子邮箱
	 * @return
	 */
	Result<Boolean> checkUserEmail(String email);
	
	/**
	 * 验证手机号码是否存在
	 * @param 手机号码
	 * @return
	 */
	Result<Boolean> checkUserMobile(String mobile);
	
	/**
	 * 验证会员编号是否存在
	 * @param 会员编号
	 * @return
	 */
	Result<Boolean> checkUserCustomerNum(String serial);
	
	/**
	 * 
	* @Description:  根据用户id查询用户绑定信息
	* @param      
	* @return 
	* @throws
	 */
	Result<AccountDetail> getBindingStatusByUserId(Long userId);

	/**
	 * 
	* @Description:  根据用户的userId重置用户的activateCode
	* @param      
	* @return 
	* @throws
	 */
	String restActivateCode(Long userId);
	
	
	
	
	
}
