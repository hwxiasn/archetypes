package com.qingbo.ginkgo.ygb.base;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:consumer.xml" })
public class Consumer {
	@Autowired TongjiService tongjiService;
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	protected void printList(List<?> list) {
		if(list==null || list.size()==0) return;
		
		for(Object item:list) {
			log.info(JSON.toJSONString(item));
		}
		log.info("list size="+list.size());
	}
	
	protected void printPage(Page<?> page) {
		if(page==null || page.getNumberOfElements()==0) {
			log.info("page is empty or null");
			return;
		}
		printList(page.getContent());
	}
	
	protected void printPager(Pager pager) {
		if(pager==null || pager.getElements()==null || pager.getElements().size()==0) {
			log.info("pager is empty or null");
			return;
		}
		printList(pager.getElements());
	}	
	
	@Test
	public void tongji() {
		Pager pager = new Pager();
		pager.setPageSize(2);
		//select count(*) from user
		//select user.id,user_name,role,qdd.money_more_more_id from sub_account sub left join sub_qdd_account qdd on sub.id=qdd.sub_account_id left join user on user.id=sub.account_id limit 0,5
		SqlBuilder sqlBuilder = new SqlBuilder()
		.select("count(*)")
		.from("sub_account sub left join sub_qdd_account qdd on sub.id=qdd.sub_account_id left join user on user.id=sub.account_id");
		Result<Integer> count = tongjiService.count(sqlBuilder);
		System.out.println(count);
		pager.init(count.getObject());
		
		sqlBuilder.select("user.id,user_name,role,qdd.money_more_more_id")
		.limit(pager.getStartRow()+","+pager.getPageSize());
		Result<List> list = tongjiService.list(sqlBuilder);
		pager.setElements(list.getObject());
		
		printPager(pager);
		
		if(pager.getTotalPages()>1) {
			pager.page(2);
			if(pager.notInitialized()) {
				sqlBuilder.select("count(*)");
				count = tongjiService.count(sqlBuilder);
				System.out.println(count);
				pager.init(count.getObject());
			}
			sqlBuilder.select("user.id,user_name,role,qdd.money_more_more_id")
			.limit(pager.getStartRow()+","+pager.getPageSize());
			list = tongjiService.list(sqlBuilder);
			pager.setElements(list.getObject());
			pager.setElements(list.getObject());
			
			printPager(pager);
		}
	}
}
