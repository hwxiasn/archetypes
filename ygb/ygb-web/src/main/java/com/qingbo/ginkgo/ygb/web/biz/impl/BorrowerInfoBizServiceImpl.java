package com.qingbo.ginkgo.ygb.web.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.BorrowerInfo;
import com.qingbo.ginkgo.ygb.project.service.BorrowerInfoService;
import com.qingbo.ginkgo.ygb.web.biz.BorrowerInfoBizService;

@Service("borrowerInfoBizService")
public class BorrowerInfoBizServiceImpl implements BorrowerInfoBizService {
	
	@Autowired private BorrowerInfoService borrowerInfoService;

	@Override
	public Pager pageInfos(BorrowerInfo info, Pager pager) {
		pager.setProperties("createTime");
		pager.setDirection("desc");
		SpecParam<BorrowerInfo> spec = new SpecParam<BorrowerInfo>();
		spec.eq("deleted", false);
		if(info != null){
			spec.eq("loanTerm", info.getLoanTerm());
			spec.like("name", info.getName());
			spec.eq("status", info.getStatus());
		}
		Result<PageObject<BorrowerInfo>> result = borrowerInfoService.pageBorrowerInfos(spec, pager);
		if(result.success()){
			pager.init(result.getObject().getTotal());
			pager.setElements(result.getObject().getList());
		}else{
			
		}
		return pager;
	}

	@Override
	public Result<Boolean> add(BorrowerInfo info) {
		return borrowerInfoService.addBorrowerInfo(info);
	}

	@Override
	public Result<Boolean> delete(Long id) {
		return borrowerInfoService.deleteBorrowerInfo(id);
	}
	
	@Override
	public Result<BorrowerInfo> getBorrowerInfo(Long id){
		return borrowerInfoService.getBorrowerInfo(id);
	}
}
