package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.DateUtil.FormatType;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;


public class Broker implements Serializable {

	private static final long serialVersionUID = 1065922687467381024L;
	
	/** ID*/
	private long id;
	/** 用户名*/
	private String userName;
	/** 真实姓名  */
	private String realName;
	/** 用户编号  */
	private String userNum;
	/** 收益分配  */
	private String profit;
	/** 创建时间  */
	private String createDate;
	/** 状态 */
	private String status;
	/** 状态名称 */
	private String statusName;
	/** 邮箱 */
	private String email;
	/** 角色 */
	private String roles;
	/** 手机号码 */
	private String mobile;
	private Long marketingUserId;
	private String registerSource;
	public Broker() {
		super();
	}
	public Broker(long id, String userName, String realName, String userNum,
			String profit, String createDate, String status,String mobile) {
		super();
		this.id = id;
		this.userName = userName;
		this.realName = realName;
		this.userNum = userNum;
		this.profit = profit;
		this.createDate = createDate;
		this.status = status;
		this.mobile = mobile;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public String getUserNum() {
		return userNum;
	}
	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}
	public String getProfit() {
		return profit;
	}
	public void setProfit(String profit) {
		this.profit = profit;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getRegisterSource() {
		return registerSource;
	}
	public void setRegisterSource(String registerSource) {
		this.registerSource = registerSource;
	}
	public Long getMarketingUserId() {
		return marketingUserId;
	}
	public void setMarketingUserId(Long marketingUserId) {
		this.marketingUserId = marketingUserId;
	}
	public static Broker fromUser(User user) {
		Broker broker = new Broker();
		
		/** ID*/
		broker.setId(user.getId());
		/** 用户名*/
		broker.setUserName(user.getUserName());
		/** 真实姓名  */
		broker.setRealName(user.getUserProfile() == null ? null : user.getUserProfile().getRealName());
		/** 用户编号  */
		broker.setUserNum(user.getUserProfile() == null ? null : user.getUserProfile().getCustomerNum());
		/** 收益分配  */
//		broker.setProfit(profit);
		/** 创建时间  */
		broker.setCreateDate(DateUtil.format(user.getCreateAt(), FormatType.DAYTIME));
		/** 状态 */
		broker.setStatus(user.getStatus());
		
		return broker;
	}
	
	
	public User toUser() {
		User user = new User();
		user.setUserName(userName);
		user.setRole(CustomerConstants.Role.BROKER.getCode());
		user.setRegisterType(CustomerConstants.UserRegisterType.PERSONAL.getCode());
		user.setRegSource(registerSource);
		UserProfile userProfile = new UserProfile();
		userProfile.setRealName(realName);
		userProfile.setMobile(mobile);
		userProfile.setEmail(email);
		user.setUserProfile(userProfile);
		return user;
	}
}
