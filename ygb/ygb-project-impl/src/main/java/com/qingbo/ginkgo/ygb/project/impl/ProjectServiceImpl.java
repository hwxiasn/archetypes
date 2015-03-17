package com.qingbo.ginkgo.ygb.project.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.base.service.CodeListService;
import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.base.util.PagerUtil;
import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.HttpClientUtilCommon;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.PropertiesUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserGroup;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants.Role;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.project.entity.CommissionDetail;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.project.entity.ContractInfo;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.InvestVoucherInfo;
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.project.entity.LetterInfo;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.entity.ProjectCount;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.InvestmentStatus;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.ProjectStatus;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.RepaymentStatus;
import com.qingbo.ginkgo.ygb.project.event.StatusChangeEvent;
import com.qingbo.ginkgo.ygb.project.listener.EventPublisherService;
import com.qingbo.ginkgo.ygb.project.repository.ProjectRepository;
import com.qingbo.ginkgo.ygb.project.service.CommissionTemplateService;
import com.qingbo.ginkgo.ygb.project.service.GuaranteeService;
import com.qingbo.ginkgo.ygb.project.service.InvestmentService;
import com.qingbo.ginkgo.ygb.project.service.ProjectService;
import com.qingbo.ginkgo.ygb.project.service.RepaymentService;
import com.qingbo.ginkgo.ygb.project.util.AmountCalculator;
import com.qingbo.ginkgo.ygb.project.util.ContractNoBuilder;
import com.qingbo.ginkgo.ygb.project.util.GenerateFile;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.lang.TradeConstants;
import com.qingbo.ginkgo.ygb.trade.service.TradeService;


@Service("projectService")
public class ProjectServiceImpl implements ProjectService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private ProjectRepository projectRepository;
	@Resource private QueuingService queuingService;
	@Resource private GuaranteeService guaranteeService;
	@Resource private InvestmentService investmentService;
	@Resource private CommissionTemplateService commissionTemplateService;
	@Resource private TradeService tradeService;
	@Resource EventPublisherService eventPublisherService;
	@Resource private AccountService accountService;
	@Resource private CustomerService customerService;
	@Resource private RepaymentService repaymentService;
	@Resource private CodeListService codeListService;

	@Autowired private TongjiService tongjiService;

	public Result<Project> getProject(Long id) {
		logger.info("");
		Project project = projectRepository.findOne(id);
		return Result.newSuccess(project);
	}

	public Result<PageObject<Project>> listProjectBySpecAndPage(SpecParam<Project> spec, Pager page) {
		spec.eq("deleted", false);// 未删除
		Pageable pageable = page.getDirection() == null || page.getProperties() == null ? new PageRequest(page.getCurrentPage() - 1, page.getPageSize()) :new PageRequest(page.getCurrentPage() - 1, page.getPageSize(),Direction.valueOf(page.getDirection()),page.getProperties().split(","));
		Page<Project> resultSet = projectRepository.findAll(SpecUtil.spec(spec), pageable);
		Result<PageObject<Project>> result = Result.newSuccess(new PageObject<Project>((int)resultSet.getTotalElements(), resultSet.getContent()));
		return result;
	}

	private Properties props = PropertiesUtil.get("service-http.properties", "utf-8");
	public Result<Boolean> updateProject(Project project) {
		project = projectRepository.save(project);
		if(ProjectConstants.ProjectStatus.REPAYED.getCode().equalsIgnoreCase(project.getStatus())){
			//通知事件,传递值为项目ID ==项目为已还款状态，则系统自动开始分佣过程
			StatusChangeEvent event = new StatusChangeEvent(project.getId(),StatusChangeEvent.RepayedId);
			eventPublisherService.publishEvent(event);
		}
		try{
			String httpUrl = props.getProperty("http_url");
			StringBuffer sb = new StringBuffer();
			//{ak}
			sb.append(httpUrl);
			sb.append("/E4805d16520de693a3fe707cdc962045.json");
			httpUrl = sb.toString();
			logger.info("update project notice other platform,info:"+httpUrl);
			//对外投资项目的父交易ID替换为项目ID--针对倍赢平台，项目ID通用
			Map m = JSON.parseObject(JSON.toJSON(project).toString(), Map.class);
			JSONObject json = HttpClientUtilCommon.post(httpUrl, m);
			String statusCode = json.getString("StatusCode");
			String resultSt = json.getString("Entity");
			logger.info("update project notice other platform,Result Status:"+statusCode+" Entity:"+resultSt);
		}catch(Exception e){
			logger.info("update project notice other platform error. By:"+e.getMessage());
		}
		
		return Result.newSuccess(true);
	}

	public Result<Project> createProject(Project project) {
		Result<Long> queuing = queuingService.next(ProjectConstants.PROJECT_QUEUING);
		if(queuing.getError() != null ){
			return Result.newFailure(queuing.getError(), queuing.getMessage());
		}
		project.setId(queuing.getObject());
		project = projectRepository.save(project);
		try{
			String httpUrl = props.getProperty("http_url");
			StringBuffer sb = new StringBuffer();
			//{ak}
			sb.append(httpUrl);
			sb.append("/E4805d16520de693a3fe707cdc962045.json");
			httpUrl = sb.toString();
			logger.info("create project notice other platform,info:"+httpUrl);
			//对外投资项目的父交易ID替换为项目ID--针对倍赢平台，项目ID通用
			Map m = JSON.parseObject(JSON.toJSON(project).toString(), Map.class);
			JSONObject json = HttpClientUtilCommon.post(httpUrl, m);
			String statusCode = json.getString("StatusCode");
			String resultSt = json.getString("Entity");
			logger.info("create project notice other platform,Result Status:"+statusCode+" Entity:"+resultSt);
		}catch(Exception e){
			logger.info("create project notice other platform error. By:"+e.getMessage());
		}
		return Result.newSuccess(project);
	}

	@Override
	public Result<ProjectCount> sumProject() {
		SpecParam<Project> spec = new SpecParam<Project>();
		spec.eq("deleted", false);// 未删除
		List<String> status = new ArrayList<String>();
		status.add(ProjectStatus.FUNDRAISING.getCode());
		status.add(ProjectStatus.FUNDRAISE_COMPLETE.getCode());
		status.add(ProjectStatus.REVIEWED_BY_GUARANTEE_COMPANY.getCode());
		status.add(ProjectStatus.PROJECT_DEAL.getCode());
		status.add(ProjectStatus.PROJECT_DEAL_D.getCode());
		status.add(ProjectStatus.PROJECT_REPAYING.getCode());
		status.add(ProjectStatus.REPAYED.getCode());
		status.add(ProjectStatus.COMPENSATION_REPAYING.getCode());
		status.add(ProjectStatus.COMPENSATION_REPAYED.getCode());
		status.add(ProjectStatus.BEING_JUDICIAL_PROCEDURES.getCode());
		spec.in("status", status);
		List<Project> list = projectRepository.findAll(SpecUtil.spec(spec));
		BigDecimal fundraise =BigDecimal.ZERO;
		BigDecimal repay =BigDecimal.ZERO;
		for(Project p:list){
			fundraise = fundraise.add(p.getProgressAmount()==null?BigDecimal.ZERO:p.getProgressAmount());
//			fundraise = fundraise.add(p.getSettleAmout()==null?BigDecimal.ZERO:p.getSettleAmout());//原有的计算投资累计金额方法 2015-1-20修改
			if(ProjectStatus.REPAYED.getCode().equalsIgnoreCase(p.getStatus())
					||ProjectStatus.COMPENSATION_REPAYING.getCode().equalsIgnoreCase(p.getStatus())
					||ProjectStatus.COMPENSATION_REPAYED.getCode().equalsIgnoreCase(p.getStatus())){
				repay = repay.add(p.getDueAmount()==null?BigDecimal.ZERO:p.getDueAmount());
			}
		}
		ProjectCount pc = new ProjectCount(fundraise,repay);
		return Result.newSuccess(pc);
	}

	public Result<Project> buildContract(Long id) {
		logger.info("ProjectService BuildContract,ProjectId:"+id);
		Result<Project> result = this.getProject(id);
		logger.info("ProjectService BuildContract "+SimpleLogFormater.formatResult(result));
		if(!result.success()){
			logger.info("ProjectService BuildContract,ProjectId:"+id +" Detail Failed.Error:"+result.getError()+" Message:"+result.getMessage());
			return Result.newFailure(result.getError(), result.getMessage());
		}
		Project project = result.getObject();
		
		//获取最大的担保函编号
		Pager pager = new Pager();
		pager.setPageSize(1);
		pager.init(1);
		pager.page(0);
		SqlBuilder sqlBuilder = new SqlBuilder();
		sqlBuilder.select("year,serial");
		sqlBuilder.from("guarantee");
		sqlBuilder.eq("user_id", project.getGuaranteeUserId());
		sqlBuilder.orderBy(" serial desc ");//year desc,
		sqlBuilder.limit(PagerUtil.limit(pager));
		Result<List> maxSerialResult = tongjiService.list(sqlBuilder);
		if(!maxSerialResult.success()){
			logger.info("ProjectService BuildContract,ProjectId:"+id +" MaxSerialResult Failed.Error:"+result.getError()+" Message:"+result.getMessage());
		}
		
		int year = 0;
		int serial = 0;
		if(maxSerialResult.success() && maxSerialResult.hasObject() && maxSerialResult.getObject().size() == 1){
			Object[] line =(Object[]) maxSerialResult.getObject().get(0);
			year = (Integer)line[0];
			serial = (Integer)line[1];
		}
		//2015.3.2修改合同命名规则
		if(year != ContractNoBuilder.YEAR){
			year = ContractNoBuilder.YEAR;
//			serial = 1;
//		}else{
//			serial = serial + 1;
		}
		serial = serial + 1;
		//合同文本相关
		String contractNo = ContractNoBuilder.contractNo(project.getType(),year, serial);
		String contractPath = project.getId()+"-CONTRACT.doc";
		String guaranteeNo = ContractNoBuilder.guaranteeNo(project.getType(),year, serial);
		String guaranteePath = project.getId()+"-LETTER.doc";
		Guarantee guarantee = null;
		try{
			//设置担保函相关的字段
			Result<Guarantee> resultGua = guaranteeService.getGuarantee(project.getGuaranteeLetterId());
			logger.info(SimpleLogFormater.formatResult(resultGua));
			if(!resultGua.success()){
				return Result.newFailure(resultGua.getError(), resultGua.getMessage());
			}
			guarantee = resultGua.getObject();
			guarantee.setYear(year);
			guarantee.setSerial(serial);
			guarantee.setGuaranteeContractSn(contractNo);
			guarantee.setGuaranteeContractPath(contractPath);
			guarantee.setGuaranteeLetterSn(guaranteeNo);
			guarantee.setGuaranteeLetterPath(guaranteePath);
			//实例化担保函信息
			Result<Boolean> update = guaranteeService.updateGuarantee(guarantee);
			logger.info("Guarantee Update for Contract Result:"+update.success()+" GuaranteeId:"+project.getGuaranteeLetterId());
		}catch(Exception e){
			logger.info("Guarantee Update for Contract Error.ID:"+id+" By:"+e.getMessage());
		}
		//总投资额
		BigDecimal totalFee = BigDecimal.ZERO;
		
		//设置投资查询条件
		SpecParam<Investment> spec = new SpecParam<Investment>();
		spec.eq("projectId", project.getId());
		spec.eq("status",InvestmentStatus.PENDING.getCode());
		pager.setPageSize(200);
		pager.init(200);
		pager.page(0);
		//投资列表
		Result<PageObject<Investment>> investPage = investmentService.listInvestmentBySpecAndPage(spec, pager);
		logger.info("ProjectService BuildContract List Investment "+SimpleLogFormater.formatResult(investPage));
		if(!investPage.success() || !investPage.hasObject() || investPage.getObject().getTotal()<=0){
			logger.info("ProjectService BuildContract,ProjectId:"+id +" Load Investment Failed.Error:"+investPage.getError()+" Message:"+investPage.getMessage());
			return Result.newFailure("", "has no Investment.Id:"+project.getId());
		}
		//投资人投资凭证处理
		List<Investment> invList = new ArrayList<Investment>();
		int i=0;
		for(Investment inv:investPage.getObject().getList()){
			i++;
			try{
				Result<Investment> upInvResult = investmentService.getInvestment(inv.getId());
				if(!upInvResult.success()){
					logger.info("ProjectService BuildContract,ProjectId:"+id +" Load Investment for Update Failed.Error:"+upInvResult.getError()+" Message:"+upInvResult.getMessage());
					continue;
				}
				Investment upInv = upInvResult.getObject();
//				upInv.setBalanceDue(AmountCalculator.calculate(inv.getBalance(), project.getInterestRate(), project.getPeriodDays()));
				upInv.setSettledDate(new Date());
				upInv.setDueDate(DateUtils.addDays(new Date(), project.getPeriodDays()));
				upInv.setInvestPath(inv.getId()+".doc");
				upInv.setInvestNo(ContractNoBuilder.investNo(project.getType(),year, serial, i));
				upInv.setInvestAccNo(ContractNoBuilder.investAccNo(project.getType(),year, serial));
				totalFee= totalFee.add(inv.getBalance());
				invList.add(upInv);
				Result<Boolean> update = investmentService.updateInvestment(upInv);
				logger.info("Update Investment for ContractNo .ID:"+inv.getId()+" Action:"+update.success());
			}catch(Exception e){
				logger.info("Update Investment for ContractNo Error.ID:"+inv.getId()+" By:"+e.getMessage());
			}
			
		}

		//实际金额与项目记录不一致
//		if(project.getProgressAmount().compareTo(totalFee)!= 0){
//			return Result.newFailure("","成交总额与投资总额不等");
//		}
		//设置项目相关的字段
		try{
			Result<Project> upProjectResult = this.getProject(project.getId());
			Project upProject = upProjectResult.getObject();
			upProject.setContractNo(contractNo);
			upProject.setSettleDate(new Date());
			upProject.setSettleAmout(totalFee);
			upProject.setDueDate(DateUtils.addDays(new Date(), project.getPeriodDays()));
			
			project.setContractNo(upProject.getContractNo());
			project.setSettleDate(upProject.getSettleDate());
			project.setSettleAmout(upProject.getSettleAmout());
			project.setDueDate(upProject.getDueDate());
			
			Result<Boolean> updateProject = updateProject(upProject);
			logger.info("UpdateProject By ContractNo Action DB .id:"+project.getId()+" Action:"+updateProject.success());
		}catch(Exception e){
			logger.info("updateProject By ContractNo Action DB Error.By:"+e.getMessage());
//			logger.debug("updateProject By ContractNo",e);
		}
		//还款总额设定：留到支付环节统计，需要确定还款总额是投资人分润还是按照模板设计
		
		Result<Boolean> doFile = this.buildFile(project.getId());//project, guarantee, invList
		logger.info("BuildContract for BuildFile End.Result:"+doFile.success()+" ProjectId:"+project.getId());
		
		return Result.newSuccess(project);
	}

	public Result<Project> buildProject(Long id) {
		logger.info("ProjectService BuildProject id:"+id);
		Result<Project> result = this.getProject(id);
		logger.info("Detail Project Info id:"+id+" Entity Load Action:"+result.success());
		if(!result.success()){
			logger.info("Detail Project Info id:"+id+" Entity Load Action:"+result.success()+" Error:"+result.getError()+" Message:"+result.getMessage());
			return Result.newFailure(result.getError(), result.getMessage());
		}
		Project project = result.getObject();
		SpecParam<Investment> spec = new SpecParam<Investment>();
		spec.eq("projectId", project.getId());
		spec.eq("status",InvestmentStatus.PENDING.getCode());
		Pager pager = new Pager();
		pager.setPageSize(200);
		pager.init(200);
		pager.page(0);
		//投资列表
		Result<PageObject<Investment>> investPage = investmentService.listInvestmentBySpecAndPage(spec, pager);
		if(!investPage.success() || !investPage.hasObject() || investPage.getObject().getTotal()<=0){
			logger.info("ProjectService List Investment Failed:"+investPage.success()+" Error:"+investPage.getError()+" Message:"+investPage.getMessage());
			return Result.newFailure("", "Project has no Investment,ID:"+project.getId());
		}
		logger.info("ProjectService List Investment OK,Size:"+investPage.getObject().getTotal());
		//分佣总包,总包与父交易更新状态同步处理，返回结果
		Trade total = new Trade();
		//分佣子包，纳入事件通知处理,父交易ID待填充
		List<Trade> feeList = new ArrayList<Trade>();
		//全部费用
		BigDecimal allFee=BigDecimal.ZERO;
		//还款费用
		BigDecimal payInvestFee=BigDecimal.ZERO;
		
		List<Investment> activeInv = new ArrayList<Investment>();

		//获取募集阶段的模板
		Result<CommissionTemplate> resultCommission = commissionTemplateService.getCommissionTemplate(project.getFundingTemplateId());
		if(!resultCommission.success()){
			logger.info("ProjectService Load FundingTemplate Failed:"+resultCommission.success()+" Error:"+resultCommission.getError()+" Message:"+resultCommission.getMessage());
			return Result.newFailure(result.getError(), result.getMessage());
		}
		logger.info("ProjectService Load FundingTemplate OK:Id:"+project.getFundingTemplateId());
		CommissionTemplate commission = resultCommission.getObject();
		
		//获取投资人列表进入分佣环节
		for(CommissionDetail role:commission.getDetails()){
			if(Role.GUARANTEE.getCode().equalsIgnoreCase(role.getRole())
					||Role.LOAN.getCode().equalsIgnoreCase(role.getRole())){//担保机构\小贷机构分佣
				try{
					BigDecimal fee = AmountCalculator.calculate(project.getSettleAmout(), role.getRate(), project.getPeriodDays());
					allFee = allFee.add(fee);
					Result<Guarantee> resultGua = guaranteeService.getGuarantee(project.getGuaranteeLetterId());
					Guarantee guarantee = resultGua.getObject();
					guarantee.setFee(fee);
					guaranteeService.updateGuarantee(guarantee);
					Result<SubAccount> guaSubAccount = accountService.getSubAccount(Long.valueOf(guarantee.getUserId()));
					Trade tradeFee = new Trade();
					//交易金额
					tradeFee.setTradeAmount(fee);
					//目标交易金额
					tradeFee.setAimTradeAmount(fee);
					//出借方交易账户ID
					tradeFee.setCreditAccount(ProjectConstants.PLATFORM_TEMP_ACCOUNT);
					//贷入人交易账户ID
					tradeFee.setDebitAccount(String.valueOf(guaSubAccount.getObject().getId()));
					tradeFee.setTradeSubjectId(String.valueOf(project.getId()));
					tradeFee.setTradeSubjectInfo("中间账号分佣");
					tradeFee.setTradeSource("");
					tradeFee.setTradeType(TradeConstants.TradeType.COMMISSION.getCode());
					tradeFee.setTradeKind(TradeConstants.TradeKind.SINGLE.getCode());
					tradeFee.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
					tradeFee.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());				
					feeList.add(tradeFee);
				}catch(Exception e){
					logger.info("Commission For GUARANTEE Error.ProjectId:"+id+" By:"+e.getMessage());
				}
			}else if(Role.PLATFORM.getCode().equalsIgnoreCase(role.getRole())){//平台分佣
				try{
					BigDecimal fee = AmountCalculator.calculate(project.getSettleAmout(), role.getRate(), project.getPeriodDays());
					allFee = allFee.add(fee);
					Trade tradeFee = new Trade();
					//交易金额
					tradeFee.setTradeAmount(fee);
					//目标交易金额
					tradeFee.setAimTradeAmount(fee);
					//出借方交易账户ID
					tradeFee.setCreditAccount(ProjectConstants.PLATFORM_TEMP_ACCOUNT);
					//贷入人交易账户ID
					tradeFee.setDebitAccount(ProjectConstants.PLATFORM_ACCOUNT);
					tradeFee.setTradeSubjectId(String.valueOf(project.getId()));
					tradeFee.setTradeSubjectInfo("中间账号分佣");
					tradeFee.setTradeSource("");
					tradeFee.setTradeType(TradeConstants.TradeType.COMMISSION.getCode());
					tradeFee.setTradeKind(TradeConstants.TradeKind.SINGLE.getCode());
					tradeFee.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
					tradeFee.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());				
					feeList.add(tradeFee);
				}catch(Exception e){
					logger.info("Commission For PLATFORM Error.ProjectId:"+id+" By:"+e.getMessage());
				}
			}else if(Role.SPONSOR.getCode().equalsIgnoreCase(role.getRole())
					||Role.SPONORG.getCode().equalsIgnoreCase(role.getRole())){//保荐人或者 保荐机构
				try{
					BigDecimal fee = AmountCalculator.calculate(project.getSettleAmout(), role.getRate(), project.getPeriodDays());
					allFee = allFee.add(fee);
					Result<SubAccount> guaSubAccount = accountService.getSubAccount(project.getSponsorUserId());
					Trade tradeFee = new Trade();
					//交易金额
					tradeFee.setTradeAmount(fee);
					//目标交易金额
					tradeFee.setAimTradeAmount(fee);
					//出借方交易账户ID
					tradeFee.setCreditAccount(ProjectConstants.PLATFORM_TEMP_ACCOUNT);
					//贷入人交易账户ID
					tradeFee.setDebitAccount(String.valueOf(guaSubAccount.getObject().getId()));
					tradeFee.setTradeSubjectId(String.valueOf(project.getId()));
					tradeFee.setTradeSubjectInfo("中间账号分佣");
					tradeFee.setTradeSource("");
					tradeFee.setTradeType(TradeConstants.TradeType.COMMISSION.getCode());
					tradeFee.setTradeKind(TradeConstants.TradeKind.SINGLE.getCode());
					tradeFee.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
					tradeFee.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());				
					feeList.add(tradeFee);	
				}catch(Exception e){
					logger.info("Commission For SPONSOR Error.ProjectId:"+id+" By:"+e.getMessage());
				}
			
			}else if(Role.AGENCY.getCode().equalsIgnoreCase(role.getRole())
					||Role.BROKER.getCode().equalsIgnoreCase(role.getRole())){//营销机构，与经纪人
				for(Investment inv:investPage.getObject().getList()){
					try{
						Result<UserGroup>  upLevelOne = customerService.getParentUserByChildId(inv.getInverstorId());
						logger.info("ProjectService Find Broker:InverstorId:"+inv.getInverstorId()+" Result:"+upLevelOne.success());
						if(!upLevelOne.success()){
							logger.info("ProjectService Find Broker Failed:InverstorId:"+inv.getInverstorId()+" Error:"+upLevelOne.getError()+" Message:"+upLevelOne.getMessage());
							continue;
						}
						//业务合规限制检查，第一个上级必须为经纪人
						Result<User> upLevelOneUser = customerService.getOnlyUserByIdOrUserName(upLevelOne.getObject().getParentUserId(), null);
						logger.info("ProjectService Find Broker User:BrokerId:"+upLevelOne.getObject().getParentUserId()+" Result:"+upLevelOneUser.success());
						if(!upLevelOneUser.success() || !Role.BROKER.getCode().equalsIgnoreCase(upLevelOneUser.getObject().getRole())){
							logger.info("ProjectService Find Broker User Failed:BrokerId:"+upLevelOne.getObject().getParentUserId()+" Error:"+upLevelOneUser.getError()+" Message:"+upLevelOneUser.getMessage());
							continue;
						}
						Result<UserGroup>  upLevelTwo = customerService.getParentUserByChildId(upLevelOne.getObject().getParentUserId());
						logger.info("ProjectService Find Agency :BrokerId:"+upLevelOne.getObject().getParentUserId()+" Result:"+upLevelTwo.success());
						if(!upLevelTwo.success()){
							logger.info("ProjectService Find Agency Failed:BrokerId:"+upLevelOne.getObject().getParentUserId()+" Error:"+upLevelTwo.getError()+" Message:"+upLevelTwo.getMessage());
							continue;
						}
						//业务合规限制检查，第一个上级必须为经纪公司
						Result<User> upLevelTwoUser = customerService.getOnlyUserByIdOrUserName(upLevelTwo.getObject().getParentUserId(), null);
						logger.info("ProjectService Find Agency User:AgencyId:"+upLevelTwo.getObject().getParentUserId()+" Result:"+upLevelTwoUser.success());
						if(!upLevelTwoUser.success() || !Role.AGENCY.getCode().equalsIgnoreCase(upLevelTwoUser.getObject().getRole())){
							logger.info("ProjectService Find Agency User Failed:AgencyId:"+upLevelTwo.getObject().getParentUserId()+" Error:"+upLevelTwoUser.getError()+" Message:"+upLevelTwoUser.getMessage());
							continue;
						}
						//到此属于有效投资
						activeInv.add(inv);
						if(Role.AGENCY.getCode().equalsIgnoreCase(role.getRole())){//经济公司分佣，按照每笔投资分佣
							try{
								//取得经纪人投资分润金额,仅在一个角色下进行统计，以免统计两遍
								payInvestFee = payInvestFee.add(inv.getBalanceDue()==null?BigDecimal.ZERO:inv.getBalanceDue()); 
								BigDecimal fee = AmountCalculator.calculate(inv.getBalance(), role.getRate(), project.getPeriodDays());
								allFee = allFee.add(fee);
								Result<SubAccount> agencySubAccount = accountService.getSubAccount(upLevelTwo.getObject().getParentUserId());
								logger.info("ProjectService Agency Funding "+SimpleLogFormater.formatResult(agencySubAccount));
								Trade tradeFee = new Trade();
								//交易金额
								tradeFee.setTradeAmount(fee);
								//目标交易金额
								tradeFee.setAimTradeAmount(fee);
								//出借方交易账户ID
								tradeFee.setCreditAccount(ProjectConstants.PLATFORM_TEMP_ACCOUNT);
								//贷入人交易账户ID
								tradeFee.setDebitAccount(String.valueOf(agencySubAccount.getObject().getId()));
								//源交易流水号
								tradeFee.setSourceTradeId(inv.getId());
								tradeFee.setTradeSubjectId(String.valueOf(project.getId()));
								tradeFee.setTradeSubjectInfo("中间账号分佣");
								tradeFee.setTradeSource("");
								tradeFee.setTradeType(TradeConstants.TradeType.COMMISSION.getCode());
								tradeFee.setTradeKind(TradeConstants.TradeKind.SINGLE.getCode());
								tradeFee.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
								tradeFee.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());				
								feeList.add(tradeFee);	
							}catch(Exception e){
								logger.info("ProjectService Agency Funding Error.InvestmentId:"+inv.getId()+" By:"+e.getMessage());
							}
						}else if(Role.BROKER.getCode().equalsIgnoreCase(role.getRole())){//经济人分佣，按照每笔投资分佣
							try{
								BigDecimal fee = AmountCalculator.calculate(inv.getBalance(), role.getRate(), project.getPeriodDays());
								allFee = allFee.add(fee);
								Result<SubAccount> brokerSubAccount = accountService.getSubAccount(upLevelOne.getObject().getParentUserId());
								logger.info("ProjectService Broker Funding "+SimpleLogFormater.formatResult(brokerSubAccount));
								Trade tradeFee = new Trade();
								//交易金额
								tradeFee.setTradeAmount(fee);
								//目标交易金额
								tradeFee.setAimTradeAmount(fee);
								//出借方交易账户ID
								tradeFee.setCreditAccount(ProjectConstants.PLATFORM_TEMP_ACCOUNT);
								//贷入人交易账户ID
								tradeFee.setDebitAccount(String.valueOf(brokerSubAccount.getObject().getId()));
								//源交易流水号
								tradeFee.setSourceTradeId(inv.getId());
								tradeFee.setTradeSubjectId(String.valueOf(project.getId()));
								tradeFee.setTradeSubjectInfo("中间账号分佣");
								tradeFee.setTradeSource("");
								tradeFee.setTradeType(TradeConstants.TradeType.COMMISSION.getCode());
								tradeFee.setTradeKind(TradeConstants.TradeKind.SINGLE.getCode());
								tradeFee.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
								tradeFee.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());				
								feeList.add(tradeFee);	
							}catch(Exception e){
								logger.info("ProjectService Broker Funding Error.InvestmentId:"+inv.getId()+" By:"+e.getMessage());
							}
						}
					}catch(Exception e){
						logger.info("Commission for AG or BR Error at:Investments .ID:"+inv.getId()+" By:"+e.getMessage());
					}
				}
			}
		}
		
		//借款人账户
		Result<SubAccount> loaneeSubAccount = accountService.getSubAccount(project.getLoaneeId());
		logger.info("ProjectService Load loaneeSubAccount "+SimpleLogFormater.formatResult(loaneeSubAccount));
		if(!loaneeSubAccount.success()){
			logger.info("ProjectService Load loaneeSubAccount Failed.LoaneeUserId:"+project.getLoaneeId()+" Error:"+loaneeSubAccount.getError()+" Message:"+loaneeSubAccount.getMessage());
			return Result.newFailure(loaneeSubAccount.getError(), loaneeSubAccount.getMessage());
		}
		//操作是否成功
		boolean success = true;
		//交易金额
		total.setTradeAmount(allFee);
		//目标交易金额
		total.setAimTradeAmount(allFee);
		//出借方交易账户ID
		total.setCreditAccount(String.valueOf(loaneeSubAccount.getObject().getId()));
		//贷入人交易账户ID
		total.setDebitAccount(ProjectConstants.PLATFORM_TEMP_ACCOUNT);
		total.setTradeSubjectId(String.valueOf(project.getId()));
		total.setTradeSubjectInfo("借款手续费");
		total.setTradeSource("");
		total.setTradeType(TradeConstants.TradeType.COMMISSION.getCode());
		total.setTradeKind(TradeConstants.TradeKind.SINGLE.getCode());
		total.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
		total.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());

		//生成还款记录
		Result<CommissionTemplate> resultPayCommission = commissionTemplateService.getCommissionTemplate(project.getRepayTemplateId());
		logger.info("ProjectService Load RepayCommission "+SimpleLogFormater.formatResult(resultPayCommission));
		CommissionTemplate payCommission = resultPayCommission.getObject();
		//全部还款总额
		BigDecimal allPayFee=BigDecimal.ZERO;
		try{
			for(CommissionDetail role:payCommission.getDetails()){
				logger.info("ProjectService calculate For Role:"+role.getRole()+" PayInvestFee:"+payInvestFee);
				//投资人收益
				if(Role.INVESTOR.getCode().equalsIgnoreCase(role.getRole())){
					if(BigDecimal.ZERO.compareTo(payInvestFee) == 0){
						payInvestFee = AmountCalculator.calculate(project.getSettleAmout(),project.getInterestRate(), project.getPeriodDays());
					}
					allPayFee = allPayFee.add(payInvestFee);
				}else{
					//其他分润角色收益
					BigDecimal repayFee =BigDecimal.ZERO; 
					repayFee = AmountCalculator.calculate(project.getSettleAmout(), role.getRate(), project.getPeriodDays());
					allPayFee = allPayFee.add(repayFee);
				}
			}
		}catch(Exception e){
			logger.info("ProjectService calculate All Pay Amount Error.By:"+e.getMessage());
		}
		try{
			result = this.getProject(project.getId());
			project = result.getObject();
			//设置预计还款总额，更新项目
			project.setDueAmount(project.getSettleAmout().add(allPayFee));
			Result<Boolean> update =this.updateProject(project);
			logger.info("ProjectService update Project DueAmount to EXECUTED id:"+project.getId()+" DueAmountIncrease:"+allPayFee+" Action:"+update.success());
		}catch(Exception e){
			logger.info("ProjectService Update Project DueAmount Error.Id:"+project.getId()+" DueAmountIncrease:"+allPayFee+" By:"+e.getMessage());
		}
		
		//生成还款计划
		Repayment allPay = new Repayment();
		allPay.setBalance(project.getDueAmount());
		allPay.setLoaneeId(project.getLoaneeId());
		allPay.setProjectId(project.getId());
		allPay.setIssueDate(project.getDueDate());
		allPay.setStatus(RepaymentStatus.PENDING.getCode());
		//还款计划实例化
		Result<Repayment> createResult = repaymentService.createRepayment(allPay);
		logger.info("ProjectService Create Repayment "+SimpleLogFormater.formatResult(createResult));
		
		//触发交易事件
		//取得募集父交易
		try{
			Result<Trade> resultTrade = tradeService.getTrade(project.getFundraiseTradeId());
			logger.info("ProjectService Do Trade for Load Trade,Id:"+project.getFundraiseTradeId()+" "+SimpleLogFormater.formatResult(resultTrade));
			if(!resultTrade.success()||!resultTrade.hasObject() || resultTrade.getObject()== null){
				logger.info("ProjectService Do Trade for Load Trade Failed,Id:"+project.getFundraiseTradeId()+" "+SimpleLogFormater.formatResult(resultTrade));
				success = false;
//				return Result.newFailure("","Not Found Project FundraiseTradeId is:"+project.getFundraiseTradeId());
			}
			Trade trade = resultTrade.getObject();
			//更新状态
			trade.setTradeStatus(TradeConstants.TradeStatusType.EXECUTING.getCode());
			Result<Trade> updateTrade = tradeService.updateTrade(trade);
			logger.info("Father Trade Execute Result="+updateTrade.success()+" FundraiseTradeId"+project.getFundraiseTradeId());
			if(!updateTrade.success()){ //交易失败
				logger.info("Father Trade Execute Result="+updateTrade.success()+" FundraiseTradeId"+project.getFundraiseTradeId()+" Object:"+SimpleLogFormater.formatResult(updateTrade));
				success = false;
//				return Result.newFailure("","Father Trade Execute Failed."+project.getFundraiseTradeId());
			}
		}catch(Exception e){
			logger.info("Father Trade Execute Exception FundraiseTradeId"+project.getFundraiseTradeId()+" By:"+e.getMessage());
			success = false;
//			return Result.newFailure("","项目成立交易失败");
		}
		//更新投资凭证状态 
		for(Investment inv:activeInv){
			try{
				Result<Investment> resultInv = investmentService.getInvestment(inv.getId());
				Investment upInvestment = resultInv.getObject();
				upInvestment.setStatus(InvestmentStatus.EXECUTED.getCode());
				Result<Boolean> update = investmentService.updateInvestment(upInvestment);
				logger.info("update Investment Status to EXECUTED id:"+inv.getId()+" Action:"+update.success());
			}catch(Exception e){
				logger.info("Update Investment for Status Error.ID:"+inv.getId());
			}
		}
		//完成分佣总交易
		Result<Trade> toFee = tradeService.createTrade(total);
		logger.info("ProjectService Do FeeTrade Create. "+SimpleLogFormater.formatResult(toFee));
		if(!toFee.success()){//分佣失败
			logger.info("ProjectService Do FeeTrade Create Fail. Error:"+toFee.getError()+" Message:"+toFee.getMessage());
			success = false;
//			return Result.newFailure("","分佣总额到中间账户失败");
		}
		try{
			result = this.getProject(project.getId());
			project = result.getObject();
			//记录分佣总交易ID
			project.setFundraiseFeeTradeId(toFee.getObject().getId());
			//更新项目状态为已成立
			if(success){
				project.setStatus(ProjectStatus.PROJECT_DEAL_D.getCode());
			}
			Result<Boolean> update =this.updateProject(project);
			logger.info("update Project Status to PB id:"+project.getId()+" Action:"+update.success());
		}catch(Exception e){
			logger.info("Project Built Status Change Error.ProjectId:"+project.getId()+" By:"+e.getMessage());
		}

		//更新分佣子交易的父交易
		for(Trade t:feeList){
			Long pti = 0L;
			if(toFee != null && toFee.getObject() != null && toFee.getObject().getId() != null){
				pti = toFee.getObject().getId();
			}else{
				pti = project.getId();
			}
			t.setParentTradeId(pti);
		}
		//通知事件,传递值为分佣子交易列表
		try{
			StatusChangeEvent event = new StatusChangeEvent(feeList,StatusChangeEvent.FundraiseFeeList);
			eventPublisherService.publishEvent(event);
		}catch(Exception e){
			logger.info("StatusChangeEvent.FundraiseFeeList ProjectId:"+id+" By:"+e.getMessage());
//			logger.error("StatusChangeEvent.FundraiseFeeList",e);
		}
		if(success){
			Result.newSuccess();
		}
		logger.info("ProjectService buildProject Failed by more Cause.");
		return Result.newFailure("", "ProjectService buildProject Failed by more Cause.");
	}

	public Result<Project> repayProject(Long id) {
		logger.info("ProjectService AutoSplit Project Start:ProjectId:"+id);
		Result<Project> result = this.getProject(id);
		//取得还款交易
		Result<Trade> resultTrade = tradeService.getTrade(result.getObject().getRepayTradeId());
		Trade trade = resultTrade.getObject();
		SpecParam<Investment> spec = new SpecParam<Investment>();
		spec.eq("projectId", id);
		spec.eq("status",InvestmentStatus.EXECUTED.getCode());
		Pager pager = new Pager();
		pager.setPageSize(200);
		pager.init(200);
		pager.page(0);
		//投资列表
		Result<PageObject<Investment>> investPage = investmentService.listInvestmentBySpecAndPage(spec, pager);
		//分润子包，纳入事件通知处理
		List<Trade> feeList = new ArrayList<Trade>();
		
		BigDecimal totalFee = BigDecimal.ZERO;
		for(Investment inv:investPage.getObject().getList()){
			try{
				Trade total = new Trade();
				BigDecimal fee =inv.getBalanceDue().add(inv.getBalance()); 
				totalFee = totalFee.add(fee);
				Result<SubAccount> invSubAccount = accountService.getSubAccount(inv.getInverstorId());
				//交易金额
				total.setTradeAmount(fee);
				//目标交易金额
				total.setAimTradeAmount(fee);
				//出借方交易账户ID
				total.setCreditAccount(ProjectConstants.PLATFORM_TEMP_ACCOUNT);
				//贷入人交易账户ID
				total.setDebitAccount(String.valueOf(invSubAccount.getObject().getId()));
				//分润父交易ID==还款交易
				total.setParentTradeId(trade.getId());
				total.setTradeSubjectId(String.valueOf(id));
				total.setTradeSubjectInfo("投资分润");
				total.setTradeSource(ProjectConstants.TradeSource);
				total.setTradeType(TradeConstants.TradeType.SPLIT.getCode());
				total.setTradeKind(TradeConstants.TradeKind.SINGLE.getCode());
				total.setTradeRelation(TradeConstants.TradeRelation.ONE_ONE.getCode());
				total.setTradeStatus(TradeConstants.TradeStatusType.PENDING.getCode());
				feeList.add(total);
			}catch(Exception e){
				logger.info("ProjectService AutoSplit Build Trade Error:ProjectId:"+id+" Investment:"+inv.getId()+" By:"+e.getMessage());
			}
		}
		if(trade.getTradeAmount().compareTo(totalFee)!=0){//分润金额不等
			return Result.newFailure("", "分润金额不等");
		}
		
//		//更新项目状态为分润中
		Project project = result.getObject();
//		project.setStatus(ProjectStatus.COMPENSATION_REPAYING.getCode());
//		Result<Boolean> resultUpdateProject = this.updateProject(project);
//		logger.info("ProjectService AutoSplit Update Project Status:ProjectId:"+id+" Result:"+SimpleLogFormater.formatResult(resultUpdateProject));
		
		//通知事件,传递值为分润子交易列表
		try{
			StatusChangeEvent event = new StatusChangeEvent(feeList,StatusChangeEvent.RepayFeeList);
			eventPublisherService.publishEvent(event);
		}catch(Exception e){
			logger.info("StatusChangeEvent.RepayFeeList ProjectId:"+id);
		}

		return Result.newSuccess(project);
	}
	
	public Result<Boolean> buildFile(Long id){//Project project,Guarantee guarantee,List<Investment> investments
		Result<Project> result = this.getProject(id);
		if(!result.success()){
			logger.info("ProjectService BuildFile Failed,Cause By no Project:id"+id);
			return Result.newFailure("", "Has No Project");
		}
		Project project = result.getObject();
		Result<Guarantee> resultGua = guaranteeService.getGuarantee(project.getGuaranteeLetterId());
		if(!resultGua.success()){
			logger.info("ProjectService BuildFile Failed,Cause By no Guarantee:id"+id);
			return Result.newFailure("", "Has No Guarantee");
		}
		Guarantee guarantee = resultGua.getObject();
		//设置投资查询条件
		SpecParam<Investment> spec = new SpecParam<Investment>();
		spec.eq("projectId", id);
//		spec.eq("status",InvestmentStatus.PENDING.getCode());
		Pager pager = new Pager();
		pager.setPageSize(200);
		pager.init(200);
		pager.page(0);
		//投资列表
		Result<PageObject<Investment>> investPage = investmentService.listInvestmentBySpecAndPage(spec, pager);
		if(!investPage.success()){
			logger.info("ProjectService BuildFile Failed,Cause By no Investment:id"+id);
			return Result.newFailure("", "Has No Investment");
		}
		List<Investment> investments = investPage.getObject().getList();
		if(investments == null || investments.size() <1){
			logger.info("ProjectService BuildFile Failed,Cause By no Investment:id"+id);
			return Result.newFailure("", "Has No Investment");
		}
		try{
			logger.info("BuildContract for BuildFile, ProjectId:"+project.getId());
			Result<User> resultUser = customerService.getUserByUserId(project.getLoaneeId());
			if(!resultUser.success()){
				logger.info("BuildContract for BuildFile Failed by no Loanee, ProjectId:"+project.getId());
				logger.info(SimpleLogFormater.formatResult(resultUser));
				return Result.newFailure("", "Has Not Found Project Loanee");
			}
			Result<User> resultGuaUser = customerService.getUserByUserId(Long.valueOf(project.getGuaranteeUserId()==null?"0":project.getGuaranteeUserId()));
			if(!resultGuaUser.success()){
				logger.info("BuildContract for BuildFile Failed by no GuaranteeUserId, ProjectId:"+project.getId());
				logger.info(SimpleLogFormater.formatResult(resultGuaUser));
				return Result.newFailure("", "Has Not Found Project GuaranteeUserId");
			}
			
			List<CodeList> loneTypes = codeListService.list(project.getPeriodType()).getObject();
			String dmonth = "";
			for(CodeList cl:loneTypes){
				if(cl.getCode().equalsIgnoreCase(project.getPeriod())){
					dmonth = cl.getName();
				}
			}
			
			ContractInfo ci = new ContractInfo();
			LetterInfo li = new LetterInfo();
	
			NumberFormat nt = NumberFormat.getPercentInstance();
			nt.setMinimumFractionDigits(1);
			String rateString ="";
			try{
				rateString = nt.format(project.getInterestRate().doubleValue());	
			}catch(Exception e){
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today =sdf.format(project.getSettleDate()==null?new Date():project.getSettleDate());
			Date end = project.getDueDate()==null?new Date():project.getDueDate();
			Date rend =DateUtils.addDays(end, 1);
			String endDate = sdf.format(end);
			String rendDate = sdf.format(rend);
			String[] ttoday = today.split("-");
			String syyyy = ttoday[0];
			String smm = ttoday[1];
			String sdd = ttoday[2];
			
			String[] etoday = endDate.split("-");
			String eyyyy = etoday[0];
			String emm = etoday[1];
			String edd = etoday[2];
	
			String[] retoday = rendDate.split("-");
			String reyyyy = retoday[0];
			String remm = retoday[1];
			String redd = retoday[2];
	
			ci.setContractNo(guarantee.getGuaranteeContractSn());
			ci.setDbhNo(guarantee.getGuaranteeLetterSn());
			ci.setdMoney((project.getSettleAmout()==null?BigDecimal.ZERO:project.getSettleAmout()).setScale(2,java.math.RoundingMode.HALF_EVEN).toString());
			ci.setEdd(edd);
			ci.setEmm(emm);
			ci.setEyyyy(eyyyy);
			ci.setPer(rateString);
			ci.setSdd(sdd);
			ci.setSmm(smm);
			ci.setSyyyy(syyyy);
			ci.setBrealname(resultUser.getObject().getUserProfile().getRealName());
	
			li.setComaddress(resultGuaUser.getObject().getEnterpriseProfile().getRegisterAddress());
			li.setContractNo(guarantee.getGuaranteeContractSn());
			li.setdMoney((project.getSettleAmout()==null?BigDecimal.ZERO:project.getSettleAmout()).setScale(2,java.math.RoundingMode.HALF_EVEN).toString());
			li.setDperiod(dmonth);
			li.setEdd(edd);
			li.setEmm(emm);
			li.setEyyyy(eyyyy);
			li.setEnterprise(resultGuaUser.getObject().getEnterpriseProfile().getEnterpriseName());
			li.setLegalname(resultGuaUser.getObject().getEnterpriseProfile().getLegalPersonName());
			li.setLetterNo(guarantee.getGuaranteeLetterSn());
			li.setPdd(sdd);
			li.setPmm(smm);
			li.setPyyyy(syyyy);
			li.setPer(rateString);
			li.setSdd(sdd);
			li.setSmm(smm);
			li.setSyyyy(syyyy);
			li.setBrealname(resultUser.getObject().getUserProfile().getRealName());
			li.setZipcode("");//resultUser.getObject().getEnterpriseProfile().getTaxRegistrationNo()
			
	
			Boolean[] fileOk = new Boolean[investments.size()+2];
			//investList 投资人投资凭证下载
			int i=0;
			for(Investment inv:investments){
				try{
					logger.info("BuildContract for InvestVoucherInfo start. Investment Id:"+inv.getId());
					if(inv.getInvestNo() == null || inv.getInvestAccNo() == null|| inv.getInvestPath() == null
							||"".equals(inv.getInvestNo()) || "".equals(inv.getInvestAccNo())|| "".equals(inv.getInvestPath())){
						logger.info("BuildContract for Investment not effectived. Investment Id:"+inv.getId());
						continue;
					}
					
					String fileName = inv.getInvestPath();
					InvestVoucherInfo ivi = new InvestVoucherInfo();
					ivi.setBrealname(resultUser.getObject().getUserProfile().getRealName());
					ivi.setBusername(resultUser.getObject().getUserName());
					ivi.setDperiod(dmonth);
					ivi.setRdd(redd);
					ivi.setRmm(remm);
					ivi.setRyyyy(reyyyy);
					ivi.setEnterprise(resultGuaUser.getObject().getEnterpriseProfile().getEnterpriseName());
					
					Result<User> resultInvUser = customerService.getUserByUserId(inv.getInverstorId());
					logger.info("BuildContract for InvestVoucherInfo "+SimpleLogFormater.formatResult(resultInvUser));
					if(!resultInvUser.success()){
						logger.info("BuildContract for InvestVoucherInfo failed caused by has not found investor.id:"+inv.getId());
						try{
							fileOk[i] = false;
						}catch(Exception e){
							i++;
						}
						continue;
					}
					ci.addTmoney(inv.getBalance().setScale(2,java.math.RoundingMode.HALF_EVEN).toString());
					ci.addTrlow(inv.getInvestNo());
					ci.setRflowNo(inv.getInvestAccNo());

					ivi.setIrealname(resultInvUser.getObject().getUserProfile().getRealName());
					ivi.setIusername(resultInvUser.getObject().getUserName());
					ivi.setPer(rateString);
					ivi.setSdd(sdd);
					ivi.setSmm(smm);
					ivi.setSyyyy(syyyy);
					ivi.setEdd(edd);
					ivi.setEmm(emm);
					ivi.setEyyyy(eyyyy);
					ivi.setTamount(((inv.getBalance()==null?BigDecimal.ZERO:inv.getBalance()).add((inv.getBalanceDue()==null?BigDecimal.ZERO:inv.getBalanceDue()))).setScale(2,java.math.RoundingMode.HALF_EVEN).toString());
					ivi.setTfees("0.00");
					ivi.setTmoney((inv.getBalance()==null?BigDecimal.ZERO:inv.getBalance()).setScale(2,java.math.RoundingMode.HALF_EVEN).toString());
					ivi.setTrlowno(inv.getInvestNo());
					ivi.setSerialNum(inv.getInvestAccNo());
					String resp = GenerateFile.singleDoc(project.getType(), GenerateFile.VOUCHER,fileName , ivi.parse());
					logger.info("BuildContract for BuildFile InvestVoucherInfo end. File "+fileName+" Result:"+resp);
					try{
						if("+OK".equalsIgnoreCase(resp)){
							fileOk[i] = true;
						}else{
							fileOk[i] = false;
						}
					}catch(Exception e){
					}
					i++;
				}catch(Exception e){
					logger.info("BuildContract for BuildFile:InvestVoucherInfo File Error"+e.getMessage());
				}
			}
			//设定合同及担保函文件名
			String contract =guarantee.getGuaranteeContractPath();
			String letter = guarantee.getGuaranteeLetterPath();
			String contrat_result = GenerateFile.singleDoc(project.getType(), GenerateFile.CONTRACT,contract , ci.parse());
			logger.info("BuildContract for BuildFile: File "+contract+" Result:"+contrat_result);
			if("+OK".equalsIgnoreCase(contrat_result)){
				fileOk[i+1] = true;
			}else{
				fileOk[i+1] = false;
			}
			String letter_result = GenerateFile.singleDoc(project.getType(), GenerateFile.LETTER,letter , li.parse());
			logger.info("BuildContract for BuildFile: File "+letter+" Result:"+letter_result);
			if("+OK".equalsIgnoreCase(letter_result)){
				fileOk[i+2] = true;
			}else{
				fileOk[i+2] = false;
			}
			StringBuffer sb = new StringBuffer();
			for(int site=0;site<fileOk.length;site++){
				sb.append(site).append("=").append(fileOk[site]).append("\t");
			}
			logger.info("BuildContract for BuildFile Result:"+sb.toString());
			try {
				logger.info("BuildContract for BuildFile OK.ProjectId:"+ project.getId());
				Project p = projectRepository.findOne(project.getId());
				// 合同已经完成，未完成状态为空
				p.setContractStatus("E");
				projectRepository.save(p);
				logger.info("BuildContract for BuildFile Update Status to File OK.ProjectId:"+ project.getId());
			} catch (Exception e) {
				logger.info("BuildContract for BuildFile Update Status to File OK Error.ProjectId:"+ project.getId());
			}
			return Result.newSuccess(true);
		}catch(Exception e){
			logger.error("BuildContract for BuildFile Error.caused by:"+e.getMessage());
		}
		return Result.newFailure("", "BuildContract for BuildFile Failed");
	}
}
