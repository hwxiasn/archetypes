package com.qingbo.ginkgo.ygb.cms.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class Message extends BaseEntity {

	private static final long serialVersionUID = -5465579750835832964L;
	
	private String sender;			// 发送方
	private Long receiverId;		// 接收人(群组)ID
	private Integer type;			// 消息类型（群组个人等）
	private String title;			// 消息标题
	private String text;			// 消息正文
	private Integer status;			// 消息状态(已读未读等)
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public Long getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
