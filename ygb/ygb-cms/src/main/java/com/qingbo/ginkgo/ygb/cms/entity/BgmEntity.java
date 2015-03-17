package com.qingbo.ginkgo.ygb.cms.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class BgmEntity extends BaseEntity {

	private static final long serialVersionUID = 4656856902682255843L;

	private String name;
	private String className;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "entity")
    private Set<BgmField> fields = new HashSet<BgmField>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Set<BgmField> getFields() {
		return fields;
	}
	public void setFields(Set<BgmField> fields) {
		this.fields = fields;
	}
	
    public void addField(BgmField field) {
        if (!this.fields.contains(field)) {
            this.fields.add(field);
            field.setEntity(this);
        }
    }
    
    public void removeField(BgmField field) {
        if (this.fields.contains(field)) {
            field.setEntity(null);
            this.fields.remove(field);
        }
    }
}
