package com.qingbo.ginkgo.ygb.account.service;

import java.math.BigDecimal;
import java.util.Map;

import com.qingbo.ginkgo.ygb.common.result.Result;

/**
 * 乾多多账户接口
 */
public interface QddAccountService {
	/**
	 * 注册（需要用户完善信息，设置乾多多登录密码和支付密码，设置密保问题答案等）<br/>
	 * Mobile手机号+Email邮箱，手机号和邮箱不能和其他用户重复<br/>
	 * RealName+IdentificationNo，真实姓名+18位身份证号，或者企业名称+15位营业执照号<br/>
	 */
	Result<String> register(Long subAccountId);
	/**
	 * 授权（需要输入乾多多支付密码）<br/>
	 */
	Result<String> authorise(Long subAccountId);
	/**
	 * 查询乾多多账户余额
	 * @return null or [balance, freezeBalance]
	 */
	Result<BigDecimal[]> balancequery(Long subAccountId);
	/**
	 * 查询多个乾多多账户余额，每次最多支持200个账户
	 */
	Result<Map<String, Map<String, String>>> balancequerys(String platform, String moneyMoreMoreIds);
	/**
	 * 判断用户是否已经注册乾多多支付账户
	 */
	Result<Boolean> isRegistered(Long subAccountId);
	/**
	 * 判断用户是否已经授权：投标、还款、二次分配审核
	 * <br/>用户需要注册，并且授权，然后才能进行投资
	 */
	Result<Boolean> isAuthorised(Long subAccountId);
	/**
	 * 传递配置参数
	 */
	Result<String> config(Long userId, String key, String value);
}
