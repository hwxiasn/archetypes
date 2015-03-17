package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

public class UserItem implements Serializable{
	private static final long serialVersionUID = 1276458977922000329L;
	/** 用户标识 */
	private Long id;
	/** 支付账户ID */
	private String moneyMoreMoreId;
	/** 客户编号 */
	private String customerNum;
	/** 用户名 */
	private String userName;
	/** 真实姓名 */
	private String realName;
	/** 用户角色：投资人，经纪人，融资人，保荐人 */
	private String role;
	/** 用户角色：投资人，经纪人，融资人，保荐人 */
	private String roleName;
	/** 推荐人编号 */
	private String refererNumber;
	/** 创建时间 */
	private String createTime;
	/** 用户状态：未激活，正常，冻结，禁用 */
	private String status;
	/** 用于显示的状态名称 */
	private String statusName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMoneyMoreMoreId() {
		return moneyMoreMoreId;
	}
	public void setMoneyMoreMoreId(String moneyMoreMoreId) {
		this.moneyMoreMoreId = moneyMoreMoreId;
	}
	public String getCustomerNum() {
		return customerNum;
	}
	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRefererNumber() {
		return refererNumber;
	}
	public void setRefererNumber(String refererNumber) {
		this.refererNumber = refererNumber;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
