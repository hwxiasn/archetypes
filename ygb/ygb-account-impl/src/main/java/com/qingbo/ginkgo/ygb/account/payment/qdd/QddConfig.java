package com.qingbo.ginkgo.ygb.account.payment.qdd;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.qingbo.ginkgo.ygb.account.enums.SubAccountType;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.PropertiesUtil;

public class QddConfig {
	private static final Map<String, QddConfig> qddConfigs = new HashMap<String, QddConfig>();
	private static final String payment = "payment.properties";
	private static final String paymentBy = "payment_by.properties";
	private static final String paymentGh = "payment_gh.properties";
	private static final String paymentGhTest = "payment_gh_test.properties";
	
	/**
	 * 获取乾多多子平台的QddConfig配置
	 * @param type 参考SubAccountType，必须以QDD_开头
	 */
	public static QddConfig get(String type) {
		QddConfig qddConfig = qddConfigs.get(type);
		if(qddConfig!=null) return qddConfig;
		
		synchronized (qddConfigs) {
			qddConfig = qddConfigs.get(type);
			if(qddConfig==null) {
				SubAccountType subAccountType = SubAccountType.getByCode(type);
				switch(subAccountType) {
				case QDD_BY: 
					if(GinkgoConfig.test) {
						qddConfig = new QddConfig(payment); 
					}else {
						qddConfig = new QddConfig(payment, paymentBy); 
					}
					break;
				case QDD_GH: 
					if(GinkgoConfig.test) {
						qddConfig = new QddConfig(payment, paymentGhTest); 
					}else {
						qddConfig = new QddConfig(payment, paymentGh); 
					}
					break;
				default: throw new IllegalArgumentException("you shouldn't come here, bad sub_account_type("+type+"): not qdd sub account");
				}
				qddConfigs.put(type, qddConfig);
			}
		}
		return qddConfig;
	}
	
	/**
	 * 根据platformId获取QddApi
	 * @param platformId p106 p120 etc.
	 * @return QddApi or null
	 */
	public static QddConfig getByPlatformId(String platformId) {
		if(StringUtils.isBlank(platformId)) return null;
		for(String type : qddConfigs.keySet()) {
			QddConfig qddConfig = qddConfigs.get(type);
			if(qddConfig.PlatformMoneymoremore().equals(platformId))
				return qddConfig;
		}
		return null;
	}
	
	private Properties props;
	private QddConfig(String resource, String ... extraResources) {
		if(extraResources==null || extraResources.length==0) {
			props = PropertiesUtil.get(resource, "utf-8");
		}else {
			props = PropertiesUtil.gets(resource, extraResources);
		}
	}
	
	/**
	 * 乾多多支付接口请求地址
	 */
	public String SubmitURLPrefix() {
		return props.getProperty("SubmitURLPrefix");
	}
	
	/**
	 * 乾多多支付接口跳转返回地址
	 */
	public String ReturnURLPrefix() {
		String ReturnURLPrefix = props.getProperty("ReturnURLPrefix");
		if(ReturnURLPrefix==null) ReturnURLPrefix = NotifyURLPrefix();
		return ReturnURLPrefix;
	}
	
	/**
	 * 乾多多支付接口请求回调通知地址
	 */
	public String NotifyURLPrefix() {
		return props.getProperty("NotifyURLPrefix");
	}
	
	/**
	 * 平台在乾多多的账号
	 */
	public String PlatformMoneymoremore() {
		return props.getProperty("PlatformMoneymoremore");
	}
	
	/**
	 * 是否开启防抵赖功能
	 */
	public int antistate() {
		return NumberUtil.parseInt(props.getProperty("antistate"), 0);
	}
	
	/**
	 * 是否已开启无密码审核（需要乾多多技术人员手动开启）
	 */
	public boolean noPasswordAudit() {
		return NumberUtil.parseBoolean(props.getProperty("noPasswordAudit"), false);
	}
	
	/**
	 * 私钥（加密请求数据校验）
	 */
	public String privateKey() {
		return props.getProperty("privateKey");
	}
	
	/**
	 * 公钥（解密验证返回数据）
	 */
	public String publicKey() {
		return props.getProperty("publicKey");
	}

	public String toString() {
		return props.toString();
	}
}
