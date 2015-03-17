package com.qingbo.ginkgo.ygb.base.entity;

import javax.persistence.Entity;

@Entity
public class UploadImage extends BaseEntity {
	private static final long serialVersionUID = -3075538095815795970L;
	
	private String objectType;
	private Long objectId;
	private String imageType;
	private String imageName;
	private String imagePath;

	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public String getImageType() {
		return imageType;
	}
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
