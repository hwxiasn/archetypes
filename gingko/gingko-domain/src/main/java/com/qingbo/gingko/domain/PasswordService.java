package com.qingbo.gingko.domain;

public interface PasswordService {
	String generateSalt();
	String encryptPassword(String rawPassword, String userName, String salt);
}
