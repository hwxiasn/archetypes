package com.qingbo.ginkgo.ygb.cms.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

/**
 * 文章正文实体
 */

@Entity
public class ArticleText extends BaseEntity {

	private static final long serialVersionUID = -860869047493521831L;

	private Long articleId;			// 文章专题
	private String textStuff;		// 正文
	
	
	public Long getArticleId() {
		return articleId;
	}
	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
	public String getTextStuff() {
		return textStuff;
	}
	public void setTextStuff(String textStuff) {
		this.textStuff = textStuff;
	}
}
