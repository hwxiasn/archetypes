package com.qingbo.ginkgo.ygb.web.controller.admin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.project.entity.BorrowerInfo;
import com.qingbo.ginkgo.ygb.web.biz.BorrowerInfoBizService;

@Controller
@RequestMapping("admin/borrower")
public class BorrowerInfoManageController {
	
	@Resource private  BorrowerInfoBizService borrowerInfoBizService;
	
	private static int DEAFAULT_PAGE_SIZE = 10;

	@RequestMapping("query")
	public String info(BorrowerInfo info, Model model, HttpServletRequest request){
		
		// 参数回显
		model.addAttribute("info", info);

		// 处理选单参数
		if(info != null){
			if(info.getLoanTerm() != null){
				info.setLoanTerm(info.getLoanTerm() == 0 ? null : info.getLoanTerm());
			}			
		}
		
		// 准备分页
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pagerSize", DEAFAULT_PAGE_SIZE));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if (totalRows != null && totalRows > 0){
			pager.init(totalRows);
		}
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if (currentPage != null && currentPage > 0){
			pager.page(currentPage);
		}
		
		// 调用服务执行查询
		pager = borrowerInfoBizService.pageInfos(info, pager);

		// 返回结果
		if(pager == null){
			return "admin/borrower/info";
		}else{
			model.addAttribute("pager", pager);
			return "admin/borrower/info";
		}
	}
	
	@RequestMapping(value="detail", method=RequestMethod.GET)
	public String detail(Long id, Model model, HttpServletRequest request){

		// 调用服务执行查询
		Result<BorrowerInfo> result = borrowerInfoBizService.getBorrowerInfo(id);

		if(result.getCode() != 0){
			return "redirect:/admin/borrower/info.html";
		}else{
			model.addAttribute("object", result.getObject());
			return "admin/borrower/detail";
		}
	}
}
