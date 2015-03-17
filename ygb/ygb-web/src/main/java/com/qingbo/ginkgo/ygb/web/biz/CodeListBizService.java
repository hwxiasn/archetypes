package com.qingbo.ginkgo.ygb.web.biz;

import java.util.List;

import com.qingbo.ginkgo.ygb.web.pojo.CodeListItem;


public interface CodeListBizService {

	/** 获取省份列表 */
	List<CodeListItem> provinces();
	
	/** 获取城市列表 */
	List<CodeListItem> cities(String province);
	
	/** 获取分行列表 */
	List<CodeListItem> branchBanks();
	
	/** 获取指定Item */
	CodeListItem getListItem(String type, String code);
}
