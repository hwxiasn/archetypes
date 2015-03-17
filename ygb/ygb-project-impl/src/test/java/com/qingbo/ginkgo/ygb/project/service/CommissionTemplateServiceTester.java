package com.qingbo.ginkgo.ygb.project.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.project.event.StatusChangeEvent;
import com.qingbo.ginkgo.ygb.project.listener.EventPublisherService;

public class CommissionTemplateServiceTester extends BaseServiceTester {
	@Autowired CommissionTemplateService commissionTemplateService;
	@Autowired EventPublisherService eventPublisherService;
	
	@Test
	public void listByPhase() {
//		List<CommissionTemplate> list = commissionTemplateService.listCommissionTemplateByPhase("F").getObject();
//		printList(list);
//		List<CommissionTemplate> list2 = commissionTemplateService.listCommissionTemplateByPhase("P").getObject();
//		printList(list2);
		StatusChangeEvent sce = new StatusChangeEvent("",StatusChangeEvent.GUARANTEE);
		eventPublisherService.publishEvent(sce);
	}
}
