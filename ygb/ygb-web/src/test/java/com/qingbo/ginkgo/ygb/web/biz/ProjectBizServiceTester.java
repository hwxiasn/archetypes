package com.qingbo.ginkgo.ygb.web.biz;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.common.result.Result;

public class ProjectBizServiceTester extends BaseServiceTester {
	@Autowired ProjectBizService projectBizService;
	
	@Test
	public void guarantees() {
		Result<List<CodeList>> guarantees = projectBizService.guarantees();
		System.out.println(guarantees);
	}
}
