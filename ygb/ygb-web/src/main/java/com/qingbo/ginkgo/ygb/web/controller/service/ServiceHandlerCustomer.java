package com.qingbo.ginkgo.ygb.web.controller.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.controller.service.ServiceController.ServiceHandler;
import com.qingbo.ginkgo.ygb.web.pojo.OpenResult;
import com.qingbo.ginkgo.ygb.web.pojo.UserActivate;
import com.qingbo.ginkgo.ygb.web.pojo.UserRegister;

public class ServiceHandlerCustomer implements ServiceHandler, InitializingBean{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired ServiceController serviceController;
	@Autowired private CustomerBizService customerBizService;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		serviceController.register(this);
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "customer";
	}

	@Override
	public JSONObject handle(Map<String, String> data) {
		// TODO Auto-generated method stub
		JSONObject json = null;
		String method = data.get("method");
		if(StringUtils.isNotBlank(method)) {
			switch(method) {
			case "register": json = register(data); break;//开户
			}
		}
		return json!=null ? json : ServiceController.BadRequest;
	}

	/**
	 * 
	* @Description:  注册{userName}/{password}/{realName}/{email}/{idNum}/{mobile}/{refererNum}其中：1.refererNum是吴掌柜在倍赢平台有一个统一的经纪人编号2.userName为吴掌柜中接入的平台+uid
	* @param      data {method:"register", userId:"141229211141010002"}
	* @return {isAuthorised: "true|false"}
	* @throws
	 */
	private JSONObject register(Map<String, String> data) {
		// TODO Auto-generated method stub
		logger.info(SimpleLogFormater.formatParams(data));
		JSONObject json = new JSONObject();
		//获取客户端传递过来的参数：
		String userName = data.get("userName");
		String password = data.get("password");
		String realName = data.get("realName");
		String email = data.get("email");
		String idNum = data.get("idNum");
		String mobile = data.get("mobile");
		String payPassword = data.get("payPassword");
		String refererNum = data.get("refererNum");
		// 验证参数
		if(userName == null || password == null |realName == null || email == null || idNum==null || mobile==null || refererNum==null){
			json.put("status", 300);
			json.put("result", "fail");
			//json = ServiceResponse.newErrorMessage("Invalid parameter.");
		}else {
			UserRegister userRegister = new UserRegister();
			userRegister.setUserName(userName);
			userRegister.setRealName(realName);
			userRegister.setEmail(email);
			userRegister.setIdNum(idNum);
			userRegister.setMobile(mobile);
			userRegister.setRefererNumber(refererNum);
			userRegister.setRole(CustomerConstants.Role.INVESTOR.getCode());
			userRegister.setRegisterSource(CustomerConstants.RegisterSource.QINGBO.getCode());
			userRegister.setRegisterType(CustomerConstants.UserRegisterType.PERSONAL.getCode());
			// 保存注册
			Result<User> isSuccess = customerBizService.investorRegister(userRegister);
			if (isSuccess.success()) {
				//自动进行激活
				UserActivate userActivate = new UserActivate();
				userActivate.setId(isSuccess.getObject().getId());
				userActivate.setPassword(payPassword);
				userActivate.setPayPassword(payPassword);
				userActivate.setUsersnid(isSuccess.getObject().getUserProfile().getIdNum());
				userActivate.setMobile(isSuccess.getObject().getUserProfile().getMobile());
				Result<UserRegister> register = customerBizService.doActivate(userActivate);
				if(register.success()) {
					json.put("status", 200);
					json.put("result", "ok");
				}else {
					json.put("status", 300);//activate fail
					json.put("result", "fail");
				}
				
			}else {
				json.put("status", 300);
				json.put("result", "fail");
				//json = ServiceResponse.newErrorMessage("register fail");
			}
		}
		return json;
	}

	
	
	
	
	
	
}
