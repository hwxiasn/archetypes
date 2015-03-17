package com.qingbo.ginkgo.ygb.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.UserSercurityInfo;

@Controller
@RequestMapping("/security")
public class SecurityController {

	@Autowired private CustomerBizService customerBizService;
	@Autowired private UserBizService userBizService;
	/** 安全中心首页 */
	@RequestMapping(value="index")
	public String security(HttpServletRequest request, Model model) {
		try{
			Long userId =ShiroTool.userId();
			//Long userId = 2L;
			AccountDetail account = userBizService.getAccountDetail(userId);
			model.addAttribute("account", account);
			// 账户信息，顶部欢迎消息需要
			//获取登陆名
			Result<UserDetail> userResult = customerBizService.getUserDetailByUserId(userId);
			UserDetail detail = new UserDetail();
			if(userResult.success()) {
				detail = userResult.getObject();
			}
			model.addAttribute("userDetail", detail);
		}catch(Exception e){}
		return "security/security";
	}
	
	/** 安全中心:修改登陆密码 */
	@RequestMapping(value="modifyQueryPass", method=RequestMethod.POST)
	public String modifyQueryPass(HttpServletRequest request, Model model, UserSercurityInfo securityInfo) {

		// 账户信息，顶部欢迎消息需要
		Long userId =ShiroTool.userId();
		//Long userId = 2L;
		AccountDetail account = new AccountDetail();
		Result<UserDetail> result1 = customerBizService.getUserDetailByUserId(userId);
		if(result1.success() && result1.getObject()!=null) {
			account.setRealName(result1.getObject().getRealName());
			account.setUserNum(result1.getObject().getCustomerNum());
		}
//		AccountDetail account = userBizService.getAccountDetail(userId);
		model.addAttribute("account", account);
		//1.验证
		if(securityInfo.getPassword()== null || ("").equalsIgnoreCase(securityInfo.getPassword())||
				securityInfo.getConfirmPassword()==null || ("").equals(securityInfo.getConfirmPassword())||
				securityInfo.getNewPassword()==null || ("").equals(securityInfo.getNewPassword())) {
			model.addAttribute("result","密码为空");
		}
		if(!(securityInfo.getNewPassword()).equals(securityInfo.getConfirmPassword())) {
			model.addAttribute("result","两次输入的密码不一致");
		}
		if(userId!=null&&!("").equals(userId)) {
			Result<Boolean> result = customerBizService.modifyLoginPassword(userId, securityInfo.getPassword(), securityInfo.getNewPassword());
			if(result.success() && result.getObject()==true) {
				model.addAttribute("result", "loginsuccess");
			}else {
				model.addAttribute("result", "loginfail");
			}
		}else {
			model.addAttribute("result", "loginfail");
		}
		
		return "security/security";
	}
	
	/** 安全中心:修改支付密码 */
	@RequestMapping(value="modifyPayPass", method=RequestMethod.POST)
	public String modifyPayPass(HttpServletRequest request, Model model, UserSercurityInfo securityInfo) {

		// 账户信息，顶部欢迎消息需要
		Long userId =ShiroTool.userId();
		//Long userId = 2L;
		
		AccountDetail account = new AccountDetail();
		Result<UserDetail> result1 = customerBizService.getUserDetailByUserId(userId);
		if(result1.success() && result1.getObject()!=null) {
			account.setRealName(result1.getObject().getRealName());
			account.setUserNum(result1.getObject().getCustomerNum());
		}
		model.addAttribute("account", account);
		//1.验证
		if(securityInfo.getNewPaymentCode()== null || ("").equalsIgnoreCase(securityInfo.getNewPaymentCode())||
				securityInfo.getConfirmPaymentCode()==null || ("").equals(securityInfo.getConfirmPaymentCode())||
				securityInfo.getPaymentCode()==null || ("").equals(securityInfo.getPaymentCode())) {
			model.addAttribute("result","密码为空");
		}
		if(securityInfo.getNewPaymentCode() != securityInfo.getConfirmPaymentCode()) {
			model.addAttribute("result","两次输入的密码不一致");
		}
		if(userId!=null && !("").equals(userId)) {
			Result<Boolean> result = customerBizService.modifyTradePassword(userId, securityInfo.getPaymentCode(), securityInfo.getNewPaymentCode());
			if(result.success() && result.getObject() == true) {
				model.addAttribute("result", "paymentsuccess");
			}
			else {
				model.addAttribute("result", "paymentfail");
			}
		}else {
			model.addAttribute("result", "paymentfail");
		}
		
		return "security/security";
	}
}
