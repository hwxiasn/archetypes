package com.qingbo.ginkgo.ygb.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.entity.BankType;
import com.qingbo.ginkgo.ygb.base.entity.CodeList;
import com.qingbo.ginkgo.ygb.base.entity.StateCity;
import com.qingbo.ginkgo.ygb.base.repository.BankTypeRepository;
import com.qingbo.ginkgo.ygb.base.repository.CodeListRepository;
import com.qingbo.ginkgo.ygb.base.repository.StateCityRepository;
import com.qingbo.ginkgo.ygb.base.service.CodeListService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;

@Service("codeListService")
public class CodeListServiceImpl implements CodeListService {
	private List<CodeList> emptyList = new ArrayList<>();
	private ErrorMessage errors = new ErrorMessage("base-error.properties");
	
	@Autowired private CodeListRepository codeListRepository;
	@Autowired private StateCityRepository stateCityRepository;
	@Autowired private BankTypeRepository bankTypeRepository;

	@Cacheable(value="itemList", key="#type")
	public Result<List<CodeList>> list(String type) {
		if(StringUtils.isBlank(type)) return errors.newFailure("BAS0001", type);
		
		List<CodeList> list = codeListRepository.findByTypeAndDeletedFalse(type);
		if(list==null || list.size()==0) return Result.newSuccess(emptyList);
		
		return Result.newSuccess(list);
	}

	@Cacheable(value="itemList", key="#type+'-'+#code")
	public Result<CodeList> item(String type, String code) {
		if(StringUtils.isBlank(type)) return errors.newFailure("BAS0001", type);
		if(StringUtils.isBlank(code)) return errors.newFailure("BAS0002", type, code);
		
		CodeList codeList = codeListRepository.findByTypeAndCode(type, code);
		if(codeList==null) return errors.newFailure("BAS0003");
		
		return Result.newSuccess(codeList);
	}

	@Cacheable(value="stateCity", key="states")
	public Result<List<CodeList>> states() {
		List<StateCity> list = stateCityRepository.findByParentCodeNullAndDeletedFalse();
		if(list==null || list.size()==0) return Result.newSuccess(emptyList);
		
		List<CodeList> codeList = new ArrayList<>(list.size());
		for(StateCity state:list) {
			CodeList item = new CodeList();
			item.setName(state.getName());
			item.setCode(state.getCode());
			codeList.add(item);
		}
		return Result.newSuccess(codeList);
	}

	@Cacheable(value="stateCity", key="'cities-'+#state")
	public Result<List<CodeList>> cities(String state) {
		if(StringUtils.isBlank(state)) return errors.newFailure("BAS0004", state);
		
		List<StateCity> list = stateCityRepository.findByParentCodeAndDeletedFalse(state);
		if(list==null || list.size()==0) return Result.newSuccess(emptyList);
		
		List<CodeList> codeList = new ArrayList<>(list.size());
		for(StateCity city:list) {
			CodeList item = new CodeList();
			item.setName(city.getName());
			item.setCode(city.getCode());
			codeList.add(item);
		}
		return Result.newSuccess(codeList);
	}

	@Cacheable(value="stateCity", key="#code")
	public Result<StateCity> stateCity(String code) {
		if(StringUtils.isBlank(code)) return errors.newFailure("BAS0005", code);
		
		StateCity stateCity = stateCityRepository.findByCode(code);
		if(stateCity==null) return errors.newFailure("BAS0006");
		
		return Result.newSuccess(stateCity);
	}

	@Cacheable(value="banks", key="banks")
	public Result<List<CodeList>> banks() {
		List<BankType> list = bankTypeRepository.findByDeletedFalse();
		if(list==null || list.size()==0) return Result.newSuccess(emptyList);
		
		List<CodeList> codeList = new ArrayList<>(list.size());
		for(BankType bank:list) {
			CodeList item = new CodeList();
			item.setName(bank.getName());
			item.setCode(bank.getCode());
			codeList.add(item);
		}
		return Result.newSuccess(codeList);
	}

	@Cacheable(value="banks", key="#code")
	public Result<BankType> bank(String code) {
		if(StringUtils.isBlank(code)) return errors.newFailure("BAS0007", code);
		
		BankType bankType = bankTypeRepository.findByCode(code);
		if(bankType==null) return errors.newFailure("BAS0008");
		
		return Result.newSuccess(bankType);
	}

}
