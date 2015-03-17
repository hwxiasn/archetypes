package com.qingbo.ginkgo.ygb.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qingbo.ginkgo.ygb.web.biz.CodeListBizService;
import com.qingbo.ginkgo.ygb.web.pojo.CodeListItem;

@Controller
@RequestMapping("/data")
public class CommonDataController {
	
	@Autowired private CodeListBizService codeListBizService;
	
	@RequestMapping("cities")
	public String getCititesByProinceId(String pid, Model model,HttpServletRequest request){
		
		List<CodeListItem> cities = codeListBizService.cities(pid);
		model.addAttribute("cities", cities);
		
		return "data/cities";
	}
}
