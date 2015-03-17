package com.qingbo.ginkgo.ygb.cms.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.cms.entity.Article;
import com.qingbo.ginkgo.ygb.cms.entity.ArticlePublishTask;
import com.qingbo.ginkgo.ygb.cms.entity.ArticleText;
import com.qingbo.ginkgo.ygb.cms.entity.Message;
import com.qingbo.ginkgo.ygb.cms.entity.MessageCounter;
import com.qingbo.ginkgo.ygb.cms.entity.Subject;
import com.qingbo.ginkgo.ygb.cms.enums.ArticlePublishTaskStatus;
import com.qingbo.ginkgo.ygb.cms.enums.ArticleStatus;
import com.qingbo.ginkgo.ygb.cms.enums.ArticleWeight;
import com.qingbo.ginkgo.ygb.cms.enums.MessageStatus;
import com.qingbo.ginkgo.ygb.cms.repository.ArticlePublishTaskRepository;
import com.qingbo.ginkgo.ygb.cms.repository.ArticleRepository;
import com.qingbo.ginkgo.ygb.cms.repository.ArticleTextRepository;
import com.qingbo.ginkgo.ygb.cms.repository.MessageRepository;
import com.qingbo.ginkgo.ygb.cms.repository.SubjectRepository;
import com.qingbo.ginkgo.ygb.cms.service.ContentService;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;

@Service("contentService")
public class ContentServiceImpl implements ContentService {
	
	private static Log log = LogFactory.getLog(ContentServiceImpl.class);
	private static final String QUEUING_MAKE_SRC = "04";
	private static final String SITES_SHARE_ROOT = "COMMON";
	
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private ArticleRepository articleRepository;
	@Autowired private ArticleTextRepository articleTextRepository;
	@Autowired private MessageRepository messageRepository;
	@Autowired private ArticlePublishTaskRepository articlePublishTaskRepository;
	
	private QueuingService queuingService;
	
	private ErrorMessage errorResult = new ErrorMessage("cms-errorcode.properties");

	/**
	 * 分页查询文章
	 */
	@Override
	public Result<PageObject<Article>> listArticles(SpecParam<Article> specParam, Pager pager) {
		log.info(SimpleLogFormater.formatParams(specParam, pager));
		
		Result<PageObject<Article>> result;
		
		// 参数校验
		if(specParam == null || pager == null){
			result = errorResult.newFailure("CMS1001");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(pager.getPageSize() <= 0){
			result = errorResult.newFailure("CMS1002");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备分页参数
		Pageable pageable = pager.getDirection() == null || pager.getProperties() == null ? 
								new PageRequest(pager.getCurrentPage() - 1, pager.getPageSize()) :
								new PageRequest(pager.getCurrentPage() - 1, pager.getPageSize(), 
												Direction.valueOf(pager.getDirection()), 
												pager.getProperties().split(","));
		// 准备查询参数
		specParam.eq("deleted", false);								// 未删除
//		specParam.eq("status", ArticleStatus.ONLINE.getCode());		// 已发布
		
		// 执行查询
		Page<Article> resultSet = null;
		try{			
			resultSet = articleRepository.findAll(SpecUtil.spec(specParam), pageable);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0001");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(new PageObject<Article>((int)resultSet.getTotalElements(), resultSet.getContent()));
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	/**
	 * 专题下前部文章列表
	 */
	@Override
	public Result<List<Article>> listTopArticles(String site, String subjectCode, Integer count) {
		log.info(SimpleLogFormater.formatParams(subjectCode, count));
		
		Result<List<Article>> result;
		
		// 参数校验
		if(subjectCode == null || count == null){
			result = errorResult.newFailure("CMS1021");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(count <= 0){
			result = errorResult.newFailure("CMS1022");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备查询参数
		SpecParam<Article> specParam = new SpecParam<Article>();
		specParam.eq("site", site);									// 指定站点
		specParam.eq("subject", subjectCode);						// 指定专题
		specParam.eq("deleted", false);								// 未删除
		specParam.eq("status", ArticleStatus.ONLINE.getCode());		// 已发布
		
		// 准备分页条件
		Sort sort = new Sort(Sort.Direction.DESC, "weight").and(new Sort(Sort.Direction.DESC, "publishTime"));	// 排序
		Pageable pageable = new PageRequest(0, count, sort);
		
		// 执行查询
		Page<Article> resultSet = null;
		try{
			resultSet = articleRepository.findAll(SpecUtil.spec(specParam), pageable);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0011");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(resultSet.getContent());
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}
	

	/**
	 * 读取文章正文
	 */
	@Override
	public Result<ArticleText> readArticle(Long articleId) {
		log.info(SimpleLogFormater.formatParams(articleId));
		
		Result<ArticleText> result;
		
		// 参数校验
		if(articleId == null){
			result = errorResult.newFailure("CMS1041");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备参数
		SpecParam<ArticleText> specParam = new SpecParam<ArticleText>();
		specParam.eq("articleId", articleId);
		
		// 执行查询
		ArticleText text = null;
		try{
			text = articleTextRepository.findOne(SpecUtil.spec(specParam));
		}catch(Exception e){
			result = errorResult.newFailure("CMS0021");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(text);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}
	
	/**
	 * 获取文章整体
	 */
	@Override
	public Result<Article> getArticle(Long articleId){

		Result<Article> result;
		
		// 参数校验
		if(articleId == null){
			result = errorResult.newFailure("CMS1042");
			return result;
		}
		
		// 准备参数
		SpecParam<Article> articleParam = new SpecParam<Article>();
		articleParam.eq("id", articleId);
		SpecParam<ArticleText> specParam = new SpecParam<ArticleText>();
		specParam.eq("articleId", articleId);
		
		// 执行查询
		Article article = null;
		ArticleText text = null;
		try{
			article = articleRepository.findOne(SpecUtil.spec(articleParam));
			text = articleTextRepository.findOne(SpecUtil.spec(specParam));
		}catch(Exception e){
			result = errorResult.newFailure("CMS0022");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		article.setTextStuff(text);
		return Result.newSuccess(article);
	}
	
	/**
	 * 获取文章项
	 */
	public Result<Article> getArticleItem(Long articleId){
		Result<Article> result;
		
		// 参数校验
		if(articleId == null){
			result = errorResult.newFailure("CMS1043");
			return result;
		}
		
		// 准备参数
		SpecParam<Article> articleParam = new SpecParam<Article>();
		articleParam.eq("id", articleId);
		
		// 执行查询
		Article article = null;
		try{
			article = articleRepository.findOne(SpecUtil.spec(articleParam));
		}catch(Exception e){
			result = errorResult.newFailure("CMS0023");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		return Result.newSuccess(article);
	}

	/**
	 * 创建文章
	 */
	@Transactional
	@Override
	public Result<Boolean> createArticle(Article article) {
		log.info(SimpleLogFormater.formatParams(article));
		
		Result<Boolean> result;
		
		// 校验参数
		if(article == null){
			result = errorResult.newFailure("CMS1061");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		ArticleText text = article.getTextStuff();
		if(text == null){
			result = errorResult.newFailure("CMS1062");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		if(article.getStatus() == null){
			result = errorResult.newFailure("CMS1063");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		if(article.getStatus() != ArticleStatus.DRAFT.getCode() && article.getStatus() != ArticleStatus.READY.getCode()){
			result = errorResult.newFailure("CMS1064");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备序列号
		Result<Long> articleQueuing = queuingService.next(QUEUING_MAKE_SRC);
	    if(articleQueuing.getError() != null ){
	    	return errorResult.newFailure("CMS0032");
	    }
	    Result<Long> textQueuing = queuingService.next(QUEUING_MAKE_SRC);
	    if(textQueuing.getError() != null ){
	    	return errorResult.newFailure("CMS0032");
	    }
	    
		// 持久对象
		try{
			article.setId(articleQueuing.getObject());
			Article articleSaved = articleRepository.save(article);
			text.setArticleId(articleSaved.getId());
			text.setId(textQueuing.getObject());
			articleTextRepository.save(text);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0031");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}

		// 返回结果
		result = Result.newSuccess(true);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	/**
	 * 修改文章
	 */
	@Override
	public Result<Boolean> updateArticle(Article article, Integer status) {
		log.info(SimpleLogFormater.formatParams(article));
		
		Result<Boolean> result;
		
		// 校验参数
		if(article == null){
			result = errorResult.newFailure("CMS1081");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(article.getId() == null){
			result = errorResult.newFailure("CMS1082");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(article.getTextStuff() == null){
			result = errorResult.newFailure("CMS1083");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		if(status == null){
			result = errorResult.newFailure("CMS1084");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		if(status != ArticleStatus.DRAFT.getCode() && status != ArticleStatus.READY.getCode()){
			result = errorResult.newFailure("CMS1085");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		try{
			// 准备对象
			Article articleFetched = articleRepository.findOne(article.getId());
			articleFetched.setSubject(article.getSubject() == null ? articleFetched.getSubject() : article.getSubject());
			articleFetched.setTitle(article.getTitle() == null ? articleFetched.getTitle() : article.getTitle());
			articleFetched.setAuthor(article.getAuthor() == null ? articleFetched.getAuthor() : article.getAuthor());
			articleFetched.setStatus(status);
			articleFetched.setWeight(article.getWeight() == null ? articleFetched.getWeight() : article.getWeight());
			ArticleText textFetched = articleTextRepository.findByArticleId(article.getId());
			textFetched.setTextStuff(article.getTextStuff().getTextStuff() == null ? textFetched.getTextStuff() : article.getTextStuff().getTextStuff());
			
			// 持久对象
			articleRepository.save(articleFetched);
			articleTextRepository.save(textFetched);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0041");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(true);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}
	
	/**
	 * 删除文章
	 */
	@Override
	public Result<Boolean> deleteArticle(Long articleId) {
		log.info(SimpleLogFormater.formatParams(articleId));
		
		Result<Boolean> result;
		
		// 参数校验
		if(articleId == null || articleId <= 0){
			result = errorResult.newFailure("CMS1181");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备对象
		Article article = articleRepository.findOne(articleId);
		if(ArticleStatus.ONLINE.getCode().equals(article.getStatus())){
			result = errorResult.newFailure("CMS1182");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		article.setDeleted(true);
		
		// 持久化处理
		try{
			articleRepository.save(article);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0141");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(true);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	/**
	 * 发布文章
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public Result<Boolean> publishArticle(Long articleId, Date publishTime) {
		log.info(SimpleLogFormater.formatParams(articleId, publishTime));
		
		Result<Boolean> result;
		
		// 参数校验
		if(articleId == null || articleId <= 0){
			result = errorResult.newFailure("CMS1101");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备对象
		Article article = articleRepository.findOne(articleId);
		if(article == null){
			result = errorResult.newFailure("CMS1103");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(ArticleStatus.DRAFT.getCode().equals(article.getStatus()) ||
				ArticleStatus.ONLINE.getCode().equals(article.getStatus())	){
			result = errorResult.newFailure("CMS1102");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		if(publishTime != null){
			// 准备序列号
			Result<Long> queuing = queuingService.next(QUEUING_MAKE_SRC);
		    if(queuing.getError() != null ){
		    	return errorResult.newFailure("CMS0042");
		    }
			
		    // 创建定时任务
			ArticlePublishTask task = new ArticlePublishTask();
			task.setId(queuing.getObject());
			task.setArticleId(articleId);
			task.setPublishTime(publishTime);
			task.setStatus(ArticlePublishTaskStatus.CREATED.getCode());
			// 修改文章状态
			article.setStatus(ArticleStatus.TOPUBLISH.getCode());
			
			// 持久操作
			articlePublishTaskRepository.save(task);
			articleRepository.save(article);
			
			// 返回结果
			result = Result.newSuccess(true);
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}else{
			
			article.setStatus(ArticleStatus.ONLINE.getCode());
			article.setPublishTime(new Date());
			
			// 持久化处理
			try{
				articleRepository.save(article);
			}catch(Exception e){
				result = errorResult.newFailure("CMS0041");
				log.error(SimpleLogFormater.formatException(result.getMessage(), e));
				log.info(SimpleLogFormater.formatResult(result));
				return result;
			}
			
			// 返回结果
			result = Result.newSuccess(true);
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
	}

	/**
	 * 下线文章
	 */
	@Override
	public Result<Boolean> offlineArticle(Long articleId) {
		log.info(SimpleLogFormater.formatParams(articleId));
		
		Result<Boolean> result;
		
		// 参数校验
		if(articleId == null || articleId <= 0){
			result = errorResult.newFailure("CMS1121");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备对象
		Article article = articleRepository.findOne(articleId);
		article.setStatus(ArticleStatus.OFFLINE.getCode());
		
		// 持久化处理
		try{
			articleRepository.save(article);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0061");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(true);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}
	

	/**
	 * 调整文章等级
	 */
	@Override
	public Result<Integer> setArticleWeight(Long articleId, Integer level, Boolean direction) {
		log.info(SimpleLogFormater.formatParams(articleId, level, direction));
		
		Result<Integer> result;
		
		// 参数校验
		if(articleId == null || articleId <= 0){
			result = errorResult.newFailure("CMS1141");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(direction == null){
			result = errorResult.newFailure("CMS1142");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}

		// 准备对象
		Article article = articleRepository.findOne(articleId);
		Integer levelToChange;
		if(level == null){
			if(direction){
				levelToChange = article.getWeight() + 1;
			}else{
				levelToChange = article.getWeight() - 1;
			}
		}else{
			if(direction){
				levelToChange = article.getWeight() + level;
			}else{
				levelToChange = article.getWeight() - level;
			}
		}
		levelToChange = levelToChange > ArticleWeight.TOP.getCode() ? ArticleWeight.TOP.getCode() : levelToChange;
		levelToChange = levelToChange < ArticleWeight.BOTTOM.getCode() ? ArticleWeight.BOTTOM.getCode() : levelToChange;
		article.setWeight(levelToChange);
		
		// 持久化处理
		try{
			articleRepository.save(article);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0071");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(levelToChange);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	/**
	 * 列出子专题
	 */
	@Override
	public Result<List<Subject>> listSubjects(String site, String subjectCode) {
		log.info(SimpleLogFormater.formatParams(subjectCode));
		
		Result<List<Subject>> result;
		
		// 参数校验
		if(subjectCode == null){
			result = errorResult.newFailure("CMS1161");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(site == null){
			return errorResult.newFailure("CMS1261");
		}
		
		// 准备查询参数
		List<String> siteIn = new ArrayList<String>();
		siteIn.add(SITES_SHARE_ROOT);
		siteIn.add(site);
		
		SpecParam<Subject> specParam = new SpecParam<Subject>();
		if(subjectCode != null){
			specParam.eq("parentCode", subjectCode);	
		}
		specParam.eq("deleted", false);				// 未删除的
		Sort sort = new Sort(Sort.Direction.ASC, "serial");
		
		// 执行查询
		List<Subject> subjects = null;
		try{
			subjects = subjectRepository.findAll(SpecUtil.spec(specParam), sort);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0081");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(subjects);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}
	
	/**
	 * 找出所有专题
	 */
	@Override
	public Result<List<Subject>> listSubjects(String site) {
		
		Result<List<Subject>> result;
		
		if(site == null){
			return errorResult.newFailure("CMS1261");
		}
		
		List<String> siteIn = new ArrayList<String>();
		siteIn.add(SITES_SHARE_ROOT);
		siteIn.add(site);
		
		// 准备查询参数
		SpecParam<Subject> specParam = new SpecParam<Subject>();
		specParam.eq("deleted", false);				// 未删除的
		specParam.in("site", siteIn);				// 网站平台
		Sort sort = new Sort(Sort.Direction.ASC, "code").and(new Sort(Sort.Direction.ASC, "serial"));
		
		// 执行查询
		List<Subject> subjects = null;
		try{
			subjects = subjectRepository.findAll(SpecUtil.spec(specParam), sort);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0121");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(subjects);
		return result;
	}
	
	/**
	 * 获取单个专题
	 */
	@Override
	public Result<Subject> getSubject(Long id) {
		log.info(SimpleLogFormater.formatParams(id));
		
		Result<Subject> result;
		
		// 参数校验
		if(id == null){
			return errorResult.newFailure("CMS1241");
		}
		
		// 准备参数
		SpecParam<Subject> subjectParam = new SpecParam<Subject>();
		subjectParam.eq("id", id);
		
		// 执行查询
		Subject subject = null;
		try{
			subject = subjectRepository.findOne(SpecUtil.spec(subjectParam));
		}catch(Exception e){
			result = errorResult.newFailure("CMS0131");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		return Result.newSuccess(subject);
	}

	/**
	 * 创建专题
	 */
	@Override
	public Result<Boolean> createSubject(Subject subject) {
		log.info(SimpleLogFormater.formatParams(subject));
		
		Result<Boolean> result;
		
		// 参数校验
		if(subject == null){
			result = errorResult.newFailure("CMS1181");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		Result<Long> queuing = queuingService.next(QUEUING_MAKE_SRC);
	    if(queuing.getError() != null ){
	    	return errorResult.newFailure("CMS0092");
	    }
		
		// 持久对象
		try{
			subject.setId(queuing.getObject());
			subjectRepository.save(subject);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0091");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
			
		// 返回结果
		result = Result.newSuccess(true);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	/**
	 * 修改专题
	 */
	@Override
	public Result<Boolean> updateSubject(Subject subject) {
		log.info(SimpleLogFormater.formatParams(subject));
		
		Result<Boolean> result;
		
		// 校验参数
		if(subject == null){
			result = errorResult.newFailure("CMS1201");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(subject.getId() == null){
			result = errorResult.newFailure("CMS1202");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		try{
			// 准备对象
			Subject subjectFetched = subjectRepository.findOne(subject.getId());
			subjectFetched.setCode(subject.getCode() == null ? subjectFetched.getCode() : subject.getCode());
			subjectFetched.setName(subject.getName() == null ? subjectFetched.getName() : subject.getName());
			subjectFetched.setParentCode(subject.getParentCode() == null ? subjectFetched.getParentCode() : subject.getParentCode());
			subjectFetched.setLeaf(subject.isLeaf() == null ? subjectFetched.isLeaf() : subject.isLeaf());
			subjectFetched.setSerial(subject.getSerial() == null ? subjectFetched.getSerial() : subject.getSerial());
			
			// 持久对象
			subjectRepository.save(subjectFetched);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0101");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}

		// 返回结果
		result = Result.newSuccess(true);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	/**
	 * 删除专题
	 */
	@Override
	public Result<Boolean> deleteSubject(Long id) {
		log.info(SimpleLogFormater.formatParams(id));
		
		Result<Boolean> result;
		
		// 参数校验
		if(id == null){
			result = errorResult.newFailure("CMS1221");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备对象
		Subject subject = subjectRepository.findOne(id);
		subject.setDeleted(true);
		
		// 持久化操作
		try{
			subjectRepository.save(subject);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0111");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}	
		
		// 返回结果
		result = Result.newSuccess(true);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	@Override
	public Result<PageObject<Message>> listMessages(SpecParam<Message> specs, Pager pager, Long userId) {
		log.info(SimpleLogFormater.formatParams(specs, pager));
		
		Result<PageObject<Message>> result;
		
		// 参数校验
		if(specs == null || pager == null){
			result = errorResult.newFailure("CMS1291");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(pager.getPageSize() <= 0){
			result = errorResult.newFailure("CMS1292");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		List<Long> receivers = new ArrayList<Long>();
		receivers.add(userId);
		receivers.add(100000L);	// TODO
		
		specs.in("receiverId", receivers);
		
		// 准备分页参数
		Pageable pageable = pager.getDirection() == null || pager.getProperties() == null ? 
								new PageRequest(pager.getCurrentPage() - 1, pager.getPageSize()) :
								new PageRequest(pager.getCurrentPage() - 1, pager.getPageSize(), 
												Direction.valueOf(pager.getDirection()), 
												pager.getProperties().split(","));
		// 准备查询参数
		specs.eq("deleted", false);								// 未删除
		
		// 执行查询
		Page<Message> resultSet = null;
		try{			
			resultSet = messageRepository.findAll(SpecUtil.spec(specs), pageable);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0151");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(new PageObject<Message>((int)resultSet.getTotalElements(), resultSet.getContent()));
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	@Override
	public Result<List<Message>> listTopMessages(Long userId, Integer count) {
		log.info(SimpleLogFormater.formatParams(userId, count));
		
		Result<List<Message>> result;
		
		// 参数校验
		if(userId == null || count == null){
			result = errorResult.newFailure("CMS1301");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(count <= 0){
			result = errorResult.newFailure("CMS1302");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备查询参数
		SpecParam<Message> specParam = new SpecParam<Message>();
		List<Long> receivers = new ArrayList<Long>();
		receivers.add(userId);
		receivers.add(100000L);	// TODO
		specParam.in("receiverId", receivers);
		specParam.eq("deleted", false);								// 未删除
		
		// 准备分页条件
		Sort sort = new Sort(Sort.Direction.DESC, "createAt");	// 排序
		Pageable pageable = new PageRequest(0, count, sort);
		
		// 执行查询
		Page<Message> resultSet = null;
		try{
			resultSet = messageRepository.findAll(SpecUtil.spec(specParam), pageable);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0161");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(resultSet.getContent());
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}
	
	@Override
	public Result<Message> createMessage(Message message){
		log.info(SimpleLogFormater.formatParams(message));
		
		Result<Message> result;
		
		// 校验参数
		if(message == null){
			result = errorResult.newFailure("CMS1311");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		if(message.getReceiverId() == null){
			result = errorResult.newFailure("CMS1312");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		
		// 准备序列号
		Result<Long> messageQueuing = queuingService.next(QUEUING_MAKE_SRC);
	    if(messageQueuing.getError() != null ){
	    	return errorResult.newFailure("CMS0032");
	    }
	    
		// 持久对象
	    Message messageSaved = null;
		try{
			message.setStatus(MessageStatus.NOREAD.getCode());
			message.setId(messageQueuing.getObject());
			messageSaved = messageRepository.save(message);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0171");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}

		// 返回结果
		result = Result.newSuccess(messageSaved);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	@Override
	public Result<Message> readMessage(Long id) {
		log.info(SimpleLogFormater.formatParams(id));
		
		Result<Message> result;
		
		// 参数校验
		if(id == null){
			result = errorResult.newFailure("CMS1321");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备对象
		Message message = messageRepository.findOne(id);
		if(message != null){
			message.setStatus(MessageStatus.READ.getCode());
		}
		
		Message msg = null;
		// 持久化操作
		try{
			msg = messageRepository.save(message);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0181");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}	
		
		// 返回结果
		result = Result.newSuccess(msg);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	@Override
	public Result<Boolean> deleteMessage(Long id) {
		log.info(SimpleLogFormater.formatParams(id));
		
		Result<Boolean> result;
		
		// 参数校验
		if(id == null){
			result = errorResult.newFailure("CMS1331");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 准备对象
		Message message = messageRepository.findOne(id);
		if(message != null){
			message.setDeleted(true);
		}
		
		// 持久化操作
		try{
			messageRepository.save(message);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0191");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}	
		
		// 返回结果
		result = Result.newSuccess(true);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	@Override
	public Result<Boolean> readMessage(String ids) {
		log.info(SimpleLogFormater.formatParams(ids));
		
		Result<Boolean> result;
		
		// 参数校验
		if(ids == null){
			result = errorResult.newFailure("CMS1341");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		List<Long> lngIds = new ArrayList<Long>();
		String[] arrayIds = ids.split(",");
		for(String strId : arrayIds){
			lngIds.add(Long.valueOf(strId));
		}
		
		// 准备对象
		SpecParam<Message> params = new SpecParam<Message>();
		params.in("id", lngIds);
		try{
		List<Message> messages = messageRepository.findAll(SpecUtil.spec(params));
			if(messages != null){
				for(Message message : messages){
					message.setStatus(MessageStatus.READ.getCode());
					messageRepository.save(message);
				}
			}
		}catch(Exception e){
			result = errorResult.newFailure("CMS0201");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(true);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	@Override
	public Result<Boolean> deleteMessage(String ids) {
		log.info(SimpleLogFormater.formatParams(ids));
		
		Result<Boolean> result;
		
		// 参数校验
		if(ids == null){
			result = errorResult.newFailure("CMS1351");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		List<Long> lngIds = new ArrayList<Long>();
		String[] arrayIds = ids.split(",");
		for(String strId : arrayIds){
			lngIds.add(Long.valueOf(strId));
		}
		
		// 准备对象
		SpecParam<Message> params = new SpecParam<Message>();
		params.in("id", lngIds);
		try{
		List<Message> messages = messageRepository.findAll(SpecUtil.spec(params));
			if(messages != null){
				for(Message message : messages){
					message.setDeleted(true);
					messageRepository.save(message);
				}
			}
		}catch(Exception e){
			result = errorResult.newFailure("CMS0211");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(true);
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}

	@Override
	public Result<MessageCounter> countMessages(Long userId) {
		log.info(SimpleLogFormater.formatParams(userId));
		
		Result<MessageCounter> result;
		
		// 参数校验
		if(userId == null){
			result = errorResult.newFailure("CMS1361");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		SpecParam<Message> params = new SpecParam<Message>();
		List<Long> receivers = new ArrayList<Long>();
		receivers.add(userId);
		receivers.add(100000L);	// TODO
		params.in("receiverId", receivers);
		params.eq("deleted", false);								// 未删除
		
		List<Message> messages = null;
		
		try{
			messages = messageRepository.findAll(SpecUtil.spec(params));
		}catch(Exception e){
			result = errorResult.newFailure("CMS0221");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		
		Integer total = 0, noread = 0;
		if(messages != null){
			total = messages.size();
			for(Message message : messages){
				if(message.getStatus() == MessageStatus.NOREAD.getCode()){
					noread ++;
				}
			}
		}

		// 返回结果
		result = Result.newSuccess(new MessageCounter(total, noread));
		log.info(SimpleLogFormater.formatResult(result));
		return result;
	}
}
