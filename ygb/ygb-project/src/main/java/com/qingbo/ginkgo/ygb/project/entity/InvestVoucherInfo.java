package com.qingbo.ginkgo.ygb.project.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投资凭证
 * 平台服务商	重庆倍赢金融服务有限公司	业务类别	财富箱
投资利率	PER	投资期限	DPERIOD 个月
担保公司	ENTERPRISE
投资流水号	TRLOWNO
投资人信息	投资接受人信息
用户名：IUSERNAME	用户名：BUSERNAME
姓  名：IREALNAME	姓  名：BREALNAME
成立日：SYYYY年SMM月SDD日	成立日：SYYYY年SMM月SDD日
到期日：EYYYY年EMM月EDD日	兑付日：RYYYY年RMM月RDD日
投资本金(元)：TMONEY	　
手续费(元)：  TFEES	　
应收总计(元)：TAMOUNT	应付总计(元)：TAMOUNT

 * 
 *
 */
public class InvestVoucherInfo {
	private String per;
	private String syyyy;
	private String smm;
	private String sdd;
	private String ryyyy;
	private String rmm;
	private String rdd;
	private String dperiod;
	private String enterprise;//ENTERPRISE;
	private String trlowno;//TRLOWNO;
	private String iusername;//IUSERNAME;
	private String irealname;//IREALNAME;
	private String busername;//BUSERNAME;
	private String brealname;//BREALNAME;
	private String tmoney;//TMONEY;
	private String tamount;//TAMOUNT;
	private String tfees;//TFEES;
	private String eyyyy;
	private String emm;
	private String edd;

	private String serialNum;
	
	public InvestVoucherInfo() {
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


	public String getRyyyy() {
		return ryyyy;
	}


	public void setRyyyy(String eyyyy) {
		this.ryyyy = eyyyy;
	}


	public String getRmm() {
		return rmm;
	}


	public void setRmm(String emm) {
		this.rmm = emm;
	}


	public String getRdd() {
		return rdd;
	}


	public void setRdd(String edd) {
		this.rdd = edd;
	}


	public String getDperiod() {
		return dperiod;
	}


	public void setDperiod(String dperiod) {
		this.dperiod = dperiod;
	}


	public String getEnterprise() {
		return enterprise;
	}


	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}


	public String getTrlowno() {
		return trlowno;
	}


	public void setTrlowno(String trlowno) {
		this.trlowno = trlowno;
	}


	public String getIusername() {
		return iusername;
	}


	public void setIusername(String iusername) {
		this.iusername = iusername;
	}


	public String getIrealname() {
		return irealname;
	}


	public void setIrealname(String irealname) {
		this.irealname = irealname;
	}


	public String getBusername() {
		return busername;
	}


	public void setBusername(String busername) {
		this.busername = busername;
	}


	public String getBrealname() {
		return brealname;
	}


	public void setBrealname(String brealname) {
		this.brealname = brealname;
	}


	public String getTmoney() {
		return tmoney;
	}


	public void setTmoney(String tmoney) {
		this.tmoney = tmoney;
	}


	public String getTamount() {
		return tamount;
	}


	public void setTamount(String tamount) {
		this.tamount = tamount;
	}


	public String getTfees() {
		return tfees;
	}


	public void setTfees(String tfees) {
		this.tfees = tfees;
	}

	public String getSerialNum() {
		return serialNum;
	}


	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
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


	public List<Object> parse(){
		List<Object> list = new ArrayList<Object>();
		Map<String,String> map = new HashMap<String,String>();
		map.put("PER", this.getPer());
		map.put("SYYYY", this.getSyyyy());
		map.put("SMM", this.getSmm());
		map.put("SDD", this.getSdd());
		map.put("RYYYY", this.getRyyyy());
		map.put("RMM", this.getRmm());
		map.put("RDD", this.getRdd());
		map.put("DMONTH", this.getDperiod());
		map.put("EYYYY", this.getEyyyy());
		map.put("EMM", this.getEmm());
		map.put("EDD", this.getEdd());

		map.put("enterprise".toUpperCase(), this.getEnterprise());
		map.put("trlowNo".toUpperCase(), this.getTrlowno());
		map.put("iUserName".toUpperCase(), this.getIusername());
		map.put("iRealName".toUpperCase(), this.getIrealname());
		map.put("bUserName".toUpperCase(), this.getBusername());
		map.put("bRealName".toUpperCase(), this.getBrealname());
		map.put("tMoney".toUpperCase(), this.getTmoney());
		map.put("tAmount".toUpperCase(), this.getTamount());
		map.put("tFees".toUpperCase(), this.getTfees());

		list.add(map);
		return list;
	}


	
}
