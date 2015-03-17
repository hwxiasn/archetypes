package com.qingbo.ginkgo.ygb.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.project.entity.BorrowerInfo;
import com.qingbo.ginkgo.ygb.web.biz.BorrowerInfoBizService;

@Controller
public class FundraisingController {

	@Resource private  BorrowerInfoBizService borrowerInfoBizService;
	
	/** 填写信息 */
	@RequestMapping(value="fundraising", method=RequestMethod.GET)
	public String fillOut(){
		return "fundraising";
	}
	
	/** 提交添加 */
	@RequestMapping(value="fundraising", method=RequestMethod.POST)
	public String doAdd(BorrowerInfo info, Model model, HttpServletRequest request){
		model.addAttribute("info", info);
		
		Result<Boolean> result = borrowerInfoBizService.add(info);
		if(result.success()){
			model.addAttribute("ok", "Y");
			return "fundraising";
		}else{
			model.addAttribute("ok", "N");
			model.addAttribute("msg", "添加失败：" + result.getMessage());
			return "fundraising";
		}
	}
}
