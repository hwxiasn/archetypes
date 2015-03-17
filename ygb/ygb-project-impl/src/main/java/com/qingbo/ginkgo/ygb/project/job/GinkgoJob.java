package com.qingbo.ginkgo.ygb.project.job;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;


import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.HttpClientUtilCommon;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.PropertiesUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.InvestmentStatus;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.ProjectStatus;
import com.qingbo.ginkgo.ygb.project.service.InvestmentService;
import com.qingbo.ginkgo.ygb.project.service.ProjectService;
import com.qingbo.ginkgo.ygb.project.util.ContractNoBuilder;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.trade.service.TradeService;

//@Component("ginkgoJob")
public class GinkgoJob {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private ProjectService projectService;
	@Resource private InvestmentService investmentService;
	@Resource private TradeService tradeService;
	private int T_10 = 10;
	private int T_5 = 5;
	/**
	 * 更新项目的投资总额、总人数和投资进度信息
	 * 防止系统正常状态运行出错
	 * 问题已修正，不必须了
	 */
//	@Scheduled(cron = "1/5 * * * * ?")
	public void patchProject(){
		logger.info("Schedule Job Task Start For handle patch project.");
		SpecParam<Project> spec = new SpecParam<Project>();
		spec.eq("status", ProjectStatus.FUNDRAISING.getCode());
		Pager pager = new Pager();
		pager.init(10);
		pager.setPageSize(10);
		pager.page(0);
		Result<PageObject<Project>> result = projectService.listProjectBySpecAndPage(spec, pager);
		if (!result.success()) {
			return;
		}
		for(Project p:result.getObject().getList()){
			try{
				SpecParam<Investment> specInv = new SpecParam<Investment>();
				specInv.eq("projectId", p.getId());
				specInv.eq("status", InvestmentStatus.PENDING.getCode());
				specInv.ne("issueDate",DateUtils.addSeconds(new Date(), -5));
				Pager page = new Pager();
				page.init(200);
				page.setPageSize(200);
				page.page(0);
				Result<PageObject<Investment>>  resultPage = investmentService.listInvestmentBySpecAndPage(specInv, page);
				BigDecimal all = BigDecimal.ZERO;
				int totalUser = 0;
				for(Investment inv:resultPage.getObject().getList()){
					totalUser++;
					all = all.add(inv.getBalance());
				}
				if(p.getTotalInvestor() < totalUser){
					p.setProgressAmount(all);
					p.setTotalInvestor(totalUser);
					BigDecimal per = all.divide(p.getTotalAmount(),4,RoundingMode.HALF_EVEN);//.setScale(4,RoundingMode.HALF_EVEN);
					p.setProgress(per.multiply(BigDecimal.valueOf(100)).intValue());
					//投资额
					if(p.getProgress() == 100){
						p.setStatus(ProjectConstants.ProjectStatus.FUNDRAISE_COMPLETE.getCode());
					}
					Result<Boolean> update = projectService.updateProject(p);
					logger.info("Job Patch Project For update."+SimpleLogFormater.formatResult(update));
				}
			}catch(Exception e){
				logger.info("Job Patch Project.Id"+p.getId()+" By:"+e.getMessage());
			}
		}
		
	}
	
	/**
	 * 每分钟的第05秒执行 待发布状态更新为发布 执行间隔1分钟 异常在方法内自行判断。
	 */
	@Scheduled(cron = "05 * * * * ?")
	public void handlePendPublish() {
		try{
			logger.info("Schedule Job Task Start For handle pending publish project.");
			SpecParam<Project> spec = new SpecParam<Project>();
			spec.eq("status", ProjectStatus.PENDING_FOR_PUBLISH.getCode());
			// 过去10分钟内的未发布项目
			spec.between("publishDate", DateUtils.addMinutes(new Date(), -10),new Date());
			Pager pager = new Pager();
			pager.init(200);
			pager.setPageSize(200);
			pager.page(0);
			Result<PageObject<Project>> result = projectService.listProjectBySpecAndPage(spec, pager);
			logger.info("Handle Pending Publish Project,Result:"+result.success());
			if (!result.success()) {
				return;
			}
			for (Project project : result.getObject().getList()) {
				project.setStatus(ProjectStatus.PUBLISHED.getCode());
				Result<Boolean> flag = projectService.updateProject(project);
				logger.info("Schedule Job for Project:" + project.getId()+ " Status:" + project.getStatus() + " Result:"+ flag.success());
			}		
		}catch(Exception e){
			logger.info("GinkgoJob handlePendPublish Exception");
			logger.debug("GinkgoJob handlePendPublish Exception",e);
		}
	}

	/**
	 * 每分钟的第15秒执行 发布状态更新为募集中 执行间隔1分钟 异常在方法内自行判断。
	 */
	@Scheduled(cron = "15 * * * * ?")
	public void handlePublished() {
		try{
			logger.info("Schedule Job Task Start For handle published project to FUNDRAISING.");
			SpecParam<Project> spec = new SpecParam<Project>();
			spec.eq("status", ProjectStatus.PUBLISHED.getCode());
			// 过去10分钟内的发布项目改为募集中
			spec.between("investStartDate", DateUtils.addMinutes(new Date(), -10),	new Date());
			Pager pager = new Pager();
			pager.init(200);
			pager.setPageSize(200);
			pager.page(0);
			Result<PageObject<Project>> result = projectService.listProjectBySpecAndPage(spec, pager);
			logger.info("Handle Published for Prject,Result:"+result.success());
			if (!result.success()) {
				return;
			}
			for (Project project : result.getObject().getList()) {
				project.setStatus(ProjectStatus.FUNDRAISING.getCode());
				Result<Boolean> flag = projectService.updateProject(project);
				logger.info("Schedule Job for Project:" + project.getId()	+ " Status:" + project.getStatus() + " Result:"+ flag.success());
			}
		}catch(Exception e){
			logger.info("GinkgoJob handlePublished Exception");
			logger.debug("GinkgoJob handlePublished Exception",e);
		}

	}

	/**
	 * 每分钟的第25秒执行 处理募集中的项目的结束状态 募集中的超时后改为募集失败 若募集成功超时后未能转为募集完成的则转状态 执行间隔周期1分钟
	 * 异常在方法内自行判断。
	 */
	@Scheduled(cron = "25 * * * * ?")
	public void handleFundraising() {
		try{
			logger.info("Schedule Job Task Start For handle FUNDRAISING project to FUNDRAISE_FALURE.");
			SpecParam<Project> spec = new SpecParam<Project>();
			spec.eq("status", ProjectStatus.FUNDRAISING.getCode());
			// 过去10分钟内的超时项目改为募集失败
			spec.between("investEndDate", DateUtils.addMinutes(new Date(), -10),new Date());
			Pager pager = new Pager();
			pager.init(200);
			pager.setPageSize(200);
			pager.page(0);
			Result<PageObject<Project>> result = projectService.listProjectBySpecAndPage(spec, pager);
			logger.info("Handle FUNDRAISING for Prject,Result:"+result.success());
			if (!result.success()) {
				return;
			}
			for (Project project : result.getObject().getList()) {
				if (project.getProgressAmount().compareTo(project.getSettleTermsAmount()) < 0) {
					project.setStatus(ProjectStatus.FUNDRAISE_FALURE.getCode());
				} else if (project.getProgressAmount().compareTo(project.getSettleTermsAmount()) >= 0
						&& project.getProgressAmount().compareTo(project.getTotalAmount()) <= 0) {// 累计金额不小于满标金额，且不高于募集目标
					project.setStatus(ProjectStatus.FUNDRAISE_COMPLETE.getCode());
				}
				Result<Boolean> flag = projectService.updateProject(project);
				logger.info("Schedule Job for Project:" + project.getId()+ " Status:" + project.getStatus() + " Result:"+ flag.success());
			}
		}catch(Exception e){
			logger.info("GinkgoJob handleFundraising Exception");
			logger.debug("GinkgoJob handleFundraising Exception",e);
		}
}

	/**
	 * 每分钟的第35秒执行 处理项目成立审核中的状态，检查是否所有必要交易完成，即可转为项目成立状态，主要检查资金流向完成与否
	 * 项目成立审核中如果所有资金交易已经完成则转为项目成立 检查项目为项目是否存在分佣父交易 执行间隔周期1分钟 异常在方法内自行判断。
	 */
//	@Scheduled(cron = "35 * * * * ?")
	public void handleProjectDeal() {
		try{
			logger.info("Schedule Job Task Start For handle PROJECT_DEAL project to PROJECT_DEAL_D.");
			SpecParam<Project> spec = new SpecParam<Project>();
			spec.eq("status", ProjectStatus.PROJECT_DEAL.getCode());
			Pager pager = new Pager();
			pager.init(200);
			pager.setPageSize(200);
			pager.page(0);
			Result<PageObject<Project>> result = projectService.listProjectBySpecAndPage(spec, pager);
			logger.info("Handle PROJECT_DEAL for Prject,Result:"+result.success());
			if (!result.success()) {
				return;
			}
			for (Project project : result.getObject().getList()) {
				if (project.getFundraiseFeeTradeId() != null&& project.getFundraiseFeeTradeId() > 0) {
					project.setStatus(ProjectStatus.PROJECT_DEAL_D.getCode());
					Result<Boolean> flag = projectService.updateProject(project);
					logger.info("Schedule Job for Project:" + project.getId()+ " Status:" + project.getStatus() + " Result:"+ flag.success());
				}
			}
		}catch(Exception e){
			logger.info("GinkgoJob handleProjectDeal Exception");
			logger.debug("GinkgoJob handleProjectDeal Exception",e);
		}
	}
	
	/**
	 *  每分钟的第45秒执行 处理处于成立中（EXECUTING）的父交易状态更新。
	 *  检查其交易下的全部子交易状态若全部为成立（EXECUTED）则修改父交易状态
	 */
	@Scheduled(cron = "45 * * * * ?")
	public void handleParentTrade(){
		try{
			logger.info("Schedule Job Task Start For handle EXECUTING Parent Trade to EXECUTED.");
			SpecParam<Trade> spec = new SpecParam<Trade>();
			spec.eq("tradeStatus", TradeConstants.TradeStatusType.EXECUTING.getCode());
			spec.eq("tradeKind", TradeConstants.TradeKind.PARENT.getCode());
			Pager pager = new Pager();
			pager.init(100);
			pager.setPageSize(100);
			pager.page(0);
			Result<PageObject<Trade>> resultList = tradeService.listTradeBySpecAndPage(spec, pager);
			logger.info("Handle EXECUTING Parent Trade to EXECUTED for Prject,Result:"+resultList.success());
			if(!resultList.success()){
				return ;
			}
			for(Trade trade:resultList.getObject().getList()){
				SpecParam<Trade> specChild = new SpecParam<Trade>();
				specChild.ne("tradeStatus", TradeConstants.TradeStatusType.EXECUTED.getCode());
				specChild.eq("tradeKind", TradeConstants.TradeKind.CHILDREN.getCode());
				specChild.eq("parentTradeId", trade.getId());
				Result<PageObject<Trade>> resultListChild = tradeService.listTradeBySpecAndPage(specChild, pager);
				if (resultListChild.success() && resultListChild.hasObject()) {
					trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTED.getCode());
					Result<Trade> update = tradeService.updateTrade(trade);
					logger.info("Handle Update Trade to EXECUTED for Prject,TradeId:"+trade.getId()+" Result:"+update.success());
				}
			}
		}catch(Exception e){
			logger.info("GinkgoJob handleParentTrade Exception");
			logger.debug("GinkgoJob handleParentTrade Exception",e);
		}
	}
	/**
	 *  对已成立的项目
	 *  每天定时检测一次项目还款期限，对符合要求的项目做项目还款提醒
	 *  暂定时间为10:00:00
	 */
	@Scheduled(cron = "0 0 10 * * ?")
	public void handleNotifyEmail(){
		try{
			logger.info("Schedule Job Task Start For handle NotifyEmail to Project is PB's status.");
			SpecParam<Project> spec = new SpecParam<Project>();
			spec.eq("status", ProjectStatus.PROJECT_DEAL_D.getCode());
			Pager pager = new Pager();
			pager.init(1000);
			pager.setPageSize(1000);
			pager.page(0);
			Result<PageObject<Project>> result = projectService.listProjectBySpecAndPage(spec, pager);
			logger.info("Handle NotifyEmail for Prject,Result:"+result.success());
			if (!result.success()) {
				return;
			}
			Date t10 = DateUtils.addDays(new Date(),T_10);
			int t10Int =parseDate(t10); 
			Date t5 = DateUtils.addDays(new Date(),T_5);
			int t5Int =parseDate(t5); 
			for (Project project : result.getObject().getList()) {
				int dueDate = parseDate(project.getDueDate());
				if(dueDate == 0){
					continue;
				}
				//T-10发通知邮件
				if(t10Int == dueDate){
					notifyEmail("T10","",project);
					//T-5发通知邮件
				}else if(t5Int == dueDate){
					notifyEmail("T5","",project);
				} 
			}
		}catch(Exception e){
			logger.info("GinkgoJob handleNotifyEmail Exception");
			logger.debug("GinkgoJob handleNotifyEmail Exception",e);
		}
	}
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private int parseDate(Date date){
		if(date == null){
			return 0;
		}
		try{
			int p = Integer.parseInt(sdf.format(date));
			return p;
		}catch(Exception e){
			logger.info("handleNotifyEmail ParseDate Input:"+date);
		}
		return 0;
	}
	
	private static final String NotifyUrl= PropertiesUtil.get(ContractNoBuilder.KEY_FILE).getProperty("notify_url");
	private int notifyEmail(String type,String msg,Project project){
		logger.info("notifyEmail Url:"+NotifyUrl+" Type:"+type+" Msg:"+msg+" Id:"+project.getId());
		JSONObject json = HttpClientUtilCommon.post(NotifyUrl+"?id="+project.getId()+"&type="+type, null);
		logger.info("notifyEmail Url:"+NotifyUrl+" Type:"+type+" Msg:"+msg+" Id:"+project.getId()+" Result:"+json.toJSONString());
		return 0;
	}
}
