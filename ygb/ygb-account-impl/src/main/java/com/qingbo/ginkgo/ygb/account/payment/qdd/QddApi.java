package com.qingbo.ginkgo.ygb.account.payment.qdd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.account.enums.SubAccountType;
import com.qingbo.ginkgo.ygb.account.payment.HttpClientUtil;
import com.qingbo.ginkgo.ygb.account.payment.PaymentUtil;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.DateUtil.FormatType;

public class QddApi {
	private static final Map<String, QddApi> qddApis = new HashMap<String, QddApi>();
	private static final Map<String, String> subAccountTypes = new HashMap<>();
	static {
		for(SubAccountType type: SubAccountType.values()) {
			if(type.getCode().startsWith("QDD_")) {
				QddApi qddApi = get(type.getCode());
				QddConfig qddConfig = qddApi.getQddConfig();
				subAccountTypes.put(qddConfig.PlatformMoneymoremore(), type.getCode());
			}
		}
	}
	/**
	 * 获取乾多多子平台的QddApi接口
	 * @param type 参考SubAccountType，必须以QDD_开头
	 */
	public static QddApi get(String type) {
		QddApi qddApi = qddApis.get(type);
		if(qddApi!=null) return qddApi;
		
		synchronized (qddApis) {
			qddApi = qddApis.get(type);
			if(qddApi==null) {
				QddConfig qddConfig = QddConfig.get(type);
				qddApi = new QddApi(qddConfig);
				qddApis.put(type, qddApi);
			}
		}
		return qddApi;
	}
	
	/**
	 * 根据platformId获取QddApi
	 * @param platformId p106 p120 etc.
	 * @return QddApi or null
	 */
	public static QddApi getByPlatformId(String platformId) {
		if(StringUtils.isBlank(platformId)) return null;
		for(String type : qddApis.keySet()) {
			QddApi qddApi = qddApis.get(type);
			if(qddApi.getQddConfig().PlatformMoneymoremore().equals(platformId))
				return qddApi;
		}
		return null;
	}
	
	public static String subAccountTypeOf(String platformId) {
		return subAccountTypes.get(platformId);
	}
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private QddConfig qddConfig;
	private QddApi(QddConfig qddConfig) {
		this.qddConfig = qddConfig;
	}
	
	public QddConfig getQddConfig() {
		return qddConfig;
	}

	/**
	 * 跳转请求
	 */
	public String redirect(String uri, Map<String, String> params) {
		String SignInfo = params.get("SignInfo");
		if(StringUtils.isBlank(SignInfo)) sign(params);
		encode(params, params.keySet().toArray(new String[0]));
		String redirect = HttpClientUtil.redirect(qddConfig.SubmitURLPrefix()+uri, params);
		return redirect;
	}
	
	/**
	 * 接口请求
	 */
	public QDDPaymentResponse post(String uri, Map<String, String> params) {
		String SignInfo = params.get("SignInfo");
		if(StringUtils.isBlank(SignInfo)) sign(params);//如果没有签名，则自动签名；在需要加密银行卡号时，会手动签名
		String[] post = HttpClientUtil.postData(qddConfig.SubmitURLPrefix()+uri, params);
		QDDPaymentResponse qddPaymentResponse = new QDDPaymentResponse(post[1]);
		return qddPaymentResponse;
	}
	
	/**
	 * 编码链接中的特殊字符
	 */
	public void encode(Map<String, String> params, String ... encodes) {
		for(String encode : encodes) {
			String needEncodeString = params.get(encode);
			if(StringUtils.isNotBlank(needEncodeString)) {
				String encodedString = PaymentUtil.encodeURL(needEncodeString);
				params.put(encode, encodedString);
			}
		}
	}
	
	/**
	 * 解码特殊字符
	 */
	public void decode(Map<String, String> params, String ... decodes) {
		for(String decode : decodes) {
			String needDecodeString = params.get(decode);
			if(StringUtils.isNotBlank(needDecodeString)) {
				String decodedString = PaymentUtil.decodeURL(needDecodeString);
				params.put(decode, decodedString);
			}
		}
	}
	
	/**
	 * 加密银行卡号
	 */
	public void encrypt(Map<String, String> params, String ... encrypts) {
		for(String encrypt : encrypts) {
			String needEncryptString = params.get(encrypt);
			if(StringUtils.isNotBlank(needEncryptString)) {
				String encryptString = RsaHelper.getInstance().encryptData(needEncryptString, qddConfig.publicKey());
				params.put(encrypt, encryptString);
			}
		}
	}
	
	/**
	 * 解密银行卡号
	 */
	public void decrypt(Map<String, String> params, String ... decrypts) {
		for(String decrypt : decrypts) {
			String needDecryptString = params.get(decrypt);
			if(StringUtils.isNotBlank(needDecryptString)) {
				String decryptString = RsaHelper.getInstance().decryptData(needDecryptString, qddConfig.privateKey());
				params.put(decrypt, decryptString);
			}
		}
	}
	
	/**
	 * 签名接口请求
	 */
	public void sign(Map<String, String> params) {
		if (qddConfig.antistate() == 1)
		{
			String RandomTimeStamp = RandomStringUtils.randomNumeric(2) + DateUtil.format(new Date(), FormatType.MICROSECONDS);
			params.put("RandomTimeStamp", RandomTimeStamp);
		}
		
		StringBuilder dataStrBuilder = new StringBuilder();
		for(String param : params.keySet()) {
			String paramValue = params.get(param);
			dataStrBuilder.append(paramValue);
		}
		String dataStr = dataStrBuilder.toString();
		
		if (qddConfig.antistate() == 1)
		{
			dataStr = MD5.getMD5Info(dataStr);
		}
		RsaHelper rsa = RsaHelper.getInstance();
		String SignInfo = rsa.signData(dataStr, qddConfig.privateKey());
		
		params.put("SignInfo", SignInfo);
	}
	
	/**
	 * 校验回调请求
	 */
	public boolean checkNotify(Map<String, String> params, String... orderedNames) {
		String signature = params.get("SignInfo");
		if(StringUtils.isBlank(signature)) {
			logger.info("checkNotify: false, empty signature");
			return false;
		}
		
		StringBuilder dataStrBuilder = new StringBuilder();
		for(String name : orderedNames) {
			String value = params.get(name);
			if(value!=null) dataStrBuilder.append(value);
		}
		String dataStr = dataStrBuilder.toString();
		if(qddConfig.antistate() == 1) {
			dataStr = MD5.getMD5Info(dataStr);
		}
		boolean verifySignature = RsaHelper.getInstance().verifySignature(signature, dataStr, qddConfig.publicKey());
		if(verifySignature == false) {
			logger.info("checkNotify: false, bad signature of request: " + JSON.toJSONString(params));
		}
		return verifySignature;
	}
}
