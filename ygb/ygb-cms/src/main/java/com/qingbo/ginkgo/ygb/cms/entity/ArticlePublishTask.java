package com.qingbo.ginkgo.ygb.cms.entity;

import java.util.Date;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

/**
 * 文章发布任务实体
 */

@Entity
public class ArticlePublishTask extends BaseEntity {

	private static final long serialVersionUID = -4085748573209601795L;
	
	private Long articleId;			// 文章专题
	private Date publishTime;		// 发布时间
	private Integer status;			// 任务状态
	
	
	public Long getArticleId() {
		return articleId;
	}
	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
	public Date getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
