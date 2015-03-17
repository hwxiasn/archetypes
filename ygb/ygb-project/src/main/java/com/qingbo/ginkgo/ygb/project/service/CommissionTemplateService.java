package com.qingbo.ginkgo.ygb.project.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;

public interface CommissionTemplateService {
	Result<CommissionTemplate> getCommissionTemplate(Long id);
	Result<CommissionTemplate> createCommissionTemplate(CommissionTemplate commissionTemplate);
	Result<Boolean> updateCommissionTemplate(CommissionTemplate commissionTemplate);
	Result<PageObject<CommissionTemplate>> listCommissionTemplateBySpecAndPage(SpecParam<CommissionTemplate> spec,Pager page);
	Result<List<CommissionTemplate>> listCommissionTemplateByPhase(String phase);
	Result<Boolean> changeStatus(Long id,String status);
	
}
