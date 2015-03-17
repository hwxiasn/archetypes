package com.qingbo.ginkgo.ygb.customer.entity;

import javax.persistence.Entity;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;

@Entity
public class Address extends BaseEntity{

	private static final long serialVersionUID = 231436252541328431L;
	
	private Long userId;//用户主键
	private String addressType;//地址类型
	private String province;//省
	private String city;//市
	private String district;//地区
	private String postcode;//邮编
	private String address1;//地址1
	private String address2;//地址2
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getAddressType() {
		return addressType;
	}
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	
}
