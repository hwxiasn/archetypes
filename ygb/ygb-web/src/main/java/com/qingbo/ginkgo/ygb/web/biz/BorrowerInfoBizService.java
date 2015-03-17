package com.qingbo.ginkgo.ygb.web.biz;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.BorrowerInfo;


/**
 * 借款人信息服务
 * 提供借款人信息的查看，列表、添加等服务
 * 返回结果统一为Result对象
 * @author Kent
 *
 */
public interface BorrowerInfoBizService {
	
	/**
	 * 获得借款人对象集分页
	 * @param info 借款人信息对象
	 * @param pageSize 分页大小
	 * @return
	 */
	Pager pageInfos(BorrowerInfo info, Pager pager);
	
	/**
	 * 添加一条借款人信息
	 * @param info 借款人信息对象
	 * @return
	 */
	Result<Boolean> add(BorrowerInfo info);
	
	/**
	 * 删除一条借款人信息
	 * @param id 将要删除的对象id
	 * @return
	 */
	Result<Boolean> delete(Long id);
	
	/**
	 * 查找一条借款人信息
	 * @param id 将要删除的对象id
	 * @return
	 */
	Result<BorrowerInfo> getBorrowerInfo(Long id);
}
