package com.qingbo.ginkgo.ygb.web.controller.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.account.entity.Account;
import com.qingbo.ginkgo.ygb.account.entity.AccountLog;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.account.service.QddAccountService;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.ServiceRequester;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;
import com.qingbo.ginkgo.ygb.web.controller.service.ServiceController.ServiceHandler;

/** gingko账户接口 */
@Component
public class ServiceHandlerAccount implements ServiceHandler, InitializingBean {
	@Autowired ServiceController serviceController;
	@Autowired AccountService accountService;
	@Autowired QddAccountService qddAccountService;
	@Autowired TongjiService tongjiService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		serviceController.register(this);
	}

	@Override
	public String name() {
		return "account";
	}

	@Override
	public JSONObject handle(Map<String, String> data) {
		JSONObject json = null;
		String method = data.get("method");
		if(StringUtils.isNotBlank(method)) {
			switch(method) {
			case "isRegistered": json = isRegistered(data); break;//是否开户
			case "register": json = register(data); break;//开户
			case "isAuthorised": json = isAuthorised(data); break;//是否授权
			case "authorise": json = authorise(data); break;//授权
			case "balance": json = balance(data); break;//查询余额
			case "accountLog": json = accountLog(data); break;//查询账务日志
			}
		}
		return json!=null ? json : ServiceController.BadRequest;
	}
	
	/**
	 * @param data {method:"isRegistered", userId:"141229211141010002"}
	 * @return {isRegistered: "true|false"}
	 */
	private JSONObject isRegistered(Map<String, String> data) {
		JSONObject json = null;
		Long userId = NumberUtil.parseLong(data.get("userId"), null);
		if(userId!=null && userId>0) {
			Result<SubAccount> subAccount = accountService.getSubAccount(userId);
			if(subAccount.hasObject()) {
				Long subAccountId = subAccount.getObject().getId();
				Result<Boolean> isRegistered = qddAccountService.isRegistered(subAccountId);
				if(isRegistered.hasObject()) {
					json = ServiceResponse.newSuccess();
					json.put("isRegistered", isRegistered.getObject());
				}else {
					json = ServiceResponse.newError(isRegistered);
				}
			}else{
				json = ServiceResponse.newError(subAccount);
			}
		}
		return json;
	}
	
	/**
	 * @param data {method:"register", userId:"141229211141010002"}
	 * @return {redirect: "register-redirect-url"}
	 */
	private JSONObject register(Map<String, String> data) {
		JSONObject json = null;
		Long userId = NumberUtil.parseLong(data.get("userId"), null);
		if(userId!=null && userId>0) {
			Result<SubAccount> subAccount = accountService.getSubAccount(userId);
			if(subAccount.hasObject()) {
				Long subAccountId = subAccount.getObject().getId();
				Result<String> register = qddAccountService.register(subAccountId);
				if(register.hasObject()) {
					json = ServiceResponse.newSuccess();
					json.put("redirect", register.getObject());
				}else {
					json = ServiceResponse.newError(register);
				}
			}else{
				json = ServiceResponse.newError(subAccount);
			}
		}
		return json;
	}
	
	/**
	 * @param data {method:"isAuthorised", userId:"141229211141010002"}
	 * @return {isAuthorised: "true|false"}
	 */
	private JSONObject isAuthorised(Map<String, String> data) {
		JSONObject json = null;
		Long userId = NumberUtil.parseLong(data.get("userId"), null);
		if(userId!=null && userId>0) {
			Result<SubAccount> subAccount = accountService.getSubAccount(userId);
			if(subAccount.hasObject()) {
				Long subAccountId = subAccount.getObject().getId();
				Result<Boolean> isRegistered = qddAccountService.isAuthorised(subAccountId);
				if(isRegistered.hasObject()) {
					json = ServiceResponse.newSuccess();
					json.put("isAuthorised", isRegistered.getObject());
				}else {
					json = ServiceResponse.newError(isRegistered);
				}
			}else{
				json = ServiceResponse.newError(subAccount);
			}
		}
		return json;
	}
	
	/**
	 * @param data {method:"authorise", userId:"141229211141010002"}
	 * @return {redirect: "authorise-redirect-url"}
	 */
	private JSONObject authorise(Map<String, String> data) {
		JSONObject json = null;
		Long userId = NumberUtil.parseLong(data.get("userId"), null);
		if(userId!=null && userId>0) {
			Result<SubAccount> subAccount = accountService.getSubAccount(userId);
			if(subAccount.hasObject()) {
				Long subAccountId = subAccount.getObject().getId();
				Result<String> authorise = qddAccountService.authorise(subAccountId);
				if(authorise.hasObject()) {
					json = ServiceResponse.newSuccess();
					json.put("redirect", authorise.getObject());
				}else {
					json = ServiceResponse.newError(authorise);
				}
			}else{
				json = ServiceResponse.newError(subAccount);
			}
		}
		return json;
	}
	
	/**
	 * @param data {method:"balance", userId:"141229211141010002"}
	 * @return {balance: "11911.50", freezeBalance: "0.00"}
	 */
	private JSONObject balance(Map<String, String> data) {
		JSONObject json = null;
		Long userId = NumberUtil.parseLong(data.get("userId"), null);
		if(userId!=null && userId>0) {
			Result<Account> account = accountService.getAccount(userId);
			if(account.hasObject()) {
				json = ServiceResponse.newSuccess();
				json.put("balance", account.getObject().getBalance());
				json.put("freezeBalance", account.getObject().getFreezeBalance());
			}else {
				json = ServiceResponse.newSuccess();
				json.put("error", account.getMessage());
			}
		}
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	private JSONObject accountLog(Map<String, String> data) {
		JSONObject json = null;
		Long accountLogId = NumberUtil.parseLong(data.get("id"), null);
		if(accountLogId!=null && accountLogId>0) {
			Result<AccountLog> accountLogResult = accountService.accountLog(accountLogId);
			if(accountLogResult.hasObject()) {
				AccountLog accountLog = accountLogResult.getObject();
				Map<String, String> accountLogMap = ServiceRequester.convert(accountLog);
				json = ServiceResponse.newSuccess();
				json.putAll(accountLogMap);
			}
		}else {
			String createAt = data.get("createAt");
			if(StringUtils.isNotBlank(createAt)) {
				SqlBuilder sqlBuilder = new SqlBuilder()
				.select("id")
				.from("account_log")
				.in("type", "IN", "OUT")
				.between("create_at", createAt+" 00:00:00", createAt+" 23:59:59");
				Result<List> listResult = tongjiService.list(sqlBuilder);
				if(listResult.hasObject()) {
					List list = listResult.getObject();
					if(list.size()>0) {
						String ids = StringUtils.join(list, ",");
						json = ServiceResponse.newSuccess();
						json.put("ids", ids);
					}
				}
			}
		}
		return json;
	}
}
