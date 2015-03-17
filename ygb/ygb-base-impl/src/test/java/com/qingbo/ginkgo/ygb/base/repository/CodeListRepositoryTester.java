package com.qingbo.ginkgo.ygb.base.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.base.entity.BankType;
import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.base.entity.StateCity;
import com.qingbo.ginkgo.ygb.base.enums.CodeListType;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;

public class CodeListRepositoryTester extends BaseRepositoryTester {
	@Autowired private CodeListRepository codeListRepository;
	@Autowired private StateCityRepository stateCityRepository;
	@Autowired private BankTypeRepository bankTypeRepository;
	
	@Test
	public void codeList() {
		List<CodeList> list = codeListRepository.findByTypeAndDeletedFalse(CodeListType.USER_ROLE.getCode());
		printList(list);
		CodeList codeList = codeListRepository.findByTypeAndCode(CodeListType.USER_ROLE.getCode(), "I");
		System.out.println(JSON.toJSONString(codeList));
	}
	
	@Test
	public void stateCity() {
		List<StateCity> states = stateCityRepository.findByParentCodeNullAndDeletedFalse();
		printList(states);
		List<StateCity> cities = stateCityRepository.findByParentCodeAndDeletedFalse("370000");
		printList(cities);
		StateCity stateCity = stateCityRepository.findByCode("370700");
		System.out.println(JSON.toJSONString(stateCity));
	}
	
	@Test
	public void bankType() {
//		List<BankType> banks = bankTypeRepository.findByDeletedFalse();
//		printList(banks);
//		BankType bankType = bankTypeRepository.findByCode("ABC");
//		System.out.println(JSON.toJSONString(bankType));
//		for(int i=0;i<10;i++) {
//			bankType = bankTypeRepository.findOne(bankType.getId());
//			System.out.println(JSON.toJSONString(bankType));
//		}
		BankType bankType = new BankType();
		bankType.setId(100L);
//		bankType.setCreateAt(DateUtil.parse("2014-11-11"));
		BankType save = bankTypeRepository.save(bankType);
		System.out.println(JSON.toJSON(save));
	}
}
