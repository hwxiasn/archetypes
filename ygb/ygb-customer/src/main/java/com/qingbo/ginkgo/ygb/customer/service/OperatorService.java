package com.qingbo.ginkgo.ygb.customer.service;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.customer.entity.Operator;
import com.qingbo.ginkgo.ygb.customer.entity.User;

public interface OperatorService {

	/**
	 * 
	* @Description:  操作员注册
	* @param     user实体中必须传递的参数： userName password status(A)  role(O) regSource 
	* @return user 返回新增的操作员实体
	* @throws
	 */
	public Result<User> operatorRegister(User user);
	/**
	 * 
	* @Description:  创建操作员 (主要是保存操作员的其他信息：操作员的级别     审核密码   所属的企业 及企业名称  备注等)
	* @param      
	* @return 
	* @throws
	 */
	public Result<Operator> createOperator(Operator operator);
	
	/**
	 * 
	* @Description:  根据操作员所在的机构id带条件分页查询该企业下的所有操作员的
	* @param      organizationId  操作员所属机构的userid  specs  分页条件及参数
	* @return 
	* @throws
	 */
	public Result<PageObject<Operator>> listOperatorByOrganizationId(
			Long organizationId, SpecParam<Operator> specs, Pager pager);
	
	
	/**
	 * 
	* @Description:  根据用户名或者id查询操作员的详细信息
	* @param      id 操作员表主键    userName 操作员的用户名    两个参数选择传递
	* @return     SpecialUser   返回操作员实体
	* @throws
	 */
	public Result<Operator> listOperator(Long id,String userName);
	
	
	/**
	 * 
	* @Description:  校验操作员的审核密码
	* @param      id 主键      loginPassword 校验密码
	* @return 
	* @throws
	 */
	public Result<Boolean> validateAuditPassword(Long id,String auditPassword);
	
	/**
	 * 
	* @Description:  重置操作员审核密码 
	* @param      id  主键        auditPassword  新的操作员审核密码
	* @return true 操作成功  false  操作失败
	* @throws
	 */
	public Result<Boolean> reseatAuditPassword(Long id,String auditPassword);
	
	/**
	 * 
	* @Description:  根据用户的userid查询用户的详细信息
	* @param      
	* @return 
	* @throws
	 */
	public Result<Operator> listOperatorByUserId(Long userId);

	
}
