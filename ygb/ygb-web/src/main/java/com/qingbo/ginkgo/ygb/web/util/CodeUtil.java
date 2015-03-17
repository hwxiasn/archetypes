package com.qingbo.ginkgo.ygb.web.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;


public class CodeUtil {

	public static String generateActivatedCode(String source) {
		String activatedCode = StringUtils.EMPTY;
		
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(source.getBytes());
			activatedCode = new String(Hex.encodeHex(messageDigest.digest()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			activatedCode = UUID.randomUUID().toString().replace("-", "");
		} 
		
		return activatedCode;
	}
	
	/**
	 * <pre>
	 * 916******@qq.com
	* 916*@qq.com
	* 916*@qq.com
	* 91**@qq.com
	* 9***@qq.com
	 */
	public static String maskEmail(String email) {
		if(email != null) {
			int at = email.indexOf('@');
			if(at > 0) {
				int from = at > 2 ? 3 : at, to = at > 3 ? at : 4;
				StringBuilder mask = new StringBuilder(email.subSequence(0, from));
				for(int i = from; i < to; i++) {
					mask.append('*');
				}
				mask.append(email.substring(at));
				return mask.toString();
			}
		}
		return email;
	}
}
