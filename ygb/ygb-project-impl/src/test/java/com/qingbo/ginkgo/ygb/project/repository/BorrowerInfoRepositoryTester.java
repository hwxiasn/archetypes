package com.qingbo.ginkgo.ygb.project.repository;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.project.entity.BorrowerInfo;

public class BorrowerInfoRepositoryTester extends BaseRepositoryTester {
	@Autowired private BorrowerInfoRepository borrowerInfoRepository;
	
	@Test
	public void testAdd(){
		BorrowerInfo info = new BorrowerInfo();
		info.setName("龙吟风");
		info.setAge(36);
		info.setIdSerial("13245674894");
		info.setSex("M");
		info.setMaritalStatus(1);
		info.setPhone("13956414544");
		info.setContactQq("245896131");
		info.setContact1("weibo:齐天大圣");
		info.setContact2("weixin:齐天大圣");
		info.setAddress("的金卡嘎斯的风格将考虑");
		
		info.setLoanAmount(300);
		info.setLoanTerm(1);
		info.setPurpose("das");
		info.setReturnSource("dsafda");
		info.setBusinessScope("德康");
		info.setRegisterCapital(800);
		info.setFoundYear(2003);
		info.setFoundMonth(11);
		info.setId(21431L);
		info.setCreateTime(new Date());
		info.setStatus(1);
		
		BorrowerInfo infoR = borrowerInfoRepository.save(info);
		
		Assert.assertEquals(21431L + "", infoR.getId() + "");
	}
}
