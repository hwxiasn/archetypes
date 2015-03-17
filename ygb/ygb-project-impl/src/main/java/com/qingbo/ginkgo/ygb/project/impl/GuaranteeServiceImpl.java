package com.qingbo.ginkgo.ygb.project.impl;

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
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.event.StatusChangeEvent;
import com.qingbo.ginkgo.ygb.project.listener.EventPublisherService;
import com.qingbo.ginkgo.ygb.project.repository.GuaranteeRepository;
import com.qingbo.ginkgo.ygb.project.service.GuaranteeService;
import com.qingbo.ginkgo.ygb.project.enums.*;

@Service("guaranteeService")
public class GuaranteeServiceImpl implements GuaranteeService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private GuaranteeRepository guaranteeRepository;
	@Resource private QueuingService queuingService;
	@Resource EventPublisherService eventPublisherService;

	public Result<Guarantee> getGuarantee(Long id) {
		Guarantee gua = guaranteeRepository.findOne(id);
		if(gua == null){
			return Result.newFailure("","担保函不存在");
		}
		return Result.newSuccess(gua);
	}

	public Result<PageObject<Guarantee>> listGuaranteeBySpecAndPage(
			SpecParam<Guarantee> spec, Pager page) {
		spec.eq("deleted", false);// 未删除
		Pageable pageable = page.getDirection() == null || page.getProperties() == null ? new PageRequest(page.getCurrentPage() - 1, page.getPageSize()) :new PageRequest(page.getCurrentPage() - 1, page.getPageSize(),Direction.valueOf(page.getDirection()),page.getProperties().split(","));
		Page<Guarantee> resultSet = guaranteeRepository.findAll(SpecUtil.spec(spec), pageable);
		Result<PageObject<Guarantee>> result = Result.newSuccess(new PageObject<Guarantee>((int)resultSet.getTotalElements(), resultSet.getContent()));
		return result;
	}

	public Result<Boolean> updateGuarantee(Guarantee guarantee) {
		if(guarantee == null) return Result.newFailure("", "Gurantee Param is null");
		guarantee = guaranteeRepository.save(guarantee);
		if(guarantee == null){
			return Result.newFailure("","担保函不存在");
		}
		return Result.newSuccess(true);
	}

	public Result<Guarantee> createGuarantee(Guarantee guarantee) {
		Result<Long> queuing = queuingService.next(ProjectConstants.PROJECT_QUEUING);
		if(queuing.getError() != null ){
			return Result.newFailure(queuing.getError(), queuing.getMessage());
		}
		guarantee.setId(queuing.getObject());
		guaranteeRepository.save(guarantee);
		return Result.newSuccess(guarantee);
	}

	public Result<Boolean> notifyEvent(Long projectId,String status) {
		try{
			logger.info("GuaranteeService notifyEvent Start ID:"+projectId+" Status:"+status);
			Guarantee guarantee = new Guarantee();
			guarantee.setProjectId(projectId);
			guarantee.setStatus(status);
			StatusChangeEvent sce = new StatusChangeEvent(guarantee,StatusChangeEvent.GUARANTEE);
			eventPublisherService.publishEvent(sce);
			logger.info("GuaranteeService notifyEvent End ID:"+projectId+" Status:"+status);
		}catch(Exception e){
			logger.info("GuaranteeService notifyEvent Error.By:"+e.getMessage());
		}
		return Result.newSuccess(true);
	}

}
