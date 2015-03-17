package com.qingbo.ginkgo.ygb.project.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.qingbo.ginkgo.ygb.common.util.PropertiesUtil;

public class ContractNoBuilder {
	public static final String DYEAR = "DYEAR";
	public static final String SERIAL = "SERIAL";
	public static final String TNO = "TNO";
	public static int YEAR = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
	public static final String KEY_FILE = "contract.properties";


	public ContractNoBuilder() {
	}
	
	/**
	 * 合同编号
	 * @param preNo
	 * @return
	 */
	public static String contractNo(String type,int year,int serial){
		String key = type+"_CONTRACTNO";
		String model = PropertiesUtil.get(KEY_FILE).getProperty(key.toUpperCase());
		model = model.replaceAll(DYEAR,String.valueOf(year));
		model = model.replaceAll(SERIAL,fixZeroLeft(String.valueOf(serial),4));
		return model;//"向南接力贷LY["+year+"]字"+fixZeroLeft(String.valueOf(serial),5)+"号";
	}
	/**
	 * 担保函编号
	 * @param preNo
	 * @return
	 */
	public static String guaranteeNo(String type,int year,int serial){
		String key = type+"_GUARANTEENO";
		String model = PropertiesUtil.get(KEY_FILE).getProperty(key.toUpperCase());
		model = model.replaceAll(DYEAR,String.valueOf(year));
		model = model.replaceAll(SERIAL,fixZeroLeft(String.valueOf(serial),4));
		return model;//"向南接力贷CN["+year+"]字"+fixZeroLeft(String.valueOf(serial),5)+"号";
	}
	
	/**
	 * 投资流水号
	 * @param preNo
	 * @return
	 */
	public static String investNo(String type,int year,int serial,int t){
		String key = type+"_INVESTNO";
		String model = PropertiesUtil.get(KEY_FILE).getProperty(key.toUpperCase());
		model = model.replaceAll(DYEAR,String.valueOf(year));
		model = model.replaceAll(SERIAL,fixZeroLeft(String.valueOf(serial),4));
		model = model.replaceAll(TNO,fixZeroLeft(String.valueOf(t),3));
		return model;//"向南接力贷CN["+year+"]字"+fixZeroLeft(String.valueOf(serial),5)+"号T"+fixZeroLeft(String.valueOf(t),3)+"号";
	}
	
	/**
	 * 投资接受人流水号
	 * @param preNo
	 * @return
	 */
	public static String investAccNo(String type,int year,int serial){
		String key = type+"_INVESTACCNO";
		String model = PropertiesUtil.get(KEY_FILE).getProperty(key.toUpperCase());
		model = model.replaceAll(DYEAR,String.valueOf(year));
		model = model.replaceAll(SERIAL,fixZeroLeft(String.valueOf(serial),4));
		return model;//"向南接力贷YC["+year+"]字"+fixZeroLeft(String.valueOf(serial),5)+"号";
	}
	
	/**
	 * 左补零
	 * @param src
	 * @param bits
	 * @return
	 */
	private static String fixZeroLeft(String src,int bits){
		if(src == null) return null;
		if(src.length()>= bits)	return src;
		while(src.length()<bits){
			src = "0".concat(src);
		}
		return src;
	}
}
