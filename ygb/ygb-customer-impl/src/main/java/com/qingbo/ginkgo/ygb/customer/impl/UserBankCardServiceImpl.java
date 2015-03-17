package com.qingbo.ginkgo.ygb.customer.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.QueuingMake;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.customer.entity.UserBankCard;
import com.qingbo.ginkgo.ygb.customer.entity.UserStatus;
import com.qingbo.ginkgo.ygb.customer.enums.CustomerConstants;
import com.qingbo.ginkgo.ygb.customer.repository.UserBankCardRepository;
import com.qingbo.ginkgo.ygb.customer.repository.UserStatusRepository;
import com.qingbo.ginkgo.ygb.customer.service.UserBankCardService;

@Service("userBankCardService")
public class UserBankCardServiceImpl implements UserBankCardService {
	
	private static Log log = LogFactory.getLog(UserBankCardServiceImpl.class);
	private static final String QUEUING_MAKE_SRC = "01";
	
	@Autowired 
	private UserBankCardRepository userBankCardRepository;
	
	@Autowired
	private QueuingService queuingService;
	
	@Autowired
	private UserStatusRepository userStatusRepository;
	
	private ErrorMessage errorResult = new ErrorMessage("errorcode-cst.properties");

	@Override
	public Result<UserBankCard> createBankCard(UserBankCard userBankCard) {
		
		Result<UserBankCard> result;
		
		// 校验参数
		if(userBankCard == null){
			result = errorResult.newFailure("CST1351");
			return result;
		}
		
		// 持久对象
		try{
			userBankCard.setId(QueuingMake.next(QUEUING_MAKE_SRC));
			userBankCardRepository.save(userBankCard);
		}catch(Exception e){
			result = errorResult.newFailure("CST0351");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}

		// 返回结果
		result = Result.newSuccess(userBankCard);
		return result;
	}

	@Override
	public Result<UserBankCard> updateBankCard(UserBankCard userBankCard) {

		Result<UserBankCard> result;
		
		// 校验参数
		if(userBankCard == null){
			result = errorResult.newFailure("CST1361");
			return result;
		}
		if(userBankCard.getId() == null){
			result = errorResult.newFailure("CST1362");
			return result;
		}
		UserBankCard card = new UserBankCard();
		try{
			// 准备对象
			card = userBankCardRepository.findOne(userBankCard.getId());
			card.setUserId(userBankCard.getUserId() == null ? card.getUserId() : userBankCard.getUserId());
			card.setBankCardType(userBankCard.getBankCardType() == null ? card.getBankCardType() : userBankCard.getBankCardType());
			card.setBankCode(userBankCard.getBankCode() == null ? card.getBankCode() : userBankCard.getBankCode());
			card.setBankBranch(userBankCard.getBankBranch() == null ? card.getBankBranch() : userBankCard.getBankBranch());
			card.setBankCardNum(userBankCard.getBankCardNum() == null ? card.getBankCardNum() : userBankCard.getBankCardNum());
			card.setBankCardAccountName(userBankCard.getBankCardAccountName() == null ? card.getBankCardAccountName() : userBankCard.getBankCardAccountName());
			card.setIdType(userBankCard.getIdType() == null ? card.getIdType() : userBankCard.getIdType());
			card.setIdNum(userBankCard.getIdNum() == null ? card.getIdNum() : userBankCard.getIdNum());
			card.setProvince(userBankCard.getProvince() == null ? card.getProvince() : userBankCard.getProvince());
			card.setCity(userBankCard.getCity() == null ? card.getCity() : userBankCard.getCity());
			card.setAddress(userBankCard.getAddress() == null ? card.getAddress() : userBankCard.getAddress());
			card.setMemo(userBankCard.getMemo() == null ? card.getMemo() : userBankCard.getMemo());
			
			// 持久对象
			userBankCardRepository.save(card);
		}catch(Exception e){
			result = errorResult.newFailure("CST0361");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(card);
		return result;
	}

	@Override
	public Result<Boolean> deleteBankCard(Long id) {
		
		Result<Boolean> result;
		
		// 参数校验
		if(id == null){
			result = errorResult.newFailure("CST1371");
			return result;
		}
		
		// 准备对象
		UserBankCard userBankCard = userBankCardRepository.findOne(id);
		userBankCard.setDeleted(true);
		
		// 持久化操作
		try{
			userBankCardRepository.save(userBankCard);
		}catch(Exception e){
			result = errorResult.newFailure("CST0371");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}	
		
		// 返回结果
		result = Result.newSuccess(true);
		return result;
	}

	@Override
	public Result<List<UserBankCard>> getBankCardByUserId(Long userId) {
		
		Result<List<UserBankCard>> result;
		
		if(userId == null){
			result = errorResult.newFailure("CST1381");
			return result;
		}
		
		// 准备查询参数
		SpecParam<UserBankCard> specParam = new SpecParam<UserBankCard>();
		specParam.eq("deleted", false);				// 未删除的
		specParam.eq("userId", userId);				// 所属用户
		
		// 执行查询
		List<UserBankCard> cards = null;
		try{
			cards = userBankCardRepository.findAll(SpecUtil.spec(specParam));
		}catch(Exception e){
			result = errorResult.newFailure("CST0381");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(cards);
		return result;
	}

	@Override
	public Result<UserBankCard> getBankCardById(Long id) {
		
		Result<UserBankCard> result;
		
		if(id == null){
			result = errorResult.newFailure("CST1391");
			return result;
		}
		
		// 执行查询
		UserBankCard card = null;
		try{
			card = userBankCardRepository.findOne(id);
		}catch(Exception e){
			result = errorResult.newFailure("CST0391");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		result = Result.newSuccess(card);
		return result;
	}
	
	/**
	 * 绑定银行卡
	 */
	@Override
	public Result<Boolean> bindBankCard(Long userId, UserBankCard userBankCard) {
		// TODO Auto-generated method stub
		log.info(SimpleLogFormater.formatParams(userId,userBankCard));
		Result<Boolean> result;
		if(userId == null) {
			result = errorResult.newFailure("CST1121");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		if(userBankCard == null) {
			result = errorResult.newFailure("CST1122");
			log.info(SimpleLogFormater.formatResult(result));
			return result;
		}
		try {
			UserBankCard  bank = userBankCardRepository.findByUserId(userId);
			if(bank == null) {
				//新增操作
				userBankCard.setId(queuingService.next(CustomerConstants.CUSTOMER_MARK).getObject());
				userBankCard.setUserId(userId);
				userBankCardRepository.save(userBankCard);
			}else {
				//修改操作
				bank.setBankBranch(userBankCard.getBankBranch());
				bank.setBankCardAccountName(userBankCard.getBankCardAccountName());
				bank.setBankCardNum(userBankCard.getBankCardNum());
				bank.setBankCode(userBankCard.getBankCode());
				bank.setBankCardType(userBankCard.getBankCardType());
				bank.setProvince(userBankCard.getProvince());
				bank.setCity(userBankCard.getCity());
				bank.setAddress(userBankCard.getAddress());
				userBankCardRepository.save(bank);
			}
			UserStatus userStatus = userStatusRepository.findByUserId(userId);
			userStatus.setBankbinding(CustomerConstants.SomethingBinding.BINDED.getCode());
			userStatusRepository.save(userStatus);
		}catch(Exception e) {
			result = errorResult.newFailure("CST0013");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			log.info(SimpleLogFormater.formatResult(result));
	    	return result;
		}
		return null;
	}

}
