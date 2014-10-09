package com.qingbo.project.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.qingbo.project.common.util.Pager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"classpath:repository.xml",
		"classpath:domain.xml",
		})
public class BaseTester {
	protected Log log = LogFactory.getLog(getClass());
	
	protected void print(Page<?> page) {
		for(Object item:page.getContent()) {
			log.info(JSON.toJSON(item));
		}
		if(page.getNumberOfElements() == 0)
			log.info("page is empty");
	}
	
	protected void printPage(Pager pager) {
		for(Object item:pager.getElements()) {
			log.info(JSON.toJSON(item));
		}
		if(pager.getElements().size() == 0)
			log.info("pager is empty");
	}
}
