package com.qingbo.ginkgo.ygb.project.service;


import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.event.StatusChangeEvent;

public interface GuaranteeService {
	/**
	 * 按ID查找担保函信息
	 * @param id
	 * @return
	 */
	Result<Guarantee> getGuarantee(Long id);
	/**
	 * 按查询条件列表担保函
	 * @param spec
	 * @param page
	 * @return
	 */
	Result<PageObject<Guarantee>> listGuaranteeBySpecAndPage(SpecParam<Guarantee>spec,Pager page);
	/**
	 * 更新担保函
	 * @param guarantee
	 * @return
	 */
	Result<Boolean> updateGuarantee(Guarantee guarantee);
	/**
	 * 创建担保函
	 * @param guarantee
	 * @return
	 */
	Result<Guarantee> createGuarantee(Guarantee guarantee);
	/**
	 * 触发担保函的操作
	 * @param event
	 * @return
	 */
	Result<Boolean> notifyEvent(Long id,String status);
	
}
