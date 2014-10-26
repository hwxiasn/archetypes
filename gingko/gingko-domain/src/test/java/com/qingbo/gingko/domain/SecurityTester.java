/*package com.qingbo.gingko.domain;

import static org.junit.Assert.assertTrue;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { 
		"classpath:shiro.xml",
		})
public class SecurityTester extends BaseTester {
	@Test
	public void login() {
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken("admin", "123456");
        try {
            subject.login(token);
            assertTrue(subject.isAuthenticated());
            assertTrue(subject.hasRole("A"));
            subject.checkPermission("*:*");
            subject.checkPermission("user:*");
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
        }
	}
}
*/