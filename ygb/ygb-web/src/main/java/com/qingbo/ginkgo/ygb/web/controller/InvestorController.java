package com.qingbo.ginkgo.ygb.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.shiro.ShiroTool;
import com.qingbo.ginkgo.ygb.web.biz.CustomerBizService;
import com.qingbo.ginkgo.ygb.web.biz.InvestmentBizService;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AccountDetail;
import com.qingbo.ginkgo.ygb.web.pojo.Investor;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.util.AssistInfoUtil;
import com.qingbo.ginkgo.ygb.web.util.VelocityUtil;

/**
 * 投资人管理Controller
 * @author: Kent
 * @date： 2014-12-9
 */
// TODO 权限控制
@Controller
@RequestMapping("investor")
public class InvestorController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired private CustomerBizService customerBizService;
	@Autowired private UserBizService userBizService;
	@Resource private AssistInfoUtil assistInfoUtil;
	@Resource private InvestmentBizService investmentBizService;
	
	private int pageSize = 5;
	//投资人管理首页列表
	@RequestMapping("investors")
	public String broker(Investor investor, Model model, HttpServletRequest request){
		try{
			// 账户信息，顶部欢迎消息需要
			Long userId =ShiroTool.userId();
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			AccountDetail account = new AccountDetail();
			if(result.success()&& result.getObject()!=null) {
				account.setRealName(result.getObject().getRealName());
				account.setUserNum(result.getObject().getCustomerNum());
			}
			model.addAttribute("account", account);
			// 查询条件回显
			model.addAttribute("search", investor);
			// 准备分页条件
			Pager pager = new Pager();
			pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
			Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
			if(totalRows != null && totalRows > 0) pager.init(totalRows);
			Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
			if(currentPage != null && currentPage > 0) pager.page(currentPage);
			investor.setBrokerUserId(userId);
			customerBizService.investorPage(pager, investor, userId);
			model.addAttribute("pager", pager);
			assistInfoUtil.init(model);
		}catch(Exception e){
			
		}
		return "investor/investorManage";
	}
	
	
	// 投资人详情
	@RequestMapping("investorDetail")
	public String investerDetail(String id, Model model, HttpServletRequest request){
		try{
			// 账户信息，顶部欢迎消息需要
			//Long usersId =ShiroTool.userId();
			Long userId =ShiroTool.userId();
			Result<UserDetail> result = customerBizService.getUserDetailByUserId(userId);
			AccountDetail account = new AccountDetail();
			if(result.success()&& result.getObject()!=null) {
				account.setRealName(result.getObject().getRealName());
				account.setUserNum(result.getObject().getCustomerNum());
			}
			model.addAttribute("account", account);
			UserDetail userDetail = new UserDetail();
			if(id!=null ||!("").equalsIgnoreCase(id)) {
				Result<UserDetail> detail = customerBizService.getUserDetailByUserId(new Long(id));
				Result<AccountDetail> result1 = customerBizService.getBindingStatusByUserId(new Long(id));
				if(detail.success()) {
					userDetail = detail.getObject();
					if(result1.success() && result1.hasObject()) {
						userDetail.setBankCardNum(result1.getObject().getIsBinding());
					}
					// 投资人的详细状态
					model.addAttribute("detail", detail.getObject());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "investor/investorDetail";
	}
	
	//添加新投资人
	@RequestMapping(value="investorAdd", method=RequestMethod.GET)
	public String brokerAdd(Model model, HttpServletRequest request){
		try{
			// 账户信息，顶部欢迎消息需要
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
		return "investor/investorAdd";
	}
	
	//处理添加的新投资人
	@RequestMapping(value="investorAdd", method=RequestMethod.POST)
	public String doBrokerAdd(Investor investor, Model model,HttpServletRequest request){
		try{
			// 账户信息，顶部欢迎消息需要
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
			if(investor==null ||("").equals(investor)) {
				check = false;
				//model.addAttribute("result", "信息填写不全");
			}
			if(investor.getUserName()==null || ("").equalsIgnoreCase(investor.getUserName()) || RegexMatch.isMatchUserName(investor.getUserName())==false) {
				check = false;
				//model.addAttribute("result", "fail");
			}
			if(investor.getRealName()==null || ("").equalsIgnoreCase(investor.getRealName())|| RegexMatch.isMatchRealName(investor.getRealName())==false) {
				//model.addAttribute("result", "真实姓名不正确");
				check = false;
			}
			if(investor.getEmail()==null || ("").equalsIgnoreCase(investor.getEmail()) || RegexMatch.isMatchEmail(investor.getEmail())==false) {
				//model.addAttribute("result", "邮箱不正确");
				check = false;
			}
			if(investor.getMobile()==null || ("").equalsIgnoreCase(investor.getMobile()) || RegexMatch.isMacthMobile(investor.getMobile())==false) {
				//model.addAttribute("result", "手机号码不正确");
				check = false;
			}
			if(check = false) {
				model.addAttribute("result", "fail");
				return "investor/investorAddResult";
			}
			if(userId!=null && !("").equals(account)) {
				//注册来源
				investor.setRegisterSource(CustomerConstants.RegisterSource.BEIYING.getCode());
				//经纪人userid
				investor.setBrokerUserId(userId);
				//处理添加的投资人
				Result<User> user= customerBizService.createInvestorByBroker(investor);
				//发送邮件
				if(user.success()) {
					String activateCode = userBizService.getUserActivatedCode(user.getObject().getId());
					Map<String, Object> map = new HashMap<>();
					map.put("activatedCode", activateCode);
					map.put("user",investor);
					String html = VelocityUtil.merge("register/activateEmail.vm", map);
					System.out.println(html);
//					MailUtil.sendHtmlEmail(investor.getEmail(),html,"财富箱激活邮件");
					model.addAttribute("result", "success");
				}else {
					model.addAttribute("result", "fail");
				}
			}else {
				model.addAttribute("result", "fail");
			}
			
		}catch(Exception ee){
			
		}
		return "investor/investorAddResult";
	}
	
	/**
	 * 列表营销机构或者经纪人下辖的投资人投资情况，按照投资顺序倒序排列 
	 */
	@RequestMapping("listInvestment")
	public String listInvestment(Model model, HttpServletRequest request){
		assistInfoUtil.init(model);
		try{
			Long userId =ShiroTool.userId();
			
			Pager pager = new Pager();
			pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
			Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
			if(totalRows != null && totalRows > 0) pager.init(totalRows);
			Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
			if(currentPage != null && currentPage > 0) pager.page(currentPage);
			
			Result<Pager> page = investmentBizService.listByAgencyOrBroker(String.valueOf(userId), userId, pager);
			logger.info("InvestorController.listInvestment by:"+userId+" Result:"+page.success());
			model.addAttribute("pager", page);
		}catch(Exception e){
			logger.info("InvestorController.listInvestment Error,By: "+e.getMessage());
		}
		return "investor/";
	}
	
	
}
