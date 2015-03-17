package com.qingbo.ginkgo.ygb.web.biz;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;

public interface CommissionTemplateBizService {
	
	/**
	 * 查询一个分佣模板的详细信息
	 * @param userId
	 * @param id
	 * @return
	 */
	Result<CommissionTemplate> detail(String userId,Long id);
	
	/**
	 * 创建一个分佣模板
	 * @param userId
	 * @param template
	 * @return
	 */
	Result<CommissionTemplate> create(String userId,CommissionTemplate template);
	
	/**
	 * 修改一个分佣模板
	 * @param userId
	 * @param template
	 * @return
	 */
	Result<Boolean> change(String userId,CommissionTemplate template);
	
	/**
	 * 列表一个分佣模板
	 * @param userId
	 * @param search
	 * @param pager
	 * @return
	 */
	Result<Pager> list(String userId,CommissionTemplate search,Pager pager);
	
	/**
	 * 更新分佣模板状态
	 * @param userId
	 * 操作者ID
	 * @param id
	 * 分佣模板ID
	 * @param status
	 * 状态，需要与系统设定一致
	 * @return
	 */
	Result<Boolean> changeStatus(String userId,Long id,String status);
	
}
