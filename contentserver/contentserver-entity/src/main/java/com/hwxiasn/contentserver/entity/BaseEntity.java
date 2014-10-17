package com.hwxiasn.contentserver.entity;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = -8460995755252776456L;

	//hibernate 乐观锁使用version字段
	@Version private Integer version;
	
//	private String createBy;
//	private String updateBy;
//	private String deleteBy;
//	private Boolean deleted;
//	@Temporal(TemporalType.TIMESTAMP) private Date createAt;
//	@Temporal(TemporalType.TIMESTAMP) private Date updateAt;
//	@Temporal(TemporalType.TIMESTAMP) private Date deleteAt;

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
//	public Boolean getDeleted() {
//		return deleted;
//	}
//	public void setDeleted(Boolean deleted) {
//		this.deleted = deleted;
//	}
//	public Date getCreateAt() {
//		return createAt;
//	}
//	public void setCreateAt(Date createAt) {
//		this.createAt = createAt;
//	}
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
