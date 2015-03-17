package com.qingbo.ginkgo.ygb.trade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.qingbo.ginkgo.ygb.base.entity.BaseEntity;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;

@Entity
public class Trade extends BaseEntity {
	private static final long serialVersionUID = 4545880164184838460L;
	private Long      batchId = 0L;// 交易批次
	private Long      parentTradeId = 0L;//父交易流水号
	private Long      sourceTradeId = 0L;//源交易流水号
	private String    tradeName = "";// 交易名称
	private String    tradeSubjectId= "";//交易标的流水号
	private String    tradeSubjectInfo= "";//交易标的描述
	private String    tradeSource= "";//交易来源
	private String    creditAccount= "";//出借方交易账户ID（帐务流水号）
	private String    creditBankName= "";//  出借方银行名称（银行代码）
	private String    creditAccountType= "";//出借方银行账户类型
	private String    creditAccountNo= "";//出借方银行账户号码
	private BigDecimal creditBalance= BigDecimal.ZERO;//出借方余额
	private BigDecimal creditFreezeBalance= BigDecimal.ZERO;//出借方冻结金额
	private String    debitAccount= "";//贷入方交易账户ID（帐务流水号）
	private String    debitBankName= "";//贷入方银行名称（银行代码）
	private String    debitAccountType= "";//  贷入方银行账户类型
	private String    debitAccountNo= "";//  贷入方银行账户号码
	private BigDecimal debitBalance= BigDecimal.ZERO;//贷入方余额
	private BigDecimal debitFreezeBalance= BigDecimal.ZERO;// 贷入方冻结金额
	private String    tradeType= "";//交易类型 （投资、还款、分佣、充值、提现、转账）
	private String    tradeKind= "";//交易子类型 （父投资、父还款、父分佣、子投资、子还款、子分佣）
	private String    tradeRelation= "";//交易双方关系 （一对一、一对多、多对一）
	private BigDecimal tradeAmount= BigDecimal.ZERO;//  实际交易金额，单位：人民币，元，保留2位精度
	private BigDecimal aimTradeAmount= BigDecimal.ZERO;//  目标交易金额，单位：人民币，元，保留2位精度
	private String    tradeStatus= "";//交易状态 （待成立、已成立、成立失败、成立中）
	private Date 	dealDate ;//执行结束时间
	private String memo = "";//备注
	
	@Transient private String 	formatDealDate ;
	
	
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	public Long getParentTradeId() {
		return parentTradeId;
	}
	public void setParentTradeId(Long parentTradeId) {
		this.parentTradeId = parentTradeId;
	}
	public String getTradeName() {
		return tradeName;
	}
	public void setTradeName(String traceName) {
		this.tradeName = traceName;
	}
	public String getTradeSubjectInfo() {
		return tradeSubjectInfo;
	}
	public void setTradeSubjectInfo(String tradeSubjectInfo) {
		this.tradeSubjectInfo = tradeSubjectInfo;
	}
	public String getTradeSource() {
		return tradeSource;
	}
	public void setTradeSource(String tradeSource) {
		this.tradeSource = tradeSource;
	}
	public String getCreditAccount() {
		return creditAccount;
	}
	public void setCreditAccount(String creditAccount) {
		this.creditAccount = creditAccount;
	}
	public String getCreditBankName() {
		return creditBankName;
	}
	public void setCreditBankName(String creditBankName) {
		this.creditBankName = creditBankName;
	}
	public String getCreditAccountNo() {
		return creditAccountNo;
	}
	public void setCreditAccountNo(String creditAccountNo) {
		this.creditAccountNo = creditAccountNo;
	}
	public BigDecimal getCreditBalance() {
		return creditBalance;
	}
	public void setCreditBalance(BigDecimal createBalance) {
		this.creditBalance = createBalance;
	}
	public BigDecimal getCreditFreezeBalance() {
		return creditFreezeBalance;
	}
	public void setCreditFreezeBalance(BigDecimal createFreezeBalance) {
		this.creditFreezeBalance = createFreezeBalance;
	}
	public String getDebitAccount() {
		return debitAccount;
	}
	public void setDebitAccount(String debitAccount) {
		this.debitAccount = debitAccount;
	}
	public String getDebitBankName() {
		return debitBankName;
	}
	public void setDebitBankName(String debitBankName) {
		this.debitBankName = debitBankName;
	}
	public String getDebitAccountNo() {
		return debitAccountNo;
	}
	public void setDebitAccountNo(String debitAccountNo) {
		this.debitAccountNo = debitAccountNo;
	}
	public BigDecimal getDebitBalance() {
		return debitBalance;
	}
	public void setDebitBalance(BigDecimal debitBalance) {
		this.debitBalance = debitBalance;
	}
	public BigDecimal getDebitFreezeBalance() {
		return debitFreezeBalance;
	}
	public void setDebitFreezeBalance(BigDecimal debitFreezeBalance) {
		this.debitFreezeBalance = debitFreezeBalance;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getTradeRelation() {
		return tradeRelation;
	}
	public void setTradeRelation(String tradeRelation) {
		this.tradeRelation = tradeRelation;
	}
	public String getTradeStatus() {
		return tradeStatus;
	}
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	public String getTradeSubjectId() {
		return tradeSubjectId;
	}
	public void setTradeSubjectId(String tradeSubjectId) {
		this.tradeSubjectId = tradeSubjectId;
	}
	public String getCreditAccountType() {
		return creditAccountType;
	}
	public void setCreditAccountType(String creditAccountType) {
		this.creditAccountType = creditAccountType;
	}
	public String getDebitAccountType() {
		return debitAccountType;
	}
	public void setDebitAccountType(String debitAccountType) {
		this.debitAccountType = debitAccountType;
	}
	public Long getSourceTradeId() {
		return sourceTradeId;
	}
	public void setSourceTradeId(Long sourceTradeId) {
		this.sourceTradeId = sourceTradeId;
	}
	public String getTradeKind() {
		return tradeKind;
	}
	public void setTradeKind(String tradeKind) {
		this.tradeKind = tradeKind;
	}
	public BigDecimal getAimTradeAmount() {
		return aimTradeAmount;
	}
	public void setAimTradeAmount(BigDecimal aimTradeAmount) {
		this.aimTradeAmount = aimTradeAmount;
	}
	public Date getDealDate() {
		return dealDate;
	}
	public void setDealDate(Date dealDate) {
		this.dealDate = dealDate;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getFormatDealDate() {
		if(this.getDealDate() != null){
			return DateUtil.format(this.getDealDate(), DateUtil.FormatType.DAYTIME);
		}
		return formatDealDate;
	}

	
	
}
