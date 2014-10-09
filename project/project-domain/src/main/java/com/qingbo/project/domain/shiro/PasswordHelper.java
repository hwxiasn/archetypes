package com.qingbo.project.domain.shiro;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class PasswordHelper {

    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    private String hashAlgorithmName = "md5";
    private int hashIterations = 2;

    public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public void setHashAlgorithmName(String hashAlgorithmName) {
		this.hashAlgorithmName = hashAlgorithmName;
	}

	public void setHashIterations(int hashIterations) {
		this.hashIterations = hashIterations;
	}

	public String generateSalt() {
    	return randomNumberGenerator.nextBytes().toHex();
    }
    
    public String encryptPassword(String rawPassword, String userName, String salt) {
    	String newPassword = new SimpleHash(
                hashAlgorithmName,
                rawPassword,
                ByteSource.Util.bytes(userName + salt),
                hashIterations).toHex();
    	return newPassword;
    }
}