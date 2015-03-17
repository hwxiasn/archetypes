package com.qingbo.ginkgo.ygb.project.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.project.entity.Repayment;

public class RepaymentServiceTester extends BaseServiceTester {

	@Autowired RepaymentService repaymentService;
	
	@Test
	public void testQuery(){
		Result<Repayment> result = repaymentService.getRepayment(123456L);
		if(result.success()){
			System.out.println(JSON.toJSON(result.getObject()));
		}
	}
}
