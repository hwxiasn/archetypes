package com.qingbo.ginkgo.ygb.project.entity;

import java.util.*;

/**
 * 投资权益回购履约担保合同 合同编号：CONTRACTNO 
 * 投资人流水号 金额（元） TRLOWNO TMONEY 
 * 投资接受人流水编号 RFLOWNO
 * 担保承诺函编号 DBHNO 
 * 投资权益本金合计 DMONEY万元人民币
 *  投资权益年化收益率 PER% 
 *  投资期间
 * SYYYY年SMM月SDD日——EYYYY年EMM月 EDD日 
 * 回购到期日 EYYYY年 EMM月EDD日的下一工作日
 * 
 * 
 */
public class ContractInfo {
	private String contractNo;
	private String rflowNo;
	private String dbhNo;
	private String dMoney;
	private String syyyy;
	private String smm;
	private String sdd;
	private String eyyyy;
	private String emm;
	private String edd;
	private String per;
	private List<String> trlow = new ArrayList<String>();
	private List<String> tmoney = new ArrayList<String>();
	private String brealname;//BREALNAME;

	public ContractInfo(){
		
	}
	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getRflowNo() {
		return rflowNo;
	}

	public void setRflowNo(String rflowNo) {
		this.rflowNo = rflowNo;
	}

	public String getDbhNo() {
		return dbhNo;
	}

	public void setDbhNo(String dbhNo) {
		this.dbhNo = dbhNo;
	}

	public String getdMoney() {
		return dMoney;
	}

	public void setdMoney(String dMoney) {
		this.dMoney = dMoney;
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

	public void addTrlow(String s) {
		if (s != null)
			trlow.add(s);
	}

	public List<String> getTrlow() {
		return trlow;
	}

	public void setTrlow(List<String> trlow) {
		this.trlow = trlow;
	}

	public void addTmoney(String s) {
		if (s != null)
			tmoney.add(s);
	}

	public List<String> getTmoney() {
		return tmoney;
	}

	public void setTmoney(List<String> tmoney) {
		this.tmoney = tmoney;
	}

	public String getPer() {
		return per;
	}

	public void setPer(String per) {
		this.per = per;
	}
	public String getBrealname() {
		return brealname;
	}

	public void setBrealname(String brealname) {
		this.brealname = brealname;
	}


	
	public List<Object> parse() {
		List<Object> list = new ArrayList<Object>();
		if(this.tmoney.size()!=this.trlow.size()){
			return null;
		}
		List<Map<String,String>> listPair = new ArrayList<Map<String,String>>();
		for(int i=0;i<tmoney.size();i++){
			Map<String,String> m = new HashMap<String,String>();
			m.put("TMONEY", tmoney.get(i));
			m.put("TRLOWNO", trlow.get(i));
			listPair.add(m);
		}
		list.add(listPair);
		Map<String,String> map = new HashMap<String,String>();
		map.put("CONTRACTNO", this.getContractNo());
		map.put("RFLOWNO", this.getRflowNo());
		map.put("DBHNO", this.getDbhNo());
		map.put("DMONEY", this.getdMoney());
		map.put("PER", this.getPer());
		map.put("SYYYY", this.getSyyyy());
		map.put("SMM", this.getSmm());
		map.put("SDD", this.getSdd());
		map.put("EYYYY", this.getEyyyy());
		map.put("EMM", this.getEmm());
		map.put("EDD", this.getEdd());
		map.put("bRealName".toUpperCase(), this.getBrealname());
		list.add(map);
		return list;
	}

}
