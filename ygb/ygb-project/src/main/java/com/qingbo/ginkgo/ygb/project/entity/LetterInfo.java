package com.qingbo.ginkgo.ygb.project.entity;

import java.util.*;

/**
 * 担 保 函
 * 编号:    LETTERNO
 * 担 保 人：ENTERPRISE
 * 住    所：COMADDRESS
 * 邮政编码：ZIPCODE
 * 法定代表人：LEGALNAME
 * 被担保的投资权益为：人民币 DMONEY 万元，年化收益率为 PER  %
 * 投资期间为 SYYYY 年 SMM 月 SDD日—  EYYYY 年 EMM 月 EDD日
 * 合同编号为：CONTRACTNO
 * 担保人：ENTERPRISE（公章）
 * PYYYY年 PMM月 PDD日
 *
 */
public class LetterInfo {
	private String letterNo;//LETTERNO;
	private String enterprise;//ENTERPRISE;
	private String comaddress;//COMADDRESS;
	private String zipcode;//ZIPCODE;
	private String legalname;//LEGALNAME;
	private String contractNo;
	private String dMoney;
	private String per;
	private String syyyy;
	private String smm;
	private String sdd;
	private String eyyyy;
	private String emm;
	private String edd;
	private String pyyyy;
	private String pmm;
	private String pdd;
	private String dmonth;
	private String brealname;//BREALNAME;

	public LetterInfo(){
		
	}
	
	public String getLetterNo() {
		return letterNo;
	}
	public void setLetterNo(String letterNo) {
		this.letterNo = letterNo;
	}
	public String getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}
	public String getComaddress() {
		return comaddress;
	}
	public void setComaddress(String comaddress) {
		this.comaddress = comaddress;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getLegalname() {
		return legalname;
	}
	public void setLegalname(String legalname) {
		this.legalname = legalname;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getdMoney() {
		return dMoney;
	}
	public void setdMoney(String dMoney) {
		this.dMoney = dMoney;
	}
	public String getPer() {
		return per;
	}
	public void setPer(String per) {
		this.per = per;
	}
	public String getSyyyy() {
		return syyyy;
	}
	public void setSyyyy(String syyyy) {
		this.syyyy = syyyy;
	}
	public String getSmm() {
		return smm;
	}
	public void setSmm(String smm) {
		this.smm = smm;
	}
	public String getSdd() {
		return sdd;
	}
	public void setSdd(String sdd) {
		this.sdd = sdd;
	}
	public String getEyyyy() {
		return eyyyy;
	}
	public void setEyyyy(String eyyyy) {
		this.eyyyy = eyyyy;
	}
	public String getEmm() {
		return emm;
	}
	public void setEmm(String emm) {
		this.emm = emm;
	}
	public String getEdd() {
		return edd;
	}
	public void setEdd(String edd) {
		this.edd = edd;
	}
	public String getPyyyy() {
		return pyyyy;
	}
	public void setPyyyy(String pyyyy) {
		this.pyyyy = pyyyy;
	}
	public String getPmm() {
		return pmm;
	}
	public void setPmm(String pmm) {
		this.pmm = pmm;
	}
	public String getPdd() {
		return pdd;
	}
	public void setPdd(String pdd) {
		this.pdd = pdd;
	}

	public String getDperiod() {
		return dmonth;
	}

	public void setDperiod(String dperiod) {
		this.dmonth = dperiod;
	}

	public String getBrealname() {
		return brealname;
	}

	public void setBrealname(String brealname) {
		this.brealname = brealname;
	}

	public List<Object> parse(){
		List<Object> list = new ArrayList<Object>();
		Map<String,String> map = new HashMap<String,String>();
		map.put("CONTRACTNO", this.getContractNo());
		map.put("LETTERNO", this.getLetterNo());
		map.put("ENTERPRISE", this.getEnterprise());
		map.put("COMADDRESS", this.getComaddress());
		map.put("ZIPCODE", this.getZipcode());
		map.put("LEGALNAME", this.getLegalname());
		map.put("DMONEY", this.getdMoney());
		map.put("PER", this.getPer());
		map.put("SYYYY", this.getSyyyy());
		map.put("SMM", this.getSmm());
		map.put("SDD", this.getSdd());
		map.put("EYYYY", this.getEyyyy());
		map.put("EMM", this.getEmm());
		map.put("EDD", this.getEdd());
		map.put("PYYYY", this.getPyyyy());
		map.put("PMM", this.getPmm());
		map.put("PDD", this.getPdd());
		map.put("DMONTH", this.getDperiod());
		map.put("bRealName".toUpperCase(), this.getBrealname());
		list.add(map);
		return list;
	}
	
	
	
}
