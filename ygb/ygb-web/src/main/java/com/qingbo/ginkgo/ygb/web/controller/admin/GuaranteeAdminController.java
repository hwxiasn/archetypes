package com.qingbo.ginkgo.ygb.web.controller.admin;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.shiro.RequiresAdminUser;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.project.entity.LoaneeInfo;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.entity.ProjectReview;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;
import com.qingbo.ginkgo.ygb.project.service.CommissionTemplateService;
import com.qingbo.ginkgo.ygb.web.biz.GuaranteeBizService;
import com.qingbo.ginkgo.ygb.web.biz.InvestmentBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectReviewBizService;
import com.qingbo.ginkgo.ygb.web.biz.RepaymentBizService;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectDetail;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;
import com.qingbo.ginkgo.ygb.web.util.AssistInfoUtil;

@Controller
@RequestMapping("admin/product")
@RequiresAdminUser
public class GuaranteeAdminController {
	private final String PAGE_PATH = "admin/product/";
	@Resource private  GuaranteeBizService guaranteeBizService;
	@Resource private  ProjectBizService projectBizService;
	@Resource private  CustomerService customerService;
	@Resource private  InvestmentBizService investmentBizService;
	@Resource private  RepaymentBizService repaymentBizService;
	@Resource private  CommissionTemplateService commissionTemplateService;
	@Resource private  AssistInfoUtil assistInfoUtil;
	@Resource private  ProjectReviewBizService projectReviewBizService;

	
	/** 担保函管理  */
	@RequestMapping("guarantee")
	public String guaranteeList(Guarantee search,Model model,HttpServletRequest request){
		String userId=String.valueOf(ShiroTool.userId());
		model.addAttribute(VariableConstants.Entity_Search, search);

		assistInfoUtil.init(model);

		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", 6));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);

		Result<Pager> resultList = guaranteeBizService.list(userId, search, pager);
		if(resultList.success()){
			model.addAttribute(VariableConstants.Pager_Guarantee, resultList.getObject());
			model.addAttribute("pager", resultList.getObject());
		}

		return  PAGE_PATH + "guarantee";
	}
	
	/** 担保函管理详情  */
	@RequestMapping("guaranteeDetail")
	public String guaranteeDetail(Long id, Model model) {
		assistInfoUtil.init(model);

		String userId=String.valueOf(ShiroTool.userId());
		//担保函信息
		Result<Guarantee> resultDetail = guaranteeBizService.detail(userId, id);
		model.addAttribute(VariableConstants.Entity_Guarantee,resultDetail.getObject());
		// 插入项目信息
		Result<Project> projectDetail = projectBizService.getProject(userId, resultDetail.getObject().getProjectId());
		Project project = projectDetail.getObject();
		Result<User> loanUser = customerService.getUserByUserId(project.getLoaneeId());
		project.setLoaneeRealName(loanUser.getObject().getUserProfile().getRealName());
		project.setLoaneeUserName(loanUser.getObject().getUserName());
		model.addAttribute(VariableConstants.Entity_Project,project);

		//借款人信息披露
		Result<LoaneeInfo> resultLoanee = projectBizService.getLoaneeInfo(id, String.valueOf(ShiroTool.userId()==null?0L:ShiroTool.userId()));
		model.addAttribute(VariableConstants.Entity_LoaneeInfo, resultLoanee.getObject());

		// 插入担保机构信息
		Result<User> guarantee = customerService.getUserByUserId(Long.parseLong(resultDetail.getObject().getUserId()));
		model.addAttribute(VariableConstants.Entity_User_Guarantee,guarantee.getObject());
		
		//插入分润模板信息
		Result<CommissionTemplate> resultCommission = commissionTemplateService.getCommissionTemplate(projectDetail.getObject().getFundingTemplateId());
		model.addAttribute(VariableConstants.Entity_CommissionTemplate_Funding,resultCommission.getObject());
		Result<CommissionTemplate> resultRepay = commissionTemplateService.getCommissionTemplate(projectDetail.getObject().getRepayTemplateId());
		model.addAttribute(VariableConstants.Entity_CommissionTemplate_Repay,resultRepay.getObject());
		
		//插入审核记录
		Result<List<ProjectReview>>  resultProjectview = projectReviewBizService.list(userId,projectDetail.getObject().getId());
		model.addAttribute(VariableConstants.List_ProjectReview,resultProjectview.getObject());
		
		// 插入投资信息
		Pager pager = new Pager();
		pager.setPageSize(200);
		pager.init(200);
		pager.page(0);
		Investment investment = new Investment();
		investment.setProjectId(resultDetail.getObject().getProjectId());
		Result<Pager> investmentShow = investmentBizService.list(userId,investment, pager);
		model.addAttribute(VariableConstants.Pager_Investment,investmentShow.getObject());
		
		// 插入还款信息
		Pager pager1 = new Pager();
		pager1.setPageSize(10);
		pager1.init(10);
		pager1.page(0);
		Repayment repay = new Repayment();
		repay.setProjectId(resultDetail.getObject().getProjectId());
		Result<Pager> repayment = repaymentBizService.list(userId, repay,pager1);
		model.addAttribute(VariableConstants.Pager_Repayment,repayment.getObject());
		
		return PAGE_PATH + "guaranteeDetail";
	}
	
	
	
}
