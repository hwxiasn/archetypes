package com.qingbo.ginkgo.ygb.web.controller.admin;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.qingbo.ginkgo.ygb.base.enums.CodeListType;
import com.qingbo.ginkgo.ygb.base.service.CodeListService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.MailUtil;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RegexMatch;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.common.util.UploadUtil;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.shiro.RequiresAdminUser;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.pojo.AgencyDetail;
import com.qingbo.ginkgo.ygb.web.pojo.AgencyItem;
import com.qingbo.ginkgo.ygb.web.pojo.AgencySearch;
import com.qingbo.ginkgo.ygb.web.pojo.BrokerAdd;
import com.qingbo.ginkgo.ygb.web.pojo.MarketingItem;
import com.qingbo.ginkgo.ygb.web.pojo.MarketingSearch;
import com.qingbo.ginkgo.ygb.web.pojo.UserDetail;
import com.qingbo.ginkgo.ygb.web.pojo.UserItem;
import com.qingbo.ginkgo.ygb.web.pojo.UserSearch;
import com.qingbo.ginkgo.ygb.web.util.AssistInfoUtil;
import com.qingbo.ginkgo.ygb.web.util.VelocityUtil;

@Controller
@RequiresAdminUser
@RequestMapping("admin/customer")
public class CustomerController{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired private UserBizService userBizService;
	@Autowired private CodeListService codeListService;
	@Resource private AssistInfoUtil assistInfoUtil;
	private final String VM_PATH = "admin/customer/";
	private int pageSize = 10;
	
	
	/**
	 * 
	* @Description:  个人用户管理主界面
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("user")
	public String user(HttpServletRequest request, Model model,UserSearch userSearch) {
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		
		Result<List<UserItem>> userList = userBizService.userPage(userSearch,pager);
		if(userList.success()) {
			model.addAttribute("user", userSearch);
			pager.setElements(userList.getObject());
			model.addAttribute("pager", pager);
			model.addAttribute("roles", codeListService.list(CodeListType.USER_ROLE.getCode()).getObject());
			model.addAttribute("statuses", codeListService.list(CodeListType.USER_STATUS.getCode()).getObject());
		}
		return VM_PATH + "user";
	}
	
	/**
	 * 
	* @Description:  跳转到个人用户新增界面
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("skipUserAdd")
	public String skipUserAdd(Model model) {
		
		model.addAttribute("roles", codeListService.list(CodeListType.USER_ROLE.getCode()).getObject());
		model.addAttribute("banks", codeListService.banks().getObject());
		model.addAttribute("provinces", codeListService.states().getObject());
		return VM_PATH + "userAdd";
	}
	
	/**
	 * 
	* @Description:  保存新增用户
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping(value="userAddSave", method=RequestMethod.POST)
	public String userAddSave(UserDetail userDetail,MultipartFile idCopyFrontFile, MultipartFile idCopyBankFile,Model model) {
		logger.info(SimpleLogFormater.formatParams(userDetail));
		logger.info("idCopyFrontFile="+idCopyFrontFile+"idCopyBankFile="+idCopyBankFile);
		UploadUtil.confirm(userDetail.getIdCopyFront());
		UploadUtil.confirm(userDetail.getIdCopyBank());
		
		String idCopyFront = UploadUtil.uploadImage(idCopyFrontFile);
		String idCopyBack = UploadUtil.uploadImage(idCopyBankFile);
		logger.info("idCopyFront="+idCopyFront+"idCopyBack="+idCopyBack);
		if(idCopyFront!=null) userDetail.getIdCopyFront();
		if(idCopyBack!=null) userDetail.getIdCopyBank();
		boolean checkParArgument = true;
		//1.验证参数
		if(userDetail== null || ("").equals(userDetail)) {
			checkParArgument = false;
		}
		if(userDetail.getUserName()==null || ("").equals(userDetail.getUserName())||RegexMatch.isMatchUserName(userDetail.getUserName())==false) {
			checkParArgument = false;
		}
		if(userDetail.getRealName()==null || ("").equals(userDetail.getRealName()) ||RegexMatch.isMatchRealName(userDetail.getRealName())==false) {
			checkParArgument = false;
		}
		if(userDetail.getEmail()==null || ("").equals(userDetail.getEmail())||RegexMatch.isMatchEmail(userDetail.getEmail())==false) {
			checkParArgument = false;
		}
		if(userDetail.getMobile()==null || ("").equals(userDetail.getMobile()) || RegexMatch.isMacthMobile(userDetail.getMobile())==false) {
			checkParArgument = false;
		}
		if(userDetail.getRole()==null ||("").equals(userDetail.getRole())) {
			checkParArgument = false;
		}
		if(checkParArgument==false) {
			return "redirect:skipUserAdd.html";
		}
		//1.保存用户添加的信息(注册类型为倍赢)
		userDetail.setRegSource(CustomerConstants.RegisterSource.BEIYING.getCode());
		//证件类型为身份证
		userDetail.setIdType(CustomerConstants.IdType.IDENTITY_CARD.getCode());
		//银行卡类型为借记卡
		userDetail.setBankCardType(CustomerConstants.bangCardType.DEBIT_CARD.getCode());
		Result<User> userFlag = userBizService.saveAddUser(userDetail);
		if(userFlag.success() && userFlag.getObject()!=null) {
			Long userId = userFlag.getObject().getId();
			if(userId!=null && !("").equals(userId)) {
				//2.保存成功后给用户发送邮件
				String userActivatedCode = userBizService.getUserActivatedCode(userId);
				userDetail.setId(userFlag.getObject().getId());
				Map<String, Object> map = new HashMap<>();
				if(userActivatedCode!=null && !("").equals(userActivatedCode)) {
					map.put("activatedCode", userActivatedCode);
					map.put("user", userDetail);
					String html = VelocityUtil.merge("register/activateEmail.vm", map);
					MailUtil.sendHtmlEmail(userDetail.getEmail(),html,"财富箱激活邮件");
				}
			}
		}
		return "redirect:user.html";
	}
	
	/**
	 * 
	* @Description:  跳转到用户修改界面
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("skipUserEdit")
	public String UserEdit(Long id, Model model) {
		Result<UserDetail> userDetail = userBizService.getUser(id);
		if(userDetail.success()) {
			model.addAttribute("user", userDetail.getObject());
			if((("2999-12-31").equals(userDetail.getObject().getIdValidTo())))
			{
				userDetail.getObject().setIdValidTo("长期");
			}
			model.addAttribute("provinces", codeListService.states().getObject());
			model.addAttribute("cities", codeListService.cities(userDetail.getObject().getProvince()).getObject());
			model.addAttribute("roles", codeListService.list(CodeListType.USER_ROLE.getCode()).getObject());
			model.addAttribute("banks", codeListService.banks().getObject());
			model.addAttribute("statuses", codeListService.list(CodeListType.USER_STATUS.getCode()).getObject());
		}
		
		return VM_PATH + "userEdit";
	}
	
	/**
	 * 
	* @Description:  保存修改的用户
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping(value="userEditSave", method=RequestMethod.POST)
	public String userEditSave(HttpServletRequest request, UserDetail user, MultipartFile idCopyFrontFile, MultipartFile idCopyBankFile) {
		logger.info("idCopyFrontFile="+idCopyFrontFile+"idCopyBankFile="+idCopyBankFile);
		 Result<UserDetail> userdetail = userBizService.getUser(user.getId());
		 if(userdetail.success() && userdetail.hasObject()) {
			 if(!StringUtils.equals(userdetail.getObject().getIdCopyFront(), user.getIdCopyFront())) UploadUtil.confirm(user.getIdCopyFront());
			 if(!StringUtils.equals(userdetail.getObject().getIdCopyBank(), user.getIdCopyBank())) UploadUtil.confirm(user.getIdCopyBank());
		 }else {
			 logger.info("没有id="+user.getId()+"的这个用户");
		 }
		String idCopyFront = UploadUtil.uploadImage(idCopyFrontFile);
		String idCopyBack = UploadUtil.uploadImage(idCopyBankFile);
		logger.info("idCopyFront="+idCopyFront+"idCopyBack="+idCopyBack);
		if(idCopyFront!=null) user.setIdCopyFront(idCopyFront);
		if(idCopyBack!=null) user.setIdCopyBank(idCopyBack);
		Result<Boolean> result= userBizService.saveEditUser(user);
		return "redirect:user.html";
	}
	
	/**
	 * 
	* @Description:  个人用户的详情(营销机构下的经纪人和经纪人下查看投资人都用此方法)
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("userDetail")
	public String userDetail(Long id, Model model) {
		// 常量合集
		assistInfoUtil.init(model);
		Result<UserDetail> userDetail = userBizService.getUser(id);
		if(userDetail.success()) {
			model.addAttribute("user", userDetail.getObject());
			if((("2999-12-31").equals(userDetail.getObject().getIdValidTo())))
			{
				userDetail.getObject().setIdValidTo("长期");
			}
			model.addAttribute("provinces", codeListService.states().getObject());
			model.addAttribute("cities", codeListService.cities(userDetail.getObject().getProvince()).getObject());
			model.addAttribute("roles", codeListService.list(CodeListType.USER_ROLE.getCode()).getObject());
			model.addAttribute("banks", codeListService.banks().getObject());
			model.addAttribute("statuses", codeListService.list(CodeListType.USER_STATUS.getCode()).getObject());
		}
		return VM_PATH + "userDetail";
	}
	

	/**
	 * 
	* @Description:  企业用户主界面
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("agency")
	public String agency(HttpServletRequest request, AgencySearch agencySearch, Model model) {
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		Result<List<AgencyItem>> result = userBizService.agencyPage(agencySearch,pager);
		if(result.success()) {
			model.addAttribute("user", agencySearch);
			pager.setElements(result.getObject());
			model.addAttribute("pager", pager);
			model.addAttribute("roles", codeListService.list(CodeListType.AGENCY_ROLE.getCode()).getObject());
			model.addAttribute("statuses", codeListService.list(CodeListType.USER_STATUS.getCode()).getObject());
		}
		//提取模板相关的枚举数据
		assistInfoUtil.init(model);
		return VM_PATH + "agency";
	}
	
	/**
	 * 
	* @Description:  跳转到企业用户新增界面
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("agencyAdd")
	public String agencyAdd(Model model) {
		model.addAttribute("roles", codeListService.list(CodeListType.AGENCY_ROLE.getCode()).getObject());
		model.addAttribute("banks", codeListService.banks().getObject());
		model.addAttribute("provinces", codeListService.states().getObject());
		return VM_PATH + "agencyAdd";
	}
	
	/**
	 * 
	* @Description:  保存新增的企业用户
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("agencyAddSave")
	public String agencyAddSave(AgencyDetail agencyDetail,MultipartFile licensePathFile, MultipartFile licenseCachetPathFile, MultipartFile legalPersonIdCopyFontFile, MultipartFile legalPersonIdCopyBackFile, MultipartFile openningLicensePathFile) {
		//logger.info("licensePathFile="+licensePathFile+"licenseCachetPathFile="+licenseCachetPathFile+"legalPersonIdCopyFontFile="+legalPersonIdCopyFontFile+"legalPersonIdCopyBackFile="+legalPersonIdCopyBackFile+"openningLicensePathFile"+openningLicensePathFile);
		UploadUtil.confirm(agencyDetail.getLicensePath());
		UploadUtil.confirm(agencyDetail.getLicenseCachetPath());
		UploadUtil.confirm(agencyDetail.getLegalPersonIdCopyBack());
		UploadUtil.confirm(agencyDetail.getLegalPersonIdCopyFont());
		UploadUtil.confirm(agencyDetail.getOpenningLicensePath());
		
		String licensePath = UploadUtil.uploadImage(licensePathFile);
		String licenseCachetPath = UploadUtil.uploadImage(licenseCachetPathFile);
		String LegalPersonIdCopyFont = UploadUtil.uploadImage(legalPersonIdCopyFontFile);
		String LegalPersonIdCopyBack = UploadUtil.uploadImage(legalPersonIdCopyBackFile);
		String openAccountLicensePic = UploadUtil.uploadImage(openningLicensePathFile);
		//logger.info("licensePath="+licensePath+"licenseCachetPath="+licenseCachetPath+"LegalPersonIdCopyFont="+LegalPersonIdCopyFont+"LegalPersonIdCopyBack="+LegalPersonIdCopyBack+"openAccountLicensePic="+openAccountLicensePic);
		if(licensePath!=null) agencyDetail.setLicensePath(licensePath);
		if(licenseCachetPath!=null) agencyDetail.setLicenseCachetPath(licenseCachetPath);
		if(LegalPersonIdCopyFont!=null) agencyDetail.setLegalPersonIdCopyFont(LegalPersonIdCopyFont);
		if(LegalPersonIdCopyBack!=null) agencyDetail.setLegalPersonIdCopyBack(LegalPersonIdCopyBack);
		if(openAccountLicensePic!=null) agencyDetail.setOpenningLicensePath(openAccountLicensePic);
		agencyDetail.setRegSource(CustomerConstants.RegisterSource.BEIYING.getCode());
		agencyDetail.setIdType(CustomerConstants.IdType.IDENTITY_CARD.getCode());
		logger.info(agencyDetail.getLicensePath()+agencyDetail.getLicenseCachetPath()+agencyDetail.getLegalPersonIdCopyBack()+agencyDetail.getLegalPersonIdCopyFont()+agencyDetail.getOpenningLicensePath());
		Result<User>  userResult = userBizService.saveAddAgency(agencyDetail);
		if(userResult.success()) {
			//发送邮件
			//2.保存成功后给用户发送邮件
			String userActivatedCode = userBizService.getUserActivatedCode(userResult.getObject().getId());
			agencyDetail.setAgencyId(userResult.getObject().getId());
			Map<String, Object> map = new HashMap<>();
			map.put("activatedCode", userActivatedCode);
			map.put("user", agencyDetail);
			String html = VelocityUtil.merge("register/activateEmail.vm", map);
			MailUtil.sendHtmlEmail(agencyDetail.getContactEmail(),html,"财富箱激活邮件");
		}
		
		return "redirect:agency.html";
	}
	
	/**
	 * 
	* @Description:  跳转到企业用户修改界面
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("agencyEdit")
	public String agencyEdit(Long id, Model model) {
		// 常量合集
		assistInfoUtil.init(model);
		Result<AgencyDetail> agency = userBizService.getAgency(id);
		model.addAttribute("agency", agency.getObject());
		model.addAttribute("roles", codeListService.list(CodeListType.AGENCY_ROLE.getCode()).getObject());
		model.addAttribute("banks", codeListService.banks().getObject());
		model.addAttribute("provinces",codeListService.states().getObject());
		model.addAttribute("citie1", codeListService.cities(agency.getObject().getProvince()).getObject());
		model.addAttribute("citie2", codeListService.cities(agency.getObject().getRegisterProvince()).getObject());
		return VM_PATH+"agencyEdit";
	}
	
	
	/**
	 * 
	* @Description:  企业用户的修改保存
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("agencyEditSave")
	public String agencyEditSave(AgencyDetail agencyDetail, MultipartFile licensePathFile, MultipartFile licenseCachetPathFile, MultipartFile legalPersonIdCopyFontFile, MultipartFile legalPersonIdCopyBackFile, MultipartFile openningLicensePathFile) {
		Result<AgencyDetail> agencyResult = userBizService.getAgency(agencyDetail.getUserId());
		if(agencyResult.success() && agencyResult.hasObject()) {
			AgencyDetail agency = agencyResult.getObject();
			if(!StringUtils.equals(agencyDetail.getLicensePath(), agency.getLicensePath())) UploadUtil.confirm(agency.getLicensePath());
			if(!StringUtils.equals(agencyDetail.getLicenseCachetPath(), agency.getLicenseCachetPath())) UploadUtil.confirm(agency.getLicenseCachetPath());
			if(!StringUtils.equals(agencyDetail.getLegalPersonIdCopyFont(), agency.getLegalPersonIdCopyFont())) UploadUtil.confirm(agency.getLegalPersonIdCopyFont());
			if(!StringUtils.equals(agencyDetail.getLegalPersonIdCopyBack(), agency.getLegalPersonIdCopyBack())) UploadUtil.confirm(agency.getLegalPersonIdCopyBack());
			if(!StringUtils.equals(agencyDetail.getOpenningLicensePath(), agency.getOpenningLicensePath())) UploadUtil.confirm(agency.getOpenningLicensePath());
		} 
		
		String licensePath = UploadUtil.uploadImage(licensePathFile);
		String licenseCachetPath = UploadUtil.uploadImage(licenseCachetPathFile);
		String legalPersonIdCopyFont = UploadUtil.uploadImage(legalPersonIdCopyFontFile);
		String legalPersonIdCopyBack = UploadUtil.uploadImage(legalPersonIdCopyBackFile);
		String openningLicensePath = UploadUtil.uploadImage(openningLicensePathFile);
		if(licensePath!=null) agencyDetail.setLicensePath(licensePath);
		if(licenseCachetPath!=null) agencyDetail.setLicenseCachetPath(licenseCachetPath);
		if(legalPersonIdCopyFont!=null) agencyDetail.setLegalPersonIdCopyFont(legalPersonIdCopyFont);
		if(legalPersonIdCopyBack!=null) agencyDetail.setLegalPersonIdCopyBack(legalPersonIdCopyBack);
		if(openningLicensePath!=null) agencyDetail.setOpenningLicensePath(openningLicensePath);
		Result<Boolean> result = userBizService.saveEditAgency(agencyDetail);
		if(result.success()) {
			logger.info("agencyEditSave is success");
		}else {
			logger.info("agencyEditSave is fail");
		}
		return "redirect:agency.html";
	}
	
	
	/**
	 * 
	* @Description:  企业用户的详情
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("agencyDetail")
	public String agencyDetail(Long id,Model model) {
		Result<AgencyDetail> agency = userBizService.getAgency(id);
		model.addAttribute("agency", agency.getObject());
		model.addAttribute("roles", codeListService.list(CodeListType.AGENCY_ROLE.getCode()).getObject());
		model.addAttribute("banks", codeListService.banks().getObject());
		model.addAttribute("provinces",codeListService.states().getObject());
		model.addAttribute("citie1", codeListService.cities(agency.getObject().getProvince()).getObject());
		model.addAttribute("citie2", codeListService.cities(agency.getObject().getRegisterProvince()).getObject());
		return VM_PATH+"agencyDetail";
	}
		
	/**
	 * 
	* @Description:  查看系统的营销机构
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("listMarketingAgency")
	public String listMarketingAgency(Model model,HttpServletRequest request, MarketingSearch marketingSearch) {
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		Result<List<MarketingItem>> result = userBizService.marktingPage(pager, marketingSearch);
		if(result.success()) {
			model.addAttribute("pager", pager);
			model.addAttribute("brokerSearch", marketingSearch);
		}
		assistInfoUtil.init(model);
		return VM_PATH+"listMarketingAgency";
	}
	
	
	/**
	 * 
	* @Description:  跳转到营销机构添加经纪人界面
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("brokerAdd")
	public String brokerAdd(Long id, Model model) {
		model.addAttribute("id", id);
		return VM_PATH+"brokerAdd";
	}
	
	
	
	
	/**
	 * 
	* @Description:   页面json验证，验证此经纪人是否已经添加进了营销机构,如果可以添加，返回客户编号
	* @param      
	* @return 
	* @throws
	 */
	@ResponseBody
	@RequestMapping("brokerCheck")
	public Object brokerCheck(Long agencyId, String userName) {
		Map<String, String> map = new HashMap<>();
		Result<User> user = userBizService.getUserByUserName(userName);
		if(user.success()) {
			UserProfile userprofile = user.getObject().getUserProfile();
			map.put("realName", userprofile.getRealName());
			//校验用户输入的用户是否已经建立关系
			Result<Boolean> check = userBizService.checkSetUpRelationship(userName);
			//说明此用户已经建立了联系
			if(check.success()&&check.getObject().equals(true)) {
				map.put("brokerExists", "true");
			}else {
			//说明用户没有建立联系，取得客户编号
				Result<String> customerResult = userBizService.getCustomerNum(agencyId);
				if(customerResult.success()) {
					map.put("customerNum", customerResult.getObject());
				}
			}
			return map;
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * 
	* @Description:  保存营销机构添加的经纪人
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("brokerAddSave")
	public String brokerAddSave(BrokerAdd brokerAdd) {
		Result<Boolean> result = userBizService.brokerAddSave(brokerAdd);
		return "redirect:listMarketingAgency.html";
	}
	
	
	/**
	 * 
	* @Description:  查看此营销机构下所有的经纪人
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("listAllBroker")
	public String listAllBorker(HttpServletRequest request, MarketingSearch marketingSearch, Model model) {
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		
		Result<List<MarketingItem>> result = userBizService.userPageForBroker(marketingSearch,pager);
		if(result.success()) {
			model.addAttribute("pager", pager);
			model.addAttribute("marketingSearch", marketingSearch);
		}
		assistInfoUtil.init(model);
		return VM_PATH+"listAllBroker";
	}
	
	/**
	 * 
	* @Description:  查看此经纪人下所有的投资人
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("listAllInvestor")
	public String listAllInvestor(HttpServletRequest request, MarketingSearch marketingSearch, Model model) {
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		Result<List<MarketingItem>> result = userBizService.userPageForInvestor(marketingSearch, pager);
		if(result.success()) {
			model.addAttribute("pager", pager);
			model.addAttribute("marketingSearch", marketingSearch);
		}
		assistInfoUtil.init(model);
		return VM_PATH+"listAllInvestor";
	}
	
	/**
	 * 
	* @Description:  对用户进行操作：修改用户的状态
	* @param      
	* @return 
	* @throws
	 */
	@RequestMapping("userStatus")
	@ResponseBody
	public Object userStatus(Long id, String status) {
		UserDetail userDetail = userBizService.updateUserStatus(id,status);
		JSONObject json = new JSONObject();
		if(userDetail!=null) {
			json.put("success", 1);
			json.put("user", userDetail);
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping("sendEmail")
	public String sendEmail(HttpServletRequest request) {
		Long userId = RequestUtil.getLongParam(request, "userId", null);
		if(userId != null) {
			String userActivatedCode = userBizService.getUserActivatedCode(userId);
			Result<UserDetail> userResult = userBizService.getUser(userId);
			if(userResult.success()) {
				Map<String, Object> map = new HashMap<>();
				map.put("activatedCode", userActivatedCode);
				map.put("user", userResult.getObject());
				String html = VelocityUtil.merge("register/activateEmail.vm", map);
				MailUtil.sendHtmlEmail(userResult.getObject().getEmail(),html,"财富箱激活邮件");
			}else {
				logger.info("发送邮件时，根据userID"+userId+"查询用户为空");
				return "fail";
			}
			
		}else {
			logger.info("用户的id未获取到");
		}
		return "success";
	}
	
	
}
