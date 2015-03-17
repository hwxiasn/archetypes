package com.qingbo.ginkgo.ygb.customer.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;

/**
 * 提供处理用户银行卡相关信息的接口
 * @author hdf
 *
 */
public interface UserBankCardService {

	/**
	 * 
	* @Description:  创建用户银行卡
	* @param      userBankCard：必须提供userId及银行卡相关信息
	* @return 
	* @throws
	 */
	public Result<UserBankCard> createBankCard(UserBankCard userBankCard);
	
	/**
	 * 
	* @Description:  更新用户银行卡信息
	* @param      userBankCard:必须提供userId及银行卡相关信息
	* @return 
	* @throws
	 */
	public Result<UserBankCard> updateBankCard(UserBankCard userBankCard);
	
	/**
	 * 
	* @Description:  删除用户银行卡信息
	* @param      id                 用户唯一标识
	* @return 
	* @throws
	 */
	public Result<Boolean> deleteBankCard(Long id);
	
	/**
	 * 
	* @Description:  根据用户id查询用户所有银行卡信息
	* @param      userId                 用户唯一标识
	* @return 	  List<UserBankCard>     用户所有银行卡信息
	* @throws
	 */
	public Result<List<UserBankCard>> getBankCardByUserId(Long userId);
	
	/**
	 * 
	* @Description:  查询某一张银行卡信息
	* @param      id         唯一主键           
	* @return UserBankCard   某一张银行卡信息
	* @throws
	 */
	public Result<UserBankCard> getBankCardById(Long id);
	
	/**
	 * 
	* @Description:  绑定银行卡
	* @param     userId 用户id
	* @param	 userBankCard 银行卡相关信息封装到实体中
	* @return 
	* @throws
	 */
	public Result<Boolean>bindBankCard(Long userId,UserBankCard userBankCard);
	
}
