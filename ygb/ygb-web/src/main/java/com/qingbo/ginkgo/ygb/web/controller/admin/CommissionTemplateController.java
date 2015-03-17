package com.qingbo.ginkgo.ygb.web.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.base.service.CodeListService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.customer.shiro.RequiresAdminUser;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.project.entity.CommissionDetail;
import com.qingbo.ginkgo.ygb.project.entity.CommissionTemplate;
import com.qingbo.ginkgo.ygb.web.biz.CommissionTemplateBizService;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;
import com.qingbo.ginkgo.ygb.web.util.AssistInfoUtil;

@Controller
@RequestMapping("admin/cost")
@RequiresAdminUser
public class CommissionTemplateController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private CommissionTemplateBizService commissionTemplateBizService;
	@Resource private CodeListService codeListService;
	@Resource private AssistInfoUtil assistInfoUtil;

	@RequestMapping("commission")
	public String list(CommissionTemplate search,Model model,HttpServletRequest request){
		String userId = String.valueOf(ShiroTool.userId());
		//传递查询条件
		model.addAttribute(VariableConstants.Entity_Search, search);
		//提取模板相关的枚举数据
		assistInfoUtil.init(model);
		//分页处理
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", 6));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		//查询条件
		Result<Pager> list = commissionTemplateBizService.list(userId, search, pager);
		if(list.success()){
			//插入模板查询结果Pager
			model.addAttribute(VariableConstants.Pager_CommissionTemplate ,list.getObject());
			model.addAttribute("pager", list.getObject());
		}
		
		return "admin/cost/commission";
	}
	
	/**
	 * 新增或者修改分佣模板
	 * @param id
	 * 可无，则为新增
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("commissionEdit")
	public String change(Long id,Model model,HttpServletRequest request){
		String userId = String.valueOf(ShiroTool.userId());
		//有ID，则为修改模板
		if(id !=null){
			Result<CommissionTemplate> detail = commissionTemplateBizService.detail(userId, id);
			//存在模板
			if(detail.success()){
				CommissionTemplate template = detail.getObject();
				//在此加工费率为%
				List<CommissionDetail> details = template.getDetails();
				//分角色费率不为空
				if(details != null && details.size()>0){
					//修改真实数据为百分比显示
					for(CommissionDetail cd:details){
						BigDecimal rate = cd.getRate().multiply(BigDecimal.valueOf(100)); 
						cd.setRate(rate);
					}
				}
				//插入模板信息
				model.addAttribute(VariableConstants.Entity_CommissionTemplate , template);
			}
		}
		//提取模板相关的枚举数据
		assistInfoUtil.init(model);

		return "admin/cost/commissionEdit";
	}
	
	@RequestMapping("doCommissionEdit")
	public String doChange(CommissionTemplate template,Model model,HttpServletRequest request){
		String userId = String.valueOf(ShiroTool.userId());
		logger.info("Change CommissionTemplate by UserId:"+ userId +" TemplateName:"+ template.getName());
		//把对应的角色的收取的利率封装为map
		ArrayList<CommissionDetail> listDetail = new ArrayList<CommissionDetail>();
		String[] roles = request.getParameterValues("roles");
		for(int i = 0;i < roles.length;i++){
			CommissionDetail detail = new CommissionDetail();
			detail.setRole(roles[i]);
			//修正百分比为实际数值
			BigDecimal rate = BigDecimal.valueOf(RequestUtil.getDoubleParam(request, "rolerate"+roles[i], 0.0)).divide(BigDecimal.valueOf(100));//request.getParameter("rolerate"+roles[i]))).divide(BigDecimal.valueOf(100)); 
			detail.setRate(rate);
//			BigDecimal rate = BigDecimal.valueOf(Double.parseDouble("0.5")).divide(BigDecimal.valueOf(101),4, RoundingMode.HALF_EVEN);
//			System.out.println(rate.setScale(4, RoundingMode.HALF_EVEN));
			listDetail.add(detail);
		}
		template.setDetails(listDetail);
		if(template.getId() == null){
			//无ID则为创建新模板
			Result<CommissionTemplate> createTemplate = commissionTemplateBizService.create(userId, template);
			if(createTemplate.success()){
				logger.info("Create CommissionTemplate OK by UserId:"+userId + " ID:"+ createTemplate.getObject().getId());
			}else{
				logger.info("Create CommissionTemplate Error by UserId:"+userId +" Error:"+createTemplate.getError() + " Message:" +createTemplate.getMessage());
			}
		}else{
			//有DI则为更新模板
			Result<Boolean> result = commissionTemplateBizService.change(userId, template);
			if(result.success()){
				logger.info("Update CommissionTemplate OK by UserId:"+userId + " ID:"+ template.getId());
			}else{
				logger.info("Update CommissionTemplate Error by UserId:"+userId +" Error:"+result.getError() + " Message:" +result.getMessage());
			}
		}
		return "redirect:commission.html";
	}
	
	@RequestMapping("userStatus")
	@ResponseBody
	public Object userStatus(Long id, String status) {
		String userId = String.valueOf(ShiroTool.userId());
		logger.info("Change CommissionTemplate by UserId:"+ userId +" TemplateId:"+ id +" Status:"+status);
		Result<Boolean> result = commissionTemplateBizService.changeStatus(userId, id, status);
		
		JSONObject json = new JSONObject();
		if(result.success()){
			if(result.getObject()){
				json.put("success", "S");
			}else{
				json.put("success", "F");
				json.put("msg", result.getMessage());
			}
		}else{
			json.put("success", "F");
			json.put("msg", result.getMessage());
		}
		return json;
	}

}
