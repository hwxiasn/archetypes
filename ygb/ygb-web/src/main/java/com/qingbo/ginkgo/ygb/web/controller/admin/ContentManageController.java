package com.qingbo.ginkgo.ygb.web.controller.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.cms.entity.Article;
import com.qingbo.ginkgo.ygb.cms.entity.ArticleText;
import com.qingbo.ginkgo.ygb.cms.entity.Subject;
import com.qingbo.ginkgo.ygb.cms.enums.ArticleStatus;
import com.qingbo.ginkgo.ygb.cms.enums.ArticleWeight;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.web.biz.ContentBizService;
import com.qingbo.ginkgo.ygb.web.pojo.ArticleView;
import com.qingbo.ginkgo.ygb.web.pojo.SubjectView;

@Controller
@RequestMapping("admin/content")
public class ContentManageController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String PAGE_PATH = "admin/content/";

	@Autowired private ContentBizService contentBizService;
	
	/**
	 * 文章列表
	 */
	@RequestMapping("article")
	public String showArticles(ArticleView query, Model model, HttpServletRequest request) {
		// 获取主题列表
		Result<List<Subject>> resultSubjects = contentBizService.listSubjects("BYCFX");
		if(resultSubjects.getCode() != 0){
			logger.error(resultSubjects.getError() + " - " + resultSubjects.getMessage());
			return PAGE_PATH + "article";
		}
		
		model.addAttribute("subjects", JSON.toJSON(resultSubjects.getObject()));
		model.addAttribute("statusMap", ArticleStatus.getCodeNameMap());
		query.setSubject(query.getSubject() == null ? "" : "" + query.getSubject());
		model.addAttribute("query", query);
		
		// 准备查询参数
		SpecParam<Article> spec = new SpecParam<Article>();
		if(query != null){
			if(query.getSubject() != null && !"00".equals(query.getSubject())) spec.eq("subject", query.getSubject());
			spec.like("title", query.getTitle());
			spec.between("createTime", formatDate(query.getDateHead()), formatDate(query.getDateTail()));
			if(query.getStatus() != null && query.getStatus() >= 0) spec.eq("status", query.getStatus());
		}
		spec.eq("site", "BYCFX");
		
		// 准备分页参数
		Pager pager = new Pager();
		pager.setPageSize(10);
		pager.setProperties("weight,publishTime");
		pager.setDirection("desc");
		
		// 调用服务执行查询
		Result<PageObject<Article>> result = contentBizService.listArticles(spec, pager);

		// 返回结果
		if(result.getCode() != 0){
			logger.error(resultSubjects.getError() + " - " + resultSubjects.getMessage());
			return PAGE_PATH + "article";
		}else{
			pager.init(result.getObject().getTotal());
			pager.setElements(result.getObject().getList());
			model.addAttribute("pager", pager);
			return PAGE_PATH + "article";
		}
	}

	/**
	 * 跳转添加文章页
	 */
	@RequestMapping("articleAdd")
	public String articleAdd(String id, Model model, HttpServletRequest request) {
		// 获取主题列表
		Result<List<Subject>> resultSubjects = contentBizService.listSubjects("BYCFX");
		if(resultSubjects.getCode() != 0){
			logger.error(resultSubjects.getError() + " - " + resultSubjects.getMessage());
			return PAGE_PATH + "article";
		}
		model.addAttribute("subjects", JSON.toJSON(resultSubjects.getObject()));
		return PAGE_PATH + "articleAdd";
	}

	/**
	 * 保存文章添加
	 */
	@RequestMapping("doArticleAdd")
	public String doArticleAdd(ArticleView articleAddForm, Model model, HttpServletRequest request) {
		
		Article article = new Article();
		article.setSubject(articleAddForm.getSubject());
		article.setTitle(articleAddForm.getTitle());
		article.setAuthor("Admin");
		article.setSite("BYCFX");
		article.setCreateTime(new Date());
		article.setStatus(articleAddForm.getStatus());
		article.setWeight(ArticleWeight.ODINARY.getCode());
		ArticleText text = new ArticleText();
		text.setTextStuff(articleAddForm.getContent());
		article.setTextStuff(text);
		
		Result<Boolean> result = null;
		try{
			result = contentBizService.createArticle(article);
		}catch(Exception e){
			logger.error(e.getMessage());
			return "redirect:article.html";
		}
		
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:article.html";
		}else{
			return "redirect:article.html";
		}
	}

	/**
	 * 跳转文章修改页
	 */
	@RequestMapping("articleEdit")
	public String articleEdit(Long id, Model model, HttpServletRequest request) {
		// 获取主题列表
		Result<List<Subject>> resultSubjects = contentBizService.listSubjects("BYCFX");
		if(resultSubjects.getCode() != 0){
			logger.error(resultSubjects.getError() + " - " + resultSubjects.getMessage());
			return PAGE_PATH + "article";
		}
		model.addAttribute("subjects", JSON.toJSON(resultSubjects.getObject()));
		
		Result<Article> result = contentBizService.getArticle(id);
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:article.html";
		}else{
			model.addAttribute("article", result.getObject());
			return PAGE_PATH + "articleEdit";
		}
	}
	
	/**
	 * 保存文章修改
	 */
	@RequestMapping("doArticleEdit")
	public String doArticleEdit(ArticleView articleUpdateForm, Model model, HttpServletRequest request) {
		Article article = new Article();
		article.setId(articleUpdateForm.getId());
		article.setTitle(articleUpdateForm.getTitle());
		article.setSubject(articleUpdateForm.getSubject());
		ArticleText text = new ArticleText();
		text.setTextStuff(articleUpdateForm.getContent());
		article.setTextStuff(text);
		
		Result<Boolean> result = null;
		try{
			result = contentBizService.updateArticle(article, articleUpdateForm.getStatus());
		}catch(Exception e){
			logger.error(e.getMessage());
			return "redirect:article.html";
		}
		
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:article.html";
		}else{
			return "redirect:article.html";
		}
	}

	/**
	 * 文章上线
	 */
	@RequestMapping("online")
	public String doOnline(Long id, Model model, HttpServletRequest request) {
		Result<Boolean> result = contentBizService.publishArticle(id, null);
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:article.html";
		}else{
			return "redirect:article.html";
		}
	}
	
	/**
	 * 下线文章
	 */
	@RequestMapping("offline")
	public String doOffline(Long id, Model model, HttpServletRequest request) {
		Result<Boolean> result = contentBizService.offlineArticle(id);
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:article.html";
		}else{
			return "redirect:article.html";
		}
	}

	/**
	 * 置顶文章
	 */
	@RequestMapping("top")
	public String doTop(Long id, Model model, HttpServletRequest request) {
		Result<Integer> result = contentBizService.setArticleWeight(id, 10, true);
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:article.html";
		}else{
			return "redirect:article.html";
		}
	}
	
	/**
	 * 删除文章
	 */
	@RequestMapping("delete")
	public String doDeleteArticle(Long id, Model model, HttpServletRequest request) {
		Result<Boolean> result = contentBizService.deleteArticle(id);
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:article.html";
		}else{
			return "redirect:article.html";
		}
	}
	
	/**
	 * 专题列表
	 */
	@RequestMapping("subject")
	public String showSubjects(Model model, HttpServletRequest request){
		// 获取主题列表
		Result<List<Subject>> result = contentBizService.listSubjects("BYCFX");

		// 返回结果
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return PAGE_PATH + "subject";
		}else{
			model.addAttribute("subjects", result.getObject());
			return PAGE_PATH + "subject";
		}
	}
	
	/**
	 * 添加专题
	 */
	@RequestMapping("subjectAdd")
	public String addSubject(Long id, Model model, HttpServletRequest request){
		// 获取主题列表
		Result<List<Subject>> resultSubjects = contentBizService.listSubjects("BYCFX");
		if(resultSubjects.getCode() != 0){
			logger.error(resultSubjects.getError() + " - " + resultSubjects.getMessage());
			return PAGE_PATH + "subject";
		}
		model.addAttribute("subjects", JSON.toJSON(resultSubjects.getObject()));
		return PAGE_PATH + "subjectAdd";
	}
	
	/**
	 * 保存添加专题
	 */
	@RequestMapping("doSubjectAdd")
	public String doAddSubject(SubjectView form, Model model, HttpServletRequest request){
		
		Subject subject = new Subject();
		subject.setSite("BYCFX");
		subject.setCode(form.getCode());
		subject.setName(form.getName());
		subject.setParentCode(form.getParentCode());
		subject.setLeaf(form.getLeaf());
		subject.setLevel(form.getLevel());
		subject.setSerial(form.getSerial());
		
		Result<Boolean> result = null;
		try{
			result = contentBizService.createSubject(subject);
		}catch(Exception e){
			logger.error(e.getMessage());
			return "redirect:subject.html";
		}
		
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:subject.html";
		}else{
			return "redirect:subject.html";
		}
	}
	
	/**
	 * 跳转修改专题页
	 */
	@RequestMapping("subjectEdit")
	public String editSubject(Long id, Model model, HttpServletRequest request){
		Result<Subject> result = contentBizService.getSubject(id);
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:subject.html";
		}else{
			model.addAttribute("subject", result.getObject());
			return PAGE_PATH + "subjectEdit";
		}
	}
	
	/**
	 * 保存修改专题
	 */
	@RequestMapping("doSubjectEdit")
	public String doEditSubject(SubjectView form, Model model, HttpServletRequest request){
		
		Subject subject = new Subject();
		subject.setId(form.getId());
		subject.setCode(form.getCode());
		subject.setName(form.getName());
		subject.setLeaf(form.getLeaf());
		subject.setSerial(form.getSerial());
		
		Result<Boolean> result = null;
		try{
			result = contentBizService.updateSubject(subject);
		}catch(Exception e){
			logger.error(e.getMessage());
			return "redirect:subject.html";
		}
		
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:subject.html";
		}else{
			return "redirect:subject.html";
		}
	}
	
	/**
	 * 删除专题
	 */
	@RequestMapping("subjectDelete")
	public String deleteSubject(Long id, Model model, HttpServletRequest request){
		Result<Boolean> result = contentBizService.deleteSubject(id);
		if(result.getCode() != 0){
			logger.error(result.getError() + " - " + result.getMessage());
			return "redirect:subject.html";
		}else{
			return "redirect:subject.html";
		}
	}
	
	/**
	 * 表单日期格式化实用
	 */
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Date formatDate(String dateString){
		Date date = null;
		if(dateString != null){
			try {
				date = sdf.parse(dateString);
			} catch (ParseException e) {
				return null;
			}
		}
		return date;
	}

}
