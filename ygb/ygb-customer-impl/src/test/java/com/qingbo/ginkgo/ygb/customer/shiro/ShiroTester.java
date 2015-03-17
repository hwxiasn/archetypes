package com.qingbo.ginkgo.ygb.customer.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:consumer.xml" ,"classpath:customer-repository.xml" ,"classpath:customer-service.xml" ,"classpath:cache.xml" ,"classpath:shiro.xml" })
public class ShiroTester {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void shiro() {
		String userName = "admin", password = "123456";
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(new UsernamePasswordToken(userName, password));
        } catch (UnknownAccountException e) {
        	logger.info("not exist userName: "+userName);
        } catch(DisabledAccountException e) {
        	logger.info("disabled userName: "+userName);
        } catch(IncorrectCredentialsException e) {
        	logger.info("incorrect password for userName: "+userName);
        } catch(ExcessiveAttemptsException e) {
        	logger.info("excessive attempts for userName: "+userName+", times="+e.getMessage());
        }
		
		if (subject.isAuthenticated()) {
			logger.info("successful login of userName:" + userName + ", front role: "+ShiroTool.isFrontAuthenticated());
		}else {
			logger.info("fail to login of userName: "+userName);
		}
		
		System.out.println(subject.hasRole("A"));
		System.out.println(subject.isPermitted("product:*"));
		System.out.println(subject.isPermitted("user:create"));
		System.out.println(subject.isPermitted("user:*"));
	}
}
