package com.qingbo.ginkgo.ygb.cms.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

/**
 * 文章条目实体
 */

@Entity
public class Article extends BaseEntity {

	private static final long serialVersionUID = 2811254267425563175L;

	private String site;			// 所属站点
	private String subject;			// 所属专题
	private String title;			// 文章标题
	private String author;			// 文章作者
	private Date createTime;		// 创建时间
	private Date publishTime;		// 发布时间
	private Integer weight;			// 权重
	private Integer status;			// 状态
	private String link;			// 链接
	@Transient private ArticleText textStuff;	// 正文
	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public ArticleText getTextStuff() {
		return textStuff;
	}
	public void setTextStuff(ArticleText textStuff) {
		this.textStuff = textStuff;
	}
}
