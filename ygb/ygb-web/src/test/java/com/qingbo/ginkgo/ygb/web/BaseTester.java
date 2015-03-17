package com.qingbo.ginkgo.ygb.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.qingbo.ginkgo.ygb.web.util.Image;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
//		"classpath:consumers.xml",
		"classpath:repository.xml",
		"classpath:service.xml",
		"classpath:cache.xml",
		"classpath:shiro.xml",
		"classpath:web.xml",
		})
public class BaseTester {
	protected Model model = new ExtendedModelMap();
	protected MockHttpServletRequest request = new MockHttpServletRequest();
	protected MockHttpServletResponse response = new MockHttpServletResponse();
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	//处理验证码
	protected void checkcode() {
		HttpSession httpSession = request.getSession();
		Image.creatImage(httpSession);
		MockHttpServletRequest mockRequest = (MockHttpServletRequest)request;
		mockRequest.addParameter("checkcode", httpSession.getAttribute("imgCode").toString());
	}
	
	protected void login(String userName, String password) {
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
	}
	
	protected void logout() {
		SecurityUtils.getSubject().logout();
	}
}
