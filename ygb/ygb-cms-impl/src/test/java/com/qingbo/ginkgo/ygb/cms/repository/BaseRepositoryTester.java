package com.qingbo.ginkgo.ygb.cms.repository;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.common.util.Pager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:cms-repository.xml"})
public class BaseRepositoryTester {
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	protected void print(Page<?> page) {
		for(Object item:page.getContent()) {
			log.info(JSON.toJSONString(item));
		}
		if(page.getNumberOfElements() == 0)
			log.info("page is empty");
	}
	
	protected void printPage(Pager pager) {
		if(pager == null || pager.getElements() == null) {
			log.info("pager is null");
			return;
		}
		if(pager.getElements().size() == 0) {
			log.info("pager is empty");
			return;
		}
		for(Object item:pager.getElements()) {
			log.info(JSON.toJSONString(item));
		}
	}
}
