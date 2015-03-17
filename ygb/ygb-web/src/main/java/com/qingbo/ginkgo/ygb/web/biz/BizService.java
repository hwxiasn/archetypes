package com.qingbo.ginkgo.ygb.web.biz;

import java.util.Map;

import com.qingbo.ginkgo.ygb.common.util.Pager;

public interface BizService {
	/** 投资统计报表 */
	Pager investsPage(Map<String, String> search, Pager pager);
}
