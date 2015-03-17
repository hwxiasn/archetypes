package com.qingbo.ginkgo.ygb.web.biz.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.cms.entity.Article;
import com.qingbo.ginkgo.ygb.cms.entity.Subject;
import com.qingbo.ginkgo.ygb.cms.service.ContentService;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.web.biz.ContentBizService;

@Service("contentBizService")
public class ContentBizServiceImpl implements ContentBizService {

	@Autowired private ContentService contentService;

	@Override
	public Result<List<Subject>> listSubjects(String tag) {
		return contentService.listSubjects(tag);
	}

	@Override
	public Result<PageObject<Article>> listArticles(SpecParam<Article> spec, Pager pager) {
		return contentService.listArticles(spec, pager);
	}
	
	@Override
	public Result<List<Article>> listTopArticles(String site, String subjectCode, Integer count) {
		return contentService.listTopArticles(site, subjectCode, count);
	}

	@Override
	public Result<Boolean> createArticle(Article article) {
		return contentService.createArticle(article);
	}

	@Override
	public Result<Article> getArticle(Long id) {
		return contentService.getArticle(id);
	}

	@Override
	public Result<Boolean> updateArticle(Article article, Integer status) {
		return contentService.updateArticle(article, status);
	}

	@Override
	public Result<Boolean> publishArticle(Long id, Date date) {
		return contentService.publishArticle(id, date);
	}

	@Override
	public Result<Boolean> offlineArticle(Long id) {
		return contentService.offlineArticle(id);
	}

	@Override
	public Result<Integer> setArticleWeight(Long id, Integer level, Boolean direction) {
		return contentService.setArticleWeight(id, level, direction);
	}

	@Override
	public Result<Boolean> deleteArticle(Long id) {
		return contentService.deleteArticle(id);
	}

	@Override
	public Result<Boolean> createSubject(Subject subject) {
		return contentService.createSubject(subject);
	}

	@Override
	public Result<Subject> getSubject(Long id) {
		return contentService.getSubject(id);
	}

	@Override
	public Result<Boolean> updateSubject(Subject subject) {
		return contentService.updateSubject(subject);
	}

	@Override
	public Result<Boolean> deleteSubject(Long id) {
		return contentService.deleteSubject(id);
	}
}
