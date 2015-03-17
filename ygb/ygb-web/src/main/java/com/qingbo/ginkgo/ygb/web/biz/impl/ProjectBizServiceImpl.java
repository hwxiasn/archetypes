package com.qingbo.ginkgo.ygb.web.biz.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.base.enums.CodeListType;
import com.qingbo.ginkgo.ygb.base.service.CodeListService;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.base.service.UploadImageService;
import com.qingbo.ginkgo.ygb.base.util.PagerUtil;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.DateUtil.FormatType;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserEnterpriseProfile;
import com.qingbo.ginkgo.ygb.customer.entity.UserGroup;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.project.entity.LoaneeInfo;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.entity.ProjectCount;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.ProjectStatus;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.RepaymentStatus;
import com.qingbo.ginkgo.ygb.project.service.CommissionTemplateService;
import com.qingbo.ginkgo.ygb.project.service.GuaranteeService;
import com.qingbo.ginkgo.ygb.project.service.LoaneeInfoService;
import com.qingbo.ginkgo.ygb.project.service.ProjectService;
import com.qingbo.ginkgo.ygb.project.util.AmountCalculator;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.trade.service.TradeService;
import com.qingbo.ginkgo.ygb.web.biz.InvestmentBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectBizService;
import com.qingbo.ginkgo.ygb.web.biz.RepaymentBizService;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectDetail;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectSearch;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectSearch.FundingProgresses;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectSearch.RepayPeriods;


@Service("projectBizService")
public class ProjectBizServiceImpl implements ProjectBizService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired private ProjectService projectService;
	@Autowired private GuaranteeService guaranteeService;
	@Autowired private CustomerService customerService;
	@Autowired private UploadImageService uploadImageService;
	@Autowired private TongjiService tongjiService;
	@Autowired private CodeListService codeListService;
	@Resource private CommissionTemplateService commissionTemplateService;
	@Resource private AccountService accountService;
	@Resource private InvestmentBizService investmentBizService;
	@Resource private TradeService tradeService;
	@Resource private LoaneeInfoService loaneeInfoService;
	@Resource private RepaymentBizService repaymentBizService;


	public Result<Pager> search(ProjectSearch projectSearch, Pager pager) {
		if(projectSearch.getType() < 0) {//我要投资
			SqlBuilder sqlBuilder = new SqlBuilder();
			sqlBuilder.from("project p left join guarantee g on p.guarantee_letter_id=g.id left join user u on g.user_id=u.id");
			if(projectSearch.getMoneyRange() > 0) {
				ProjectSearch.MoneyRanges moneyRange = ProjectSearch.MoneyRanges.values()[projectSearch.getMoneyRange()-1];
				sqlBuilder.between("total_amount", ""+moneyRange.minMoney*10000, ""+moneyRange.maxMoney*10000);
			}
			if(projectSearch.getRepayPeriod() > 0) {
				RepayPeriods repayPeriod = ProjectSearch.RepayPeriods.values()[projectSearch.getRepayPeriod()-1];
				sqlBuilder.between("due_date", DateUtil.format(DateUtils.addDays(new Date(), repayPeriod.minMonths*30), FormatType.DAYTIME), DateUtil.format(DateUtils.addDays(new Date(), repayPeriod.maxMonths*30), FormatType.DAYTIME));
			}
			if(projectSearch.getFundingProgress() > 0) {
				FundingProgresses fundingProgress = ProjectSearch.FundingProgresses.values()[projectSearch.getFundingProgress()-1];
				sqlBuilder.between("progress", ""+fundingProgress.minPercent, ""+fundingProgress.maxPercent);//需要加百分比字段，否则很难搜索
			}
			if(projectSearch.getGuaranteeEnterprise() != null && !projectSearch.getGuaranteeEnterprise().trim().equals("")){
				sqlBuilder.eq("u.id", projectSearch.getGuaranteeEnterprise());
			}
			if(projectSearch.getLoaneeId() != null){
				sqlBuilder.eq("p.loanee_id", projectSearch.getLoaneeId().toString());
			}
			
			if(pager.notInitialized()) {
				sqlBuilder.select("count(p.id)");
				Result<Integer> count = tongjiService.count(sqlBuilder);
				if(count.success()) pager.init(count.getObject());
			}
			
			// 创建时间倒序
			sqlBuilder.orderBy(" p.create_at desc ");
			
			sqlBuilder.select("p.id");
			sqlBuilder.limit(PagerUtil.limit(pager));
			@SuppressWarnings("rawtypes")
			Result<List> list = tongjiService.list(sqlBuilder);
			if(list.success()) {
				List<ProjectDetail> projects = new ArrayList<>();
				for(Object obj : list.getObject()) {
					Long projectId = NumberUtil.parseLong(ObjectUtils.toString(obj), null);
					if(projectId!=null) {
						Result<Project> project = projectService.getProject(projectId);
						if(project.success()) {
							ProjectDetail projectDetail = fromProject(project.getObject());
							if(projectDetail!=null) projects.add(projectDetail);
						}
					}
				}
				pager.setElements(projects);
			}
			return Result.newSuccess(pager);
		}else {//type 0-最新发布 1-最新还款
			SpecParam<Project> specs = new SpecParam<>();
			if(projectSearch.getType()==1) {
				specs.eq("status", "RC");
			}else {
				specs.in("status", Arrays.asList("P","F","PD","PB"));
			}
			// 创建时间倒序
			pager.setProperties("createAt");
			pager.setDirection("desc");
			
			Result<PageObject<Project>> result = projectService.listProjectBySpecAndPage(specs, pager);
			if(result.success()) {
				if(pager.notInitialized()) pager.init(result.getObject().getTotal());
				List<ProjectDetail> list = new ArrayList<>();
				for(Project project : result.getObject().getList()) {
					ProjectDetail detail = fromProject(project);
					list.add(detail);
				}
				pager.setElements(list);
				return Result.newSuccess(pager);
			}else {
				return Result.newFailure(result);
			}
		}
	}
	
	private ProjectDetail fromProject(Project project) {
		ProjectDetail detail = new ProjectDetail();
		detail.setProject(project);
		detail.setInterestPercent(NumberUtil.percentFormat(project.getInterestRate()));
		detail.setInvestPeriod(project.getPeriod());
		detail.setInvestProgress(String.valueOf(project.getProgress()));
		detail.setInvestStart(DateUtil.format(project.getInvestStartDate(), FormatType.DAYTIME));
		detail.setInvestEnd(DateUtil.format(project.getInvestEndDate(), FormatType.DAYTIME));
		CodeList status = codeListService.item(CodeListType.PROJECT_STATUS.getCode(), project.getStatus()).getObject();
		if(status!=null) detail.setStatusName(status.getName());
		detail.setTotalAmount(project.getTotalAmount().intValue()/10000);
		if(project.getLoaneeId()!=null) {
			Result<User> loaneeResult = customerService.getUserByUserId(project.getLoaneeId());
			if(loaneeResult.hasObject()) {
				User loanee = loaneeResult.getObject();
				detail.setBorrowerName(loanee.getUserName());
				UserProfile userProfile = loanee.getUserProfile();
				if(userProfile!=null) {
					detail.setRealName(userProfile.getRealName());
				}
			}
		}
		Long guaranteeLetterId = project.getGuaranteeLetterId();
		if(guaranteeLetterId!=null) {
			Result<Guarantee> guaranteeResult = guaranteeService.getGuarantee(guaranteeLetterId);
			if(guaranteeResult.hasObject()) {
				Guarantee guarantee = guaranteeResult.getObject();
				detail.setGuaranteeLetterPath(guarantee.getCommitmentLetterPath());
				detail.setCommitmentLetterSn(guarantee.getCommitmentLetterSn());
				String userId = guarantee.getUserId();
				Long guaranteeUserId = NumberUtil.parseLong(userId, null);
				if(guaranteeUserId!=null) {
					detail.setGuarantee(userId);
					Result<User> guaranteeUserResult = customerService.getUserByUserId(guaranteeUserId);
					if(guaranteeUserResult.hasObject()) {
						User guaranteeUser = guaranteeUserResult.getObject();
						UserEnterpriseProfile userEnterpriseProfile = guaranteeUser.getEnterpriseProfile();
						if(userEnterpriseProfile!=null) {
							detail.setGuaranteeName(userEnterpriseProfile.getEnterpriseName());
						}
					}
				}
			}
		}
		//根据progress >= 100来判断项目是否已成立或满标，不用状态是因为项目状态太多，项目成立后还有一系列的状态变化
		detail.setInvestRemains(project.getProgress() >= 100 ? "0" : project.getSettleTermsAmount().subtract(project.getProgressAmount()).toPlainString());
		detail.setProjectImages(uploadImageService.projectImages(project.getId()).getObject());
		return detail;
	}

	@Override
	public Result<ProjectDetail> project(Long id) {
		Result<Project> project = projectService.getProject(id);
		if(project.success() && project.getObject()!=null) {
			ProjectDetail projectDetail = fromProject(project.getObject());
			return Result.newSuccess(projectDetail);
		}
		return Result.newFailure(project);
	}

	@Override
	public Result<BigDecimal> totalInvestments() {
		Result<ProjectCount> pc = projectService.sumProject();
		if(pc.success()){
			return Result.newSuccess(pc.getObject().getSettleAmout());
		}
		return Result.newFailure(pc.getError(), pc.getMessage());
	}

	@Override
	public Result<BigDecimal> totalRepayments() {
		Result<ProjectCount> pc = projectService.sumProject();
		if(pc.success()){
			return Result.newSuccess(pc.getObject().getDueAmount());
		}
		return Result.newFailure(pc.getError(), pc.getMessage());
	}

	@Override
	public Result<List<CodeList>> guarantees() {
		List<CodeList> list = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		Result<List> listResult = tongjiService.list(new SqlBuilder("user.id,ent.enterprise_name", "user left join user_enterprise_profile ent on user.id=ent.user_id").eq("user.register_type", "E").eq("user.role", "G"));
		if(listResult.success()) {
			for(Object obj : listResult.getObject()) {
				Object[] arr = (Object[])obj;
				CodeList codeList = new CodeList();
				codeList.setCode(ObjectUtils.toString(arr[0]));
				codeList.setName(ObjectUtils.toString(arr[1]));
				list.add(codeList);
			}
		}
		return Result.newSuccess(list);
	}

	@Override
	public Result<List<CodeList>> sponsors() {
		List<CodeList> list = new ArrayList<>();
		return Result.newSuccess(list);
	}

	/**
	 * 投资：投资父交易由定时任务
	 */
	public Result<Boolean> invest(Long id, String userId, BigDecimal investAmount) {
		logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount);
		Result<User> investUser = customerService.getUserByUserId(Long.valueOf(userId));	
		Result<SubAccount> account = accountService.getSubAccount(investUser.getObject().getId());
		logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" Account:"+account.success());
		if(!account.success()){
			logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" Account Result:"+SimpleLogFormater.formatResult(account));
		}
		if(account.getObject().getBalance().compareTo(investAmount)<0){
			return Result.newFailure("1", "投资额大于余额");
		}
		Result<Project> resultProject = projectService.getProject(id);
		logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" Project:"+resultProject.success());
		if(!resultProject.success()){
			logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" Project Result:"+SimpleLogFormater.formatResult(resultProject));
		}
		Project project = resultProject.getObject();
		//项目剩余可投资额
		BigDecimal releaseAmount =  project.getTotalAmount().subtract(resultProject.getObject().getProgressAmount());
		if(releaseAmount.compareTo(investAmount)<0){
			//投资额大于可投资额
			logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" InvestAmount >releaseAmount");
			return Result.newFailure("2", "投资额大于可投资额");
		}
//		//借款人信息
//		Result<User> loaneeUser = customerService.getUserByUserId(resultProject.getObject().getLoaneeId());	
		//借款人默认账号
		Result<SubAccount> accountLoanee = accountService.getSubAccount(resultProject.getObject().getLoaneeId());
		logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" accountLoanee:"+accountLoanee.success());
		if(!resultProject.success()){
			logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" accountLoanee Result:"+SimpleLogFormater.formatResult(accountLoanee));
		}
		Investment investment = new Investment();
		investment.setStatus(ProjectConstants.InvestmentStatus.ADDING.getCode());
		investment.setBalance(investAmount);
		investment.setBalanceDue(AmountCalculator.calculate(investAmount, project.getInterestRate(), resultProject.getObject().getPeriodDays()));
		investment.setInverstorId(investUser.getObject().getId());
		investment.setProjectId(project.getId());
		investment.setLoaneeId(project.getLoaneeId());
		investment.setIssueDate(new Date());
		Result<Investment> createInv = investmentBizService.create(userId, investment);
		logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" createInv:"+createInv.success());
		if(!createInv.success()){
			logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" createInvestment Result:"+SimpleLogFormater.formatResult(createInv));
			return Result.newFailure("3","创建投资凭证失败");
		}
		investment = createInv.getObject();
		Trade trade = new Trade();
		//交易金额
		trade.setTradeAmount(investAmount);
		//目标交易金额
		trade.setAimTradeAmount(investAmount);
		//出借方交易账户ID
		trade.setCreditAccount(String.valueOf(account.getObject().getId()));
		//借款人交易账户ID
		trade.setDebitAccount(String.valueOf(accountLoanee.getObject().getId()));
		
		//产品的募集父交易不存在，则创建父交易
		if(project.getFundraiseTradeId() == null ||project.getFundraiseTradeId() == 0){
			Trade pTrade = new Trade();
			//目标交易金额
			pTrade.setAimTradeAmount(resultProject.getObject().getTotalAmount());
			//出借方交易账户ID
			pTrade.setCreditAccount("");
			//借款人交易账户ID
			pTrade.setDebitAccount(String.valueOf(accountLoanee.getObject().getId()));
			pTrade.setTradeSubjectId(String.valueOf(project.getId()));
			pTrade.setTradeSubjectInfo("募集父交易");
			pTrade.setTradeSource("");
			pTrade.setTradeType(TradeConstants.TradeType.INVEST.getCode());
			pTrade.setTradeKind(TradeConstants.TradeKind.PARENT.getCode());
			pTrade.setTradeRelation(TradeConstants.TradeRelation.MANY_ONE.getCode());
			pTrade.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());
			Result<Trade> pTradeResult = tradeService.createTrade(pTrade);
			logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" pTradeResult:"+pTradeResult.success());
			if(!pTradeResult.success()){
				logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" pTradeResult Result:"+SimpleLogFormater.formatResult(pTradeResult));
				return Result.newFailure("4","创建投资凭证失败");
			}
			if(pTradeResult.success()){
				project.setFundraiseTradeId(pTradeResult.getObject().getId());
				projectService.updateProject(project);
			}
		}
		
		//设定投资交易的父交易
		trade.setParentTradeId(resultProject.getObject().getFundraiseTradeId());
		trade.setTradeSubjectId(String.valueOf(investment.getId()));
		trade.setTradeSubjectInfo("投资凭证");
		trade.setTradeSource("");
		trade.setTradeType(TradeConstants.TradeType.INVEST.getCode());
		trade.setTradeKind(TradeConstants.TradeKind.CHILDREN.getCode());
		trade.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
		trade.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());

		//发起一个交易
		Result<Trade> tradeResult = tradeService.createTrade(trade);
		logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" tradeResult:"+tradeResult.success());
		if(!tradeResult.success()){
			logger.info("ProjectBizSerivce Invest Start.Id:"+id+" UserId:"+userId+" InvestAmount:"+investAmount+" tradeResult Result:"+SimpleLogFormater.formatResult(tradeResult));
			return Result.newFailure("5","创建投资凭证失败");
		}
		//交易执行成功
		if(tradeResult.success()){
			//更新投资凭证
			try{
				createInv = investmentBizService.detail(userId, createInv.getObject().getId());
				investment = createInv.getObject();
				investment.setStatus(ProjectConstants.InvestmentStatus.PENDING.getCode());
				investment.setTradeId(tradeResult.getObject().getId());
				Result<Boolean> update = investmentBizService.update(userId, investment);
				logger.info("Project Investment Update:"+SimpleLogFormater.formatResult(update));
			}catch(Exception e){
				logger.info("Project Invest Update DB Error.By:"+e.getMessage());
			}
			
			//更新项目状态,增加已投资金额，百分比，增加投资人书
			try{
				resultProject = projectService.getProject(id);
				project = resultProject.getObject();
				project.setTotalInvestor(project.getTotalInvestor()+1);
				project.setProgressAmount(project.getProgressAmount().add(investAmount));
				BigDecimal per = project.getProgressAmount().divide(project.getTotalAmount(),4,RoundingMode.HALF_EVEN);//.setScale(4,RoundingMode.HALF_EVEN)
				project.setProgress(per.multiply(BigDecimal.valueOf(100)).intValue());
				//投资额
				if(project.getProgress() == 100){
					project.setStatus(ProjectConstants.ProjectStatus.FUNDRAISE_COMPLETE.getCode());
				}
				Result<Boolean> update = projectService.updateProject(project);
				logger.info("Project Invest progress Update:"+SimpleLogFormater.formatResult(update));
			}catch(Exception e){
				logger.info("Project Invest progress Update DB Error.By:"+e.getMessage());
			}
			return Result.newSuccess();
		}
		return Result.newFailure(tradeResult.getError(),tradeResult.getMessage());
	}

	public Result<Boolean> compensatory(Long projectId, String userId, BigDecimal investAmount) {
		logger.info("ProjectBizService compensatory ProjectId:"+projectId +" UserId:"+userId + " RepayAmount:"+ investAmount);
		//查找还款计划
		Result<Repayment> result = repaymentBizService.detail(userId, projectId);
		if(!result.success()){
			logger.info("ProjectBizService compensatory has no Repayment. ProjectId:"+projectId +" UserId:"+userId + " RepayAmount:"+ investAmount);
			return Result.newFailure(result.getError(), result.getMessage());
		}
		//查找借款人账号
		Result<SubAccount> invSubAccount = accountService.getSubAccount(result.getObject().getLoaneeId());
		if(!invSubAccount.success()){
			logger.info("ProjectBizService compensatory has no Loanee Account. ProjectId:"+projectId +" UserId:"+userId + " LoaneeId:"+ result.getObject().getLoaneeId());
			return Result.newFailure(invSubAccount.getError(), invSubAccount.getMessage());
		}
		//查找执行人的机构信息
		Result<UserGroup> resultUserGroup = customerService.getParentUserByChildId(ShiroTool.userId());
		logger.info("ProjectBizService compensatory ProjectId:"+projectId+" UserId:"+ShiroTool.userId()+" Amount:"+investAmount+" Find Parent Info:"+resultUserGroup.success());
		//查询投资账户
		Result<SubAccount> subAccountResult = accountService.getSubAccount(resultUserGroup.getObject().getParentUserId());
		logger.info("ProjectBizService compensatory ProjectId:"+projectId+" UserId:"+ShiroTool.userId()+" Amount:"+investAmount+" Find Parent Acount:"+subAccountResult.success());

		
		Trade trade = new Trade();
		//交易金额
		trade.setTradeAmount(result.getObject().getBalance());
		//目标交易金额
		trade.setAimTradeAmount(result.getObject().getBalance());
		//出借方交易账户ID--代偿人账户id
		trade.setCreditAccount(String.valueOf(subAccountResult.getObject().getId()));
		//贷入人交易账户ID--借款人账户ID
		trade.setDebitAccount(String.valueOf(invSubAccount.getObject().getId()));
		//代偿交易标的ID==还款交易
		trade.setTradeSubjectId(String.valueOf(result.getObject().getId()));
		trade.setTradeSubjectInfo("代偿");
		trade.setTradeSource(ProjectConstants.TradeSource);
		trade.setTradeType(TradeConstants.TradeType.TRANSFER.getCode());
		trade.setTradeKind(TradeConstants.TradeKind.SINGLE.getCode());
		trade.setTradeRelation(TradeConstants.TradeRelation.ONE_MANY.getCode());
		trade.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());
		
		//执行还款交易
		Result<Trade> toFee = tradeService.createTrade(trade);
		logger.info("ProjectBizService compensatory Create Trade. ProjectId:"+projectId +" UserId:"+userId + " CreditAccount:"+ invSubAccount.getObject().getId()+" Result:"+SimpleLogFormater.formatResult(toFee));
		if(!toFee.success()){//还款失败
			logger.info("ProjectBizService compensatory Create Trade Failed. ProjectId:"+projectId +" UserId:"+userId + " CreditAccount:"+ invSubAccount.getObject().getId());
			return Result.newFailure(toFee.getError(),toFee.getMessage());
		}
		
		Result<Boolean> repay = this.repay(projectId, userId, investAmount);
		logger.info("ProjectBizService compensatory ProjectId:"+projectId +" UserId:"+userId + " RepayAmount:"+ investAmount+" do Repay:"+repay.success()+" Detail:"+SimpleLogFormater.formatResult(repay));
		return repay;
	}
	
	
	public Result<Boolean> repay(Long projectId, String userId, BigDecimal investAmount) {
		logger.info("ProjectBizService Repay ProjectId:"+projectId +" UserId:"+userId + " RepayAmount:"+ investAmount);
		Result<Repayment> result = repaymentBizService.detail(userId, projectId);
		if(!result.success()){
			logger.info("ProjectBizService Repay has no Repayment. ProjectId:"+projectId +" UserId:"+userId + " RepayAmount:"+ investAmount);
			return Result.newFailure(result.getError(), result.getMessage());
		}
		Result<Project> resultProject = projectService.getProject(result.getObject().getProjectId());
		if(!resultProject.success()){
			logger.info("ProjectBizService Repay has no Project. ProjectId:"+projectId +" UserId:"+userId + " ProjectId:"+ result.getObject().getProjectId());
			return Result.newFailure(resultProject.getError(), resultProject.getMessage());
		}
//		if(result.getObject().getBalance().compareTo(resultProject.getObject().getDueAmount())!=0){
//			return Result.newFailure("","还款凭证金额与项目还款金额不等");
//		}
		Result<SubAccount> invSubAccount = accountService.getSubAccount(result.getObject().getLoaneeId());
		if(!invSubAccount.success()){
			logger.info("ProjectBizService Repay has no Loanee Account. ProjectId:"+projectId +" UserId:"+userId + " LoaneeId:"+ result.getObject().getLoaneeId());
			return Result.newFailure(invSubAccount.getError(), invSubAccount.getMessage());
		}
		Trade trade = new Trade();
		//交易金额
		trade.setTradeAmount(result.getObject().getBalance());
		//目标交易金额
		trade.setAimTradeAmount(result.getObject().getBalance());
		//出借方交易账户ID--借款人id
		trade.setCreditAccount(String.valueOf(invSubAccount.getObject().getId()));
		//贷入人交易账户ID
		trade.setDebitAccount(ProjectConstants.PLATFORM_TEMP_ACCOUNT);
		//分润父交易ID==还款交易
		trade.setTradeSubjectId(String.valueOf(result.getObject().getId()));
		trade.setTradeSubjectInfo("还款");
		trade.setTradeSource(ProjectConstants.TradeSource);
		trade.setTradeType(TradeConstants.TradeType.REPAY.getCode());
		trade.setTradeKind(TradeConstants.TradeKind.PARENT.getCode());
		trade.setTradeRelation(TradeConstants.TradeRelation.ONE_MANY.getCode());
		trade.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());
		
		//执行还款交易
		Result<Trade> toFee = tradeService.createTrade(trade);
		logger.info("ProjectBizService Repay Create Trade. ProjectId:"+projectId +" UserId:"+userId + " CreditAccount:"+ invSubAccount.getObject().getId()+" Result:"+SimpleLogFormater.formatResult(toFee));
		if(!toFee.success()){//还款失败
			logger.info("ProjectBizService Repay Create Trade Failed. ProjectId:"+projectId +" UserId:"+userId + " CreditAccount:"+ invSubAccount.getObject().getId());
			return Result.newFailure(toFee.getError(),toFee.getMessage());
		}
		
		//更新项目状态为已还款
		Result<Boolean> upRepay = repaymentBizService.update(userId, result.getObject()	.getId(), RepaymentStatus.EXECUTED.getCode());
		logger.info("ProjectBizService Repay Update Repayment. ProjectId:"+projectId +" UserId:"+userId + " ProjectId:"+ result.getObject().getProjectId()+" Result:"+SimpleLogFormater.formatResult(upRepay));
		
		//更新项目状态为已还款
		Project project = resultProject.getObject();
		project.setStatus(ProjectConstants.ProjectStatus.REPAYED.getCode());
		project.setRepayTradeId(toFee.getObject().getId());
		Result<Boolean> resultBoolean = projectService.updateProject(project);
		logger.info("ProjectBizService Repay Update Project. ProjectId:"+projectId +" UserId:"+userId + " ProjectId:"+ result.getObject().getProjectId()+" Result:"+SimpleLogFormater.formatResult(resultBoolean));
		if(!resultBoolean.success()){
			logger.info("ProjectBizService Repay Update Project Failed. ProjectId:"+projectId +" UserId:"+userId + " ProjectId:"+ result.getObject().getProjectId());
			return Result.newFailure(resultBoolean.getError(),resultBoolean.getMessage());
		}

		return Result.newSuccess(true);
	}

	public Result<Project> createProject(String userId, Project project,Guarantee guarantee) {
		project.setStatus(ProjectConstants.ProjectStatus.PENDING_FOR_APPROVE.getCode());
		Result<Project> resultProject = projectService.createProject(project);

		guarantee.setProjectId(resultProject.getObject().getId());
		guarantee.setStatus(ProjectConstants.GuaranteeStatus.COMMITING.getCode());
		Result<Guarantee> resultGuarantee = guaranteeService.createGuarantee(guarantee);
		
		resultProject = projectService.getProject(resultProject.getObject().getId());
		Project upProject = resultProject.getObject();
		upProject.setGuaranteeLetterId(resultGuarantee.getObject().getId());
		
		Result<Boolean> update = projectService.updateProject(upProject);
		
		//锁定分润模板
		if(project.getFundingTemplateId()>0){
			Result<CommissionTemplate> funding = commissionTemplateService.getCommissionTemplate(project.getFundingTemplateId());
			logger.info(SimpleLogFormater.formatResult(funding));
			if(funding.success()){
				if(!funding.getObject().isLocked()){
					CommissionTemplate fundingUp = funding.getObject();
					fundingUp.setLocked(true);
					Result<Boolean> updateResult = commissionTemplateService.updateCommissionTemplate(fundingUp);
					logger.info(SimpleLogFormater.formatResult(updateResult));
				}
			}
		}
		//锁定分润模板
		if (project.getRepayTemplateId() > 0) {
			Result<CommissionTemplate> funding = commissionTemplateService.getCommissionTemplate(project.getRepayTemplateId());
			logger.info(SimpleLogFormater.formatResult(funding));
			if (funding.success()) {
				if(!funding.getObject().isLocked()){
					CommissionTemplate fundingUp = funding.getObject();
					fundingUp.setLocked(true);
					Result<Boolean> updateResult = commissionTemplateService.updateCommissionTemplate(fundingUp);
					logger.info(SimpleLogFormater.formatResult(updateResult));
				}
			}
		}
		return Result.newSuccess(upProject);
	}

	public Result<Pager> list(String userId, Project search, Pager pager) {
		logger.info("");
		SpecParam<Project> spec = new SpecParam<Project>();
		//处理项目名称
		spec.like("name", search.getName());
		//按照借款人用户名查询 loaneeUserName
		spec.eq("loaneeUserName", search.getLoaneeUserName());
		//处理按照担保公司查询条件
		spec.eq("guaranteeUserId", search.getGuaranteeUserId());
		//处理不同的状态信息
		if(search.getStatus() != null && !search.getStatus().equalsIgnoreCase("")){
			String[] status = search.getStatus().split(",");
			if(status.length == 1){
				spec.eq("status", status[0]);
			}else{
				List<String> l = new ArrayList<String>();
				for(String s:status){
					l.add(s);
				}
				spec.in("status", l);
			}
		}
		
		// 创建ID倒序
		pager.setProperties("id");
		pager.setDirection("desc");
		
		Result<PageObject<Project>> result = projectService.listProjectBySpecAndPage(spec, pager);
		if(result.success()){
			pager.setElements(result.getObject().getList());
			pager.init(result.getObject().getTotal());
			return Result.newSuccess(pager);
		}
//		List<ProjectDetail> details = new ArrayList<ProjectDetail>();
//		if(result.success() && result.getObject() != null){
//			for(Project proj : result.getObject().getList()){
//				details.add(this.fromProject(proj));
//			}
//			pager.setElements(details);
//			pager.init(result.getObject().getTotal());
//		}
		return Result.newFailure("", "Search is Error");
	}
	
	public Result<Pager> detailList(String userId, Project search, Pager pager) {
		logger.info("");
		SpecParam<Project> spec = new SpecParam<Project>();
		//处理项目名称
		spec.like("name", search.getName());
		//按照借款人用户名查询 loaneeUserName
		spec.eq("loaneeUserName", search.getLoaneeUserName());
		//处理按照担保公司查询条件
		spec.eq("guaranteeUserId", search.getGuaranteeUserId());
		//处理不同的状态信息
		if(search.getStatus() != null && !search.getStatus().equalsIgnoreCase("")){
			String[] status = search.getStatus().split(",");
			if(status.length == 1){
				spec.eq("status", status[0]);
			}else{
				List<String> l = new ArrayList<String>();
				for(String s:status){
					l.add(s);
				}
				spec.in("status", l);
			}
		}
		Result<PageObject<Project>> result = projectService.listProjectBySpecAndPage(spec, pager);
		List<ProjectDetail> details = new ArrayList<ProjectDetail>();
		if(result.success() && result.getObject() != null){
			for(Project proj : result.getObject().getList()){
				details.add(this.fromProject(proj));
			}
			pager.setElements(details);
			pager.init(result.getObject().getTotal());
			return Result.newSuccess(pager);
		}
		return Result.newFailure("", "Search is Error");
	}

	public Result<Project> getProject(String userId, Long id) {
		Result<Project> project = projectService.getProject(id);
		return project;
	}

	public Result<Boolean> updateProject(String userId, Project project) {
		Result<Boolean> result = projectService.updateProject(project);
		return result;
	}

	public Result<Boolean> reviewed(Long projectId, String userId, Date now,boolean pass, String reason) {
		Result<Project> projectResult = projectService.getProject(projectId);
		Project project = projectResult.getObject();
		if(pass){
			boolean flag = (now == null);
			project.setPublishDate(flag?new Date():now);
			if(flag){
				project.setStatus(ProjectStatus.PUBLISHED.getCode());
			}else{
				project.setStatus(ProjectStatus.PENDING_FOR_PUBLISH.getCode());	
			}
		}else{
			project.setStatus(ProjectStatus.REJECT_PROJECT.getCode());
		}
		Result<Boolean> update = projectService.updateProject(project);
		return update;
	}
	
	@Override
	public ProjectDetail projectDetail(Long id) {
		ProjectDetail detail = null;
		Result<Project> result = projectService.getProject(id);
		if(result.success() && result.getObject() != null){
			detail = this.fromProject(result.getObject());
		}
		return detail;
	}

	public Result<LoaneeInfo> createLoaneeInfo(LoaneeInfo info, String userId) {
		Result<LoaneeInfo> save = loaneeInfoService.create(info);
		logger.info("Create LoaneeInfo Result:"+SimpleLogFormater.formatResult(save));
		return save;
	}

	public Result<LoaneeInfo> getLoaneeInfo(Long projectId, String userId) {
		Result<LoaneeInfo> save = loaneeInfoService.detail(projectId);
		logger.info("Load LoaneeInfo Result:"+SimpleLogFormater.formatResult(save));
		return save;
	}
	
}
