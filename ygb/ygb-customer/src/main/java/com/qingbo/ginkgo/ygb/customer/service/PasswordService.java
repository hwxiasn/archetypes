package com.qingbo.ginkgo.ygb.customer.service;

public interface PasswordService {
	/**
	 * 生成密码盐，32位长度
	 */
	String generateSalt();
	/**
	 * 根据用户名和密码盐，加密原密码，结果存入数据库密码字段
	 */
	String encryptPassword(String rawPassword, String userName, String salt);
}
