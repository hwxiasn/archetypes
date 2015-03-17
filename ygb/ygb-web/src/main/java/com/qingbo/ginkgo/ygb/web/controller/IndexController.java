package com.qingbo.ginkgo.ygb.web.controller;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.LoaneeInfo;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.ProjectStatus;
import com.qingbo.ginkgo.ygb.project.service.CommissionTemplateService;
import com.qingbo.ginkgo.ygb.project.service.GuaranteeService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectBizService;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectDetail;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectSearch;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;
import com.qingbo.ginkgo.ygb.web.util.AssistInfoUtil;
import com.qingbo.ginkgo.ygb.web.util.Image;
import com.qingbo.ginkgo.ygb.web.util.VelocityUtil;

@Controller
public class IndexController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private int pageSize = 5;
	
	@Autowired private UserBizService userBizService;
	@Autowired private ProjectBizService projectBizService;
	@Resource private AssistInfoUtil assistInfoUtil;
	@Resource private CommissionTemplateService commissionTemplateService;
	@Resource private AccountService accountService;
	@Resource private GuaranteeService guaranteeService;

	@RequestMapping("index")
	public String index(HttpServletRequest request, Model model) {
		assistInfoUtil.init(model);
		
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		
		int type = RequestUtil.getIntParam(request, "type", 0);
		Project search = new Project();		
		if(type == 1){
			search.setStatus(ProjectStatus.REPAYED.getCode());
		}else{
			search.setStatus(ProjectStatus.FUNDRAISING.getCode()
					+","+ProjectStatus.PUBLISHED.getCode()
					+","+ProjectStatus.FUNDRAISE_FALURE.getCode()
					+","+ProjectStatus.FUNDRAISE_COMPLETE.getCode()
					+","+ProjectStatus.REVIEWED_BY_GUARANTEE_COMPANY.getCode()
					+","+ProjectStatus.PROJECT_DEAL.getCode()
					+","+ProjectStatus.PROJECT_DEAL_D.getCode()
					+","+ProjectStatus.PROJECT_REPAYING.getCode()
					+","+ProjectStatus.REPAYED.getCode()
					);
		}
		Result<Pager> searchResult = projectBizService.list(String.valueOf(ShiroTool.userId()==null?0L:ShiroTool.userId()), search, pager);
		if(searchResult.success()) {
			pager = searchResult.getObject();
			model.addAttribute(VariableConstants.Pager_Project,pager);
			model.addAttribute("pager", pager);
		}

		model.addAttribute("totalInvestments", projectBizService.totalInvestments().getObject());
		model.addAttribute("totalRepayments", projectBizService.totalRepayments().getObject());
		
		model.addAttribute("type", type);
		
		return "index";
	}
	
	@RequestMapping("readyInvest")
	public String readyInvest(Long id,HttpServletRequest request, Model model) {
		assistInfoUtil.init(model);
		if(ShiroTool.userId() == null){
			return "login";
		}
		Result<Project> result = projectBizService.getProject(String.valueOf(ShiroTool.userId()==null?0L:ShiroTool.userId()), id);
		model.addAttribute(VariableConstants.Entity_Project, result.getObject());

		return "readyInvest";
	}	
	
	//检验用户的可用投资额度
	@ResponseBody
	@RequestMapping(value="checkMoney",method=RequestMethod.POST)
	public JSONObject checkMoney(HttpServletRequest request, HttpServletResponse response, Model model){
		JSONObject obj = new JSONObject();
		double money = RequestUtil.getDoubleParam(request, "money", 0.0);
		AccountDetail  ad = userBizService.getAccountDetail(ShiroTool.userId());
		if(ad.getBalance() != null && !"".equals(ad.getBalance()) && Double.valueOf(ad.getBalance())>=money){
			obj.put("result", "success");
		}
		return obj;
	}
	
	
	@RequestMapping("index1")
	public String index1(HttpServletRequest request, Model model) {
		//暂不用
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		
		int type = RequestUtil.getIntParam(request, "type", 0);
		
		ProjectSearch projectSearch = new ProjectSearch();
		projectSearch.setType(type);
		Result<Pager> search = projectBizService.search(projectSearch, pager);
		if(search.success()) {
			pager = search.getObject();
		}
		
		model.addAttribute("totalInvestments", projectBizService.totalInvestments().getObject());
		model.addAttribute("totalRepayments", projectBizService.totalRepayments().getObject());
		
		model.addAttribute("pager", pager);
		model.addAttribute("type", type);
		return "index";
	}
	
	@RequestMapping("goInvest")
	public String goInvest(HttpServletRequest request, ProjectSearch search, Model model) {
		// 附加信息
		assistInfoUtil.init(model);
		
		// 条件回显
		model.addAttribute(VariableConstants.Entity_Search, search);

		// 实体参数
		Project project = new Project();
		project.setStatus(ProjectStatus.FUNDRAISING.getCode());		// 状态：
		
		// 分页参数
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);

		
		Result<Pager> searchResult = projectBizService.list(String.valueOf(ShiroTool.userId()==null?0L:ShiroTool.userId()), project, pager);
		if(searchResult.success()) {
			pager = searchResult.getObject();
			model.addAttribute(VariableConstants.Pager_Project,pager);
			model.addAttribute("pager", pager);
		}

		return "goInvest";
	}
	
	@RequestMapping("goInvest1")
	public String goInvest1(HttpServletRequest request, ProjectSearch projectSearch, Model model) {
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);

		Result<Pager> search = projectBizService.search(projectSearch, pager);
		if(search.success()) {
			pager = search.getObject();
		}
		
		model.addAttribute("pager", pager);
		model.addAttribute("project", projectSearch);
		model.addAttribute("guarantees", projectBizService.guarantees().getObject());
		return "goInvest";
	}
	
	@RequestMapping("investInfo/{id}")
	public String investInfo(@PathVariable Long id, Model model) {
		assistInfoUtil.init(model);
		Result<Project> result = projectBizService.getProject(String.valueOf(ShiroTool.userId()==null?0L:ShiroTool.userId()), id);
		model.addAttribute(VariableConstants.Entity_Project, result.getObject());
		
		Result<LoaneeInfo> resultLoanee = projectBizService.getLoaneeInfo(id, String.valueOf(ShiroTool.userId()==null?0L:ShiroTool.userId()));
		model.addAttribute(VariableConstants.Entity_LoaneeInfo, resultLoanee.getObject());
		
		Result<Guarantee> resultGua = guaranteeService.getGuarantee(result.getObject().getGuaranteeLetterId());
		model.addAttribute(VariableConstants.Entity_Guarantee,resultGua.getObject());

		Result<UserDetail> resultUser = userBizService.getUser(result.getObject().getLoaneeId());
		model.addAttribute(VariableConstants.Entity_User,resultUser.getObject());
		//插入分润模板信息
		Result<CommissionTemplate> resultCommission = commissionTemplateService.getCommissionTemplate(result.getObject().getRepayTemplateId());
		model.addAttribute(VariableConstants.Entity_CommissionTemplate,resultCommission.getObject());

	//		Result<ProjectDetail> project = projectBizService.project(id);
//		if(project.success()) {
//			model.addAttribute("detail", project.getObject());
//		}
//		model.addAttribute("id", id);
		return "investInfo";
	}
	
	@RequestMapping("investInfo1/{id}")
	public String investInfo1(@PathVariable Long id, Model model) {
		//暂不用
		
		/**
response.expires = 0
response.expiresabsolute = Now() - 1
response.addHeader "pragma","no-cache"
response.addHeader "cache-control","private"
Response.CacheControl = "no-cache"		//*/
		
		Result<ProjectDetail> project = projectBizService.project(id);
		if(project.success()) {
			model.addAttribute("detail", project.getObject());
		}
		model.addAttribute("id", id);
		return "investInfo";
	}
	
	@RequestMapping(value="login", method=RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	public String login(HttpServletRequest request, Model model, String userName, String password) {
		logger.info("IndexController Login UserName="+userName);
		boolean checkImgCode = Image.checkImgCode(request);
		if(!checkImgCode) {
			logger.info("IndexController Login UserName="+userName+" ImgCode:"+checkImgCode);
			model.addAttribute("error", "验证码错误！");
			return login();
		}
		
		if(StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
			logger.info("IndexController Login UserName="+userName+" Password is null");
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
			logger.info("IndexController Login UserName="+userName+" Login OK.");
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
		
		logger.info("IndexController Login UserName="+userName+" Login OK.Authenticated:"+subject.isAuthenticated());
		if(subject.isAuthenticated()) {
			if(ShiroTool.isFrontAuthenticated()) {
				logger.warn("front login succeed: "+userName+", role="+ShiroTool.userRole());
				return "redirect:index.html";
			}else {
				logger.warn("前台登录用户角色错误："+userName+", role="+ShiroTool.userRole());
				userBizService.logout();
				model.addAttribute("error", "角色错误！");
				return "redirect:login.html";
			}
		}
		
		return "login";
	}
	
	@RequestMapping("logout")
	public String logout() {
		userBizService.logout();
		return "redirect:/index.html";
	}
	
	@RequestMapping("checkcode")
	public void checkcode(HttpServletRequest request, HttpServletResponse response){
		try{
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 1L);
			response.setHeader("Cache-Control", "no-cache");
			response.addHeader("Cache-Control", "no-store");
			response.setContentType("image/jpeg");
			BufferedImage image = Image.creatImage(request.getSession());
	        ImageIO.write(image, "JPEG", response.getOutputStream());  
		}catch(Exception e){
			logger.warn("fail to write checkcode." , e);
		}
	}
	
	@RequestMapping("help")
	public String help(HttpServletRequest request,Model model) {
		return "help";
	}
	
	@RequestMapping("statics/{path}")
	public String statics(@PathVariable String path) {
		return "statics/"+path;
	}
	
	@RequestMapping("aboutUs")
	public String about(HttpServletRequest request,Model model){
		return "aboutUs";
	}
	
	@RequestMapping("mobile")
	public String mobile(HttpServletRequest request,Model model){
		return "mobile";
	}
	//平台公告
	@RequestMapping("platformNotice")
	public String platformNotice(HttpServletRequest request,Model model){
		return "platformNotice";
	}
	//行业动态
	@RequestMapping("industryNew")
	public String industryNew(HttpServletRequest request,Model model){
		return "industryNew";
	}
	//平台公告详情
	@RequestMapping("newsDetail")
	public String newsDetail(HttpServletRequest request,Model model,Long newsId){
		return "news/newsDetail"+newsId;
	}
	@RequestMapping("vm")
	public String vm(HttpServletRequest request,Model model,@RequestParam String path){
		try{//测试邮件页面内容是否正常
			Map<String, Object> map = new HashMap<>();
			map.put("activatedCode", "adsfsd");
			String merge = VelocityUtil.merge(path+".vm", map);
			System.out.println(merge);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return path;
	}
	@RequestMapping("404")
	public String pageNotFound(HttpServletRequest request, Model model) {
		model.addAttribute("error", "caifuxiang test error hints");
		return "404";
	}
	
	//处理投资,成功返回success,失败返回fail
	@ResponseBody
	@RequestMapping(value="goInvest/doInvest", method=RequestMethod.POST)
	public JSONObject doInvest(HttpServletRequest request, HttpServletResponse response, Model model){
		JSONObject obj = new JSONObject();
		//投资项目
		String investProjectId = request.getParameter("investProjectId");
		// 是否同意条款，1表示同意，0表示没同意
		String agree = request.getParameter("agree");
		//未同意投资条款
		if(!"1".equals(agree)){
			try{
				response.sendRedirect("/investInfo.html");
			}catch(Exception e){}
		}
		// 支付密码
		String password =RequestUtil.getStringParam(request, "password", "");// request.getParameter("password");
		//交易密码检查留待服务支持
		Result<Boolean> passwordCheck = accountService.validatePassword(ShiroTool.userId(), password);
		//密码不正确
		if(!passwordCheck.success()){
			obj.put("result", "failed");
			obj.put("stateCode", "支付密码校验失败");
//			try{
//				response.sendRedirect("investInfo/"+investProjectId+".html");
//			}catch(Exception e){}
//			return "investInfo/"+investProjectId+"";
			return obj;
		}else{
			if(!passwordCheck.getObject()){
				obj.put("result", "failed");
				obj.put("stateCode", "支付密码不正确");
				return obj;
			}
		}
		// 投资的金额
		double investMoney = RequestUtil.getDoubleParam(request, "investMoney", 0.0);//request.getParameter("investMoney");
		//交易金额留待服务支持
		BigDecimal investAmount = BigDecimal.valueOf(investMoney).multiply(BigDecimal.valueOf(10000.0));
		//查询投资账户
		Result<SubAccount> subAccountResult = accountService.getSubAccount(ShiroTool.userId());
		if(!subAccountResult.hasObject()){
			obj.put("result", "failed");
			obj.put("stateCode", "账户不存在");
//			try{
//				response.sendRedirect("investInfo/"+investProjectId+".html");
//			}catch(Exception e){}
//			return "investInfo/"+investProjectId+"";
			return obj;
		}
		//资金不足
		if(investAmount.compareTo(subAccountResult.getObject().getBalance()) > 0){
			obj.put("result", "failed");
			obj.put("stateCode", "余额不足");
//			try{
//				response.sendRedirect("investInfo/"+investProjectId+".html");
//			}catch(Exception e){}
//			return "investInfo/"+investProjectId+"";
			return obj;
		}
		//投资金额乘以万元单位
		try{
			Result<Boolean> investResult = projectBizService.invest(Long.valueOf(investProjectId), String.valueOf(ShiroTool.userId()), investAmount);
			logger.info("IndexController Invest Id:"+investProjectId +" UserId:"+ShiroTool.userId()+" InvestAmount:"+investAmount+" "+SimpleLogFormater.formatResult(investResult));
			if(investResult.success()){
				obj.put("result", "success");
				return obj;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		obj.put("result", "failed");
		obj.put("stateCode", "投资失败");
		try{
			response.sendRedirect("investInfo/"+investProjectId+".html");
		}catch(Exception e){}
		return obj;
//		return "investInfo/"+investProjectId+"";
	}
	
}
