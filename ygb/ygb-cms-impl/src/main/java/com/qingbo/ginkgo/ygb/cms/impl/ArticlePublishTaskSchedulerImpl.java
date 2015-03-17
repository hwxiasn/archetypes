package com.qingbo.ginkgo.ygb.cms.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.cms.entity.Article;
import com.qingbo.ginkgo.ygb.cms.entity.ArticlePublishTask;
import com.qingbo.ginkgo.ygb.cms.enums.ArticlePublishTaskStatus;
import com.qingbo.ginkgo.ygb.cms.enums.ArticleStatus;
import com.qingbo.ginkgo.ygb.cms.repository.ArticlePublishTaskRepository;
import com.qingbo.ginkgo.ygb.cms.repository.ArticleRepository;
import com.qingbo.ginkgo.ygb.cms.service.ArticlePublishTaskScheduler;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;

//@Component("articlePublishTaskScheduler")
public class ArticlePublishTaskSchedulerImpl implements ArticlePublishTaskScheduler {
	
	private static Log log = LogFactory.getLog(ArticlePublishTaskSchedulerImpl.class);
	
	@Autowired private ArticleRepository articleRepository;
	@Autowired private ArticlePublishTaskRepository articlePublishTaskRepository;

	@Override
	@Scheduled(cron="0 0/5 * * * ? ")
	@Transactional
	public void schedulePublishment(){
		log.info("Start scheduling article publishment...");
		
		// 找出未执行的发布任务
		SpecParam<ArticlePublishTask> specParam = new SpecParam<ArticlePublishTask>();
		specParam.eq("deleted", false);											// 未删除
		specParam.eq("status", ArticlePublishTaskStatus.CREATED.getCode());		// 已发布
		
		List<ArticlePublishTask> tasks = null;
		
		try{
			tasks = articlePublishTaskRepository.findAll(SpecUtil.spec(specParam));
		}catch(Exception e){
			log.error("Query error occured while getting non-executed article publish tasks. " + e.getMessage());
			return;
		}
		
		// 筛选应该发布的服务（1.发布，2.修改发布状态）
		Long articleId;
		if(tasks != null){
			for(ArticlePublishTask task :tasks){
				if(task == null){
					continue;
				}
				if(task.getPublishTime() == null){
					log.error("Null publish time ! Article publish task id : " + task.getId());
					continue;
				}
				if(task.getPublishTime().getTime() <= System.currentTimeMillis()){
					try{
						articleId = task.getArticleId();
						Article article = articleRepository.findOne(articleId);
						article.setStatus(ArticleStatus.ONLINE.getCode());
						article.setPublishTime(new Date());
						articleRepository.save(article);
						
						task.setStatus(ArticlePublishTaskStatus.EXECUTED.getCode());
						articlePublishTaskRepository.save(task);
					}catch(Exception e){
						log.error("Query error occured while executing article publish task! " + e.getMessage());
					}
				}
			}
		}
		
		log.debug("Article publishment task finished.");
	}
}
