package com.qingbo.ginkgo.ygb.web.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.account.service.QddAccountService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.shiro.RequiresFrontUser;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants.TradeStatusType;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants.TradeType;
import com.qingbo.ginkgo.ygb.web.biz.CodeListBizService;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.biz.InvestmentBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectBizService;
import com.qingbo.ginkgo.ygb.web.biz.TradeBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.CodeListItem;
import com.qingbo.ginkgo.ygb.web.pojo.StatInfo;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;

@Controller
@RequestMapping("/account")
@RequiresFrontUser
public class UserAccountController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired private CustomerBizService customerBizService;
	@Autowired private CodeListBizService codeListBizService;
	@Autowired private TradeBizService tradeBizService;
	@Autowired private AccountService accountService;
	@Autowired private QddAccountService qddAccountService;
	@Autowired private InvestmentBizService investmentBizService;
	@Autowired private ProjectBizService projectBizService;

	@RequestMapping("index")
	public String accountCenter(Model model,HttpServletRequest request) {
		accountDetail(model);
		return "myAccount/myAccount";
	}
	
	private void accountDetail(Model model) {
		if(ShiroTool.userId() == null) return;
		AccountDetail accountDetail = new AccountDetail();
		Result<UserDetail> userDetailResult = customerBizService.getUserDetailByUserId(ShiroTool.userId());
		if(userDetailResult.hasObject()) {
			UserDetail userDetail = userDetailResult.getObject();
			accountDetail.setRealName(userDetail.getRealName());
			accountDetail.setUserNum(userDetail.getCustomerNum());
			accountDetail.setLocked(userDetail.getStatus());
			accountDetail.setBank(userDetail.getBankCode());
		}
		
		// 是否绑定银行卡
		Result<UserBankCard> result = customerBizService.getUserBankCard(ShiroTool.userId());
		if(result.hasObject()){
			accountDetail.setIsBinding("1");
			accountDetail.setBank(result.getObject().getBankCode());
			accountDetail.setBankCardNum(result.getObject().getBankCardNum());
		}else{
			accountDetail.setIsBinding("0");
		}
		
		Result<SubAccount> subAccountResult = accountService.getSubAccount(ShiroTool.userId());
		boolean needRegister = true, needAuthorised = true;
		if(subAccountResult.hasObject()) {
			SubAccount subAccount = subAccountResult.getObject();
			accountDetail.setBalance(subAccount.getBalance().toPlainString());
			accountDetail.setLockBalance(subAccount.getFreezeBalance().toPlainString());
			
			Result<Boolean> registered = qddAccountService.isRegistered(subAccount.getId());
			if(registered.hasObject() && registered.getObject()) needRegister = false;
			if(needRegister == false) {//如果已开户，则检查是否需要授权
				Result<Boolean> authorised = qddAccountService.isAuthorised(subAccount.getId());
				if(authorised.hasObject() && authorised.getObject()) needAuthorised = false;
			}
		}
		Result<StatInfo> sumUser = investmentBizService.sumUser(ShiroTool.userId().toString());
		if(sumUser.hasObject()) {
			StatInfo statInfo = sumUser.getObject();
			accountDetail.setInvestCount(String.valueOf(statInfo.getCount()));
			accountDetail.setTotalInvest(statInfo.getAmount().toPlainString());
		}
		// 账户信息
		model.addAttribute("account", accountDetail);
		// 控制参数
		model.addAttribute("needRegister", needRegister ? "1" : "0");
		model.addAttribute("needAuthorised", needAuthorised ? "1" : "0");
	}
	
	@RequestMapping(value="accountIn", method=RequestMethod.GET)
	public String accountIn(HttpServletRequest request, Model model){
		accountDetail(model);
		return "myAccount/accountIn";
	}
	
	@RequestMapping(value="accountIn", method=RequestMethod.POST)
	public String doAccountIn(HttpServletRequest request, Model model){
		BigDecimal money = NumberUtil.parseBigDecimal(request.getParameter("fastInMoney"), null);
		Long userId = ShiroTool.userId();
		
		if(userId!=null && money!=null) {
			String type = RequestUtil.getStringParam(request, "type", null);
			qddAccountService.config(userId, "RechargeType", type); // set type or clear type
			
			Result<String> result = tradeBizService.deposit(String.valueOf(userId), money);
			if(result.success()){
				return "redirect:" + result.getObject();
			}
		}
		
		return "redirect:/account/none";
	}
	
	/**
	 * 还款操作
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="doRepayMoney", method=RequestMethod.POST)
	public JSONObject doRepayMoney(Long id,BigDecimal money,String password,HttpServletRequest request,HttpServletResponse response){
		JSONObject obj = new JSONObject();
		Result<Boolean> passwordCheck = accountService.validatePassword(ShiroTool.userId(), password);
		//密码不正确
		if(!passwordCheck.success()){
			logger.info("UserAccountControll DoRepayMoney failed for Password is wrong.");
			obj.put("result", "failed");
			obj.put("stateCode", "支付密码校验失败");
			return obj;
		}else{
			if(!passwordCheck.getObject()){
				logger.info("UserAccountControll DoRepayMoney failed for Password is wrong by no Object.");
				obj.put("result", "failed");
				obj.put("stateCode", "支付密码不正确");
				return obj;
			}
		}
		//查询投资账户
		Result<SubAccount> subAccountResult = accountService.getSubAccount(ShiroTool.userId());
		if(!subAccountResult.hasObject()){
			logger.info("UserAccountControll DoRepayMoney failed for SubAccount is wrong by no Object.");
			obj.put("result", "failed");
			obj.put("stateCode", "账户不存在");
			return obj;
		}
		//资金不足
		if(money.compareTo(subAccountResult.getObject().getBalance()) > 0){
			logger.info("UserAccountControll DoRepayMoney failed for Not Enough Money");
			obj.put("result", "failed");
			obj.put("stateCode", "余额不足");
			return obj;
		}
		Result<Boolean> result = projectBizService.repay(id, String.valueOf(ShiroTool.userId()), money);
		if(result.success()){
			logger.info("UserAccountControll DoRepayMoney success");
			obj.put("result", "success");
		}else{
			logger.info("UserAccountControll DoRepayMoney failed for repay");
			obj.put("result", "failed");
			obj.put("stateCode", result.getMessage());
		}
		return obj;
	}
	
	@RequestMapping(value="accountOut", method=RequestMethod.GET)
	public String accountOut(HttpServletRequest request, Model model){
		accountDetail(model);
		
		return "myAccount/accountOut";
	}
	
	@RequestMapping(value="accountOut", method=RequestMethod.POST)
	public String doAccountOut(HttpServletRequest request, Model model){
		// 提现金额
		String money = request.getParameter("money");
		Result<String> result = tradeBizService.withdraw(String.valueOf(ShiroTool.userId() == null ? 0L : ShiroTool.userId()), 
														 BigDecimal.valueOf(Double.valueOf(money)));
		if(result.success()){
			return "redirect:" + result.getObject();
		}else{
			return "redirect:/account/none";
		}
	}
	
	@RequestMapping("payRegister")
	public String paymentPlatformRegister(Model model,HttpServletRequest request){
		// 账务服务接口生成链接
		String url = GinkgoConfig.getProperty("front_url");
		Result<SubAccount> subAccountResult = accountService.getSubAccount(ShiroTool.userId());
		if(subAccountResult.hasObject()) {
			SubAccount subAccount = subAccountResult.getObject();
			Result<String> register = qddAccountService.register(subAccount.getId());
			if(register.hasObject()) url = register.getObject();
			else {
				model.addAttribute("error", "获取乾多多开户链接失败："+register.getMessage());
				logger.warn("fail to get register url. userId="+ShiroTool.userId()+",userName="+ShiroTool.userName()+",subAccountId="+subAccount.getId());
			}
		}else {
			model.addAttribute("error", "获取乾多多子账户出错："+subAccountResult.getMessage());
			logger.warn("fail to get sub_account. userId="+ShiroTool.userId()+",userName="+ShiroTool.userName());
		}
		if(model.containsAttribute("error")) {
			accountCenter(model, request);
			return "myAccount/myAccount";
		}else {
			return "redirect:" + url;
		}
	}
	
	@RequestMapping("payAudit")
	public String paymentPlatformAudit(Model model,HttpServletRequest request){
		// 账务服务接口生成链接
		String url = GinkgoConfig.getProperty("front_url");
		Result<SubAccount> subAccountResult = accountService.getSubAccount(ShiroTool.userId());
		if(subAccountResult.hasObject()) {
			SubAccount subAccount = subAccountResult.getObject();
			Result<String> authorise = qddAccountService.authorise(subAccount.getId());
			if(authorise.hasObject()) url = authorise.getObject();
			else {
				model.addAttribute("error", "获取乾多多授权链接失败："+authorise.getMessage());
				logger.warn("fail to get authorise url. userId="+ShiroTool.userId()+",userName="+ShiroTool.userName()+",subAccountId="+subAccount.getId());
			}
		}else {
			model.addAttribute("error", "获取乾多多子账户出错："+subAccountResult.getMessage());
			logger.warn("fail to get sub_account. userId="+ShiroTool.userId()+",userName="+ShiroTool.userName());
		}
		if(model.containsAttribute("error")) {
			accountCenter(model, request);
			return "myAccount/myAccount";
		}else {
			return "redirect:" + url;
		}
	}
	
	@RequestMapping(value="bindBankCard", method=RequestMethod.GET)
	public String bindBankCard(HttpServletRequest request, Model model){
		Long userId = ShiroTool.userId();
		// 账户信息
		AccountDetail account = new AccountDetail();
		//基本信息
		Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
		if(result.success()) {
			account.setUserName(result.getObject().getUserName());
			account.setRealName(result.getObject().getRealName());
			account.setEmail(result.getObject().getEmail());
			account.setUserNum(result.getObject().getCustomerNum());
			account.setTelephone(result.getObject().getMobile());
			account.setCardNum(result.getObject().getIdNum());
		}
		// 银行卡信息
		Result<UserBankCard> result1 = customerBizService.getUserBankCard(userId);
		if(result1.success() && result1.hasObject()) {
			account.setBank(result1.getObject().getBankCode());
			account.setBankCardNum(result1.getObject().getBankCardNum());
			account.setProvince(result1.getObject().getProvince());
			account.setCity(result1.getObject().getCity());
		}
		model.addAttribute("account", account);
		// 省份-城市-银行信息
		List<CodeListItem> banks = codeListBizService.branchBanks();
		List<CodeListItem> provinces = codeListBizService.provinces();
		List<CodeListItem> cities = codeListBizService.cities(account.getProvince());
		model.addAttribute("provinces", provinces);
		model.addAttribute("cities", cities);
		model.addAttribute("banks", banks);		
		return "myAccount/bindBankCard";
	}
	
	@RequestMapping(value="bindBankCard", method=RequestMethod.POST)
	public String doBindBankCard(AccountDetail detail, HttpServletRequest request, Model model){
		Long userId = ShiroTool.userId();
		AccountDetail account = new AccountDetail();
		Result<UserDetail> result = null;
		if(userId!=null && !("").equals(userId)) {
		// 账户信息
			result = customerBizService.getUserDetailByUserId(userId);
			if(result.success() && result.getObject().getUserId()!=null) {
				account.setRealName(result.getObject().getRealName());
				account.setUserName(result.getObject().getUserName());
			}else {
				logger.error("用户userId为"+userId+"的用户没有找到");
			}
		}
		model.addAttribute("account", account);
		//验证添加银行卡的参数
		boolean flag = true;
		if(detail.getBank()==null || ("").equals(detail.getBank())) {
			flag = false;
		}
		if(detail.getBankCardNum()==null ||("").equals(detail.getBankCardNum())) {
			flag = false;
		}
		if(detail.getProvince()==null || ("").equals(detail.getProvince())) {
			flag = false;
		}
		if(detail.getCity()==null || ("").equals(detail.getCity())) {
			flag = false;
		}
		if(flag==false) {
			logger.info("用户id为"+userId+"的用户提供的绑定银行卡参数不全，需要重新填写");
			model.addAttribute("status", "fail");
			model.addAttribute("result", "绑定银行卡信息提供不全");
			return "myAccount/bindBankCardResult";
		}
		//查库此userid是否已经存在银行卡记录，如果存在做更新操作，不存在做新增操作
		Result<UserBankCard> cardResult = customerBizService.getUserBankCard(userId);
		Result<UserBankCard> result1 = null;
		if(cardResult.success() && cardResult.hasObject()) {
			UserBankCard userBankCard = cardResult.getObject();
			userBankCard.setBankCardNum(detail.getBankCardNum());
			userBankCard.setBankCode(detail.getBank());
			userBankCard.setBankCardAccountName(result.getObject().getRealName());
			userBankCard.setIdNum(result.getObject().getIdNum());
			userBankCard.setProvince(detail.getProvince());
			userBankCard.setCity(detail.getCity());
			userBankCard.setBankCardType(CustomerConstants.bangCardType.DEBIT_CARD.getCode());
			userBankCard.setIdType(CustomerConstants.IdType.IDENTITY_CARD.getCode());
			result1 = customerBizService.modifyBankCard(userBankCard, userId);
		}else {
			//添加银行卡
			UserBankCard userBankCard = new UserBankCard();
			userBankCard.setBankCardAccountName(result.getObject().getRealName());
			userBankCard.setIdNum(result.getObject().getIdNum());
			userBankCard.setBankCode(detail.getBank());
			userBankCard.setBankCardNum(detail.getBankCardNum());
			userBankCard.setBankCardType(CustomerConstants.bangCardType.DEBIT_CARD.getCode());
			userBankCard.setProvince(detail.getProvince());
			userBankCard.setCity(detail.getCity());
			userBankCard.setIdType(CustomerConstants.IdType.IDENTITY_CARD.getCode());
			userBankCard.setUserId(userId);
			logger.info("需要绑定银行卡的用户是"+userBankCard.getUserId());
			result1 = customerBizService.createBankCard(userBankCard);
		}
		if(result1.success()&& result1.getObject()!=null) {
			model.addAttribute("status", "success");
			model.addAttribute("bankCardNum",result1.getObject().getBankCardNum());
		}else {
			logger.info("用户id为"+userId);
			model.addAttribute("status", "fail");
			model.addAttribute("result", result1.getMessage());
		}
		return "myAccount/bindBankCardResult";
	}
	
	@RequestMapping("accountDetail")
	public String showAccountDetail(HttpServletRequest request, Model model){
		try{
			// 信息类型, base表示基本信息，bank表示银行卡信息
			String info = request.getParameter("info");
			model.addAttribute("info", info);
			Long userId = ShiroTool.userId();
			AccountDetail account = new AccountDetail();
			//基本信息
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			if(result.success()) {
				account.setUserName(result.getObject().getUserName());
				account.setRealName(result.getObject().getRealName());
				account.setEmail(result.getObject().getEmail());
				account.setUserNum(result.getObject().getCustomerNum());
				account.setTelephone(result.getObject().getMobile());
				account.setCardNum(result.getObject().getIdNum());
			}
			// 银行卡信息
			Result<UserBankCard> result1 = customerBizService.getUserBankCard(userId);
			if(result1.success() && result1.hasObject()) {
				account.setBank(result1.getObject().getBankCode());
				account.setBankCardNum(result1.getObject().getBankCardNum());
				account.setProvince(result1.getObject().getProvince());
				account.setCity(result1.getObject().getCity());
			}
			model.addAttribute("account", account);
			// 省份-城市-银行信息
			List<CodeListItem> banks = codeListBizService.branchBanks();
			List<CodeListItem> provinces = codeListBizService.provinces();
			List<CodeListItem> cities = codeListBizService.cities(account.getProvince());
			model.addAttribute("provinces", provinces);
			model.addAttribute("cities", cities);
			model.addAttribute("banks", banks);		

		}catch(Exception e){
			
		}
		return "myAccount/myAccountDetail";
	}
	
	@RequestMapping("myAccountIn")
	public String listDeposit(Trade search, HttpServletRequest request, Model model){
		// 账户信息
		accountDetail(model);
		// 交易状态字典
		model.addAttribute(VariableConstants.MAP_TRADE_STATUS, TradeConstants.TradeStatusType.getCodeNameMap());
		
		String userId=String.valueOf(ShiroTool.userId());
		Result<SubAccount> account = accountService.getSubAccount(ShiroTool.userId());
		search.setTradeType(TradeType.DEPOSIT.getCode());
		SpecParam<Trade> spec = new SpecParam<Trade>();
		spec.eq("tradeType", search.getTradeType());
		spec.eq("tradeStatus",TradeStatusType.EXECUTED.getCode());
		spec.eq("debitAccount", String.valueOf(account.getObject().getId()));
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", 6));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		Result<Pager> depositPage = tradeBizService.listTrade(userId, spec, pager);
		logger.info(SimpleLogFormater.formatResult(depositPage));
		model.addAttribute("pager", depositPage.getObject());
		
		return "myAccount/myAccountIn";
	}
	
	@RequestMapping("myAccountOut")
	public String listWithdraw(Trade search,HttpServletRequest request, Model model){
		// 账户信息
		accountDetail(model);
		// 交易状态字典
		model.addAttribute(VariableConstants.MAP_TRADE_STATUS, TradeConstants.TradeStatusType.getCodeNameMap());
		
		String userId=String.valueOf(ShiroTool.userId());
		Result<SubAccount> account = accountService.getSubAccount(ShiroTool.userId());
		search.setTradeType(TradeType.WITHDRAW.getCode());
		SpecParam<Trade> spec = new SpecParam<Trade>();
		spec.eq("tradeType", search.getTradeType());
		spec.eq("tradeStatus",TradeStatusType.EXECUTED.getCode());
		spec.eq("creditAccount", String.valueOf(account.getObject().getId()));
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", 6));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		Result<Pager> depositPage = tradeBizService.listTrade(userId, spec, pager);
		logger.info(SimpleLogFormater.formatResult(depositPage));
		model.addAttribute("pager", depositPage.getObject());
		return "myAccount/myAccountOut";
	}


}
