package com.qingbo.ginkgo.ygb.web.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.LoaneeInfo;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectDetail;
import com.qingbo.ginkgo.ygb.web.pojo.ProjectSearch;

public interface ProjectBizService {
	/** 搜索项目 */
	Result<Pager> search(ProjectSearch projectSearch, Pager pager);
	/** 项目详情 */
	Result<ProjectDetail> project(Long id);
	/** 总投资额 */
	Result<BigDecimal> totalInvestments();
	/** 总还款额 */
	Result<BigDecimal> totalRepayments();
	/** 担保公司列表 */
	Result<List<CodeList>> guarantees();
	/** 保荐机构列表 */
	Result<List<CodeList>> sponsors();
	
	
	Result<Project> getProject(String userId,Long id);
	
	ProjectDetail projectDetail(Long id);
	
	/**
	 * 列表项目
	 */
	Result<Pager> list(String userId,Project search,Pager pager); 
	
	/**
	 * 列表项目Detail
	 */
	Result<Pager> detailList(String userId, Project search, Pager pager);
	
	/**
	 * 创建项目
	 */
	Result<Project> createProject(String userId,Project project,Guarantee guarantee);
	/**
	 * 更新项目状态
	 */
	Result<Boolean> updateProject(String userId,Project project);

	/**
	 * 投资人投资
	 * 身份验证不在此处理，预处理
	 * @param id
	 * @param userId
	 * @param investAmount
	 * @return
	 */
	Result<Boolean> invest(Long id,String userId,BigDecimal investAmount);
	
	/**
	 * 借款人还款
	 * 身份验证不在此处理，预处理
	 * @param repaymentId
	 * 还款凭证ID
	 * @param userId
	 * 用户ID
	 * @param repayAmount
	 * 还款金额
	 * @return
	 */
	Result<Boolean> repay(Long repaymentId,String userId,BigDecimal repayAmount);
	/**
	 * 审核项目
	 * @param projectId
	 * 项目ID
	 * @param userId
	 * 操作员ID
	 * @param now
	 * 发布时间
	 * @param pass
	 * 是否通过BOOLEAN
	 * @param reason
	 * 拒绝原因
	 * @return
	 */
	Result<Boolean> reviewed(Long projectId,String userId,Date now,boolean pass,String reason);
	
	/**
	 * 创建借款人信息披露
	 * @param info
	 * @param userId
	 * @return
	 */
	Result<LoaneeInfo> createLoaneeInfo(LoaneeInfo info,String userId);
	/**
	 * 按照项目获取借款人信息披露
	 * @param projectId
	 * @param userId
	 * @return
	 */
	Result<LoaneeInfo> getLoaneeInfo(Long projectId,String userId);
	/**
	 * 代偿还款
	 * @param projectId
	 * 项目ID
	 * @param userId
	 * 操作人的ID
	 * @param investAmount
	 * 代偿金额
	 * @return
	 */
	Result<Boolean> compensatory(Long projectId, String userId, BigDecimal investAmount);
}
