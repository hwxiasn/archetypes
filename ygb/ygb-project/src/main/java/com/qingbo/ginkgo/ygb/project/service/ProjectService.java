package com.qingbo.ginkgo.ygb.project.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.Investment;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.entity.ProjectCount;


public interface ProjectService {
	
	/**
	 * 根据ID获取项目详情
	 * @param id
	 * 项目ID，为系统规则设定18位数字
	 * @return
	 * Result<Project>
	 */
	Result<Project> getProject(Long id);
	
	/**
	 * 根据分页查询条件列表项目
	 * @param spec
	 * @param page
	 * @return
	 */
	Result<PageObject<Project>> listProjectBySpecAndPage(SpecParam<Project> spec,Pager page);
	
	/**
	 * 更新项目状态
	 * @param project
	 * @return
	 */
	Result<Boolean> updateProject(Project project);

	/**
	 * 更新项目状态
	 * @param project
	 * @return
	 */
	Result<Project> createProject(Project project);
	
	/**
	 * 平台项目统计
	 * ProjectCount 包括融资总额和还款总额，单位为元，精确到小数点2位
	 * getDueAmount() 还款总额
	 * getSettleAmout()融资总额
	 * @param project
	 * @return
	 */
	Result<ProjectCount> sumProject();
	
	/**
	 * 创建项目合同相关事宜，完成对担保函、担保合同、投资凭证的文件名及合同名等设定
	 * 完成项目成立时间、预计还款时间、担保费、投资收益金额的计算
	 */
	Result<Project> buildContract(Long id);
	
	/**
	 * 项目成立 完成支付相关业务
	 * 1、通知交易完成投资授权确认
	 * 2、锁定
	 */
	Result<Project> buildProject(Long id);

	/**
	 * 还款分润子交易
	 * 借款人完成总款支付后由触发器执行后续的逐笔还款子交易
	 */
	Result<Project> repayProject(Long id);
	
	/**
	 * 项目合同制作
	 * @param project
	 * Project
	 * @param guarantee
	 * Guarantee
	 * @param investments
	 * List<Investment>
	 * @return
	 */
	public Result<Boolean> buildFile(Long id);//Project project,Guarantee guarantee,List<Investment> investments
}
