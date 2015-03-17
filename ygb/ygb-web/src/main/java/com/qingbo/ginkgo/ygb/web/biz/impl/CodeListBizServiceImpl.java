package com.qingbo.ginkgo.ygb.web.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.base.service.CodeListService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.web.biz.CodeListBizService;
import com.qingbo.ginkgo.ygb.web.pojo.CodeListItem;

@Service("codeListBizService")
public class CodeListBizServiceImpl implements CodeListBizService {

	@Autowired private CodeListService codeListService;

	@Override
	public List<CodeListItem> provinces() {
		List<CodeListItem> items = new ArrayList<CodeListItem>();
		Result<List<CodeList>> result = codeListService.states();
		if(result.success()){
			for(CodeList codeItem : result.getObject()){
				items.add(new CodeListItem(codeItem.getCode(), codeItem.getName()));
			}
		}
		return items;
	}

	@Override
	public List<CodeListItem> cities(String province) {
		List<CodeListItem> items = new ArrayList<CodeListItem>();
		Result<List<CodeList>> result = codeListService.cities(province);
		if(result.success()){
			for(CodeList codeItem : result.getObject()){
				items.add(new CodeListItem(codeItem.getCode(), codeItem.getName()));
			}
		}
		return items;
	}

	@Override
	public List<CodeListItem> branchBanks() {
		List<CodeListItem> items = new ArrayList<CodeListItem>();
		Result<List<CodeList>> result = codeListService.banks();
		if(result.success()){
			for(CodeList codeItem : result.getObject()){
				items.add(new CodeListItem(codeItem.getCode(), codeItem.getName()));
			}
		}
		return items;
	}

	@Override
	public CodeListItem getListItem(String type, String code) {
		CodeListItem item = new CodeListItem();
		Result<CodeList> result = codeListService.item(type, code);
		if(result.success() && result.getObject() != null){
			item.code = result.getObject().getCode();
			item.name = result.getObject().getName();
		}
		return item;
	}
	
	
}
