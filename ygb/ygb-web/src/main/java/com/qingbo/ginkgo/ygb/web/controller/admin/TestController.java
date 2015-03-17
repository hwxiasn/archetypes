package com.qingbo.ginkgo.ygb.web.controller.admin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.MailUtil;
import com.qingbo.ginkgo.ygb.common.util.PropertiesUtil;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.event.StatusChangeEvent;
import com.qingbo.ginkgo.ygb.project.service.GuaranteeService;
import com.qingbo.ginkgo.ygb.project.service.ProjectService;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.trade.service.DealService;
import com.qingbo.ginkgo.ygb.trade.service.TradeService;
import com.qingbo.ginkgo.ygb.web.biz.GuaranteeBizService;
import com.qingbo.ginkgo.ygb.web.biz.InvestmentBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectReviewBizService;
import com.qingbo.ginkgo.ygb.web.biz.RepaymentBizService;
import com.qingbo.ginkgo.ygb.web.util.AssistInfoUtil;
import com.qingbo.ginkgo.ygb.web.util.VelocityUtil;

@Controller
public class TestController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource private GuaranteeService guaranteeService;
	@Resource private ProjectService projectService;
	@Resource private DealService dealService;
	@Resource private TradeService tradeService;
	@Autowired private ProjectBizService projectBizService;
	@Autowired private CustomerService customerService;

//	@Autowired private CustomerService customerService;
//	@Autowired private UploadImageService uploadImageService;
//	@Autowired private TongjiService tongjiService;
//	@Autowired private CodeListService codeListService;
//	@Resource private CommissionTemplateService commissionTemplateService;
//	@Resource private AccountService accountService;
//	@Resource private InvestmentBizService investmentBizService;
//	@Resource private TradeService tradeService;
//	@Resource private RepaymentBizService repaymentBizService;

//	@Resource private InvestmentRepository investmentRepository;
//	@Resource private ProjectRepository projectRepository;
//	@Resource private TradeRepository tradeRepository;

	@RequestMapping("manTrade")
	public String manTrade(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		double investAmount = RequestUtil.getDoubleParam(request, "investAmount", 0.00);
		String creditAccount = RequestUtil.getStringParam(request, "creditAccount", null);
		String debitAccount = RequestUtil.getStringParam(request, "DebitAccount", null);
		if(investAmount ==0.00 || creditAccount== null || debitAccount == null){
        	logger.info("Man Trade info is null");
			return "redirect:login.html";
		}
		String user = RequestUtil.getStringParam(request, "user_name", "");
		String password = RequestUtil.getStringParam(request, "password", "");
		if("".equals(user) || "".equals(password) || !"admin".equalsIgnoreCase(user)){
        	logger.info("Man Trade auth is null");
			return "redirect:login.html";
		}
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(new UsernamePasswordToken(user, password));
        } catch (Exception e) {
        	logger.info("Man Trade UserLogin Error.BY:"+e.getMessage());
        	return "redirect:login.html";
        }
		if(!subject.isAuthenticated()) {
			return "redirect:login.html";
		}
		Trade trade = new Trade();
		//交易金额
		trade.setTradeAmount(BigDecimal.valueOf(investAmount));
		//目标交易金额
		trade.setAimTradeAmount(BigDecimal.valueOf(investAmount));
		//出借方交易账户ID
		trade.setCreditAccount(creditAccount);
		//借款人交易账户ID
		trade.setDebitAccount(debitAccount);
		trade.setTradeSubjectInfo("手工凭证");
		trade.setTradeSource("");
		trade.setTradeType(TradeConstants.TradeType.COMMISSION.getCode());
		trade.setTradeKind(TradeConstants.TradeKind.CHILDREN.getCode());
		trade.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
		trade.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());

		//发起一个交易
		try{
			Result<Trade> tradeResult = tradeService.createTrade(trade);
			logger.info("TestController Man Trade Info:investAmount:"+investAmount+" creditAccount:"+creditAccount+" debitAccount:"+debitAccount+" tradeResult:"+tradeResult.success()+" Result:"+SimpleLogFormater.formatResult(tradeResult));
		}catch(Exception e){
			e.printStackTrace();
		}
		return "redirect:index.html";
	}
	
	@RequestMapping("manDeal")
	public String manDeal(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		Long dealId = RequestUtil.getLongParam(request, "id", 0L);
		if(dealId ==0L){
			return "redirect:login.html";
		}
		String user = RequestUtil.getStringParam(request, "user_name", "");
		String password = RequestUtil.getStringParam(request, "password", "");
		if("".equals(user) || "".equals(password) || !"admin".equalsIgnoreCase(user)){
			return "redirect:login.html";
		}
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(new UsernamePasswordToken(user, password));
        } catch (Exception e) {
        	logger.info("Man Deal UserLogin Error.BY:"+e.getMessage());
        	return "redirect:login.html";
        }
		if(!subject.isAuthenticated()) {
			return "redirect:login.html";
		}
		Result<Boolean> result = dealService.dealExecute(dealId);
		logger.info("Man Deal Result:"+result.success()+" DealId:"+dealId);
		return "redirect:index.html";
	}
	
	@RequestMapping("manEvent")
	public String manEvent(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		Long projectId = RequestUtil.getLongParam(request, "id", 0L);
		String status = RequestUtil.getStringParam(request, "status", "");
		if(projectId ==0L || "".equalsIgnoreCase(status)){
			return "redirect:login.html";
		}
		String user = RequestUtil.getStringParam(request, "user_name", "");
		String password = RequestUtil.getStringParam(request, "password", "");
		if("".equals(user) || "".equals(password) || !"admin".equalsIgnoreCase(user)){
			return "redirect:login.html";
		}
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(new UsernamePasswordToken(user, password));
        } catch (Exception e) {
        	logger.info("Man Event UserLogin Error.BY:"+e.getMessage());
        	return "redirect:login.html";
        }
		if(!subject.isAuthenticated()) {
			return "redirect:login.html";
		}
		try{
			guaranteeService.notifyEvent(projectId,status);
			logger.info("Man Event Notify ProjectId:"+projectId+" Status:"+status);
		}catch(Exception e){
			logger.error(SimpleLogFormater.formatException("通知担保函事件失败。", e));
		}
		return "redirect:index.html";
	}
	
	@RequestMapping("manFile")
	public String manFile(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		Long projectId = RequestUtil.getLongParam(request, "id", 0L);
		String status = RequestUtil.getStringParam(request, "status", "");
		if(projectId ==0L || "".equalsIgnoreCase(status)){
			
		}
		String user = RequestUtil.getStringParam(request, "user_name", "");
		String password = RequestUtil.getStringParam(request, "password", "");
		if("".equals(user) || "".equals(password) || !"admin".equalsIgnoreCase(user)){
			return "redirect:login.html";
		}
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(new UsernamePasswordToken(user, password));
        } catch (Exception e) {
        	logger.info("Man File UserLogin Error.BY:"+e.getMessage());
        	return "redirect:login.html";
        }
		if(!subject.isAuthenticated()) {
			return "redirect:login.html";
		}
		Result<Boolean> result = projectService.buildFile(projectId);
		logger.info("Man File BuildFile ProjectId:"+projectId+" Result:"+result.success());
		return "redirect:index.html";
	}	
	
	
	
	@RequestMapping("notifyEmail")
	public String notifyEmail(HttpServletRequest request){
		String type = RequestUtil.getStringParam(request, "type", "");
		Long id = RequestUtil.getLongParam(request, "id", 0L);
		logger.info("notifyEmail input type:"+type+" ProjectId:"+id);
		if(id == 0L){
			return "";
		}
		int step = notifyEmail(type,id);
		logger.info("notifyEmail input type:"+type+" ProjectId:"+id+" Step:"+step);
		return "index";
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
	private String parseDate(Date date){
		if(date == null){
			return "";
		}
		try{
			return sdf.format(date);
		}catch(Exception e){
			logger.info("notifyEmail parseDate Input:"+date);
		}
		return "";
	}
	
	private static final String managers= PropertiesUtil.get("ginkgo.properties").getProperty("managers");
	private int notifyEmail(String type,Long id){
		String veloTemp ="notifyEmail_loanee.vm";
		Result<Project> resultProject = projectBizService.getProject("Job", id);
		if(!resultProject.success()){
			return -1;
		}
		Map<String, Object> map = new HashMap<>();
		Result<User> resultUser = customerService.getUserByUserId(resultProject.getObject().getLoaneeId());
		if(!resultUser.success()){
			return -2;
		}
		Project project = resultProject.getObject();
		project.setLoaneeRealName(resultUser.getObject().getUserProfile().getRealName());
		map.put("Build_Date", parseDate(project.getSettleDate()));
		map.put("T_Date", parseDate(project.getDueDate()));
		map.put("T1_Date",parseDate(DateUtils.addDays(project.getDueDate(),-1)));
		map.put("Project", project);
		
//		if("T10".equalsIgnoreCase(type)){
//		}else if("T5".equalsIgnoreCase(type)){
//		}
		String html = VelocityUtil.merge(veloTemp, map);
		boolean flag = MailUtil.sendHtmlEmail(resultUser.getObject().getUserProfile().getEmail(),html,"财富箱还款提醒邮件");
		logger.info("notifyEmail input type:"+type+" ProjectId:"+id+" Notify Loanee Result:"+flag);
		
		if(managers == null){
			return -3;
		}
		String[] noties = managers.split(",");
		if(noties == null || noties.length<1){
			return -4;
		}
		String veloTempManager ="notifyEmail_manager.vm";
		for(String single:noties){
			try{
				String[] info = single.split(":");
				map.put("Manager_Name", info[0]);
				String note = VelocityUtil.merge(veloTempManager, map);
				boolean noteFlag = MailUtil.sendHtmlEmail(info[1],note,"财富箱还款提醒邮件");
				logger.info("notifyEmail input type:"+type+" ProjectId:"+id+" Notify Manager Email:"+info[1]+" Result:"+noteFlag);
			}catch(Exception e){
				logger.info("notifyEmail input type:"+type+" ProjectId:"+id+" Notify Manager Error. By:"+e.getMessage());
			}
		}
		return 0;
	}

	
	/**
//	@RequestMapping("autoEvent")
	public String autoEvent(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		//此方法自动执行事件驱动，生成项目审核一级和二级操作
		List<Project> list = projectRepository.findAll();
//		for(Project p:list){
//			try{
//				this.runEvent(p.getId(), "CH");
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
		for(Project p:list){
			try{
				this.runEvent(p.getId(), "PS");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return "redirect:index.html";
	}	
	
	private void runEvent(Long projectId,String status){
		Guarantee guarantee = new Guarantee();
		guarantee.setProjectId(projectId);
		guarantee.setStatus(status);
		StatusChangeEvent sce = new StatusChangeEvent(guarantee,StatusChangeEvent.GUARANTEE);
		try{
			guaranteeService.notifyEvent(sce);
		}catch(Exception e){
			logger.error(SimpleLogFormater.formatException("通知担保函事件失败。", e));
		}
		
	}
	
//	@RequestMapping("manPatch")
	public String manPatch(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		//此方法用于系统上线后的自动由投资建立交易的过程
		Long projectId = RequestUtil.getLongParam(request, "id", 0L);
		if(projectId > 0){
			this.runPatch(projectId);
		}else{
			List<Project> list = projectRepository.findAll();
			for(Project p:list){
				try{
					this.runPatch(p.getId());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return "redirect:index.html";
	}
	
	
	private void runPatch(Long projectId){
		List<Investment> listInvest = investmentRepository.findByProjectIdAndDeletedFalse(projectId);
		Result<Project> resultProject = projectService.getProject(projectId);
		Project project = resultProject.getObject();
		//产品的募集父交易不存在，则创建父交易
		if(resultProject.getObject().getFundraiseTradeId() == null ||resultProject.getObject().getFundraiseTradeId() == 0){
			Trade pTrade = new Trade();
			//目标交易金额
			pTrade.setAimTradeAmount(resultProject.getObject().getTotalAmount());
			//出借方交易账户ID
			pTrade.setCreditAccount("");
			//借款人交易账户ID
			pTrade.setDebitAccount(String.valueOf(resultProject.getObject().getLoaneeId()));
			pTrade.setTradeSubjectId(String.valueOf(resultProject.getObject().getId()));
			pTrade.setTradeSubjectInfo("募集父交易");
			pTrade.setTradeSource("");
			pTrade.setTradeType(TradeConstants.TradeType.INVEST.getCode());
			pTrade.setTradeKind(TradeConstants.TradeKind.PARENT.getCode());
			pTrade.setTradeRelation(TradeConstants.TradeRelation.MANY_ONE.getCode());
			pTrade.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());
			Result<Trade> pTradeResult = tradeService.createTrade(pTrade);
			if(pTradeResult.success()){
				project = resultProject.getObject();
				project.setFundraiseTradeId(pTradeResult.getObject().getId());
				projectService.updateProject(project);
			}
		}
		for(Investment inv:listInvest){
			Trade trade = new Trade();
			//交易金额
			trade.setTradeAmount(inv.getBalance());
			//目标交易金额
			trade.setAimTradeAmount(inv.getBalance());
			//出借方交易账户ID
			trade.setCreditAccount(String.valueOf(inv.getInverstorId()));
			//借款人交易账户ID
			trade.setDebitAccount(String.valueOf(resultProject.getObject().getLoaneeId()));
			//设定投资交易的父交易
			trade.setParentTradeId(resultProject.getObject().getFundraiseTradeId());
			trade.setTradeSubjectId(String.valueOf(inv.getId()));
			trade.setTradeSubjectInfo("投资凭证");
			trade.setTradeSource("");
			trade.setTradeType(TradeConstants.TradeType.INVEST.getCode());
			trade.setTradeKind(TradeConstants.TradeKind.CHILDREN.getCode());
			trade.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
			trade.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());
			//发起一个交易
			Result<Trade> tradeResult = tradeService.createTrade(trade);
			//交易执行成功
			if(tradeResult.success()){
				//更新投资凭证
				try{
					Investment newInvest = investmentRepository.findOne(inv.getId());
					newInvest.setStatus(ProjectConstants.InvestmentStatus.PENDING.getCode());
					newInvest.setBalanceDue(AmountCalculator.calculate(inv.getBalance(), project.getInterestRate(), resultProject.getObject().getPeriodDays()));
					newInvest.setTradeId(tradeResult.getObject().getId());
					investmentRepository.save(newInvest);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
//	@RequestMapping("autoStatus")
	public String autoStatus(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		List<Project> list = projectRepository.findAll();
		for(Project p:list){
			if(ProjectStatus.PROJECT_DEAL.getCode().equalsIgnoreCase(p.getStatus())
					||ProjectStatus.PROJECT_DEAL_D.getCode().equalsIgnoreCase(p.getStatus())
					||ProjectStatus.REPAYED.getCode().equalsIgnoreCase(p.getStatus())){
				List<Investment> listInv = investmentRepository.findByProjectIdAndDeletedFalse(p.getId());
				for(Investment inv:listInv){
					try{
						BigDecimal due = AmountCalculator.calculate(inv.getBalance(), p.getInterestRate(), p.getPeriodDays());
						inv.setBalanceDue(due);
						investmentRepository.save(inv);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		return "redirect:index.html";
	} 
	
//	@RequestMapping("autoTrade")
	public String autoTrade(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try{
			List<Trade> list = tradeRepository.findAll();
			for(Trade tr:list){
				try{dealService.execute(tr);}catch(Exception e){e.printStackTrace();}
				
			}
		}catch(Exception e){e.printStackTrace();}
		return "redirect:index.html";
	}
	
	//*/
}
