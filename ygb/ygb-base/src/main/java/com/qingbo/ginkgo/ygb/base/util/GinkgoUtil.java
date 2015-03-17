package com.qingbo.ginkgo.ygb.base.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class GinkgoUtil {

	private static final Logger logger = LoggerFactory.getLogger(GinkgoUtil.class);
	
	
	public static String generateActivatedCode(String source) {
		String activatedCode = StringUtils.EMPTY;
		
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(DigestALGEnum.MD5.getName());
			messageDigest.update(source.getBytes());
			activatedCode = new String(Hex.encodeHex(messageDigest.digest()));
			logger.info("activatedCode : " + activatedCode);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			activatedCode = UUID.randomUUID().toString().replace("-", "");
		} 
		
		return activatedCode;
	}
	
	
	public static enum DigestALGEnum {
		SHA256("SHA-256"),
		MD5("MD5");
		private String name;
		
		DigestALGEnum(String name) {
			this.name = name;
		}
		
		public static DigestALGEnum getByName(String name) {
			for (DigestALGEnum _enum : values()) {
				if (_enum.getName().equals(name)) {
					return _enum;
				}
			}
			return null;
		}
		
		public String getName() {
			return name;
		}
	}
	
}
