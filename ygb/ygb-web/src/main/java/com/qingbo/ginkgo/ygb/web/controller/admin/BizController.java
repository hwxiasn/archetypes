package com.qingbo.ginkgo.ygb.web.controller.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.RequestUtil;
import com.qingbo.ginkgo.ygb.web.biz.BizService;

//@RequiresAdminUser
@Controller
@RequestMapping("admin/biz")
public class BizController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String PAGE_PATH = "admin/biz/";
	private final int pageSize = 20;
	
	@Resource private BizService bizService;
	
	@RequestMapping("invests")
	public String invests(HttpServletRequest request, HttpServletResponse response, Model model) {
		Pager pager = new Pager();
		pager.setPageSize(RequestUtil.getIntParam(request, "pageSize", pageSize));
		Integer totalRows = RequestUtil.getIntParam(request, "totalRows", null);
		if(totalRows!=null && totalRows>0) pager.init(totalRows);
		Integer currentPage = RequestUtil.getIntParam(request, "currentPage", null);
		if(currentPage!=null && currentPage>0) pager.page(currentPage);
		
		Map<String, String> search = RequestUtil.stringMap(request);
		String byRole = RequestUtil.getStringParam(request, "byRole", "broker");
		if(!search.containsKey("byRole")) search.put("byRole", byRole);
		pager = bizService.investsPage(search, pager);
		if(pager.getPageSize() == 0) {
			String[] titles = null;
			if("broker".equals(byRole)) {
				titles = new String[] {"用户ID","登录名","经纪人","营销机构","投资人数","投资笔数","业绩（万）"};
			}else {
				titles = new String[] {"用户ID","登录名","营销机构","投资人数","投资笔数","业绩（万）"};
			}
			Workbook workbook = export(titles, pager);
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("content-disposition", "attachment;filename=" + "registerExport_" + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_HH-mm-ss")+".xls");
			try{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				workbook.write(baos);
				response.setContentLength(baos.size());
				IOUtils.copy(new ByteArrayInputStream(baos.toByteArray()), response.getOutputStream());
			}catch (Exception e) {
				e.printStackTrace();
				logger.warn("registerExport failed.", e);
			}
			return null;
		}else {
			model.addAttribute("pager", pager);
			model.addAttribute("register", search);
			return PAGE_PATH + "invests";
		}
	}
	
	private Workbook export(String[] titles, Pager pager) {
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();
		int r = 0, col = 0;
		Row row = sheet.createRow(r++);
		//列标题
		for(String title : titles) {
			row.createCell(col++).setCellValue(title);
		}
		for(Object obj : pager.getElements()) {
			row = sheet.createRow(r++);
			col = 0;
			Object[] arr = (Object[])obj;
			for(Object item:arr) {
				row.createCell(col++).setCellValue(ObjectUtils.toString(item));
			}
		}
		return wb;
	}
}
