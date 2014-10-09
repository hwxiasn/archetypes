package com.qingbo.project.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.project.domain.shiro.common.RoleUtil;
import com.qingbo.project.entity.User;
import com.qingbo.project.service.UserService;

@Controller
public class IndexController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired private UserService userService;

	@RequestMapping("index")
	public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "index";
	}

	@ResponseBody
	@RequestMapping(value="string")
	public Object string(String msg) {
		logger.info(msg);
		return msg;
	}
	
	@ResponseBody
	@RequestMapping(value="json")
	public JSONObject json(String msg) {
		logger.info(msg);
		JSONObject json = new JSONObject();
		json.put("msg", msg);
		return json;
	}
	
	@RequestMapping(value="login", method=RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	public String login(String userName, String password) {
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(new UsernamePasswordToken(userName, password));
        } catch (IncorrectCredentialsException | UnknownAccountException e) {
            logger.warn("fail to login with userName:"+userName+",password:"+password, e);
            return "login";
        }
		
		if (subject.isAuthenticated()) {
			if(RoleUtil.isFrontRole() == false) {
				logger.warn("用户"+userName+"不是前台用户角色");
				SecurityUtils.getSubject().logout();
				return "login";
			}
			
			User user = userService.getUser(userName);
			logger.info("user login success: " + user.getUserName());
		}else {
			logger.info("user login failed: "+userName);
			return "login";
		}
		
		return "index";
	}
}
