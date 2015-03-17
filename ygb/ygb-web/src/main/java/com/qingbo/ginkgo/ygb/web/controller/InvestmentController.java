package com.qingbo.ginkgo.ygb.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.customer.shiro.RequiresFrontUser;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
//import com.qingbo.ginkgo.ygb.customer.shiro.RequiresFrontUser;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.web.biz.CodeListBizService;
import com.qingbo.ginkgo.ygb.web.biz.CommissionTemplateBizService;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.biz.GuaranteeBizService;
import com.qingbo.ginkgo.ygb.web.biz.InvestmentBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.CodeListItem;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectDetail;
import com.qingbo.ginkgo.ygb.web.pojo.StatInfo;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;
import com.qingbo.ginkgo.ygb.web.util.AssistInfoUtil;

@Controller
@RequiresFrontUser
@RequestMapping("myAccount")
public class InvestmentController {
	
	@Resource private InvestmentBizService investmentBizService;
	@Resource private ProjectBizService projectBizService;
	@Resource private GuaranteeBizService guaranteeBizService;
	@Resource private CommissionTemplateBizService commissionTemplateBizService;
	@Resource private CodeListBizService codeListBizService;
	
	@Resource private AssistInfoUtil assistInfoUtil;
	@Autowired private CustomerBizService customerBizService;
	
	@RequestMapping("investRecord")
	public String list(Investment search, Model model, HttpServletRequest request){
		// 常量合集
		assistInfoUtil.init(model);

		// 账户信息
		accountDetail(model);
//		// 项目状态字典
//		model.addAttribute(VariableConstants.MAP_PROJECT_STATUS, ProjectConstants.ProjectStatus.getCodeNameMap());
		//传递查询条件
		model.addAttribute(VariableConstants.Entity_Search, search);
		String userId = String.valueOf(ShiroTool.userId());
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", 6));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		//设置为只能查看自己的记录
		search.setInverstorId(ShiroTool.userId());

		Result<Pager> result = investmentBizService.list(userId, search, pager);
		if(result.getError() == null){
			model.addAttribute("pager", result.getObject());
		}
		Result<StatInfo> resultStat = investmentBizService.sumUser(userId);
		if(resultStat.success()){
			model.addAttribute("tradeTotalAmount", resultStat.getObject());
		}
		
		
		return "myAccount/investRecord";
	}
	
	@RequestMapping("investRecordDetail")
	public String detail(Long id, Model model){
		// 账户信息
		accountDetail(model);
		// 投资状态字典
		model.addAttribute(VariableConstants.MAP_INVESTMENT_STATUS, ProjectConstants.InvestmentStatus.getCodeNameMap());

		String userId = String.valueOf(ShiroTool.userId());
		
		// 投资信息
		Result<Investment> result1 = investmentBizService.detail(userId, id);
		if(result1.success() && result1.getObject() != null){
			model.addAttribute(VariableConstants.Entity_Investment, result1.getObject());

			// 项目信息
			Result<ProjectDetail> result2 = projectBizService.project(result1.getObject().getProjectId());
			if(result2.success() && result2.getObject() != null){
				ProjectDetail detail = result2.getObject();
				// 获取模板名称 
				if(detail.getProject() != null){
					Result<CommissionTemplate> result5 = commissionTemplateBizService.detail(userId, detail.getProject().getRepayTemplateId());
					if(result5.success() && result5.getObject() != null){
						CodeListItem item = codeListBizService.getListItem("COMMISSION_TYPE", result5.getObject().getAllotType());
						detail.setTemplateName(item.name);
					}
				}
				model.addAttribute(VariableConstants.Entity_ProjectDetail, detail);
				
				// 担保信息
				Result<Guarantee> result3 = guaranteeBizService.detail(userId, detail.getProject().getGuaranteeLetterId());
				if(result3.success() && result3.getObject() != null){
					model.addAttribute(VariableConstants.Entity_Guarantee, result3.getObject());
				}
			}
		}
		return "myAccount/investRecordDetail";
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
