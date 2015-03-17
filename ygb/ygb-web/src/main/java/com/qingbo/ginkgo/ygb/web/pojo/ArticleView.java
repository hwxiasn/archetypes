package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class ArticleView implements Serializable {

	private static final long serialVersionUID = -4220832319944544329L;

	private Long id;
	private String title;
	private String subject;
	private String content;
	private String dateHead;
	private String dateTail;
	private Integer status;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDateHead() {
		return dateHead;
	}
	public void setDateHead(String dateHead) {
		this.dateHead = dateHead;
	}
	public String getDateTail() {
		return dateTail;
	}
	public void setDateTail(String dateTail) {
		this.dateTail = dateTail;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
