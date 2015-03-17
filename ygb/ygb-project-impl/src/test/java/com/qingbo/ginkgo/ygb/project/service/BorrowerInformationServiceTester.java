package com.qingbo.ginkgo.ygb.project.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.BorrowerInfo;

public class BorrowerInformationServiceTester extends BaseServiceTester {
	
	@Autowired private BorrowerInfoService borrowerInfoService;
	
	/**
	 * 测试分页查询文章列表
	 */
	public void pageBorrowerInfo(){
		Pager pager = new Pager();
		pager.setPageSize(2);
		SpecParam<BorrowerInfo> spec = new SpecParam<BorrowerInfo>();
		Result<PageObject<BorrowerInfo>> result = borrowerInfoService.pageBorrowerInfos(spec, pager);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			pager.init(result.getObject().getTotal());
			pager.setElements(result.getObject().getList());
			printPager(pager);
		}
	}
	
	/**
	 * 测试添加专题
	 */
	@Test
	public void addBorrowerInfo(){
		BorrowerInfo info = new BorrowerInfo();
		info.setName("1341");
		info.setAge(12);
		info.setIdSerial("21342143213421");
		info.setSex("M");
		info.setMaritalStatus(1);
		info.setPhone("1234213");
		info.setContactQq("2341234");
//		info.setContact1("weibo:齐天大圣");
//		info.setContact2("weixin:齐天大圣");
		info.setAddress("12341234");
		
		info.setLoanAmount(12);
		info.setLoanTerm(3);
		info.setPurpose("12341234");
		info.setReturnSource("34134");
		info.setBusinessScope("231");
		info.setRegisterCapital(32);
		info.setFoundYear(1233);
		info.setFoundMonth(2);
		info.setMessage("314");
		
		Result<Boolean> result = borrowerInfoService.addBorrowerInfo(info);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(result.getObject());
		}
	}
	
	/**
	 * 测试删除专题
	 */
	public void deleteBorrowerInfo(){
		// 删除
		Result<Boolean> result = borrowerInfoService.deleteBorrowerInfo(21431L);
		System.out.println(result.getObject());
	}
	
}
