package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class CodeListItem implements Serializable {

	private static final long serialVersionUID = 5714293204804698324L;
	
	public String code;
	public String name;
	private String role;
	
	public CodeListItem(){
		super();
	}
	
	public CodeListItem(String code, String name){
		super();
		this.code = code;
		this.name = name;
	}
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
