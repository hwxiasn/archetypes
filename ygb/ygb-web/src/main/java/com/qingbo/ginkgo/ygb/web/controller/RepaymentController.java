package com.qingbo.ginkgo.ygb.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.shiro.RequiresFrontUser;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
//import com.qingbo.ginkgo.ygb.customer.shiro.RequiresFrontUser;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.web.biz.CodeListBizService;
import com.qingbo.ginkgo.ygb.web.biz.CommissionTemplateBizService;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.biz.GuaranteeBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectBizService;
import com.qingbo.ginkgo.ygb.web.biz.RepaymentBizService;
import com.qingbo.ginkgo.ygb.web.biz.TradeBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.CodeListItem;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectDetail;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectSearch;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;

@Controller
@RequiresFrontUser
@RequestMapping("myAccount")
public class RepaymentController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource private RepaymentBizService repaymentBizService;
	@Resource private ProjectBizService projectBizService;
	@Resource private GuaranteeBizService guaranteeBizService;
	@Resource private TradeBizService tradeBizService;
	@Resource private CommissionTemplateBizService commissionTemplateBizService;
	@Resource private CodeListBizService codeListBizService;
	
	@Autowired private CustomerBizService customerBizService;
	
	/**
	 * 列表还款概要
	 * @param search
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("investAcceptRecord")
	public String list(ProjectSearch search, Model model, HttpServletRequest request){
		// 账户信息
		accountDetail(model);
		
		// 项目状态字典
		model.addAttribute(VariableConstants.MAP_PROJECT_STATUS, ProjectConstants.ProjectStatus.getCodeNameMap());
		
		//传递查询条件
		model.addAttribute(VariableConstants.Entity_Search, search);
		
		search.setLoaneeId(ShiroTool.userId());
		
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", 3));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		
		Result<Pager> result = projectBizService.search(search, pager);
		if(result.success()){
			model.addAttribute("pager", pager);
		}
		return "myAccount/investAcceptRecord";
	}
	
	/**
	 * 查看还款详情
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("investAcceptDetail")
	public String detail(Long id, Model model){
		String userId = String.valueOf(ShiroTool.userId());
		
		// 账户信息
		accountDetail(model);
		
		// 还款状态字典
		model.addAttribute(VariableConstants.MAP_REPAYMENT_STATUS, ProjectConstants.RepaymentStatus.getCodeNameMap());
		model.addAttribute(VariableConstants.MAP_TRADE_STATUS, TradeConstants.TradeStatusType.getCodeNameMap());
		
		try{
			//还款信息
			Result<Repayment> result1 = repaymentBizService.detail(userId, id);
			logger.info("还款信息 : " + SimpleLogFormater.formatResult(result1));
			if(result1.hasObject()){
				model.addAttribute(VariableConstants.Entity_Repayment, result1.getObject());
				
				//项目详情
				Result<ProjectDetail> result2 = projectBizService.project(result1.getObject().getProjectId());
				logger.info("项目详情信息 : " + SimpleLogFormater.formatResult(result1));
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
					
					//担保函
					Result<Guarantee> result3 = guaranteeBizService.detail(userId, detail.getProject().getGuaranteeLetterId());
					logger.info("担保函信息 : " + SimpleLogFormater.formatResult(result1));
					if(result3.success()){
						model.addAttribute(VariableConstants.Entity_Guarantee, result3.getObject());
					}
					
					//募集交易详情
					Result<Trade> result4 = tradeBizService.detail(userId, detail.getProject().getFundraiseTradeId());
					logger.info("募集交易信息 : " + SimpleLogFormater.formatResult(result1));
					if(result4.success()){
						model.addAttribute(VariableConstants.Entity_Trade, result4.getObject());
					}
				}
			}
		}catch(Exception e){
			logger.info("Show Repayment Detail.Id:"+id);
//			logger.error("查看详情出错 : " + e.getMessage());
//			logger.error("查看还款详情 : ", e);
		}
		return "myAccount/investAcceptDetail";
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
