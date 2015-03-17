package com.qingbo.ginkgo.ygb.customer.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.entity.UserEnterpriseProfile;
import com.qingbo.ginkgo.ygb.customer.entity.UserGroup;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.entity.UserStatus;




public interface CustomerService {

	/**
	 * 
	* @Description: 保存用户开户信息
	* @param   User(调用注册接口必须有的参数为：用户类型; 用户来源; 角色; 用户真实姓名; 邮箱;)
	* (用例场景：1.个人用户注册必须传递的参数：user.register=P;registerSource;Role;userProfile.realName;userProfile.email;
	* 		  2.企业用户注册必须传递的参数：user.register=E;registerSource;Role;userEnterpriseProfile.enterpriseName;userEnterpriseProfile.contactEmail)
	* @return  Boolean  true 注册成功  注册失败会返回错误信息
	* @throws
	 */
	public Result<User> register(User user);
	
	
	/**
	 * 
	* @Description: 用于上下级建立连接关系 
	* @param  必须传入的参数: userGroup.childUserId(子userId) parentUserAlias和userGroup.parentUserId可以选择性传入
	* @return Result<UserGroup>    返回类型 
	* @throws
	 */
	public Result<Boolean> setUpUserRelationship(UserGroup userGroup,String parentUserAlias);
	
	
	/**
	 * @Description:  解除上下级关系
	 * @param      childUserId    下级userId
	 * @return 	
	 * @throws
	 */
	public Result<Boolean> removeUserRelationship(Long childUserId);
	
	
	/**
	 * 
	* @Description:  根据关系表中的子元素查询父元素
	* @param      childUserId 子元素的userId
	* @return 
	* @throws
	 */
	public Result<UserGroup> getParentUserByChildId(Long childUserId);
	
	/**
	 * 
	* @Description:  根据关系表中的父元素查询子元素
	* @param      parentUserId 父元素的userId
	* @return 
	* @throws
	 */
	public Result<List<UserGroup>> getChildrenUserByParentId(Long parentUserId);
	
	/**
	 * 
	* @Description: 根据客户编号查询客户的详细信息
	* @param  userAlias：客户编号
	* @return Result<User>客户详细信息(包含user和userProfile信息) 
	* @throws
	 */
	public Result<User> getUserByCustomerNum(String customerNum);
	
	
	/**
	 * 
	* @Description:根据用户名查询用户的全部信息 
	* @param  userName 用户名
	* @return Result<User>    用户信息及详情
	* @throws
	 */
	public Result<User>getUserByUserName(String userName);
	
	/**
	 * 
	* @Description:根据用户id查询用户详情
	* @param      userId 用户id
	* @return 	  Result<User>  用户信息及详情   
	* @throws
	 */
	
	public Result<User>getUserByUserId(Long userId);
	
	/**
	 * 
	* @Description:  根据激活码查询用户信息
	* @param      activatedCode  激活码
	* @return 	  Result<User>   用户信息机详情
	* @throws
	 */
	public Result<User> getUserByActivatedCode(String activatedCode);
	
	/**
	 * 
	* @Description:   根据用户的id或者用户名查询用户的信息(返回结果不包括userProfile和userEnterpriseProfile)
	* @param      id 或者 userName
	* @return     User基本信息(用户名、注册类型、注册来源、注册时间、角色、登录密码、密码盐、激活码)
	* @throws
	 */
	public Result<User> getOnlyUserByIdOrUserName(Long id,String userName);
	
	/**
	 * 
	* @Description:  根据条件分页查询用户
	* @param      
	* @return 
	* @throws
	 */
	public Result<PageObject<User>> page(SpecParam<User> specs, Pager pager); 
	
	/**
	 * 
	* @Description: 用户激活
	* @param user(必须传递参数 userid、password封装到user中)
	* @param     参数 
	* @return Result<Boolean>    返回类型 
	* @throws
	 */
	public Result<Boolean> doActivate(User user);
	
	
	/**
	 * 
	* @Description: 用户登录
	* @param  userName
	* @param  password
	* @return Result<Boolean>    返回类型 
	* @throws
	 */
	public Result<Boolean> doLogin(String userName,String password);
	
	
	/**
	 * 
	* @Description:  更新用户状态
	* @param    userId 用户主键   userStatus 包含用户状态 是否绑定银行卡 是否实名认证 是否绑定邮箱手机 根据传入的状态做更新   
	* @return 
	* @throws
	 */
	public Result<Boolean> updateUserStatus(Long userId,UserStatus userStatus);
	
	
	
	/**
	 * 
	* @Description:   完善个人用户信息
	* @param      user
	* 1.如果是个人用户的修改,必须传递registerType=P,并且将修改的信息封装到userProfile中。最后user.setUserProfile(userProfile)中;
	* 2.如果是企业用户的修改,必须传递registerType=E,并且将修改的信息封装到enterpriseProfile中。最后setEnterpriseProfile(enterpriseProfile)中。
	* @return   true：修改成功      false 修改失败
	* @throws
	 */
	public Result<Boolean> updateUserProfile(UserProfile userProfile);
	
	/**
	 * 
	* @Description:  更新企业信息表
	* @param      
	* @return 
	* @throws
	 */
	public Result<Boolean> updateUserEnterPrise(UserEnterpriseProfile userEnterpriseProfile);
	/**
	 * 
	* @Description:   实名认证
	* @param    userId 用户id    user 实名认证信息封装到user中
	* @return 
	* @throws
	
	//public Result<Boolean> realNameAuth(Long userId,User user);
	 */

	/**
	 * 
	* @Description:  重新绑定手机及邮箱
	* @param      
	* @return 
	* @throws
	 */
	public Result<Boolean> updateUserBinding(Long usersId, String mobile,String email);
	
	
	/**
	 * 
	* @Description:  修改登陆密码
	* @param      
	* @return 
	* @throws
	 */
	public Result<Boolean> updateLoginPassword(Long userId, String oldPassword, String newPassword);
	
	/**
	 * 
	* @Description:  重置登录密码
	* @param      
	* @return 
	* @throws
	 */
	public Result<Boolean> resetLoginPassword(Long userId, String password);
	
	/**
	 * 
	* @Description:  校验用户密码
	* @param      
	* @return 
	* @throws
	 */
	public Result<Boolean> validateLoginPassword(Long userId,String password);
	
	/**
	 * 
	* @Description:  校验用户名、邮箱、手机等是否重复
	* @param  1.如果需要校验用户名，传递参数：userName;不需要校验的参数均传递null 例如：checkRepeat(example001,null,null)
	* @param  2.如果需要校验手机，传递参数：mobile；不需要校验的参数均传递null
	* @param  3.如果需要校验邮箱，传递参数：email；不需要校验的参数均传递null   
	* @return  true:表示未重复;false:表示重复
	* @throws
	 */
	public Result<Boolean> checkRepeat(String userName,String mobile,String email);
	
	/**
	 * 
	* @Description:  校验操作员密码
	* @param      id 与用户相关表中的userId,用户表的主键         auditPassword  审核密码
	* @return 	   true 审核密码正确  false 审核密码错误
	* @throws
	 */
	public Result<Boolean> validateAuditPassword(Long id,String auditPassword);
	
	/**
	 * 
	* @Description: 修改操作员密码
	* @param      id: 与用户相关表中的userId,用户表的主键  oldPassword 旧审核密码  newPassword 新审核密码
	* @return 
	* @throws
	 */
	public Result<Boolean> updateAuditPassword(Long id,String oldPassword,String newPassword);
	
	/**
	 * 
	* @Description:  重置操作员的审核密码
	* @param      id：userId
	* @return 
	* @throws
	 */
	public Result<Boolean> restAuditPassword(Long id,String password);
	
	/**
	 * 
	* @Description:  添加审核密码
	* @param      id：userprofile表的主键，userName：用户名，password审核密码
	* @return 
	* @throws
	 */
	public Result<Boolean> createAuditPassword(Long id,String userName,String password);


	/**
	 * 
	* @Description:  根据userId取得用户绑定信息
	* @param      
	* @return 
	* @throws
	 */
	public Result<UserStatus> getBindingStatus(Long userId);




	/**\
	 * 
	* @Description:  仅修改user表中的信息，目前仅用户重置activatecode
	* @param      
	* @return 
	* @throws
	 */

	public Result<User> updateOnlyUser(User user);
	
	/**
	 * 
	* @Description:  创建操作员
	* @param      
	* @return 
	* @throws
	 */
	//public Result<User> createOperator(User user);
	
	/**
	 * 根据rootId查找全部的子用户
	 * @param rootId
	 * @return
	 */
	public Result<List<UserGroup>> getChildrenUserByRootId(Long rootId) ;
	
	/**
	 * 
	* @Description:  根据userId查询此用户是否是吴掌柜的客户
	* @param      userId
	* @return     true:是吴掌柜的客户      false:不是吴掌柜的客户
	* @throws
	 */
	public Result<Boolean> isWbossUser(Long userId);
}
