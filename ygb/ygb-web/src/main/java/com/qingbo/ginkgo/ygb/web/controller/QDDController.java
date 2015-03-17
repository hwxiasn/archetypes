package com.qingbo.ginkgo.ygb.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.account.entity.AccountDaily;
import com.qingbo.ginkgo.ygb.account.payment.qdd.QDDPaymentUtil;
import com.qingbo.ginkgo.ygb.account.service.AccountDailyService;
import com.qingbo.ginkgo.ygb.account.service.QddAccountLogService;
import com.qingbo.ginkgo.ygb.account.service.QddAccountService;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.common.util.MailUtil;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;

@RestController
@RequestMapping("callback/qdd")
public class QDDController {
	private static final String success = "SUCCESS";
	private static final Logger logger = LoggerFactory.getLogger(QDDController.class);
	
	@Autowired QddAccountLogService qddAccountLogService;
	@Autowired QddAccountService qddAccountService;
	@Autowired TongjiService tongjiService;
	@Autowired AccountDailyService accountDailyService;
	
	@RequestMapping("loanRegister")
	public Object loanRegister(HttpServletRequest request) {
		Map<String, String> stringMap = RequestUtil.stringMap(request);
		String callback = JSON.toJSONString(stringMap);
		logger.info(request.getMethod()+request.getRequestURI()+callback);
		
		Result<Boolean> checkNotify = qddAccountLogService.checkNotify(stringMap, "AccountType", "AccountNumber", "Mobile", "Email", "RealName", "IdentificationNo", "LoanPlatformAccount", "MoneymoremoreId", "PlatformMoneymoremore", "AuthFee", "AuthState", "RandomTimeStamp", "Remark1", "Remark2", "Remark3", "ResultCode");
		if(!checkNotify.success()) return null;
		
		Result<Boolean> doRegister = qddAccountLogService.doRegister(stringMap);
		return doRegister.success() ? success : null;
	}
	
	@RequestMapping("loanRegisterReturn")
	public Object loanRegisterReturn(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> stringMap = RequestUtil.stringMap(request);
		String callback = JSON.toJSONString(stringMap);
		logger.info(request.getMethod()+request.getRequestURI()+callback);
		
		Result<Boolean> checkNotify = qddAccountLogService.checkNotify(stringMap, "AccountType", "AccountNumber", "Mobile", "Email", "RealName", "IdentificationNo", "LoanPlatformAccount", "MoneymoremoreId", "PlatformMoneymoremore", "AuthFee", "AuthState", "RandomTimeStamp", "Remark1", "Remark2", "Remark3", "ResultCode");
		if(checkNotify.success()) qddAccountLogService.doRegister(stringMap);
		
		int type = -1;//1-index,2-auth,3-message
		if(type < 0 && !checkNotify.success()) type = 1;
		
		String ResultCode = stringMap.get("ResultCode");
		String Message = stringMap.get("Message");
		String auth = null;
		if(type<0 && "88".equals(ResultCode) || "16".equals(ResultCode)) {
			Long subAccountId = RequestUtil.getLongParam(request, "subAccountId", null);
			if(subAccountId!=null && subAccountId>0) {
				Result<Boolean> authorised = qddAccountService.isAuthorised(subAccountId);
				if(authorised.success() && !authorised.getObject()) {
					Result<String> authorise = qddAccountService.authorise(subAccountId);
					if(authorise.success()) auth = authorise.getObject();
					if(StringUtils.isNotBlank(auth)) type = 2;
				}
			}
		}else if(type<0 && StringUtils.isNotBlank(ResultCode) && StringUtils.isNotBlank(Message)) {
			type = 3;
		}
		
		try {
			if(type == 3) {
				PrintWriter writer = response.getWriter();
				writer.write("<html>");
				writer.write("<meta charset=\"UTF-8\">");
				writer.write("<head><script>");
				writer.write("alert('注册失败："+Message+"!');");
				writer.write("location.href='"+GinkgoConfig.getProperty("front_url")+"';");
				writer.write("</script></head>");
				writer.write("<body></body>");
				writer.write("</html>");
				writer.close();
			}else if(type == 2) {
				response.sendRedirect(auth);
			}else {
				response.sendRedirect(GinkgoConfig.getProperty("front_url"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping("loanAuthorise")
	public Object loanAuthorise(HttpServletRequest request) {
		Map<String, String> stringMap = RequestUtil.stringMap(request);
		String callback = JSON.toJSONString(stringMap);
		logger.info(request.getMethod()+request.getRequestURI()+callback);
		
		Result<Boolean> checkNotify = qddAccountLogService.checkNotify(stringMap, "MoneymoremoreId", "PlatformMoneymoremore", "AuthorizeTypeOpen", "AuthorizeTypeClose", "AuthorizeType", "RandomTimeStamp", "Remark1", "Remark2", "Remark3", "ResultCode");
		if(!checkNotify.success()) return null;
		
		Result<Boolean> doAuthorise = qddAccountLogService.doAuthorise(stringMap);
		return doAuthorise.success() ? success : null;
	}
	
	@RequestMapping("loanRecharge")
	public Object loanRecharge(HttpServletRequest request) {
		Map<String, String> stringMap = RequestUtil.stringMap(request);
		String callback = JSON.toJSONString(stringMap);
		logger.info(request.getMethod()+request.getRequestURI()+callback);
		
		Result<Boolean> checkNotify = qddAccountLogService.checkNotify(stringMap, "RechargeMoneymoremore", "PlatformMoneymoremore", "LoanNo", "OrderNo", "Amount", "RechargeType", "FeeType", "CardNoList", "RandomTimeStamp", "Remark1", "Remark2", "Remark3", "ResultCode");
		if(!checkNotify.success()) return null;
		
		Result<Boolean> doRecharge = qddAccountLogService.doRecharge(stringMap);
		return doRecharge.success() ? success : null;
	}
	
	@RequestMapping("loanTransfer")
	public Object loanTransfer(HttpServletRequest request) {
		Map<String, String> stringMap = RequestUtil.stringMap(request);
		String callback = JSON.toJSONString(stringMap);
		logger.info(request.getMethod()+request.getRequestURI()+callback);
		
		stringMap.put("LoanJsonList", QDDPaymentUtil.decodeURL(stringMap.get("LoanJsonList")));
		Result<Boolean> checkNotify = qddAccountLogService.checkNotify(stringMap, "LoanJsonList", "PlatformMoneymoremore", "Action", "RandomTimeStamp", "Remark1", "Remark2", "Remark3", "ResultCode");
		if(!checkNotify.success()) return null;
		
		Result<Boolean> doTransfer = qddAccountLogService.doTransfer(stringMap);
		return doTransfer.success() ? success : null;
	}
	
	@RequestMapping("loanAudit")
	public Object loanAudit(HttpServletRequest request) {
		Map<String, String> stringMap = RequestUtil.stringMap(request);
		String callback = JSON.toJSONString(stringMap);
		logger.info(request.getMethod()+request.getRequestURI()+callback);
		
		Result<Boolean> checkNotify = qddAccountLogService.checkNotify(stringMap, "LoanNoList", "LoanNoListFail", "PlatformMoneymoremore", "AuditType", "RandomTimeStamp", "Remark1", "Remark2", "Remark3", "ResultCode");
		if(!checkNotify.success()) return null;
		
		Result<Boolean> doAudit = qddAccountLogService.doAudit(stringMap);
		return doAudit.success() ? success : null;
	}
	
	@RequestMapping("loanWithdraw")
	public Object loanWithdraw(HttpServletRequest request) {
		Map<String, String> stringMap = RequestUtil.stringMap(request);
		String callback = JSON.toJSONString(stringMap);
		logger.info(request.getMethod()+request.getRequestURI()+callback);
		
		Result<Boolean> checkNotify = qddAccountLogService.checkNotify(stringMap, "WithdrawMoneymoremore", "PlatformMoneymoremore", "LoanNo", "OrderNo", "Amount", "FeeMax", "FeeWithdraws", "FeePercent", "Fee", "FreeLimit", "FeeRate", "FeeSplitting", "RandomTimeStamp", "Remark1", "Remark2", "Remark3", "ResultCode");
		if(!checkNotify.success()) return null;
		
		Result<Boolean> doWithdraw = qddAccountLogService.doWithdraw(stringMap);
		return doWithdraw.success() ? success : null;
	}
	
	@RequestMapping("loanRelease")
	public Object loanRelease(HttpServletRequest request) {
		Map<String, String> stringMap = RequestUtil.stringMap(request);
		String callback = JSON.toJSONString(stringMap);
		logger.info(request.getMethod()+request.getRequestURI()+callback);
		
		Result<Boolean> checkNotify = qddAccountLogService.checkNotify(stringMap, "MoneymoremoreId", "PlatformMoneymoremore", "LoanNo", "OrderNo", "Amount", "ReleaseType", "RandomTimeStamp", "Remark1", "Remark2", "Remark3", "ResultCode");
		if(!checkNotify.success()) return null;
		
		Result<Boolean> doRelease = qddAccountLogService.doRelease(stringMap);
		return doRelease.success() ? success : null;
	}
	
	@RequestMapping("balanceCheck")
	public Object balanceCheck(HttpServletRequest request) {
		String PlatformMoneymoremore = request.getParameter("PlatformMoneymoremore");
		logger.info("balanceCheck platform "+PlatformMoneymoremore);
		if(StringUtils.isBlank(PlatformMoneymoremore)) return success;
		
		SqlBuilder sqlBuilder = new SqlBuilder("qdd.id,qdd.money_more_more_id,sub.balance,sub.freeze_balance,user.user_name", "sub_qdd_account qdd left join sub_account sub on qdd.id=sub.id left join user on sub.account_id=user.id");
		sqlBuilder.eq("qdd.platform_id", PlatformMoneymoremore);
		sqlBuilder.eq("authorised", "1");
		List<?> list = tongjiService.list(sqlBuilder).getObject();
		
		if(list==null || list.size()==0) return "empty list";
			
		Map<String, Map<String, String>> balances = new HashMap<>();
		
		Iterator<?> iterator = list.iterator();
		while(iterator.hasNext()) {
			StringBuilder platformIds = new StringBuilder();
			int idx = 0;
			do {
				Object[] arr = (Object[])iterator.next();
				String paymentAccountId = arr[1].toString();
				if(StringUtils.isNotBlank(paymentAccountId)) {
					if(platformIds.length()==0) {
						platformIds.append(paymentAccountId);
					}else {
						platformIds.append(","+paymentAccountId);
					}
					idx++;
				}
			}while(iterator.hasNext() && idx<200);
			
			Result<Map<String, Map<String, String>>> balancequerys = qddAccountService.balancequerys(PlatformMoneymoremore, platformIds.toString());
			if(balancequerys.success()) balances.putAll(balancequerys.getObject());
		}
		
		StringBuilder info = new StringBuilder();
		for(Object account : list) {
			Object[] arr = (Object[])account;
			Map<String, String> map = balances.get(ObjectUtils.toString(arr[1]));
			BigDecimal balance = NumberUtil.parseBigDecimal(ObjectUtils.toString(arr[2]), BigDecimal.ZERO);
			BigDecimal freezeBalance = NumberUtil.parseBigDecimal(ObjectUtils.toString(arr[3]), BigDecimal.ZERO);
			BigDecimal qddBalance = map!=null ? NumberUtil.parseBigDecimal(map.get("balance"), BigDecimal.ZERO) : BigDecimal.ZERO;
			BigDecimal qddFreezeBalance = map!=null ? NumberUtil.parseBigDecimal(map.get("freezeBalance"), BigDecimal.ZERO) : BigDecimal.ZERO;
			if(!balance.equals(qddBalance) || !freezeBalance.equals(qddFreezeBalance)) {
				info.append("id="+ObjectUtils.toString(arr[0]));
				info.append(",user="+ObjectUtils.toString(arr[4]));
				info.append(",balance="+ObjectUtils.toString(arr[2]));
				info.append(",freeze="+ObjectUtils.toString(arr[3]));
				info.append(",qdd="+ObjectUtils.toString(arr[1]));
				info.append(",qdd_balance="+(map!=null?map.get("balance"):"null"));
				info.append(",qdd_freeze="+(map!=null?map.get("freezeBalance"):"null"));
				info.append("\r\n<br/>");
			}
		}
		if(info.length()==0) {
			logger.info("balanceCheck Platform "+PlatformMoneymoremore+" success");
			return success;
		}else {
			Boolean email = RequestUtil.getBoolParam(request, "email", false);
			String message = info.toString();
			logger.info("balanceCheck platform "+PlatformMoneymoremore+" info ");
			logger.info(message);
			if(email) {
				String toEmail = RequestUtil.getStringParam(request, "to", "xhongwei@qingber.com");
				MailUtil.sendHtmlEmail(toEmail, message, GinkgoConfig.platform+" Balance Warn - "+PlatformMoneymoremore);
			}
			return message;
		}
	}
	
	@RequestMapping("orderQuery")
	public Object orderQuery(HttpServletRequest request) {
		String PlatformMoneymoremore = request.getParameter("PlatformMoneymoremore");
		String BeginTime = request.getParameter("BeginTime");
		String EndTime = RequestUtil.getStringParam(request, "EndTime", BeginTime);
		if(StringUtils.isBlank(PlatformMoneymoremore) || StringUtils.isBlank(BeginTime)) return "require PlatformMoneymoremore and BeginTime";
		if(BeginTime.length()!=10 || EndTime.length()!=10) return "bad BeginTime or EndTime";
		
		BeginTime += " 00:00:00";
		EndTime += " 23:59:59";
		
		Map<String, String> params = new LinkedHashMap<>();
		params.put("PlatformMoneymoremore", PlatformMoneymoremore);
		params.put("BeginTime", BeginTime);
		params.put("EndTime", EndTime);
		params.put("Action", "1");
		Result<Boolean> orderquery = qddAccountLogService.orderquery(params);
		
		if(orderquery.success()) {
			params.put("Action", "2");
			orderquery = qddAccountLogService.orderquery(params);
		}
		
		if(orderquery.success()) {
			params.put("Action", "");
			orderquery = qddAccountLogService.orderquery(params);
		}
		return success;
	}
	
	@RequestMapping("accountDaily")
	public Object accountDaily(HttpServletRequest request) {
		Long userId = RequestUtil.getLongParam(request, "userId", null);
		String from = RequestUtil.getStringParam(request, "from", null);
		String to = RequestUtil.getStringParam(request, "to", null);
		if(userId!=null && StringUtils.isNotBlank(from) && StringUtils.isNotBlank(to)) {
			Result<Map<String, AccountDaily>> findData = accountDailyService.findData(userId, from, to);
			return findData.toString();
		}else if(userId!=null && StringUtils.isNotBlank(from)) {
			Result<AccountDaily> findOne = accountDailyService.findOne(userId, from);
			return findOne.toString();
		}else {
			Result<Boolean> handleDaily = accountDailyService.handleDaily();
			return handleDaily.toString();
		}
	}
}
