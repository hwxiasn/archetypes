package com.qingbo.ginkgo.ygb.customer.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;

public class UserBankCardServiceTester extends BaseServiceTester {
	
	@Autowired private UserBankCardService userBankCardService;
	
	public void testAddBankCard(){
		UserBankCard card = new UserBankCard();
		card.setUserId(123456789L);
		card.setBankCardType("C");
		card.setBankCode("ABC");
		card.setBankBranch("门司区分行营业部");
		card.setBankCardNum("622123456789123");
		card.setBankCardAccountName("崔成国");
		card.setIdType("J");
		card.setIdNum("51122819921207121X");
		card.setProvince("福冈县");
		card.setCity("北九州市");
		card.setAddress("门司区敬爱高等学校教务处");
		card.setMemo("农行信用卡1");
		
		Result<UserBankCard> result = userBankCardService.createBankCard(card);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(result.getObject());
		}
	}
	
	public void testUpdateBankCard(){
		UserBankCard card = new UserBankCard();
		card.setId(141118150306010001L);
		card.setUserId(123456789L);
		card.setBankCardType("D");
		card.setBankCode("CIB");
		card.setBankBranch("敬爱支行");
		card.setBankCardNum("622123456789124");
		card.setBankCardAccountName("崔成国");
		card.setIdType("S");
		card.setIdNum("511228199212071213");
		card.setProvince("福冈");
		card.setCity("北九州");
		card.setAddress("门司区敬爱高等学校行政处");
		card.setMemo("兴业储蓄卡1");
		
		Result<UserBankCard> result = userBankCardService.updateBankCard(card);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(result.getObject());
		}
	}
	
	public void testDeleteBankCard(){
		Result<Boolean> result = userBankCardService.deleteBankCard(141118150306010001L);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(result.getObject());
		}
	}
	
	public void testListUserBankCards(){
		Result<List<UserBankCard>> result = userBankCardService.getBankCardByUserId(123456789L);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			for(Object object : result.getObject()){
				System.out.println(JSON.toJSON(object));
			}
		}
	}
	
	@Test
	public void testGetUserBankCard(){
		Result<UserBankCard> result = userBankCardService.getBankCardById(141118150014010001L);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(JSON.toJSON(result.getObject()));
		}
	}
}
