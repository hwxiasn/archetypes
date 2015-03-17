package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.DateUtil.FormatType;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.customer.entity.UserProfile;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;

public class Investor implements Serializable {

	private static final long serialVersionUID = -2696870113940005301L;
	
	/** ID*/
	private long id;
	/** 用户名*/
	private String userName;
	/** 真实姓名  */
	private String realName;
	/** 联系邮箱  */
	private String email;
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
	/** 注册来源 */
	private String registerSource;
	/** 手机号码 */
	private String mobile;
	/** 经纪人的userId */
	private Long brokerUserId;
	public Investor() {
		super();
	}
	public Investor(long id, String userName, String realName, String userNum, String profit, String createDate, String status) {
		super();
		this.id = id;
		this.userName = userName;
		this.realName = realName;
		this.userNum = userNum;
		this.profit = profit;
		this.createDate = createDate;
		this.status = status;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	
	public String getRegisterSource() {
		return registerSource;
	}
	public void setRegisterSource(String registerSource) {
		this.registerSource = registerSource;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public Long getBrokerUserId() {
		return brokerUserId;
	}
	public void setBrokerUserId(Long brokerUserId) {
		this.brokerUserId = brokerUserId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public static Investor fromUser(User user) {
		Investor invester = new Investor();
		
		invester.setId(user.getId());
		invester.setEmail(user.getUserProfile() == null ? null : user.getUserProfile().getEmail());
		invester.setCreateDate(DateUtil.format(user.getCreateAt(), FormatType.DAYTIME));
		invester.setRealName(user.getUserProfile() == null ? null : user.getUserProfile().getRealName());
		invester.setStatus(user.getStatus());
		invester.setUserName(user.getUserName());
		invester.setProfit("");
		invester.setUserNum(user.getUserProfile() == null ? null : user.getUserProfile().getCustomerNum());
		
		return invester;
	}
	
	
	public User toUser() {
		User user = new User();
		user.setUserName(userName);
		user.setRole(CustomerConstants.Role.INVESTOR.getCode());
		user.setRegSource(registerSource);
		user.setRegisterType(CustomerConstants.UserRegisterType.PERSONAL.getCode());
		UserProfile userProfile = new UserProfile();
		userProfile.setRealName(realName);
		userProfile.setMobile(mobile);
		userProfile.setEmail(email);
		user.setUserProfile(userProfile);
		return user;
	}
}
