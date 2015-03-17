package com.qingbo.ginkgo.ygb.cms.service;

import java.util.Date;
import java.util.List;

import com.qingbo.ginkgo.ygb.cms.entity.Article;
import com.qingbo.ginkgo.ygb.cms.entity.ArticleText;
import com.qingbo.ginkgo.ygb.cms.entity.Message;
import com.qingbo.ginkgo.ygb.cms.entity.MessageCounter;
import com.qingbo.ginkgo.ygb.cms.entity.Subject;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;

public interface ContentService {
	/**
	 * 分页查询文章
	 */
	Result<PageObject<Article>> listArticles(SpecParam<Article> specs, Pager pager);
	
	/**
	 * 查询专题文章列表
	 */
	Result<List<Article>> listTopArticles(String site, String subjectCode, Integer count);
	
	/**
	 * 读取文章内容
	 */
	Result<ArticleText> readArticle(Long articleId);
	
	/**
	 * 获取文章整体
	 */
	Result<Article> getArticle(Long articleId);
	
	/**
	 * 获取文章项
	 */
	Result<Article> getArticleItem(Long articleId);
	
	/**
	 * 创建文章
	 */
	Result<Boolean> createArticle(Article article);
	
	/**
	 * 修改文章
	 */
	Result<Boolean> updateArticle(Article article, Integer status);
	
	/**
	 * 删除文章
	 */
	Result<Boolean> deleteArticle(Long articleId);
	
	/**
	 * 发布文章
	 */
	Result<Boolean> publishArticle(Long articleId, Date publishTime);
	
	/**
	 * 下线文章
	 */
	Result<Boolean> offlineArticle(Long articleId);
	
	/**
	 * 调整文章级数
	 */
	Result<Integer> setArticleWeight(Long articleId, Integer level, Boolean direction);
	
		
	/**
	 * 列出子专题
	 */
	Result<List<Subject>> listSubjects(String site, String subjectCode);
	
	/**
	 * 找出所有专题
	 */
	Result<List<Subject>> listSubjects(String site);
	
	/**
	 *  获取专题
	 */
	Result<Subject> getSubject(Long id);
	
	/**
	 * 创建专题
	 */
	Result<Boolean> createSubject(Subject subject);
	
	/**
	 * 修改专题
	 */
	Result<Boolean> updateSubject(Subject subject);
	
	/**
	 * 删除专题
	 */
	Result<Boolean> deleteSubject(Long id);
	
	/**
	 * 分页查询消息
	 */
	Result<PageObject<Message>> listMessages(SpecParam<Message> specs, Pager pager, Long userId);
	
	/**
	 * 查询用户消息列表
	 */
	Result<List<Message>> listTopMessages(Long userId, Integer count);
	
	/**
	 * 创建消息
	 */
	Result<Message> createMessage(Message message);
	
	/**
	 * 阅读消息
	 */
	Result<Message> readMessage(Long id);
	
	/**
	 * 删除消息
	 */
	Result<Boolean> deleteMessage(Long id);
	
	/**
	 * 批量设为已读
	 */
	Result<Boolean> readMessage(String ids);
	
	/**
	 * 批量删除消息
	 */
	Result<Boolean> deleteMessage(String ids);
	
	/**
	 * 消息简易统计
	 */
	Result<MessageCounter> countMessages(Long userId);
}
