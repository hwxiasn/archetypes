package com.qingbo.ginkgo.ygb.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.MailUtil;
import com.qingbo.ginkgo.ygb.common.util.RegexMatch;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants.Role;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.pojo.UserActivate;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.UserRegister;
import com.qingbo.ginkgo.ygb.web.util.CodeUtil;
import com.qingbo.ginkgo.ygb.web.util.Image;
import com.qingbo.ginkgo.ygb.web.util.VelocityUtil;


@Controller
public class UserController {
	
	@Autowired private CustomerBizService customerBizService;
	@Autowired private UserBizService userBizService;
	
	private String VM_Path = "register/";
	
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String register(HttpServletRequest request,Model model){
		String err = request.getParameter("errs");
		if(!StringUtils.isEmpty(err)){
			model.addAttribute("err", err);
		}
		return VM_Path+"/register";
	}
	
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String doRegister(HttpServletRequest request, HttpSession session, UserRegister userRegister, Model model) {
		try{
				String agree = request.getParameter("agree");
				boolean checkImgCode = Image.checkImgCode(request);
				boolean check = true;
				if(!checkImgCode) {
					model.addAttribute("errs", "验证码错误！");
					check = false;
				}
				if(agree==null || !agree.equals("1")) {
					model.addAttribute("errs", "服务条款没有勾选");
					check = false;
				}
				// 校验用户名
				if(userRegister.getUserName()==null || ("").equals(userRegister.getUserName().trim())) {
					model.addAttribute("errs", "用户名不能为空");
					check = false;
				}
				if(RegexMatch.isMatchUserName(userRegister.getUserName())==false) {
					model.addAttribute("errs", "用户名必须是6-20位字母、数字或者字母与数字的组合");
					check = false;
				}
				if(RegexMatch.isMacthMobile(userRegister.getMobile())==false) {
					model.addAttribute("errs", "手机号码不正确");
					check = false;
				}
				if(RegexMatch.isMatchEmail(userRegister.getEmail())==false) {
					model.addAttribute("errs", "邮箱不正确");
					check = false;
				}
				if(RegexMatch.isMatchRealName(userRegister.getRealName())==false) {
					model.addAttribute("errs", "真实姓名不正确");
					check = false;
				}
				if(check==false) {
					model.addAttribute("register", userRegister);
					return "register/register";
				}
			// 装填其它信息
			userRegister.setRole(Role.INVESTOR.getCode());											// 角色
			userRegister.setUserName(userRegister.getUserName().trim().toLowerCase());			    // 用户名
			userRegister.setRegisterSource(CustomerConstants.RegisterSource.BEIYING.getCode());		// 注册来源
			userRegister.setRegisterType(CustomerConstants.UserRegisterType.PERSONAL.getCode());	// 注册类型
			// 保存注册
			Result<User> isSuccess = customerBizService.investorRegister(userRegister);
			//给用户发送激活邮件
			if (isSuccess.success()) {
				String userActivatedCode = userBizService.getUserActivatedCode(isSuccess.getObject().getId());
				Map<String, Object> map = new HashMap<>();
				map.put("activatedCode", userActivatedCode);
				map.put("user",userRegister);
				String html = VelocityUtil.merge("register/activateEmail.vm", map);
				//System.out.println(html);
				MailUtil.sendHtmlEmail(userRegister.getEmail(),html,"财富箱激活邮件");
			}else {
				model.addAttribute("errs",isSuccess.getMessage());
				return "register/register";
			}
			model.addAttribute("userRegister", userRegister);
		}catch(Exception e){
			// Log
			e.printStackTrace();
			model.addAttribute("errs", "系统繁忙");
			return "register/register";
		}
		return "register/registerSuccess";
	}
	
	@RequestMapping(value = "activate", method = RequestMethod.GET)
	public String activate(HttpSession session, String activatedCode, Model model){
		try {
			Result<UserRegister> userRegister = customerBizService.checkUserActivatedCode(activatedCode);
			if (userRegister.success()) {
				if (userRegister.getObject().getId() != null) {
					model.addAttribute("userRegister", userRegister.getObject());
					return "register/activate";
				} else {
					return "redirect:index.html";
				}
			} else {
				return "redirect:index.html";
			}
		} catch (Exception e) {
			return "redirect:index.html";
		}
	}
	
	@RequestMapping(value = "activate", method = RequestMethod.POST)
	public String doActivate(UserActivate userActivate, Model model, HttpServletRequest request){
		System.out.println(RequestUtil.stringMap(request));
		try{
			//校验参数：登录密码 平台交易密码 身份证号码 
			boolean checkResult = true;
			if(userActivate.getPassword()==null || ("").equalsIgnoreCase(userActivate.getPassword())||
					userActivate.getConfirmPayPassword()==null || ("").equalsIgnoreCase(userActivate.getConfirmPayPassword())||
					userActivate.getPayPassword() ==null || ("").equalsIgnoreCase(userActivate.getPayPassword())||
					userActivate.getConfirmPayPassword() ==null || ("").equals(userActivate.getConfirmPayPassword())||
					userActivate.getUsersnid()==null || ("").equalsIgnoreCase(userActivate.getUsersnid())||
					userActivate.getMobile()==null || ("").equals(userActivate.getMobile())) {
				checkResult = false; 
			}
			if (!userActivate.getPassword().equals(userActivate.getConfirmPassword())) {
				checkResult = false;
			}
			if (!userActivate.getPayPassword().equals(userActivate.getConfirmPayPassword())) {
				checkResult = false;
			}
			if (checkResult==false) {
				model.addAttribute("userRegister", userActivate);
				model.addAttribute("result", "激活信息填写有误");
				return "register/activate";
			}
			//激活用户
			Result<UserRegister> register = customerBizService.doActivate(userActivate);
			//激活成功，返回成功界面
			if (register.success()) {
				model.addAttribute("userRegister", userActivate);
				return "register/activateSuccess";
			}else {
			//激活失败，返回当前页面
				model.addAttribute("userRegister", userActivate);
				model.addAttribute("result", register.getMessage());
				return "register/activate";
			}
		}catch(Exception e){
			// TODO log
			return "register/activate";
		}
	}
	
	/** 找回密码 */
	@RequestMapping("forgetPassword")
	public String findPassword(HttpServletRequest request, Model model){
		String err = request.getParameter("errs");
		if(!StringUtils.isEmpty(err) && "code".equals(err)){
			model.addAttribute("errs", "验证码错误");
		}
		return "register/forgetPassword";
	}
	
	/** 找回密码 发邮箱 */
	@RequestMapping("findPassword")
	public String sendEmail(HttpServletRequest request, Model model, HttpSession session){
		String imgCode = RequestUtil.getStringParam(request, "checkcode", "");
		boolean checkImgCode = Image.checkImgCode(session, imgCode);
		if(!checkImgCode) {
			return "redirect:forgetPassword.html?errs=code";
		}
		String username = RequestUtil.getStringParam(request, "userName", "");
		Result<UserDetail> result = customerBizService.getUserDetailByUserName(username);
		if(result.success() && result.getObject()!=null) {
			if(result.getObject().getEmail()!=null && !("").equalsIgnoreCase(result.getObject().getEmail())) {
				Result<UserDetail> resu = customerBizService.getUserDetailByUserName(username);
				if(resu.success()) {
					//重置激活码
					String activateCode = customerBizService.restActivateCode(resu.getObject().getUserId());
					if(activateCode!=null && !("").equals(activateCode)) {
						Map<String, Object> map = new HashMap<>();
						map.put("activatedCode", activateCode);
						map.put("user",result.getObject());
						String html = VelocityUtil.merge("register/resetPasswordEmail.vm", map);
						System.out.println(html);
						boolean flag = MailUtil.sendHtmlEmail(result.getObject().getEmail(),html,"财富箱找回密码邮件");
						model.addAttribute("sendEmail",flag);
						model.addAttribute("emailHost",  resu.getObject().getEmail());
						model.addAttribute("checkcode",activateCode);
					}else {
						model.addAttribute("sendEmail",false);
					}
				}
			}
		}
		return "register/findPasswordResult";
	}
	
	/**
	 * 跳转到重置密码页面
	 */
	@RequestMapping(value="resetPassword", method = RequestMethod.GET)
	public String resetPassword(Model model, String activatedCode) {
		User user1 = new User();
		if(activatedCode!=null || !("").equals(activatedCode)) {
			Result<UserRegister> result = customerBizService.checkUserActivatedCode(activatedCode);
			if(result.success()) {
				user1.setId(result.getObject().getId());
				user1.setUserName(result.getObject().getUserName());
			}
		}
		model.addAttribute("user", user1);
		model.addAttribute("activatedCode", activatedCode);
		return "register/resetPassword";
	}
	
	/**
	 * 测试   重设密码的邮件内容
	 */
	@RequestMapping(value="resetEmail", method = RequestMethod.GET)
	public String resetEmail(Model model, String activatedCode) {
		UserRegister user = new UserRegister();
		user.setRealName("齐天大圣");
		model.addAttribute("user", user);
		model.addAttribute("activatedCode", activatedCode);
		return "register/resetPasswordEmail";
	}
	
	/**
	 * 测试   激活账户的邮件内容
	 */
	@RequestMapping(value="activateEmail", method = RequestMethod.GET)
	public String activateEmail(Model model, String activatedCode) {
		UserRegister user = new UserRegister();
		user.setRealName("齐天大圣");
		model.addAttribute("user", user);
		model.addAttribute("activatedCode", activatedCode);
		return "register/activateEmail";
	}
	
	/**
	 * 提交表单重置密码
	 */
	@RequestMapping(value="resetPassword", method = RequestMethod.POST)
	public String resetPassword(Model model, Long userId, String activatedCode, String newPassword, String newPaymentPassword) {
		if(activatedCode!=null && !("").equals(activatedCode) && newPassword!=null && !("").equals(newPassword) && newPaymentPassword!=null && !("").equals(newPaymentPassword)) {
			Result<UserRegister> result = customerBizService.checkUserActivatedCode(activatedCode);
			if(result.success() && result.getObject()!=null) {
				Result<Boolean> loginResult = customerBizService.resetLoginPassword(userId, newPassword);
				Result<Boolean> payResult = customerBizService.resetTradePassword(userId, newPaymentPassword);
				if(loginResult.success() && payResult.success()) {
					model.addAttribute("resetPasswordResult", true);
				}
			}
		}else {
			model.addAttribute("resetPasswordResult", false);
		}
		return "register/resetPasswordResult";
	}
	
	@ResponseBody
	@RequestMapping("checkUsername")
	public JSONObject checkUsername(String username,HttpServletRequest request,HttpServletResponse response) {
		Result<Boolean> result = customerBizService.checkUserName(username);
		JSONObject json = new JSONObject();
		if(result.success() && result.getObject()){
			//json = new JSONObject();
			json.put("msg", "success");
		}else{
			json.put("msg", "exist");
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value="checkEmail",method=RequestMethod.POST)
	public JSONObject checkEmail(String email, HttpServletRequest request, HttpServletResponse response, Model model){
		Result<Boolean> result = customerBizService.checkUserEmail(email);
		JSONObject json = new JSONObject();
		try{
		if(result.success() && result.getObject()){
			json.put("msg", "y");
		}else{
			json.put("msg", "n");
		}
		}catch(Exception e){}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value="checkPhone",method=RequestMethod.POST)
	public JSONObject checkPhone(String mobile,HttpServletRequest request,HttpServletResponse response,Model model){
		JSONObject json = new JSONObject();
		Result<Boolean> result = customerBizService.checkUserMobile(mobile);
		try{
			if(result.success() && result.getObject()){
				json.put("msg", "y");
			}else{
				json.put("msg", "n");
			}
		}catch(Exception e){}
		return json;
	}
	
	//查看用户输入的推荐人编号是否为经纪人的编号
	@ResponseBody
	@RequestMapping("checkRefererNumber")
	public JSONObject checkRefererNumber(String refererNumber, HttpServletRequest request, HttpServletResponse response) {
	//	System.out.println("##############验证上级用户：" + refererNumber);
		JSONObject json = new JSONObject();
		Result<Boolean> result = customerBizService.checkUserCustomerNum(refererNumber);
		try {
			if(result.success() && result.getObject()==true){
				json.put("msg", "y");
			}else{
				json.put("msg", "n");
			}
			return json;
		} catch (Exception e) {
			json.put("msg", "n");
		}
		return json;
	}
	
	/**  通过用户名读取用户的邮箱*/
	@ResponseBody
	@RequestMapping(value="findUser",method=RequestMethod.POST)
	public JSONObject findUser(String username,HttpServletRequest request,HttpServletResponse response,Model model){
		JSONObject json = new JSONObject();
		try{
			Result<UserDetail> result = customerBizService.getUserDetailByUserName(username);
			if(result.success()){
				json.put("email", CodeUtil.maskEmail(result.getObject().getEmail()));
	//			json.put("moblie", user.getMobile());
			}
		}catch(Exception e){}
		return json;
	}
	
	
	
	
}
