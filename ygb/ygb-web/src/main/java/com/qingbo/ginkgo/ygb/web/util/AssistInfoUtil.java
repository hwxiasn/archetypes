package com.qingbo.ginkgo.ygb.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.base.enums.CodeListType;
import com.qingbo.ginkgo.ygb.base.service.CodeListService;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants.Role;
import com.qingbo.ginkgo.ygb.customer.service.CustomerService;
import com.qingbo.ginkgo.ygb.project.entity.CommissionDetail;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.InvestmentStatus;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants.RepaymentStatus;
import com.qingbo.ginkgo.ygb.project.service.CommissionTemplateService;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;

@Service
public class AssistInfoUtil {
	@Resource private CodeListService codeListService;
	@Resource private CustomerService customerService;
	@Resource private CommissionTemplateService commissionTemplateService;

	
	public Result<Boolean> init(Model model){
		//插入投资交易状态
		model.addAttribute(VariableConstants.Map_CodeList_InvestmentStatus, InvestmentStatus.getCodeNameMap());

		//插入还款状态
		model.addAttribute(VariableConstants.Map_CodeList_RepaymentStatus, RepaymentStatus.getCodeNameMap());
		
		//插入模板状态
		Result<List<CodeList>> codeList = codeListService.list(VariableConstants.CODElIST_COMMISSION_STATUS);
		Map<String,CodeList> mapStatus = new HashMap<String,CodeList>();
		for(CodeList cl:codeList.getObject()){
			mapStatus.put(cl.getCode(), cl);
		}
		model.addAttribute(VariableConstants.Map_CodeList_CommissionTemplateStatus, mapStatus);
		
		//插入分佣阶段
		codeList = codeListService.list(VariableConstants.CODELIST_COMMISSION_PHASE);
		Map<String,CodeList> mapPhase = new HashMap<String,CodeList>();
		for(CodeList cl:codeList.getObject()){
			mapPhase.put(cl.getCode(), cl);
		}
		model.addAttribute(VariableConstants.Map_CodeList_CommissionTemplatePhase , mapPhase);

		//插入分佣角色
		codeList = codeListService.list(VariableConstants.CODELIST_COMMISSION_ROLE );
		Map<String,CodeList> mapRole = new HashMap<String,CodeList>();
		for(CodeList cl:codeList.getObject()){
			mapRole.put(cl.getCode(), cl);
		}
		model.addAttribute(VariableConstants.Map_CodeList_CommissionTemplateRole, mapRole);
		
		//插入分佣方式
		codeList = codeListService.list(VariableConstants.CODELIST_COMMISSION_TYPE);
		Map<String,CodeList> mapType = new HashMap<String,CodeList>();
		for(CodeList cl:codeList.getObject()){
			mapType.put(cl.getCode(), cl);
		}
		model.addAttribute(VariableConstants.Map_CodeList_CommissionTemplateType, mapType);

		// 项目状态字典
		model.addAttribute(VariableConstants.MAP_PROJECT_STATUS, ProjectConstants.ProjectStatus.getCodeNameMap());
		

		//最低投资额
		model.addAttribute(VariableConstants.List_CodeList_MinInvestAmount, codeListService.list(CodeListType.MIN_AMOUNT.getCode()).getObject());
		
		//插入借款期限类型及借款期限
		List<CodeList> loneTypes = codeListService.list(CodeListType.LOAN_TYPE.getCode()).getObject();
		Map<CodeList, List<CodeList>> tempMap = new HashMap<CodeList, List<CodeList>>();
		Map<String, Map<String,CodeList>> tempMap2Map = new HashMap<String, Map<String,CodeList>>();
		for (CodeList item : loneTypes) {
			List<CodeList> details = codeListService.list(item.getCode()).getObject();
			tempMap.put(item, details);
			Map<String,CodeList> itemMap = new HashMap<String,CodeList>();
			for(CodeList cl:details){
				itemMap.put(cl.getCode(), cl);
			}
			tempMap2Map.put(item.getCode(), itemMap);
		}
		model.addAttribute(VariableConstants.Map_CodeList_LoanType, tempMap);
		model.addAttribute(VariableConstants.Map_CodeList_LoanType_Map, tempMap2Map);
		
		//最低条件
		model.addAttribute(VariableConstants.List_CodeList_SettleType, codeListService.list(CodeListType.SETTLE_TYPE.getCode()).getObject());
		
		//筹资阶段分润模板
		List<CommissionTemplate> fundrasing = commissionTemplateService.listCommissionTemplateByPhase(ProjectConstants.PhaseForFundraise).getObject();
		List<CommissionTemplate> fundrasingList = new ArrayList<CommissionTemplate>();
		Map<String,CommissionTemplate> fundrasingMap = new HashMap<String,CommissionTemplate>();
		for (CommissionTemplate template : fundrasing) {
			//正常状态
			if("A".equalsIgnoreCase(template.getStatus())){
				String details = getTemplateDetail(template.getDetails(),mapRole);
				template.setRoleRates(details);
				fundrasingMap.put(String.valueOf(template.getId()), template);
				fundrasingList.add(template);
			}
		}
		model.addAttribute(VariableConstants.List_CommissionTemplate_Fundrasing,fundrasingList);
		model.addAttribute(VariableConstants.Map_CommissionTemplate_Fundrasing,fundrasingMap);
		
		//还款阶段分润模板
		List<CommissionTemplate> repays = commissionTemplateService.listCommissionTemplateByPhase(ProjectConstants.PhaseForRepay).getObject();
		List<CommissionTemplate> repaysList = new ArrayList<CommissionTemplate>();
		Map<String,CommissionTemplate> repaysMap = new HashMap<String,CommissionTemplate>();
		for (CommissionTemplate template : repays) {
			//正常状态
			if("A".equalsIgnoreCase(template.getStatus())){
				String details = getTemplateDetail(template.getDetails(),mapRole);
				template.setRoleRates(details);
				repaysMap.put(String.valueOf(template.getId()), template);
				repaysList.add(template);
			}
		}
		model.addAttribute(VariableConstants.List_CommissionTemplate_Repay,repaysList);
		model.addAttribute(VariableConstants.Map_CommissionTemplate_Repay,repaysMap);
		
		Pager pager = new Pager();
		pager.setPageSize(1000);
		pager.init(1000);
		pager.page(0);
		SpecParam<User> specs = new SpecParam<User>();
		List<String> role = new ArrayList<String>();
		role.add(Role.GUARANTEE.getCode());
		role.add(Role.LOAN.getCode());
		specs.in("role", role);
		
		//担保机构
		Result<PageObject<User>> roles = customerService.page(specs, pager);
		List<User> guaranteeList = roles.getObject().getList();
		List<User> guaranteeListAll = new ArrayList<User>(); 
		Map<String, String> guaranteeMapAll = new HashMap<String, String>();
		for(User u : guaranteeList){
			Result<User> result = customerService.getUserByUserId(u.getId());
			u = result.getObject();
			guaranteeListAll.add(u);
			guaranteeMapAll.put(String.valueOf(u.getId()), u.getEnterpriseProfile() == null ? null : u.getEnterpriseProfile().getEnterpriseName());
		}
		model.addAttribute(VariableConstants.List_User_Guarantee, guaranteeListAll);
		model.addAttribute(VariableConstants.Map_User_Guarantee, guaranteeMapAll);
		
		//担保函状态
		List<CodeList> guaStatusList = codeListService.list(CodeListType.GUARANTEELETTER_STATUS.getCode()).getObject();
		model.addAttribute(VariableConstants.List_CodeList_GuaranteeStatus , guaStatusList);
		Map<String,CodeList> guaranteeStatusMap = new HashMap<String,CodeList>();
		for(CodeList c:guaStatusList){
			guaranteeStatusMap.put(c.getCode(), c);
		}
		model.addAttribute(VariableConstants.Map_CodeList_GuaranteeStatus , guaranteeStatusMap);

		//保荐机构
		role.clear();
		role.add(Role.SPONORG.getCode());
		roles = customerService.page(specs, pager);
		List<User> sponsorList = roles.getObject().getList();
		List<User> sponsorListAll = new ArrayList<User>(); 
		for(User u:sponsorList){
			Result<User> result = customerService.getUserByUserId(u.getId());
			u = result.getObject();
			sponsorListAll.add(u);
		}
		model.addAttribute(VariableConstants.List_User_Sponsor,sponsorListAll);
		//企业用户角色
		Result<List<CodeList>> agencyList = codeListService.list(CodeListType.AGENCY_ROLE.getCode());
		Map<String,CodeList> mapRoles = new HashMap<String,CodeList>();
		for(CodeList cl:agencyList.getObject()){
			mapRoles.put(cl.getCode(), cl);
		}
		model.addAttribute(VariableConstants.Map_Agency_role, mapRoles);
		//个人用户角色
		Result<List<CodeList>> userList = codeListService.list(CodeListType.USER_ROLE.getCode());
		Map<String,CodeList> mapUserRole = new HashMap<String,CodeList>();
		for(CodeList cl:userList.getObject()){
			mapUserRole.put(cl.getCode(), cl);
		}
		model.addAttribute(VariableConstants.Map_user_role, mapUserRole);
		//个人用户状态
		Result<List<CodeList>> statusList = codeListService.list(CodeListType.USER_STATUS.getCode());
		Map<String,CodeList> mapUserStatus = new HashMap<String,CodeList>();
		for(CodeList cl:statusList.getObject()){
			mapUserStatus.put(cl.getCode(), cl);
		}
		model.addAttribute(VariableConstants.Map_user_status, mapUserStatus);
		
		
		
		return Result.newSuccess();
	}
	
	private String getTemplateDetail(List<CommissionDetail> details,Map<String,CodeList> mapRole) {
		StringBuffer sb = new StringBuffer();
		for (CommissionDetail detail : details) {
			sb.append(mapRole.get(detail.getRole()).getName());
			sb.append(":");
			sb.append(NumberUtil.percentFormat(detail.getRate()));
			sb.append(" ");
		}

		return sb.toString();
	}
	

}
