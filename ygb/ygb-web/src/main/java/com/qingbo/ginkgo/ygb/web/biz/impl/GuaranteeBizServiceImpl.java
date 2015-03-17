package com.qingbo.ginkgo.ygb.web.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.entity.ProjectReview;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.project.event.StatusChangeEvent;
import com.qingbo.ginkgo.ygb.project.service.GuaranteeService;
import com.qingbo.ginkgo.ygb.project.service.ProjectService;
import com.qingbo.ginkgo.ygb.web.biz.GuaranteeBizService;
import com.qingbo.ginkgo.ygb.web.biz.ProjectReviewBizService;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;

@Service("guaranteeBizService")
public class GuaranteeBizServiceImpl implements GuaranteeBizService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private GuaranteeService guaranteeService;
	@Resource private ProjectService projectService;
	@Resource private CustomerService customerService;
	@Resource private ProjectReviewBizService projectReviewBizService;

	
	public Result<Guarantee> detail(String userId, Long id) {
		logger.info("Detail Guarantee UserId:" + userId+ " GuaranteeId:" + id);
		Result<Guarantee> result = guaranteeService.getGuarantee(id);
		logger.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	public Result<Guarantee> audit(Long id, String userId, String level) {
		logger.info("Audit Guarantee UserId:" + userId + " GuaranteeId:"+id+" Level:"+level);
		Result<Guarantee> result = this.detail(userId, id);
		if(!result.success()){
			logger.info("Audit Guarantee UserId:"+userId+ " GuaranteeId:"+id+" Level:"+level+" Error:"+result.getError()+" Message:"+result.getMessage());
			return Result.newFailure(result.getError(),result.getMessage());
		}
		//记录用户操作日志
		//项目审核日志
		try{
			ProjectReview pr = new ProjectReview();
			try{
				pr.setReviewerId(ShiroTool.userId()==null?0L:ShiroTool.userId());
				Result<User> actor = customerService.getUserByUserId(ShiroTool.userId());
				logger.info(SimpleLogFormater.formatResult(actor));
				if(actor.success()){
					pr.setReviewerName(actor.getObject().getUserName());
					pr.setReviewerName(actor.getObject().getUserProfile().getRealName());
				}
			}catch(Exception e){
				logger.error("CustomerService.getUserByUserId failed:by" + e.getMessage());
			}
			pr.setReviewCode("Audit Project");
			pr.setReviewName("担保函审核："+("L1".equalsIgnoreCase(level)?"一级审核":("L2".equalsIgnoreCase(level)?"二级审核":"")));
			pr.setProjectId(result.getObject().getProjectId());
			pr.setResult("通过");
			String loginId = String.valueOf(ShiroTool.userId());
			Result<ProjectReview> upReview = projectReviewBizService.create(loginId, pr);
//			logger.info(SimpleLogFormater.formatResult(upReview));

		}catch(Exception e){
			logger.info("Guarantee audit: ProjectId:"+id);
//			logger.error("Guarantee audit:", e);
		}

		//
		Guarantee guarantee = result.getObject();
		Result<Project> resultProject= projectService.getProject(guarantee.getProjectId());
		Project project = resultProject.getObject();
		//若担保函不在需要审核的两个状态则退出
		if(!guarantee.getStatus().equalsIgnoreCase(ProjectConstants.GuaranteeStatus.COMMITING.getCode()) 
				&& !guarantee.getStatus().equalsIgnoreCase(ProjectConstants.GuaranteeStatus.CHECKING.getCode())){
			String error = "";
			String message = "不是待审批的担保函";
			logger.info("Audit Guarantee UserId:"+userId+ " GuaranteeId:"+id+" Level:"+level+" Error:"+error+" Message:"+message);
			return Result.newFailure(error,message);
		}
		String oldStatus = "";
		//根据不同等级做出状态变更操作
		if(VariableConstants.GuaranteeLetterReviewLevel.LEVEL1.getCode().equalsIgnoreCase(level)
				&& ProjectConstants.GuaranteeStatus.COMMITING.getCode().equalsIgnoreCase(guarantee.getStatus())){
			oldStatus = guarantee.getStatus();
			guarantee.setStatus(ProjectConstants.GuaranteeStatus.CHECKING.getCode());
		}else if(VariableConstants.GuaranteeLetterReviewLevel.LEVEL2.getCode().equalsIgnoreCase(level)
				&& ProjectConstants.GuaranteeStatus.CHECKING.getCode().equalsIgnoreCase(guarantee.getStatus())){
			oldStatus = guarantee.getStatus();
			guarantee.setStatus(ProjectConstants.GuaranteeStatus.SIGN.getCode());
			//更新项目状态为项目成立审核
			project.setStatus(ProjectConstants.ProjectStatus.PROJECT_DEAL.getCode());
			Result<Boolean> updateProject = projectService.updateProject(project);
			logger.info(SimpleLogFormater.formatResult(updateProject)+" Project Id:"+project.getId()+" Status:"+project.getStatus());
		}else{
			String error = "";
			String message = "审批级别与担保函可审级别不符";
			logger.info("Audit Guarantee UserId:"+userId+ " GuaranteeId:"+id+" Level:"+level+" Error:"+error+" Message:"+message);
			return Result.newFailure(error,message);
		}
		logger.info("Audit Guarantee Result UserId:"+userId+ " GuaranteeId:"+id+" Level:"+level+" Before Status:"+oldStatus+" Now Status:"+guarantee.getStatus());	
		Result<Boolean> update = guaranteeService.updateGuarantee(guarantee);
		logger.info(SimpleLogFormater.formatResult(update)+" Status:"+guarantee.getStatus());
		//在此加入事件驱动，完成担保函编号，合同编号等事务
//		StatusChangeEvent sce = new StatusChangeEvent(guarantee,StatusChangeEvent.GUARANTEE);
		try{
			logger.info("NotifyEvent Start ID:"+guarantee.getProjectId()+" Status:"+guarantee.getStatus());
			guaranteeService.notifyEvent(guarantee.getProjectId(),guarantee.getStatus());
			logger.info("NotifyEvent End ID:"+guarantee.getProjectId()+" Status:"+guarantee.getStatus());
		}catch(Exception e){
			logger.info("NotifyEvent Exception ProjectId:"+id+" By:"+e.getMessage());
//			logger.error(SimpleLogFormater.formatException("通知担保函事件失败。", e));
		}
		return result;
	}

	public Result<Guarantee> create(String userId, Guarantee guarantee) {
		logger.info("Create Guarantee UserId:"+userId+ " ProjectId:"+guarantee.getProjectId());
		Result<Guarantee> result = guaranteeService.createGuarantee(guarantee);
		logger.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	public Result<Pager> list(String userId, Guarantee search,Pager page) {
		logger.info("List Guarantee UserId:"+userId);
		SpecParam<Guarantee> spec = new SpecParam<Guarantee>();
		spec.eq("userId", search.getUserId());
		spec.eq("status", search.getStatus());
		spec.like("commitmentLetterSn", search.getCommitmentLetterSn());
		spec.like("guaranteeContractSn", search.getGuaranteeContractSn());
		spec.like("guaranteeLetterSn", search.getGuaranteeLetterSn());
		
		// 创建时间倒序
		page.setProperties("createAt");
		page.setDirection("desc");
		
		Result<PageObject<Guarantee>> guaranteeList = guaranteeService.listGuaranteeBySpecAndPage(spec, page);
		logger.info(SimpleLogFormater.formatResult(guaranteeList));
		if(guaranteeList.success()){
//			List<Guarantee> list = guaranteeList.getObject().getList();
//			List<Guarantee> show = new ArrayList<Guarantee>();
//			for(Guarantee l:list){
//				Result<Project> tmpProject = projectService.getProject(l.getProjectId());
//				if(tmpProject.success()){
//					l.setProject(tmpProject.getObject());
//	//				l.setStatusName(ProjectConstants.GuaranteeStatus.getByCode(l.getStatus()).getName());
//					show.add(l);
//				}
//			}
			page.setElements(guaranteeList.getObject().getList());
			page.init(guaranteeList.getObject().getTotal());
			return Result.newSuccess(page);
		}
		return Result.newFailure(guaranteeList.getError(), guaranteeList.getMessage());
	}

	public Result<Guarantee> change(String userId, Guarantee guarantee) {
		logger.info("Change Guarantee UserId:"+userId+ " GuaranteeId:"+guarantee.getId());
		Result<Boolean> update = guaranteeService.updateGuarantee(guarantee);
		logger.info(SimpleLogFormater.formatResult(update));
		return Result.newSuccess(guarantee);
	}

}
