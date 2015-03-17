package com.qingbo.ginkgo.ygb.web.pojo;

import java.io.Serializable;

import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.DateUtil.FormatType;
import com.qingbo.ginkgo.ygb.customer.entity.User;

public class Operator implements Serializable {

	private static final long serialVersionUID = -3687283589785123025L;
	
	/** ID*/
	private Long id;
	/** 所属的担保公司的ID*/
	private Long guaranteeId;
	/** 用户名 */
	private String userName;
	/** 备注  */
	private String remark;
	/** 创建时间  */
	private String creatDate;
	/** 操作员类型  */
	private String operatorType;
	/** 登陆密码  */
	private String password;
	/** 审核密码  */
	private String auditPassword;
	/** 状态  */
	private String status;
	//级别
	private String level;
	
	private String registerSource;
	public Operator() {
		super();
	}
	public Operator(Long id, String userName, String remark, String creatDate, String operatorType, String status,String level) {
		super();
		this.id = id;
		this.userName = userName;
		this.remark = remark;
		this.creatDate = creatDate;
		this.operatorType = operatorType;
		this.status = status;
		this.level = level;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGuaranteeId() {
		return guaranteeId;
	}
	public void setGuaranteeId(Long guaranteeId) {
		this.guaranteeId = guaranteeId;
	}
	public String getCreatDate() {
		return creatDate;
	}
	public void setCreatDate(String creatDate) {
		this.creatDate = creatDate;
	}
	public String getOperatorType() {
		return operatorType;
	}
	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAuditPassword() {
		return auditPassword;
	}
	public void setAuditPassword(String auditPassword) {
		this.auditPassword = auditPassword;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getRegisterSource() {
		return registerSource;
	}
	public void setRegisterSource(String registerSource) {
		this.registerSource = registerSource;
	}
	public static Operator forOperator(User user) {
		Operator operator = new Operator();
		
		operator.id = user.getId();
		operator.userName = user.getUserName();
		operator.remark = user.getUserProfile() == null ? null : user.getUserProfile().getMemo();
		operator.creatDate = DateUtil.format(user.getCreateAt(), FormatType.DAYTIME);
		operator.status = user.getStatus();
		
		return operator;
	}
	
}
