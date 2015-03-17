package com.qingbo.ginkgo.ygb.customer.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.customer.entity.Address;

public class UserAddressServiceTester extends BaseServiceTester {

	@Autowired private UserAddressService userAddressService;
	
	public void testAddAddress(){
		Address address = new Address();
		address.setUserId(123456789L);
		address.setAddressType("P");
		address.setProvince("四川");
		address.setCity("南充");
		address.setDistrict("顺庆");
		address.setPostcode("6254XX");
		address.setAddress1("医学街3号");
		address.setAddress2("阳光幼儿园");
		
		Result<Boolean> result = userAddressService.createUserAddress(address);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(result.getObject());
		}
	}
	
	public void testDeleteAddress(){
		Result<Boolean> result = userAddressService.deleteUserAddress(141118113757010001L);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(result.getObject());
		}
	}
	
	public void testUpdateAddress(){
		Address address = new Address();
		address.setId(141118113757010001L);
		address.setUserId(123456789L);
		address.setAddressType("E");
		address.setProvince("四川省");
		address.setCity("南充市");
		address.setDistrict("顺庆区");
		address.setPostcode("XX54XX");
		address.setAddress1("医学街3#");
		address.setAddress2("阳光幼儿园教务处");
		
		Result<Boolean> result = userAddressService.updateUserAddress(address);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(result.getObject());
		}
	}
	
	@Test
	public void testListAddress(){
		Result<List<Address>> result = userAddressService.getUserAddress(123456789L);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			for(Object object : result.getObject()){
				System.out.println(JSON.toJSON(object));
			}
		}
	}
}
