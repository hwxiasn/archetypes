package com.qingbo.ginkgo.ygb.web.biz;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;



/**
 * 担保函业务系统服务
 * 提供担保函的查看，列表、审批、更新等服务
 * 返回结果统一为Result对象
 * @author xiejinjun
 *
 */
public interface GuaranteeBizService {
	
	/**
	 * 按照担保函编号查询担保函详细信息
	 * @param userId
	 * 操作者ID
	 * @param id
	 * 担保函ID
	 */
	Result<Guarantee> detail(String userId,Long id);
	
	/**
	 * 担保函审核
	 * @param id
	 * 担保函ID
	 * @param userId
	 * 审核者ID
	 * @param level
	 * 审核级别
	 * @return
	 */
	Result<Guarantee> audit(Long id,String userId,String level);
	
	/**
	 * 创建担保函
	 * @param userId
	 * 操作者ID
	 * @param guarantee
	 * 担保函信息
	 * @return
	 */
	Result<Guarantee> create(String userId,Guarantee guarantee);

	/**
	 * 列表当前用户查询的担保函
	 * userId为空则不区分用户提取
	 * @param userId
	 * 当前使用用户ID，可为空，操作员则传入担保机构的ID，后台管理可以不填
	 * @param search
	 * Guarantee 查询的条件，仅查询Guarantee自身信息
	 * @param page
	 * 分页
	 * @return
	 */
	Result<Pager> list(String userId,Guarantee search,Pager page);
	
	/**
	 * 更新担保函信息
	 * @param userId
	 * 操作者ID
	 * @param guarantee
	 * 更新的担保函信息
	 * @return
	 */
	Result<Guarantee> change(String userId,Guarantee guarantee);
	
	
}
