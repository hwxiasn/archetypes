package com.qingbo.ginkgo.ygb.customer.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.customer.entity.Address;

/**
 * 提供会员地址的相关服务
 * @author hdf
 *
 */
public interface UserAddressService {

	
	/**
	 * 
	* @Description:  创建用户的地址信息
	* @param      address   必须提供address.userId和地址的相关信息
	* @return 
	* @throws
	 */
	public Result<Boolean> createUserAddress(Address address);
	
	/**
	 * 
	* @Description:  修改用户地址信息
	* @param      address 必须提供address.userId和地址的相关信息
	* @return 
	* @throws
	 */
	public Result<Boolean> updateUserAddress(Address address);
	
	/**
	 * 
	* @Description:  删除用户地址信息
	* @param      id 
	* @return 
	* @throws
	 */
	public Result<Boolean> deleteUserAddress(Long id);
	
	/**
	 * 
	* @Description:  根据用户id查询用户的所有类型的地址信息
	* @param      
	* @return 
	* @throws
	 */
	public Result<List<Address>> getUserAddress(Long userId);
}
