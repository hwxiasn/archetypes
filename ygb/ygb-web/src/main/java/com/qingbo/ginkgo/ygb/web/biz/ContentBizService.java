package com.qingbo.ginkgo.ygb.web.biz;

import java.util.Date;
import java.util.List;

import com.qingbo.ginkgo.ygb.cms.entity.Article;
import com.qingbo.ginkgo.ygb.cms.entity.Subject;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;

public interface ContentBizService {

	/**
	 * 列出平台下所有专题
	 * @param tag 平台标志
	 * @return
	 */
	Result<List<Subject>> listSubjects(String tag);
	
	/**
	 * 分页查询文章
	 * @param 查询条件
	 * @param 分页配置
	 * @return
	 */
	Result<PageObject<Article>> listArticles(SpecParam<Article> spec, Pager pager);
	
	/**
	 * 查询专题热门文章
	 * @param site			站点平台
	 * @param subjectCode	专题编码
	 * @param count			指定条数
	 * @return
	 */
	Result<List<Article>> listTopArticles(String site, String subjectCode, Integer count);
	
	/**
	 * 创建一篇文章
	 * @param article 文章实体
	 * @return
	 */
	Result<Boolean> createArticle(Article article);
	
	/**
	 * 查询指定ID的文章
	 * @param id 文章ID
	 * @return
	 */
	Result<Article> getArticle(Long id);
	
	/**
	 * 修改文章
	 * @param article 文章
	 * @param status 状态
	 * @return
	 */
	Result<Boolean> updateArticle(Article article, Integer status);
	
	/**
	 * 上线一篇文章
	 * @param id	文章ID
	 * @param date	发布时间（定时发布使用）
	 * @return
	 */
	Result<Boolean> publishArticle(Long id, Date date);
	
	/**
	 * 下线文章
	 * @param id 文章ID
	 * @return
	 */
	Result<Boolean> offlineArticle(Long id);
	
	/**
	 * 设定文章权重
	 * @param id		文章ID
	 * @param level		权重级数
	 * @param direction	增量方向（1，升级； -1，降级）
	 * @return
	 */
	Result<Integer> setArticleWeight(Long id, Integer level, Boolean direction);
	
	/**
	 * 删除文章
	 * @param id
	 * @return
	 */
	Result<Boolean> deleteArticle(Long id);
	
	/**
	 * 创建专题
	 * @param subject 专题实体
	 * @return
	 */
	Result<Boolean> createSubject(Subject subject);
	
	/**
	 * 获取一个专题
	 * @param id 专题ID
	 * @return
	 */
	Result<Subject> getSubject(Long id);
	
	/**
	 * 修改一个专题 
	 * @param subject 专题实体
	 * @return
	 */
	Result<Boolean> updateSubject(Subject subject);
	
	/**
	 * 删除一个专题
	 * @param id 专题ID
	 * @return
	 */
	Result<Boolean> deleteSubject(Long id);
}
