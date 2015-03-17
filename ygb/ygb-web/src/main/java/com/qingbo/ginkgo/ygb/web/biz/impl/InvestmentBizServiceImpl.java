package com.qingbo.ginkgo.ygb.web.biz.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserGroup;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.service.InvestmentService;
import com.qingbo.ginkgo.ygb.project.service.ProjectService;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.web.biz.InvestmentBizService;
import com.qingbo.ginkgo.ygb.web.biz.TradeBizService;
import com.qingbo.ginkgo.ygb.web.pojo.StatInfo;


@Service("investmentBizService")
public class InvestmentBizServiceImpl implements InvestmentBizService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private InvestmentService investmentService;
	@Resource private ProjectService projectService;
	@Resource private TongjiService tongjiService;
	@Resource private CustomerService customerService;
	@Resource private TradeBizService tradeBizService;
	@Resource private AccountService accountService;

	public Result<Investment> detail(String userId, Long id) {
		logger.info("");
		if(userId == null || id == null){
			return Result.newFailure("", "");
		}
		Result<Investment> result = investmentService.getInvestment(id);
		if(result.success()){
			Investment invShow = (Investment)result.getObject();
			Result<Project> project = projectService.getProject(result.getObject().getProjectId());
			invShow.setProject(project.getObject());
			invShow.setStatusName(result.getObject().getStatus());
			invShow.setFormatIssueDate(DateUtil.format(result.getObject().getIssueDate(), DateUtil.FormatType.DAYTIME));
			invShow.setFormatSettledDate(DateUtil.format(result.getObject().getSettledDate(), DateUtil.FormatType.DAYTIME));
			invShow.setFormatDueDate(DateUtil.format(result.getObject().getDueDate(), DateUtil.FormatType.DAYTIME));
			return Result.newSuccess(invShow);
		}
		return Result.newFailure(result.getError(), result.getMessage());
	}


	@Override
	public Result<Pager> list(String userId, Investment investment, Pager pager) {
		
		SpecParam<Investment> spec = new SpecParam<Investment>();
		if(investment.getProjectId() != 0)spec.eq("projectId", investment.getProjectId());
		if(investment.getLoaneeId() != 0)spec.eq("loaneeId", investment.getLoaneeId());
		if(investment.getInverstorId() != 0)spec.eq("inverstorId", investment.getInverstorId());
		if(investment.getStatus()!=null && !"".equals(investment.getStatus())){
			spec.eq("status", investment.getStatus());
		}
		if(investment.getStartDate()!= null && investment.getEndDate() != null){
			spec.between("settledDate", DateUtil.parse(investment.getStartDate()), DateUtil.parse(investment.getEndDate()));
		}else if(investment.getSearchDate() != null){
			Date before = DateUtils.addDays(new Date(), 0 - Integer.parseInt(investment.getSearchDate()));
			spec.between("settledDate", before, new Date());
		}
		if(investment.getInverstorId() != null && investment.getInverstorId() >0L){
			spec.eq("inverstorId", investment.getInverstorId());
		}
		
		// 排序 创建时间倒序
		pager.setProperties("id");
		pager.setDirection("desc");
		
		Result<PageObject<Investment>> result = investmentService.listInvestmentBySpecAndPage(spec, pager);
		List<Investment> list = new ArrayList<Investment>();
		if(result.success() && result.getObject() != null){
			for(Investment inv:result.getObject().getList()){
				Result<Project> project = projectService.getProject(inv.getProjectId());
				if(!project.success()){
					logger.info("Error Detail Project by Investment ID:"+inv.getId());
				}
				inv.setProject(project.getObject());
				inv.setStatusName(inv.getStatus());
				inv.setFormatIssueDate(DateUtil.format(inv.getIssueDate(), DateUtil.FormatType.DAYTIME));
				inv.setFormatSettledDate(DateUtil.format(inv.getSettledDate(), DateUtil.FormatType.DAYTIME));
				inv.setFormatDueDate(DateUtil.format(inv.getDueDate(), DateUtil.FormatType.DAYTIME));
				list.add(inv);
			}
		}
		pager.setElements(list);
		pager.init(result.getObject().getTotal());
		return Result.newSuccess(pager);
	}


	public Result<Investment> create(String userId, Investment investment) {
		logger.info("");
		Result<Investment> result = investmentService.createInvestment(investment);
		return result;
	}
	
	public Result<Boolean> update(String userId, Investment investment) {
		if(investment.getId() == null ){
			return Result.newFailure("","");
		}
		Result<Boolean> result = investmentService.updateInvestment(investment);
		return result;
	}

	public Result<StatInfo> sumUser(String userId) {
		SqlBuilder sql = new SqlBuilder("count(*) as c,sum(balance) as inv,sum(balance_due) as due","investment");
		sql.eq("inverstor_id", userId);
		sql.in("status", "P","E","I","X");
		Result<List> result = tongjiService.list(sql);
		if(result.success() && result.hasObject() ){
			StatInfo st = new StatInfo();
			Object[] info = (Object[])result.getObject().get(0);
			st.setCount(NumberUtil.parseInt(ObjectUtils.toString(info[0]), 0));
			st.setAmount(NumberUtil.parseBigDecimal(ObjectUtils.toString(info[1]), BigDecimal.ZERO));
			return Result.newSuccess(st);
		}
		return Result.newFailure("", "统计投资信息异常");
	}

	public Result<Pager> listByAgencyOrBroker(String actorId, Long userId, Pager pager) {
		logger.info("InvestmentBizService.listByAgencyOrBroker ActorId:"+actorId+" UserId:"+userId);
		Result<User> resultUser = customerService.getUserByUserId(userId);
		logger.info("InvestmentBizService.listByAgencyOrBroker ActorId:"+actorId+" UserId:"+userId+" Find User:"+resultUser.success());
		if(!resultUser.success()){
			return Result.newFailure("", "Has Not Found User By "+userId);
		}
		Result<SubAccount> resultSubAccount = accountService.getSubAccount(userId);
		logger.info("InvestmentBizService.listByAgencyOrBroker ActorId:"+actorId+" UserId:"+userId+" Find User SubAccount:"+resultSubAccount.success());
		if(!resultSubAccount.success()){
			return Result.newFailure("", "Has Not Found User SubAccount By "+userId);
		}
		Result<List<UserGroup>> resultUserGroup = null;
		if(CustomerConstants.Role.AGENCY.getCode().equalsIgnoreCase(resultUser.getObject().getRole())){
			resultUserGroup = customerService.getChildrenUserByRootId(userId);
		}else if(CustomerConstants.Role.BROKER.getCode().equalsIgnoreCase(resultUser.getObject().getRole())){
			resultUserGroup = customerService.getChildrenUserByParentId(userId);
		}
		if(resultUserGroup == null || !resultUserGroup.success()){
			return Result.newFailure("", "Has Not Children User By "+userId);
		}
		List<Long> investorList = new ArrayList<Long>();
		for(UserGroup ug:resultUserGroup.getObject()){
			investorList.add(ug.getChildUserId());
		}
		logger.info("InvestmentBizService.listByAgencyOrBroker ActorId:"+actorId+" UserId:"+userId+" Children Size:"+investorList.size());

		SpecParam<Investment> spec = new SpecParam<Investment>();
		spec.in("inverstorId", investorList);
		// 排序 创建时间倒序
		pager.setProperties("id");
		pager.setDirection("desc");
		
		Result<PageObject<Investment>> result = investmentService.listInvestmentBySpecAndPage(spec, pager);
		logger.info("InvestmentBizService.listByAgencyOrBroker ActorId:"+actorId+" UserId:"+userId+" Investment:"+result.success());
		if(!result.success()){
			return Result.newFailure("", "Has Not Investment for Investor By "+userId);
		}
		List<Investment> list = new ArrayList<Investment>();
		if(result.success() && result.getObject() != null){
			for(Investment inv:result.getObject().getList()){
//				Result<Project> project = projectService.getProject(inv.getProjectId());
//				if(!project.success()){
//					logger.info("Error Detail Project by Investment ID:"+inv.getId());
//				}
//				inv.setProject(project.getObject());
//				inv.setStatusName(inv.getStatus());
//				inv.setFormatIssueDate(DateUtil.format(inv.getIssueDate(), DateUtil.FormatType.DAYTIME));
//				inv.setFormatSettledDate(DateUtil.format(inv.getSettledDate(), DateUtil.FormatType.DAYTIME));
//				inv.setFormatDueDate(DateUtil.format(inv.getDueDate(), DateUtil.FormatType.DAYTIME));
				
				Result<User> tmpUser = customerService.getUserByUserId(inv.getInverstorId());
				if(!tmpUser.success()){
					logger.info("Error Detail User by Investment ID:"+inv.getId());
				}else{
					inv.setInvestorName(tmpUser.getObject().getUserProfile().getRealName());	
				}
				Result<Pager> resultPager = tradeBizService.detailBySourceAndDebitAccount(actorId, String.valueOf(inv.getId()),String.valueOf(resultSubAccount.getObject().getAccountId()),TradeConstants.TradeType.COMMISSION.getCode());
				if(!resultPager.success()){
					logger.info("Error Detail Commission Trade by Investment ID:"+inv.getId());
				}else{
					Trade t = (Trade)resultPager.getObject().getElements().get(0);
					inv.setCommissionFee(t.getTradeAmount());
				}
				list.add(inv);
			}
		}
		pager.setElements(list);
		pager.init(result.getObject().getTotal());
		return Result.newSuccess(pager);
	}
}
