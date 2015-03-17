package com.qingbo.ginkgo.ygb.base.service;

import java.util.List;

import com.qingbo.ginkgo.ygb.base.entity.BankType;
import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.base.entity.StateCity;
import com.qingbo.ginkgo.ygb.common.result.Result;

public interface CodeListService {
	/**
	 * 获取下拉框列表项集合
	 * @param type 参考CodeListType
	 */
	Result<List<CodeList>> list(String type);
	
	/**
	 * 获取下拉框列表项元素信息，例如中文名称等
	 * @param type 参考CodeListType
	 * @param code CodeList.code
	 */
	Result<CodeList> item(String type, String code);
	
	/**
	 * 获取省份列表
	 */
	Result<List<CodeList>> states();
	
	/**
	 * 获取城市列表
	 * @param state 参考接口{@link #states states()}
	 */
	Result<List<CodeList>> cities(String state);
	
	/**
	 * 获取省市信息，例如获取乾多多代码信息
	 */
	Result<StateCity> stateCity(String code);
	
	/**
	 * 获取银行类型列表
	 */
	Result<List<CodeList>> banks();
	
	/**
	 * 获取银行信息，例如获取乾多多相关代码信息
	 * @param code 参考接口{@link #banks banks()}
	 */
	Result<BankType> bank(String code);
}
