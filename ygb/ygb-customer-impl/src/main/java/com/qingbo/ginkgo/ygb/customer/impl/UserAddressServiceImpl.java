package com.qingbo.ginkgo.ygb.customer.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.QueuingMake;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.entity.Address;
import com.qingbo.ginkgo.ygb.customer.repository.UserAddressRepository;
import com.qingbo.ginkgo.ygb.customer.service.UserAddressService;

@Service("userAddressService")
public class UserAddressServiceImpl implements UserAddressService {
	private static Log log = LogFactory.getLog(UserAddressServiceImpl.class);
	private static final String QUEUING_MAKE_SRC = "01";
	
	@Autowired private UserAddressRepository userAddressRepository;
	
	private ErrorMessage errorResult = new ErrorMessage("errorcode-cst.properties");

	@Override
	public Result<Boolean> createUserAddress(Address address) {
		
		Result<Boolean> result;
		
		// 校验参数
		if(address == null){
			result = errorResult.newFailure("CST1301");
			return result;
		}
		
		// 持久对象
		try{
			address.setId(QueuingMake.next(QUEUING_MAKE_SRC));
			userAddressRepository.save(address);
		}catch(Exception e){
			result = errorResult.newFailure("CST0301");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}

		// 返回结果
		result = Result.newSuccess(true);
		return result;
	}

	@Override
	public Result<Boolean> updateUserAddress(Address address) {

		Result<Boolean> result;
		
		// 校验参数
		if(address == null){
			result = errorResult.newFailure("CST1311");
			return result;
		}
		if(address.getId() == null){
			result = errorResult.newFailure("CST1312");
			return result;
		}
		
		try{
			// 准备对象
			Address addressFetched = userAddressRepository.findOne(address.getId());
			addressFetched.setUserId(address.getUserId() == null ? addressFetched.getUserId() : address.getUserId());
			addressFetched.setAddressType(address.getAddressType() == null ? addressFetched.getAddressType() : address.getAddressType());
			addressFetched.setProvince(address.getProvince() == null ? addressFetched.getProvince() : address.getProvince());
			addressFetched.setCity(address.getCity() == null ? addressFetched.getCity() : address.getCity());
			addressFetched.setDistrict(address.getDistrict() == null ? addressFetched.getDistrict() : address.getDistrict());
			addressFetched.setPostcode(address.getPostcode() == null ? addressFetched.getPostcode() : address.getPostcode());
			addressFetched.setAddress1(address.getAddress1() == null ? addressFetched.getAddress1() : address.getAddress1());
			addressFetched.setAddress2(address.getAddress2() == null ? addressFetched.getAddress2() : address.getAddress2());
			
			// 持久对象
			userAddressRepository.save(addressFetched);
		}catch(Exception e){
			result = errorResult.newFailure("CST0311");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(true);
		return result;
	}

	@Override
	public Result<Boolean> deleteUserAddress(Long id) {
		
		Result<Boolean> result;
		
		// 参数校验
		if(id == null){
			result = errorResult.newFailure("CST1321");
			return result;
		}
		
		// 准备对象
		Address address = userAddressRepository.findOne(id);
		address.setDeleted(true);
		
		// 持久化操作
		try{
			userAddressRepository.save(address);
		}catch(Exception e){
			result = errorResult.newFailure("CST0321");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}	
		
		// 返回结果
		result = Result.newSuccess(true);
		return result;
	}

	@Override
	public Result<List<Address>> getUserAddress(Long userId) {
		Result<List<Address>> result;
		
		if(userId == null){
			result = errorResult.newFailure("CST1331");
			return result;
		}
		
		// 准备查询参数
		SpecParam<Address> specParam = new SpecParam<Address>();
		specParam.eq("deleted", false);				// 未删除的
		specParam.eq("userId", userId);				// 所属用户
		
		// 执行查询
		List<Address> addresses = null;
		try{
			addresses = userAddressRepository.findAll(SpecUtil.spec(specParam));
		}catch(Exception e){
			result = errorResult.newFailure("CST0331");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(addresses);
		return result;
	}

}
