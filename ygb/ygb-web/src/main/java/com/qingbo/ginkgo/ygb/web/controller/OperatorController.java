package com.qingbo.ginkgo.ygb.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RegexMatch;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.pojo.Operator;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.VariableConstants;
import com.qingbo.ginkgo.ygb.web.util.AssistInfoUtil;
import com.qingbo.ginkgo.ygb.web.util.Image;

/**
 * 操作员管理Controller
 * @author: Kent
 * @date： 2014-12-10
 */
// TODO 权限控制
@Controller
@RequestMapping("operator")
public class OperatorController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired private CustomerBizService customerBizService;
	@Autowired private UserBizService userBizService;
	@Resource private AssistInfoUtil assistInfoUtil;
	private int psgeSize = 5;
	
	
	//操作员管理首页列表
	@RequestMapping("operators")
	public String broker(Operator operator, Model model, HttpServletRequest request){
		try{
			// 账户信息，顶部欢迎消息需要
			Long userId =ShiroTool.userId();
			//Long userId = 10L;
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			model.addAttribute("account", result.getObject());
			// 数据列表
			// 准备分页条件
			Pager pager = new Pager();
			pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", psgeSize));
			Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
			if(totalRows != null && totalRows > 0) pager.init(totalRows);
			Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
			if(currentPage != null && currentPage > 0) pager.page(currentPage);
			//Long guaranteeId = operator.getGuaranteeId();
			operator.setGuaranteeId(userId);
			Result<List<Operator>> list  = customerBizService.operatorPage(pager, operator, userId);
			if(list.success()) {
				model.addAttribute("pager", pager);
				assistInfoUtil.init(model);
			}
			// 查询条件回显
			model.addAttribute("search", operator);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "operator/operatorManage";
	}
	
	// 操作员详情
	@RequestMapping("operatorDetail")
	public String investerDetail(String id, Model model, HttpServletRequest request,String level){
		try{
			String err1 = request.getParameter("msg1");
			String err2 = request.getParameter("msg2");
			if(!StringUtils.isEmpty(err1)){
				model.addAttribute("msg1", err1);
			}
			if(!StringUtils.isEmpty(err2)) {
				model.addAttribute("msg2", err2);
			}
			// 账户信息，顶部欢迎消息需要
			//Long userId =ShiroTool.userId();
			Long userId = new Long(id);
			//Long userId = 8L;
			Result<UserDetail> result1 = customerBizService.getUserDetailByUserId(userId);
			model.addAttribute("account", result1.getObject());
			// 状态映射
			Map<String,String> status = new HashMap<String,String>();
			status.put("A", "正常");
			status.put("D", "禁用");
			model.addAttribute("status", status);
			// 等级映射
			model.addAttribute("L1", VariableConstants.GuaranteeLetterReviewLevel.LEVEL1);
			model.addAttribute("L2", VariableConstants.GuaranteeLetterReviewLevel.LEVEL2);
			model.addAttribute("L3", VariableConstants.GuaranteeLetterReviewLevel.LEVEL3);
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			Operator operator = new Operator();
			if(result.success()) {
				operator.setId(result.getObject().getUserId());
				operator.setUserName(result.getObject().getUserName());
				operator.setStatus(result.getObject().getStatus());
				operator.setRemark(result.getObject().getMemo());
				operator.setLevel(level);
			}
			// 操作员的详细状态
			model.addAttribute("operator", operator);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "operator/operatorDetail";
	}
	
	//添加新操作员
	@RequestMapping(value="operatorAdd", method=RequestMethod.GET)
	public String operatorAdd(Model model, HttpServletRequest request){
		try{
			
			// 账户信息，顶部欢迎消息需要
			Long userId =ShiroTool.userId();
			//Long userId = 10L;
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			model.addAttribute("account", result.getObject());
			// 等级映射
			model.addAttribute("L1", VariableConstants.GuaranteeLetterReviewLevel.LEVEL1);
			model.addAttribute("L2", VariableConstants.GuaranteeLetterReviewLevel.LEVEL2);
			model.addAttribute("result", RequestUtil.getStringParam(request, "result", null));
			String err = request.getParameter("error");
			if(!StringUtils.isEmpty(err)){
				model.addAttribute("err", err);
			}
			
		}catch(Exception ee){
			ee.printStackTrace();
		}
		return "operator/operatorAdd";
	}
	
	//处理添加的新操作员
	@RequestMapping(value="operatorAdd", method=RequestMethod.POST)
	public String doBrokerAdd(Operator operator, Model model,HttpServletRequest request){
		try{
			boolean checkImgCode = Image.checkImgCode(request);
			if(!checkImgCode) {
				logger.error("验证码错误");
				model.addAttribute("error", "验证码错误！");
			}
			// 账户信息，顶部欢迎消息需要
			Long userId =ShiroTool.userId();
			//Long userId = 10L;
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			model.addAttribute("account", result.getObject());
			boolean check = true;
			if(operator==null || ("").equals(operator)) {
				model.addAttribute("error", "信息填写不全");
				logger.error("信息填写不全");
				check = false;
			}
			if(operator.getUserName()==null || ("").equalsIgnoreCase(operator.getUserName()) || RegexMatch.isMatchUserName(operator.getUserName())==false) {
				model.addAttribute("error", "用户名不正确");
				logger.error("用户名不正确");
				check = false;
			}
			if(operator.getPassword()==null || ("").equalsIgnoreCase(operator.getPassword())) {
				model.addAttribute("error","密码不正确");
				logger.error("密码不正确");
				check = false;
			}
			if(operator.getOperatorType()==null ||("").equalsIgnoreCase(operator.getOperatorType())) {
				model.addAttribute("error","操作员级别为空");
				logger.error("操作员级别为空");
				check = false;
			}
			if(operator.getAuditPassword()==null || ("").equals(operator.getAuditPassword())) {
				model.addAttribute("error","操作员审核密码为空");
				logger.error("操作员审核密码为空");
				check = false;
			}
			if(check==false) {
				model.addAttribute("operator", operator);
				return "operator/operatorAdd";
			}
			operator.setGuaranteeId(userId);
			Result<User>  result1 = customerBizService.createOperator(operator);
			if(result1.success()) {
				logger.info("userid为"+operator.getGuaranteeId()+"的担保机构为操作员"+result1.getObject().getId()+"开户成功");
			}
		}catch(Exception ee){
			ee.printStackTrace();
		}
		return "redirect:/operator/operators.html";
	}
	
	//处理修改登录密码
	@RequestMapping(value="resetLoginPass", method=RequestMethod.POST)
	public String resetLoginPassword(Model model,HttpServletRequest request,Operator operator){
//		System.out.println("操作员ID：" + id);
//		System.out.println("登录密码：" + password);
//		System.out.println("级别：" + level);
//		System.out.println("操作员ID：" + operator.getId());
//		System.out.println("级别：" + operator.getLevel());
//		System.out.println("登录密码：" + operator.getPassword());
		Long id = operator.getId();
		String password = operator.getPassword();
		String level = operator.getLevel();
		if(id==null || ("").equals(id) || password==null ||("").equals(password)) {
			model.addAttribute("msg1", "重置登录密码出错");
			return "redirect:/operator/operatorDetail.html?id="+id+"&level="+level+"";
		}
		Result<Boolean> flag = customerBizService.resetLoginPassword(id, password);
		if(flag.success()) {
			model.addAttribute("msg1", "重置登录密码成功");
		}
		
			return "redirect:/operator/operatorDetail.html?id="+id+"&level="+level+"";
	}
	
	//处理修改审核密码
	@RequestMapping(value="resetAuditPass", method=RequestMethod.POST)
	public String resetAuditPassword(Operator operator, Model model,HttpServletRequest request){
//		System.out.println("操作员ID：" + id);
//		System.out.println("审核密码：" + auditPassword);
		Long id = operator.getId();
		String auditPassword = operator.getAuditPassword();
		String level = operator.getLevel();
		if(id==null || ("").equals(id) || auditPassword==null ||("").equals(auditPassword)) {
			model.addAttribute("msg2", "重置审核密码出错");
			return "redirect:/operator/operatorDetail.html?id="+id+"&level="+level+"";
		}
		Result<Boolean> flag = customerBizService.resetAuditPassword(id, auditPassword);
		if(flag.success()) {
			model.addAttribute("msg2", "重置登录密码成功");
		}
		return "redirect:/operator/operatorDetail.html?id="+id+"&level="+level+"";
		}

}
