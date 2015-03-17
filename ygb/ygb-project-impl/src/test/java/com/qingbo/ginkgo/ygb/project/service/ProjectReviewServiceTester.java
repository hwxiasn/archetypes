package com.qingbo.ginkgo.ygb.project.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.project.entity.ProjectReview;

public class ProjectReviewServiceTester extends BaseServiceTester {

	@Autowired ProjectReviewService projectReviewService;
	
	@Test
	public void testQuery(){
		Result<List<ProjectReview>> result = projectReviewService.list(141225154708050002L);
		if(result.success()){
			System.out.println(JSON.toJSON(result.getObject()));
		}
	}
}
