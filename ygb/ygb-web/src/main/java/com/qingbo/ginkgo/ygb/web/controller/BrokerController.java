package com.qingbo.ginkgo.ygb.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.MailUtil;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RegexMatch;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.Broker;
import com.qingbo.ginkgo.ygb.web.pojo.PersonDetail;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.util.VelocityUtil;

/**
 * 经纪人管理Controller
 * @author: Kent
 * @date： 2014-12-9
 */
// TODO 权限控制
@Controller
@RequestMapping("broker")
public class BrokerController {
	
	@Autowired private CustomerBizService customerBizService;
	@Autowired private UserBizService userBizService;
	private int pagerSize = 5;
	
	//经纪人管理首页列表
	@RequestMapping("brokers")
	public String broker(Broker broker, Model model, HttpServletRequest request){
		try{
			// 账户信息
			Long userId =ShiroTool.userId();
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			AccountDetail account = new AccountDetail();
			if(result.success()&& result.getObject()!=null) {
				account.setRealName(result.getObject().getRealName());
				account.setUserNum(result.getObject().getCustomerNum());
			}
			model.addAttribute("account", account);

			// 查询条件回显
			model.addAttribute("search", broker);
			
			// 准备分页条件
			Pager pager = new Pager();
			pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pagerSize));
			Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
			if(totalRows != null && totalRows > 0) pager.init(totalRows);
			Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
			if(currentPage != null && currentPage > 0) pager.page(currentPage);

			// 执行查询，返回分页
			customerBizService.brokerPage(pager, broker, userId);
			model.addAttribute("pager", pager);
		}catch(Exception e){
			
		}
		return "marketingAgency/brokerManage";
	}
	
	// 经纪人详情
	@RequestMapping("brokerDetail")
	public String brokerDetail(Long id, Model model, HttpServletRequest request){
		try{
			// 账户信息
			Long userId =ShiroTool.userId();
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			AccountDetail account = new AccountDetail();
			if(result.success()&& result.getObject()!=null) {
				account.setRealName(result.getObject().getRealName());
				account.setUserNum(result.getObject().getCustomerNum());
			}
			model.addAttribute("account", account);
			
			// 查询详情
			PersonDetail detail = new PersonDetail();
			Result<AccountDetail> result1 = customerBizService.getBindingStatusByUserId(userId);
			if(result1.success() && result1.getObject() != null){
				detail.setBankCardBind("1".equals(result1.getObject().getIsBinding()) ? "已绑定" : "未绑定");
			}
			AccountDetail temp = userBizService.getAccountDetail(id);
			detail.setUserNum(temp.getUserNum());
			detail.setRealName(temp.getRealName());
			detail.setMobile(temp.getTelephone());
			// 经纪人的详细状态
			model.addAttribute("detail", detail);
		}catch(Exception e){
			
		}
		return "marketingAgency/brokerDetail";
	}
	
	//添加新经济人
	@RequestMapping(value="brokerAdd", method=RequestMethod.GET)
	public String brokerAdd(Model model, HttpServletRequest request){
		try{
			// 账户信息
			Long userId =ShiroTool.userId();
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			AccountDetail account = new AccountDetail();
			if(result.success()&& result.getObject()!=null) {
				account.setRealName(result.getObject().getRealName());
				account.setUserNum(result.getObject().getCustomerNum());
			}
			model.addAttribute("account", account);
		}catch(Exception ee){
			
		}
		return "marketingAgency/brokerAdd";
	}
	
	//处理添加的新经纪人
	@RequestMapping(value="brokerAdd", method=RequestMethod.POST)
	public String doBrokerAdd(Broker broker, Model model,HttpServletRequest request){
		try{
			// 账户信息
			Long userId =ShiroTool.userId();
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			AccountDetail account = new AccountDetail();
			if(result.success()&& result.getObject()!=null) {
				account.setRealName(result.getObject().getRealName());
				account.setUserNum(result.getObject().getCustomerNum());
			}
			model.addAttribute("account", account);
			//与页面的交互信息
			boolean check = true;
			if(broker==null ||("").equals(broker)) {
				//model.addAttribute("result", "信息填写不全");
				check = false;
			}
			if(broker.getUserName()==null || ("").equalsIgnoreCase(broker.getUserName()) || RegexMatch.isMatchUserName(broker.getUserName())==false) {
				//model.addAttribute("result", "fail");
				check = false;
			}
			if(broker.getRealName()==null || ("").equalsIgnoreCase(broker.getRealName())|| RegexMatch.isMatchRealName(broker.getRealName())==false) {
				//model.addAttribute("result", "真实姓名不正确");
				System.out.println("真实姓名不正确");
				check = false;
			}
			if(broker.getEmail()==null || ("").equalsIgnoreCase(broker.getEmail()) || RegexMatch.isMatchEmail(broker.getEmail())==false) {
				//model.addAttribute("result", "邮箱不正确");
				System.out.println("邮箱不正确");
				check = false;
			}
			if(broker.getMobile()==null || ("").equalsIgnoreCase(broker.getMobile()) || RegexMatch.isMacthMobile(broker.getMobile())==false) {
				//model.addAttribute("result", "手机号码不正确");
				System.out.println("手机号码不正确");
				check = false;
			}
			if(check==false) {
				model.addAttribute("result", "fail");
				return "marketingAgency/brokerAddResult";
			}
			if(userId!=null && !("").equals(userId)) {
				//处理添加的经纪人
				broker.setMarketingUserId(userId);
				broker.setRegisterSource(CustomerConstants.RegisterSource.BEIYING.getCode());
				Result<User> result1 = customerBizService.createBrokerByMarketing(broker);
				if(result1.success()){
					// 发邮件
					String activateCode = userBizService.getUserActivatedCode(result1.getObject().getId());
					Map<String, Object> map = new HashMap<>();
					map.put("activatedCode", activateCode);
					map.put("user", result1.getObject());
					String html = VelocityUtil.merge("register/activateEmail.vm", map);
					MailUtil.sendHtmlEmail(broker.getEmail(),html,"财富箱激活邮件");
					model.addAttribute("result", "success");
				}else{
					model.addAttribute("result", "fail");
				}
			}else {
				model.addAttribute("result", "fail");
			}
			
		}catch(Exception ee){
			
		}
		return "marketingAgency/brokerAddResult";
	}
}
