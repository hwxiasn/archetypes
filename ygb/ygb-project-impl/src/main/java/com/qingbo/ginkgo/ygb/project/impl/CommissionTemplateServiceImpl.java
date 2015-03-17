package com.qingbo.ginkgo.ygb.project.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.CommissionDetail;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.project.repository.CommissionDetailRepository;
import com.qingbo.ginkgo.ygb.project.repository.CommissionTemplateRepository;
import com.qingbo.ginkgo.ygb.project.service.CommissionTemplateService;
import com.qingbo.ginkgo.ygb.project.enums.*;

@Service("commissionTemplateService")
public class CommissionTemplateServiceImpl implements CommissionTemplateService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private CommissionTemplateRepository commissionTemplateRepository;
	@Resource private CommissionDetailRepository commissionDetailRepository;
	@Resource private QueuingService queuingService;

	public Result<CommissionTemplate> getCommissionTemplate(Long id) {
		CommissionTemplate comm = commissionTemplateRepository.findOne(id);
		List<CommissionDetail> details = commissionDetailRepository.findByTemplateIdAndDeletedFalse(comm.getId());
		comm.setDetails(details);
		return Result.newSuccess(comm);
	}

	
	public Result<CommissionTemplate> createCommissionTemplate(CommissionTemplate commissionTemplate) {
		//保存新的模板
		commissionTemplate.setId(queuingService.next(ProjectConstants.PROJECT_QUEUING).getObject());
		commissionTemplateRepository.save(commissionTemplate);
		
		//插入角色关系
		for(CommissionDetail cd:commissionTemplate.getDetails()){
			cd.setId(queuingService.next(ProjectConstants.PROJECT_QUEUING).getObject());
			cd.setTemplateId(commissionTemplate.getId());
			commissionDetailRepository.save(cd);
		}
		
		return Result.newSuccess(commissionTemplate);
	}

	public Result<Boolean> updateCommissionTemplate(CommissionTemplate commissionTemplate) {
		CommissionTemplate old = commissionTemplateRepository.findOne(commissionTemplate.getId());
		if(old.isLocked()){
			return Result.newFailure("", "模板已锁定");
		}
		//更新模板库中的信息
		commissionTemplateRepository.save(commissionTemplate);
		//删除原系统中的全部角色关系
		List<CommissionDetail> details = commissionDetailRepository.findByTemplateIdAndDeletedFalse(commissionTemplate.getId());
		commissionDetailRepository.deleteInBatch(details);
		
		//插入新的角色关系
		for(CommissionDetail cd:commissionTemplate.getDetails()){
			cd.setId(queuingService.next(ProjectConstants.PROJECT_QUEUING).getObject());
			cd.setTemplateId(commissionTemplate.getId());
			commissionDetailRepository.save(cd);
		}
		
		return Result.newSuccess(true);
	}

	public Result<PageObject<CommissionTemplate>> listCommissionTemplateBySpecAndPage(SpecParam<CommissionTemplate> spec, Pager page) {
		spec.eq("deleted", false);// 未删除
		Pageable pageable = page.getDirection() == null || page.getProperties() == null ? new PageRequest(page.getCurrentPage() - 1, page.getPageSize()) :new PageRequest(page.getCurrentPage() - 1, page.getPageSize(),Direction.valueOf(page.getDirection()),page.getProperties().split(","));
		Page<CommissionTemplate> resultSet = commissionTemplateRepository.findAll(SpecUtil.spec(spec), pageable);
		Result<PageObject<CommissionTemplate>> result = Result.newSuccess(new PageObject<CommissionTemplate>((int)resultSet.getTotalElements(), resultSet.getContent()));
		return result;
	}


	@Override
	public Result<List<CommissionTemplate>> listCommissionTemplateByPhase(String phase) {
		SpecParam<CommissionTemplate> specs = new SpecParam<>();
		specs.eq("deleted", false);// 未删除
		specs.eq("allotPhase", phase);
		List<CommissionTemplate> findAll = commissionTemplateRepository.findAll(SpecUtil.spec(specs));
		for(CommissionTemplate temp:findAll) {
			List<CommissionDetail> details = commissionDetailRepository.findByTemplateIdAndDeletedFalse(temp.getId());
			temp.setDetails(details);
		}
		return Result.newSuccess(findAll);
	}


	public Result<Boolean> changeStatus(Long id, String status) {
		CommissionTemplate old = commissionTemplateRepository.findOne(id);
		if(old == null){
			return Result.newFailure("null", "模板不存在！");
		}else{
			if(old.isLocked()){
				return Result.newFailure("", "模板已锁定");
			}
			old.setStatus(status);
			//更新模板库中的信息
			commissionTemplateRepository.save(old);
			return Result.newSuccess(true);
		}
	}

}
