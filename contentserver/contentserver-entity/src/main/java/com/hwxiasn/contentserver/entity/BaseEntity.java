package com.hwxiasn.contentserver.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@MappedSuperclass
@SuppressWarnings("serial")
public abstract class BaseEntity implements Serializable {
	//所有实体都有自增的整型主键
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
	//hibernate 乐观锁使用version字段
	@Version private Integer version;
	
//	private String createBy;
//	private String updateBy;
//	private String deleteBy;
	private boolean deleted;
	@Temporal(TemporalType.TIMESTAMP) private Date createAt;
//	@Temporal(TemporalType.TIMESTAMP) private Date updateAt;
//	@Temporal(TemporalType.TIMESTAMP) private Date deleteAt;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
//	public String getCreateBy() {
//		return createBy;
//	}
//	public void setCreateBy(String createBy) {
//		this.createBy = createBy;
//	}
//	public String getUpdateBy() {
//		return updateBy;
//	}
//	public void setUpdateBy(String updateBy) {
//		this.updateBy = updateBy;
//	}
//	public String getDeleteBy() {
//		return deleteBy;
//	}
//	public void setDeleteBy(String deleteBy) {
//		this.deleteBy = deleteBy;
//	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
//	public Date getUpdateAt() {
//		return updateAt;
//	}
//	public void setUpdateAt(Date updateAt) {
//		this.updateAt = updateAt;
//	}
//	public Date getDeleteAt() {
//		return deleteAt;
//	}
//	public void setDeleteAt(Date deleteAt) {
//		this.deleteAt = deleteAt;
//	}
}
