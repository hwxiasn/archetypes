package com.qingbo.ginkgo.ygb.web.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserGroup;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.shiro.RequiresFrontUser;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.project.entity.LoaneeInfo;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.entity.ProjectReview;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;
import com.qingbo.ginkgo.ygb.project.service.CommissionTemplateService;
import com.qingbo.ginkgo.ygb.project.service.ProjectReviewService;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.biz.GuaranteeBizService;
import com.qingbo.ginkgo.ygb.web.biz.InvestmentBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectBizService;
import com.qingbo.ginkgo.ygb.web.biz.RepaymentBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;
import com.qingbo.ginkgo.ygb.web.util.AssistInfoUtil;

@Controller
@RequiresFrontUser
@RequestMapping("guarantee")
public class GuaranteeController {
	private final String PAGE_PATH = "guarantee/";
	@Resource private GuaranteeBizService guaranteeBizService;
	@Resource private ProjectBizService projectBizService;
	@Resource private CustomerService customerService;
	@Resource private InvestmentBizService investmentBizService;
	@Resource private RepaymentBizService repaymentBizService;
	@Resource private AssistInfoUtil assistInfoUtil;
	@Resource private CommissionTemplateService commissionTemplateService;
	@Resource private ProjectReviewService projectReviewService;
	@Autowired private CustomerBizService customerBizService;
	@Resource private AccountService accountService;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static int DEFAULT_PAGE_SIZE = 5;


	/** 担保函管理  */
	@RequestMapping("guarantee")
	public String guaranteeList(Guarantee search, Model model, HttpServletRequest request){
		// 回传查询条件
		model.addAttribute("search", search);
		// 账户信息
		accountDetail(model);
		// 附加信息
		assistInfoUtil.init(model);
		
		String userId=String.valueOf(ShiroTool.userId());
		logger.info("guaranteeList Search UserId:"+userId);
		model.addAttribute(VariableConstants.Entity_Search, search);
		Result<User> userInfo = customerService.getUserByUserId(Long.valueOf(userId));
		logger.info(SimpleLogFormater.formatResult(userInfo));
		//若用户是担保机构
		if(CustomerConstants.Role.GUARANTEE.getCode().equalsIgnoreCase(userInfo.getObject().getRole())
			||CustomerConstants.Role.LOAN.getCode().equalsIgnoreCase(userInfo.getObject().getRole())	){
			search.setUserId(String.valueOf(userInfo.getObject().getId()));
		}else if(userInfo.getObject().getRole().equalsIgnoreCase(CustomerConstants.Role.OPERATOR.getCode())){
			Result<UserGroup> ug = customerService.getParentUserByChildId(ShiroTool.userId());
			logger.info(SimpleLogFormater.formatResult(ug));
			if(!ug.success()){
				model.addAttribute(VariableConstants.Status_Error, ug.getMessage());
				model.addAttribute(VariableConstants.Status_Message , ug.getMessage());
				model.addAttribute(VariableConstants.Result , "fail");
				return PAGE_PATH + "guarantee";
			}
			search.setUserId(String.valueOf(ug.getObject().getParentUserId()));
		}else{
			model.addAttribute(VariableConstants.Status_Error, "");
			model.addAttribute(VariableConstants.Status_Message, "非担保机构操作人员");
			model.addAttribute(VariableConstants.Result, "fail");
			return PAGE_PATH + "guarantee";
		}
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", DEFAULT_PAGE_SIZE));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);

		Result<Pager> resultList = guaranteeBizService.list(userId, search, pager);
		logger.info(SimpleLogFormater.formatResult(resultList));
		if(resultList.getError() == null){
			model.addAttribute("pager", resultList.getObject());
		}
		return PAGE_PATH + "guarantee";
	}
	
	/** 担保函管理详情  */
	@RequestMapping("guaranteeDetail")
	public String guaranteeDetail(Long id, Model model) {
		// 条件回填
		model.addAttribute("id", id);
		
		// 账户信息
		accountDetail(model);

		// 附加信息
		assistInfoUtil.init(model);

		String userId=String.valueOf(ShiroTool.userId());
		Result<UserGroup> resultUserGroup = customerService.getParentUserByChildId(ShiroTool.userId());
		model.addAttribute(VariableConstants.Entity_UserGroup,resultUserGroup.getObject());
		//担保函信息
		Result<Guarantee> resultDetail = guaranteeBizService.detail(userId, id);
		model.addAttribute(VariableConstants.Entity_Guarantee,resultDetail.getObject());
		// 插入项目信息
		Result<Project> projectDetail = projectBizService.getProject(userId, resultDetail.getObject().getProjectId());
		model.addAttribute(VariableConstants.Entity_Project,projectDetail.getObject());
		//借款人信息披露
		Result<LoaneeInfo> resultLoanee = projectBizService.getLoaneeInfo(id, String.valueOf(ShiroTool.userId()==null?0L:ShiroTool.userId()));
		model.addAttribute(VariableConstants.Entity_LoaneeInfo, resultLoanee.getObject());
		
		// 插入担保机构信息
		Result<User> guarantee = customerService.getUserByUserId(Long.parseLong(resultDetail.getObject().getUserId()));
		model.addAttribute(VariableConstants.Entity_User_Guarantee,guarantee.getObject());
		
		//插入分润模板信息
		Result<CommissionTemplate> resultCommission = commissionTemplateService.getCommissionTemplate(projectDetail.getObject().getRepayTemplateId());
		model.addAttribute(VariableConstants.Entity_CommissionTemplate,resultCommission.getObject());
		
		//插入审核记录
		Result<List<ProjectReview>> resultProjectview = projectReviewService.list(projectDetail.getObject().getId());
		if(resultProjectview.hasObject()){
			model.addAttribute(VariableConstants.List_ProjectReview, resultProjectview.getObject());
		}
		
		// 插入投资信息
		Pager pager = new Pager();
		pager.setPageSize(200);
		pager.init(200);
		pager.page(0);
		Investment investment = new Investment();
		investment.setProjectId(resultDetail.getObject().getProjectId());
		Result<Pager> investmentShow = investmentBizService.list(null, investment, pager);
		model.addAttribute(VariableConstants.Pager_Investment,investmentShow.getObject());
		
		// 插入还款信息
		Pager pager1 = new Pager();
		pager1.setPageSize(100);
		pager1.init(100);
		pager1.page(0);
		Repayment repay = new Repayment();
		repay.setProjectId(resultDetail.getObject().getProjectId());
		Result<Pager> repayment = repaymentBizService.list(userId, repay,pager1);
		model.addAttribute(VariableConstants.Pager_Repayment,repayment.getObject());
		
		return PAGE_PATH + "guaranteeDetail";
	}
	
	
	/**
	 * 担保公司代偿
	 * @param id
	 * 项目ID
	 * @param auditPassword
	 * 审核密码
	 * @param level
	 * 审核等级
	 */
	@ResponseBody
	@RequestMapping("compensatoryAudit")
	public JSONObject compensatoryAudit(Long id, String auditPassword, String level,BigDecimal investAmount, Model model){
		logger.info("GuaranteeControll compensatoryAudit ProjectId:"+id+" UserId:"+ShiroTool.userId()+" Level:"+level+" Amount:"+investAmount);
		JSONObject json = new JSONObject();
		String userId=String.valueOf(ShiroTool.userId());
		//检验用户的审核权限
		Result<Boolean>  audit = customerService.validateAuditPassword(ShiroTool.userId(), auditPassword);
		logger.info("GuaranteeControll compensatoryAudit ProjectId:"+id+" UserId:"+ShiroTool.userId()+" Level:"+level+" Amount:"+investAmount+" Password:"+audit.success());
		//审核鉴权失败,退出
		if(!audit.success()){
			json.put("ERROR", audit.getError());
			json.put("MESSAGE", "鉴权失败，请重试。");
			json.put("result", "fail");
			return json;
		}else{
			if(!audit.getObject()){
				logger.info("GuaranteeControll compensatoryAudit ProjectId:"+id+" UserId:"+ShiroTool.userId()+" Level:"+level+" Amount:"+investAmount+" Password has no Object.");
				json.put("MESSAGE", "审核密码错误！");
				json.put("result", "fail");
				return json;
			}
		}
		
		Result<UserGroup> resultUserGroup = customerService.getParentUserByChildId(ShiroTool.userId());
		logger.info("GuaranteeControll compensatoryAudit ProjectId:"+id+" UserId:"+ShiroTool.userId()+" Level:"+level+" Amount:"+investAmount+" Find Parent Info:"+resultUserGroup.success());
		if(!resultUserGroup.success()){
			logger.info("GuaranteeControll compensatoryAudit ProjectId:"+id+" UserId:"+ShiroTool.userId()+" Level:"+level+" Amount:"+investAmount+" Find Parent Info:"+resultUserGroup.success());
			json.put("ERROR", audit.getError());
			json.put("MESSAGE", "担保机构不存在");
			json.put("result", "fail");
			return json;
		}
		//查询投资账户
		Result<SubAccount> subAccountResult = accountService.getSubAccount(resultUserGroup.getObject().getParentUserId());
		logger.info("GuaranteeControll compensatoryAudit ProjectId:"+id+" UserId:"+ShiroTool.userId()+" Level:"+level+" Amount:"+investAmount+" Find Parent Acount:"+subAccountResult.success());
		if(!subAccountResult.hasObject()){
			logger.info("GuaranteeControll compensatoryAudit failed for SubAccount is wrong by no Object.");
			json.put("ERROR", subAccountResult.getError());
			json.put("MESSAGE", "账户不存在");
			json.put("result", "fail");
			return json;
		}
		//资金不足
		if(investAmount.compareTo(subAccountResult.getObject().getBalance()) > 0){
			logger.info("GuaranteeControll compensatoryAudit failed for Not Enough Money");
			json.put("ERROR", "");
			json.put("MESSAGE", "余额不足");
			json.put("result", "fail");
			return json;
		}
		
		//代偿
		Result<Boolean> result = projectBizService.compensatory(id, userId, investAmount);
		logger.info("GuaranteeControll compensatoryAudit ProjectId:"+id+" UserId:"+ShiroTool.userId()+" Level:"+level+" Amount:"+investAmount+" do compensatoryAudit Result:"+result.success());
		if(result.success()){
			json.put("ERROR", result.getError());
			json.put("MESSAGE", result.getMessage());
			json.put("result", "success");
			return json;
		}else{
			logger.info("GuaranteeControll compensatoryAudit ProjectId:"+id+" UserId:"+ShiroTool.userId()+" Level:"+level+" Amount:"+investAmount+" do compensatoryAudit Result:"+result.success()+" "+SimpleLogFormater.formatResult(result));
			json.put("ERROR", result.getError());
			json.put("MESSAGE", result.getMessage());
			json.put("result", "fail");
			return json;
		}
	}
	
	
	/**
	 * 担保函审核
	 * @param id
	 * 担保函ID
	 * @param auditPassword
	 * 审核密码
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("guaranteeAudit")
	public JSONObject guaranteeAudit(Long id, String auditPassword, String level, Model model){
		JSONObject json = new JSONObject();
		String userId=String.valueOf(ShiroTool.userId());
		//检验用户的审核权限
		Result<Boolean>  audit = customerService.validateAuditPassword(Long.parseLong(userId), auditPassword);
		//审核鉴权失败,退出
		if(!audit.success()){
			json.put("ERROR", audit.getError());
			json.put("MESSAGE", "鉴权失败，请重试。");
			json.put("result", "fail");
			return json;
		}else{
			if(!audit.getObject()){
				json.put("MESSAGE", "审核密码错误！");
				json.put("result", "fail");
				return json;
			}
		}
		
		//担保函审批
		Result<Guarantee> resultAudit = guaranteeBizService.audit(id, userId, level);
		if(resultAudit.success()){
			json.put("ERROR", resultAudit.getError());
			json.put("MESSAGE", resultAudit.getMessage());
			json.put("result", "success");
			return json;
		}else{
			json.put("ERROR", resultAudit.getError());
			json.put("MESSAGE", resultAudit.getMessage());
			json.put("result", "fail");
			return json;
		}
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
		
		// 账户信息
		model.addAttribute("account", accountDetail);
	}
}
