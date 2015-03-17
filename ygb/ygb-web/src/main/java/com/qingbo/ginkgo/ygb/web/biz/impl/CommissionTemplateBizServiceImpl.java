package com.qingbo.ginkgo.ygb.web.biz.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.project.service.CommissionTemplateService;
import com.qingbo.ginkgo.ygb.web.biz.CommissionTemplateBizService;

@Service("commissionTemplateBizService")
public class CommissionTemplateBizServiceImpl implements CommissionTemplateBizService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private CommissionTemplateService commissionTemplateService;
	
	public Result<CommissionTemplate> detail(String userId, Long id) {
		if(userId == null || id == null){
			logger.info("Param is null UserId:"+userId + " TemplateId:"+id);
			return Result.newFailure("", "");
		}
		Result<CommissionTemplate> result = commissionTemplateService.getCommissionTemplate(id);
		logger.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	public Result<CommissionTemplate> create(String userId,CommissionTemplate template) {
		Result<CommissionTemplate> result = commissionTemplateService.createCommissionTemplate(template);
		return result;
	}

	public Result<Boolean> change(String userId,CommissionTemplate template) {
		Result<CommissionTemplate> find = this.detail(userId, template.getId());
		if(find == null){
			return Result.newFailure("","");
		}
		CommissionTemplate obj = find.getObject();
		obj.setStatus(template.getStatus());
		obj.setAllotPhase(template.getAllotPhase());
		obj.setAllotType(template.getAllotType());
		obj.setName(template.getName());
		obj.setMemo(template.getMemo());
		obj.setDetails(template.getDetails());
		Result<Boolean> result = commissionTemplateService.updateCommissionTemplate(obj);
		return result;
	}

	public Result<Pager> list(String userId,CommissionTemplate search, Pager pager) {
		
		SpecParam<CommissionTemplate> spec = new SpecParam<CommissionTemplate>();
		spec.like("name", search.getName());
		spec.eq("status", search.getStatus());
		spec.eq("allotPhase", search.getAllotPhase());
		spec.eq("allotType", search.getAllotType());
		

		Result<PageObject<CommissionTemplate>> resultPage = commissionTemplateService.listCommissionTemplateBySpecAndPage(spec, pager);
		pager.setElements(resultPage.getObject().getList());
		pager.init(resultPage.getObject().getTotal());
		
		return Result.newSuccess(pager);
	}

	public Result<Boolean> changeStatus(String userId, Long id, String status) {
		logger.info("");
		Result<Boolean> result = commissionTemplateService.changeStatus(id, status);
		logger.info(SimpleLogFormater.formatResult(result));
		return result;
	}

}
