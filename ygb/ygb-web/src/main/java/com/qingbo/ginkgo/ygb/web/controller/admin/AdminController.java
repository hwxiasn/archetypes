package com.qingbo.ginkgo.ygb.web.controller.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.common.util.MailUtil;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.customer.shiro.RequiresAdminUser;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.util.Image;

@Controller
@RequestMapping("admin")
public class AdminController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired private UserBizService userBizService;
	@Autowired private CacheManager cacheManager;
	
	private String VM_Path = "admin/";
	
	@RequestMapping(value="login", method=RequestMethod.GET)
	public String login() {
		return VM_Path + "login";
	}
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	public String login(HttpServletRequest request, String userName, String password, Model model) {
		boolean checkImgCode = Image.checkImgCode(request);
		if(!checkImgCode) {
			model.addAttribute("error", "验证码错误！");
			return login();
		}
		
		if(StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
			model.addAttribute("error", "请输入用户名和登录密码");
			return login();
		}
		
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(new UsernamePasswordToken(userName, password));
			int tryCount = 3;
			while(StringUtils.isBlank(ShiroTool.userRole()) && tryCount-->0) {
				logger.info("not get user role, try again.");
				subject.logout();
				subject.login(new UsernamePasswordToken(userName, password));
			}
        } catch (UnknownAccountException e) {
        	logger.info("not exist userName: "+userName);
        	model.addAttribute("error", "用户不存在！");
        } catch(DisabledAccountException e) {
        	logger.info("disabled userName: "+userName);
        	model.addAttribute("error", "用户已禁用！");
        } catch(IncorrectCredentialsException e) {
        	logger.info("incorrect password for userName: "+userName);
        	model.addAttribute("error", "密码错误！");
        } catch(ExcessiveAttemptsException e) {
        	logger.info("excessive attempts for userName: "+userName+", times="+e.getMessage());
        	model.addAttribute("error", "密码连续错误"+e.getMessage()+"次，请5分钟后再试！");
        }
		
		if(subject.isAuthenticated()) {
			if(ShiroTool.isAdminAuthenticated()) {
				logger.warn("admin login succeed: "+userName+", role="+ShiroTool.userRole());
				return "redirect:home.html";
			}else {
				logger.warn("后台登录用户角色错误："+userName+", role="+ShiroTool.userRole());
				subject.logout();
				model.addAttribute("error", "角色错误！");
				return "redirect:login.html";
			}
		}
		
		return VM_Path + "login";
	}
	
	@RequestMapping("logout")
	public String logout() {
		userBizService.logout();
		return "redirect:/admin/login.html";
	}
	
	@RequiresAdminUser
	@RequestMapping("home")
	public String home() {
		return VM_Path + "home";
	}
	@RequestMapping("top")
	public String top(Model model) {
		model.addAttribute("user", ShiroTool.userName());
		return VM_Path + "layout/top";
	}
	@RequiresAdminUser
	@RequestMapping("left")
	public String left() {
		return VM_Path + "layout/left";
	}
	@RequestMapping("welcome")
	public String welcome(Model model) {
		return VM_Path + "layout/welcome";
	}
	@RequestMapping("monitor")
	public void monitor(HttpServletRequest request, HttpServletResponse response) {
		String log = RequestUtil.getStringParam(request, "log", null);
		if(log!=null) {
			String toEmail = RequestUtil.getStringParam(request, "to", "xhongwei@qingber.com");
			MailUtil.sendHtmlEmail(toEmail, log, "[Monitor Log Warn] - Some Service Might be gone.");
		}
		response.setStatus(200);
	}
	@RequiresAdminUser
	@RequestMapping("caches")
	public String caches(HttpServletRequest request, HttpServletResponse response, Model model) {
		String name = RequestUtil.getStringParam(request, "name", null);
		if(name!=null) {
			Cache cache = cacheManager.getCache(name);
			if(cache==null) model.addAttribute("message", "cache: "+name+" not exist.");
			else {
				String key = RequestUtil.getStringParam(request, "key", null);
				if(StringUtils.isNotBlank(key)) {
					ValueWrapper valueWrapper = cache.get(key);
					if(valueWrapper==null) model.addAttribute("message", "cache: "+name+", key: "+key+" not exist.");
					else {
						Object value = valueWrapper.get();
						boolean remove = RequestUtil.getBoolParam(request, "remove", false);
						if(remove) {
							cache.evict(key);
							model.addAttribute("message", "cache: "+name+", key: "+key+", removed value: "+JSON.toJSONString(value));
						}else {
							model.addAttribute("message", "cache: "+name+", key: "+key+", value: "+JSON.toJSONString(value));
						}
					}
				}else {
					boolean remove = RequestUtil.getBoolParam(request, "remove", false);
					if(remove) {
						cache.clear();
						model.addAttribute("message", "cache: "+name+" clear");
					}
				}
			}
		}
		model.addAttribute("caches", cacheManager.getCacheNames());
		return VM_Path + "caches";
	}
}
