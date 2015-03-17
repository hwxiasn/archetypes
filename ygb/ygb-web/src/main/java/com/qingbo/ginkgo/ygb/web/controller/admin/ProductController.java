package com.qingbo.ginkgo.ygb.web.controller.admin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.base.enums.UploadImageType;
import com.qingbo.ginkgo.ygb.base.service.CodeListService;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.base.service.UploadImageService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.MailUtil;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.PropertiesUtil;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.common.util.StringUtil;
import com.qingbo.ginkgo.ygb.common.util.UploadUtil;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants.Role;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.shiro.RequiresAdminUser;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.project.entity.CommissionDetail;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.project.entity.LoaneeInfo;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.entity.ProjectReview;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.ProjectStatus;
import com.qingbo.ginkgo.ygb.project.service.CommissionTemplateService;
import com.qingbo.ginkgo.ygb.project.util.PeriodCounter;
import com.qingbo.ginkgo.ygb.web.biz.GuaranteeBizService;
import com.qingbo.ginkgo.ygb.web.biz.InvestmentBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectReviewBizService;
import com.qingbo.ginkgo.ygb.web.biz.RepaymentBizService;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectDetail;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;
import com.qingbo.ginkgo.ygb.web.util.AssistInfoUtil;
import com.qingbo.ginkgo.ygb.web.util.VelocityUtil;

@RequiresAdminUser
@Controller
@RequestMapping("admin/product")
public class ProductController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired private UploadImageService uploadImageService;
	@Autowired private CustomerService customerService;
	@Autowired private CodeListService codeListService;
	@Autowired private CommissionTemplateService commissionTemplateService;
	@Autowired private TongjiService tongjiService;
	
	@Autowired private ProjectBizService projectBizService;
	@Resource private GuaranteeBizService guaranteeBizService;
	@Resource private AssistInfoUtil assistInfoUtil;
	@Resource private  InvestmentBizService investmentBizService;
	@Resource private  RepaymentBizService repaymentBizService;
	@Resource private ProjectReviewBizService projectReviewBizService;

	private final String PAGE_PATH = "admin/product/";
	private int pageSize = 10;
	
	/**
	 * 发布新产品 
	 */
	@RequestMapping(value="productAdd", method=RequestMethod.GET)
	public String productAdd(HttpServletRequest request, Model model) {
		assistInfoUtil.init(model);
		return PAGE_PATH + "productAdd";
	}
	

	/**
	 * 发布新产品保存
	 * @param project
	 * @param guarantee
	 */
	@RequestMapping(value="productSave", method=RequestMethod.POST)
	public String productSave(Project project,LoaneeInfo loanee, Guarantee guarantee, HttpServletRequest request, Model model) {
		
		try{
			//解析借款人信息
			Result<User> borrowerResult = customerService.getUserByUserName(project.getLoaneeUserName());
			if(borrowerResult.success()) {
				User borrower = borrowerResult.getObject();
				project.setLoaneeId(borrower.getId());
			}
		}catch(Exception e){
			logger.error("发布产品失败，借款人信息解析出错，" + e.getMessage());
			model.addAttribute("OK", "N");
			return PAGE_PATH + "productAdd";
		}
			
		//获取担保机构ID
		project.setGuaranteeUserId(guarantee.getUserId());
			
		try{	
			//解析借款周期
			String investmentPeriod = "1";
			//借款期限1.固定期限 2.定制天数3.定制月份
			if ((VariableConstants.InvestmentPeriodType.FIX_PERIOD).equals(project.getPeriodType())) {
				investmentPeriod = RequestUtil.getStringParam(request, "periodTypeFIX_PERIOD", investmentPeriod);
			} else if ((VariableConstants.InvestmentPeriodType.DAYS).equals(project.getPeriodType())) {
				investmentPeriod = RequestUtil.getStringParam(request, "periodTypeDAYS", investmentPeriod);
			} else if ((VariableConstants.InvestmentPeriodType.MONTHS).equals(project.getPeriodType())) {
				investmentPeriod = RequestUtil.getStringParam(request, "periodTypeMONTHS", investmentPeriod);
			}
			project.setPeriod(investmentPeriod);
			//借款天数
			project.setPeriodDays(PeriodCounter.getDays(project.getPeriodType(), Integer.valueOf(project.getPeriod())));
		}catch(Exception e){
			logger.error("发布产品失败，借款周期解析出错，" + e.getMessage());
			model.addAttribute("OK", "N");
			return PAGE_PATH + "productAdd";
		}
			
		try{
			//解析满标金额
			if(project.getSettleTerms().equalsIgnoreCase("FULL")){
				project.setSettleTermsAmount(project.getTotalAmount());
			}else if(project.getSettleTerms().equalsIgnoreCase("FIX_AMOUNT")){
				project.setSettleTermsAmount(BigDecimal.valueOf(RequestUtil.getDoubleParam(request,"settleTermsValueFIX_AMOUNT", 0.0)));
			}else if(project.getSettleTerms().equalsIgnoreCase("FIX_PERCENTAGE")){
				project.setSettleTermsAmount(project.getTotalAmount().multiply(BigDecimal.valueOf(0.01)).multiply(BigDecimal.valueOf(RequestUtil.getDoubleParam(request,"settleTermsValueFIX_PERCENTAGE", 0.0))));
			} 
		}catch(Exception e){
			logger.error("发布产品失败，满标金额解析出错，" + e.getMessage());
			model.addAttribute("OK", "N");
			return PAGE_PATH + "productAdd";
		}
		
		try{
			//处理投资收益年利率
			Result<CommissionTemplate> repayCommResult = commissionTemplateService.getCommissionTemplate(project.getRepayTemplateId());
			logger.info(SimpleLogFormater.formatResult(repayCommResult));
			if(!repayCommResult.success()){
				model.addAttribute("OK", "N");
				return PAGE_PATH + "productAdd";
			}
			CommissionTemplate repay = repayCommResult.getObject();
			BigDecimal rate = BigDecimal.ZERO;
			for(CommissionDetail detail:repay.getDetails()){
				if(CustomerConstants.Role.INVESTOR.getCode().equalsIgnoreCase(detail.getRole())){
					rate = detail.getRate();
				}
			}
			project.setInterestRate(rate);
		}catch(Exception e){
		}
	
		try{
			//最低投资额的其他投资额 未选择指定投资额
			if(project.getMinimalInvestment().compareTo(BigDecimal.ZERO)<1){
				Boolean minimumInvestmentAmountOther = RequestUtil.getBoolParam(request,"minimumInvestmentAmountOther", false);
				double minimumInvestmentAmountValue = 0;
				if (minimumInvestmentAmountOther) {
					minimumInvestmentAmountValue = RequestUtil.getDoubleParam(request,"minimumInvestmentAmountValue", minimumInvestmentAmountValue);
					project.setMinimalInvestment(BigDecimal.valueOf(minimumInvestmentAmountValue));
				}
			}
			
			//处理万元转换为元格式
			//处理融资总额
			project.setTotalAmount(project.getTotalAmount().multiply(BigDecimal.valueOf(10000)));
			//处理最低投资额
			project.setMinimalInvestment(project.getMinimalInvestment().multiply(BigDecimal.valueOf(10000)));
			//处理满标金额
			project.setSettleTermsAmount(project.getSettleTermsAmount().multiply(BigDecimal.valueOf(10000)));
		}catch(Exception e){
			logger.error("发布产品失败，最低投资额等解析出错，" + e.getMessage());
			model.addAttribute("OK", "N");
			return PAGE_PATH + "productAdd";
		}	
	
		try{
			//处理投资起止时间
			project.setInvestStartDate(DateUtil.parse(RequestUtil.getStringParam(request, "startDate", null)));
			project.setInvestEndDate(DateUtil.parse(RequestUtil.getStringParam(request, "endDate", null)));
		}catch(Exception e){
			logger.error("发布产品失败，投资起止时间处理出错，" + e.getMessage());
			model.addAttribute("OK", "N");
			return PAGE_PATH + "productAdd";
		}	
	
		try{
			//处理保荐人信息
			if(project.getSponsorUserName()!=null && !project.getSponsorUserName().equals("")) {
				Result<User> sponsorResult = customerService.getUserByUserName(project.getSponsorUserName());
				if(sponsorResult.success()) {
					User sponsor = sponsorResult.getObject();
					project.setSponsorUserId(sponsor.getId());
				}
			}
		}catch(Exception e){
			logger.error("发布产品失败，保荐人信息处理出错，" + e.getMessage());
			model.addAttribute("OK", "N");
			return PAGE_PATH + "productAdd";
		}	
		boolean uploadProjectImages = false;

		try{
			//项目操作日志
			ProjectReview pr = new ProjectReview();
			pr.setProjectId(0L);
			try{
				pr.setReviewerId(ShiroTool.userId()==null?0L:ShiroTool.userId());
				Result<User> actor = customerService.getUserByUserId(ShiroTool.userId());
				logger.info("ProductSave:"+SimpleLogFormater.formatResult(actor));
				if(actor.success()){
					pr.setReviewerUserName(actor.getObject().getUserName());
					pr.setReviewerName(actor.getObject().getUserProfile().getRealName());
				}
			}catch(Exception e){
				logger.error("ProductSave:CustomerService.getUserByUserId failed:by" + e.getMessage());
			}
			pr.setReviewCode("ADD Project");
			pr.setReviewName("产品发布");
			pr.setResult("");
			pr.setReason("");

			String loginId = String.valueOf(ShiroTool.userId());

			//创建项目
			try{
				Result<Project> projectResult = projectBizService.createProject(loginId, project, guarantee);
				logger.info("Create Project Result:"+SimpleLogFormater.formatResult(projectResult));
				if(projectResult.success()){
					uploadProjectImages = true;
					pr.setProjectId(projectResult.getObject().getId());
					loanee.setProjectId(projectResult.getObject().getId());
					pr.setResult("成功");
				}else{
					pr.setResult("失败");
				}
			}catch(Exception e){
				logger.error("ProjectBizService.createProject failed:by" + e.getMessage());
			}
			
			try{
				Result<ProjectReview> upReview = projectReviewBizService.create(loginId, pr);
				logger.info(SimpleLogFormater.formatResult(upReview));
			}catch(Exception e){
				logger.error("ProjectReviewBizService.create failed:by" + e.getMessage());
			}
			
			try{
				Result<LoaneeInfo> save = projectBizService.createLoaneeInfo(loanee, loginId);
				logger.info("ProductController Create LoaneeInfo Result:"+SimpleLogFormater.formatResult(save));
			}catch(Exception e){
				logger.error("ProductController Create LoaneeInfo failed:by" + e.getMessage());
			}
			
			if(uploadProjectImages) {
				//filesBase
				String filesBase = RequestUtil.getStringParam(request, "filesBase", null);
				String filesDiya = RequestUtil.getStringParam(request, "filesDiya", null);
				String filesDiaocha = RequestUtil.getStringParam(request, "filesDiaocha", null);
				String filesKuaiji = RequestUtil.getStringParam(request, "filesKuaiji", null);
				String filesLvshi = RequestUtil.getStringParam(request, "filesLvshi", null);
				String filesZichan = RequestUtil.getStringParam(request, "filesZichan", null);
				Map<String, List<String>> projectImages = new HashMap<String, List<String>>();
				projectImages.put(UploadImageType.BASE.getCode(), StringUtil.linesToList(filesBase));
				projectImages.put(UploadImageType.PLEDGE.getCode(), StringUtil.linesToList(filesDiya));
				projectImages.put(UploadImageType.INVESTIGATION.getCode(), StringUtil.linesToList(filesDiaocha));
				projectImages.put(UploadImageType.ACCOUNTANT_AUDIT.getCode(), StringUtil.linesToList(filesKuaiji));
				projectImages.put(UploadImageType.LAYWER_AUDIT.getCode(), StringUtil.linesToList(filesLvshi));
				projectImages.put(UploadImageType.ASSETS_APPRAISAL.getCode(), StringUtil.linesToList(filesZichan));
				uploadImageService.updateProjectImages(project.getId(), projectImages);
				
				UploadUtil.confirm(RequestUtil.getStringParam(request, "commitmentLetterPath", null));
			}
		}catch(Exception e){
			logger.error("发布产品失败，新建项目出错，" + e.getMessage());
			model.addAttribute("OK", "N");
			return PAGE_PATH + "productAdd";
		}	
		
		return "redirect:/admin/product/productAdd.html";
	}
	
	
	/**
	 * 产品发布审核结果，提供按照担保公司ID或者项目状态查询
	 * @param userId
	 * 担保公司ID
	 */
	@RequestMapping("productReviewed")
	public String productReviewed(Project search, HttpServletRequest request, Model model) {
		// 常量合集
		assistInfoUtil.init(model);
		
		// 返回查询条件
		model.addAttribute(VariableConstants.Entity_Search, search);
		
		// 项目状态字典
		model.addAttribute(VariableConstants.MAP_PROJECT_STATUS, ProjectConstants.ProjectStatus.getCodeNameMap());
		
		//未选择状态则设定状态
		if(search != null && "".equals(search.getStatus())){
			search.setStatus(ProjectStatus.APPROVED.getCode()+","+ProjectStatus.REJECT_PROJECT.getCode());
		}
		
		// 分页条件
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		
		// 用户ID
		String actionUserId = String.valueOf(ShiroTool.userId());
		
		// 查询结果
		Result<Pager> result = projectBizService.detailList(actionUserId, search, pager);
		if(result.hasObject()){
			model.addAttribute("pager", result.getObject());
		}

		return PAGE_PATH + "productReviewed";
	}
	
	@RequestMapping("productReviewedDetail")
	public String projectReviewedDetail(Long id, Model model){
		// 常量合集
		assistInfoUtil.init(model);
		
		// 用户
		String userId = String.valueOf(ShiroTool.userId());

		// 项目详情
		ProjectDetail detail = projectBizService.projectDetail(id);
		model.addAttribute(VariableConstants.Entity_Project, detail);

		Result<LoaneeInfo> resultLoanee = projectBizService.getLoaneeInfo(id, String.valueOf(ShiroTool.userId()==null?0L:ShiroTool.userId()));
		model.addAttribute(VariableConstants.Entity_LoaneeInfo, resultLoanee.getObject());
		

		//插入分润模板信息
		if(detail != null){
			if(detail.getProject() != null){
				Result<CommissionTemplate> resultCommission = commissionTemplateService.getCommissionTemplate(detail.getProject().getFundingTemplateId());
				model.addAttribute(VariableConstants.Entity_CommissionTemplate_Funding,resultCommission.getObject());
				Result<CommissionTemplate> resultRepay = commissionTemplateService.getCommissionTemplate(detail.getProject().getRepayTemplateId());
				model.addAttribute(VariableConstants.Entity_CommissionTemplate_Repay,resultRepay.getObject());
			}
		}
		
		//插入审核记录
		Result<List<ProjectReview>> resultProjectview = projectReviewBizService.list(userId, id);
		if(resultProjectview.hasObject()){
			model.addAttribute(VariableConstants.List_ProjectReview, resultProjectview.getObject());
		}else{
			logger.error("获取审核记录失败：" + resultProjectview.getMessage());
		}
		
		return PAGE_PATH + "productReviewedDetail";
	}
	
	/**
	 * 项目管理
	 * 提供按照担保公司ID或者项目状态查询
	 */
	@RequestMapping("project")
	public String project(Project search, HttpServletRequest request, Model model) {
		// 常量合集
		assistInfoUtil.init(model);
		
		// 返回查询条件
		model.addAttribute(VariableConstants.Entity_Search, search);

		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows != null && totalRows > 0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		pager.setDirection("desc");
		pager.setProperties("id");
		String actionUserId = String.valueOf(ShiroTool.userId());

		Result<Pager> result = projectBizService.detailList(actionUserId, search, pager);
		if(result.success() && result.getObject() != null){
			model.addAttribute("pager", result.getObject());
		}
		
		
		/**
		SqlBuilder sqlBuilder = new SqlBuilder();
		sqlBuilder.from("project left join user borrower on project.loanee_id=borrower.id left join guarantee on project.guarantee_letter_id=guarantee.id left join user guaranteeUser on guarantee.user_id=guaranteeUser.id left join user_enterprise_profile ent on guaranteeUser.id=ent.user_id");
		sqlBuilder.like("borrower.user_name", request.getParameter("loanee"));
		sqlBuilder.between("project.total_amount", request.getParameter("moneyMin"), request.getParameter("moneyMax"));
		sqlBuilder.between("project.invest_start_date", request.getParameter("beginTime"), request.getParameter("endTime"));
		sqlBuilder.eq("ent.user_id", request.getParameter("guaranteeEnterprise"));
		sqlBuilder.like("project.name", request.getParameter("projectName"));
		sqlBuilder.eq("project.status", request.getParameter("projectStatus"));
		if(pager.notInitialized()) {
			sqlBuilder.select("count(project.id)");
			Result<Integer> count = tongjiService.count(sqlBuilder);
			if(count.success()) pager.init(count.getObject());
		}
		
		sqlBuilder.select("project.id,project.name,borrower.user_name,project.total_amount,project.settle_terms_amount,ent.enterprise_name,project.invest_start_date,project.invest_end_date,project.status");
		sqlBuilder.limit(PagerUtil.limit(pager));
		@SuppressWarnings("rawtypes")
		Result<List> list = tongjiService.list(sqlBuilder);
		if(list.success()) pager.setElements(list.getObject());
		
		model.addAttribute("pager", pager);
		model.addAttribute("guarantees", projectBizService.guarantees().getObject());
		model.addAttribute("projectStatus", codeListService.list(CodeListType.PROJECT_STATUS.getCode()).getObject());
		model.addAttribute("request", RequestUtil.stringMap(request));
		//*/
		return PAGE_PATH + "project";
	}
	
	/**
	 * 产品发布审核结果详情页
	 * @param id
	 * 项目ID
	 */
	@RequestMapping("projectDetail")
	public String projectDetail(Long id, Model model) {
		// 常量合集
		assistInfoUtil.init(model);

		// 项目详情
		ProjectDetail detail = projectBizService.projectDetail(id);
		model.addAttribute(VariableConstants.Entity_Project, detail);
		//借款人信息披露
		Result<LoaneeInfo> resultLoanee = projectBizService.getLoaneeInfo(id, String.valueOf(ShiroTool.userId()==null?0L:ShiroTool.userId()));
		model.addAttribute(VariableConstants.Entity_LoaneeInfo, resultLoanee.getObject());
		
		//插入担保函信息
		Result<Guarantee> resultGuarantee = guaranteeBizService.detail(null, detail.getProject().getGuaranteeLetterId());
		model.addAttribute(VariableConstants.Entity_Guarantee, resultGuarantee.getObject());
		
		//插入分润模板信息
		if(detail != null){
			if(detail.getProject() != null){
				Result<CommissionTemplate> resultCommission = commissionTemplateService.getCommissionTemplate(detail.getProject().getFundingTemplateId());
				model.addAttribute(VariableConstants.Entity_CommissionTemplate_Funding,resultCommission.getObject());
				Result<CommissionTemplate> resultRepay = commissionTemplateService.getCommissionTemplate(detail.getProject().getRepayTemplateId());
				model.addAttribute(VariableConstants.Entity_CommissionTemplate_Repay,resultRepay.getObject());
			}
		}
		
		// 插入投资信息
		Pager pager = new Pager();
		pager.setPageSize(200);
		pager.init(200);
		pager.page(0);
		Investment investment = new Investment();
		investment.setProjectId(id);
		Result<Pager> investmentShow = investmentBizService.list(null, investment, pager);
		model.addAttribute(VariableConstants.Pager_Investment,investmentShow.getObject());
		
		// 插入还款信息
		Pager pager1 = new Pager();
		pager1.setPageSize(200);
		pager1.init(200);
		pager1.page(0);
		Repayment repay = new Repayment();
		repay.setProjectId(id);
		Result<Pager> repayment = repaymentBizService.list(null, repay, pager1);
		model.addAttribute(VariableConstants.Pager_Repayment, repayment.getObject());
		
		return PAGE_PATH + "projectDetail";
	}
	
	/**
	 * 待审核产品列表
	 * 查询条件，按照担保公司ID查询，无其他查询条件
	 * @param userId
	 * 担保公司ID
	 */
	@RequestMapping("productReviewing")
	public String productReviewing(Long userId, Model model, HttpServletRequest request){
		//返回查询条件
		model.addAttribute(VariableConstants.Entity_Search, userId);
		
		// 担保机构列表
		assistInfoUtil.init(model);

		// 登录用户
		String actionUserId = String.valueOf(ShiroTool.userId());
		
		// 分页准备
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);

		// 查询参数
		Project search = new Project();
		search.setStatus(ProjectConstants.ProjectStatus.PENDING_FOR_APPROVE.getCode());
		if(userId != null && userId > 0L){
			search.setGuaranteeUserId(String.valueOf(userId));
		}
		
		// 查询结果
		Result<Pager> result = projectBizService.detailList(actionUserId, search, pager);
		if(result.success() && result.getObject() != null){
			model.addAttribute("pager", result.getObject());
		}

		return PAGE_PATH + "productReviewing";
	}
	
	/**
	 * 产品待审核页--产品详情  
	 * @param id
	 * 项目ID
	 */
	@RequestMapping("productReviewingEdit")
	public String productReviewingEdit(Long id, Model model, HttpServletRequest request) {
		// 常量合集
		assistInfoUtil.init(model);

		// 项目详情
		ProjectDetail detail = projectBizService.projectDetail(id);
		model.addAttribute(VariableConstants.Entity_Project, detail);

		Result<LoaneeInfo> resultLoanee = projectBizService.getLoaneeInfo(id, String.valueOf(ShiroTool.userId()==null?0L:ShiroTool.userId()));
		model.addAttribute(VariableConstants.Entity_LoaneeInfo, resultLoanee.getObject());
		
		
		//插入分润模板信息
		if(detail != null){
			if(detail.getProject() != null){
				Result<CommissionTemplate> resultCommission = commissionTemplateService.getCommissionTemplate(detail.getProject().getFundingTemplateId());
				model.addAttribute(VariableConstants.Entity_CommissionTemplate_Funding,resultCommission.getObject());
				Result<CommissionTemplate> resultRepay = commissionTemplateService.getCommissionTemplate(detail.getProject().getRepayTemplateId());
				model.addAttribute(VariableConstants.Entity_CommissionTemplate_Repay,resultRepay.getObject());
			}
		}

		return PAGE_PATH + "productReviewingEdit";
	}
	
	
	@RequestMapping("reviewProject")
	public String reviewProject(Long projectId, Model model, HttpServletRequest request) {
		
		// 参数
		boolean pass = RequestUtil.getBoolParam(request, "result", false);
		String reason = RequestUtil.getStringParam(request, "reason", "");
		String date = RequestUtil.getStringParam(request, "fixedTime", "");
		Date now = DateUtil.parse(date);
		
		// 用户
		String userId = String.valueOf(ShiroTool.userId());
		
		Result<Boolean> result = projectBizService.reviewed(projectId, userId, now, pass, reason);
		if(result.hasObject()){
			if(!result.getObject()){
				logger.error("审核失败 : " + result.getMessage());
			}
		}else{
			logger.error("审核失败 : " + result.getError());
		}
		return "redirect:/admin/product/productReviewing.html";
	}
	
	
	@ResponseBody
	@RequestMapping("checkusername")
	public JSONObject checkusername(String name){
		if(name == null || "".equals(name)) return null;
		Result<User> user = customerService.getUserByUserName(name);
		if(user.success() && user.hasObject()){
			JSONObject json = new JSONObject();
			if(Role.BORROWER.getCode().equalsIgnoreCase(user.getObject().getRole())){
				json.put("mark", "yes");
			}
			json.put("userName", name);
			json.put("realName", user.getObject().getUserProfile().getRealName());
			json.put("projectNum", "0");
			json.put("investmentNum", "0");
			return json;
		}
		return null;
	}
	
	
}
