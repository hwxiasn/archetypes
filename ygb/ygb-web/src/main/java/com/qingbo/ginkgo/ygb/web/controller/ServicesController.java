package com.qingbo.ginkgo.ygb.web.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.customer.entity.User;
import com.qingbo.ginkgo.ygb.web.biz.ProjectBizService;
import com.qingbo.ginkgo.ygb.web.biz.UserBizService;
import com.qingbo.ginkgo.ygb.web.pojo.OpenResult;

@RestController
@RequestMapping("/services/v1")
public class ServicesController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private AccountService accountService;
	@Autowired private UserBizService userBizService;
	@Autowired private ProjectBizService projectBizService;

	/**
	 * 倍赢金融接受外部平台直接投资服务
	 */
	@RequestMapping(value="/invest/{ak}/{id}/{userName}/{password}/{amount}")
	public Result<String> invest(@PathVariable String ak,@PathVariable String id, @PathVariable String userName, @PathVariable String password, @PathVariable double amount){
		// 验证参数
		if(ak == null || id == null || userName == null || password == null || amount == 0.0){
			return Result.newFailure(2, "param is null");
		}
		// 验证ak 
		if(!"E4805d16520de693a3fe707cdc962045".equals(ak)){
			return Result.newFailure(4, "ak is invalid");
		}
		
		Result<User> resultUser = userBizService.getUserByUserName(userName);
		if(resultUser == null){
			logger.info("ServicesController Invest find user detail ProjectId:"+id +" UserName:"+userName+" InvestAmount:"+amount+" Result is null");
			return Result.newFailure(1, "user not exists");
		}
		logger.info("ServicesController Invest find user detail ProjectId:"+id +" UserName:"+userName+" InvestAmount:"+amount+" Result:"+resultUser.success());
		if(!resultUser.success()){
			return Result.newFailure(1, resultUser.getMessage());
		}
		
		//交易密码检查留待服务支持
		Result<Boolean> passwordCheck = accountService.validatePassword(resultUser.getObject().getId(), password);
		logger.info("ServicesController Invest check user password ProjectId:"+id +" UserName:"+userName+" InvestAmount:"+amount+" Result:"+passwordCheck.success());
		//密码不正确
		if(!passwordCheck.success()){
			return Result.newFailure(3, passwordCheck.getMessage());
		}
		//交易金额留待服务支持
		BigDecimal investAmount = BigDecimal.valueOf(amount);
		//查询投资账户
		Result<SubAccount> subAccountResult = accountService.getSubAccount(resultUser.getObject().getId());
		logger.info("ServicesController Invest find user account ProjectId:"+id +" UserName:"+userName+" InvestAmount:"+amount+" Result:"+subAccountResult.success());
		if(!subAccountResult.success()){
			return Result.newFailure(6, subAccountResult.getMessage());
		}
		//资金不足
		if(investAmount.compareTo(subAccountResult.getObject().getBalance()) > 0){
			return Result.newFailure(5, "not enough balance");
		}
		//投资金额乘以万元单位
		try{
			Result<Boolean> investResult = projectBizService.invest(Long.valueOf(id),String.valueOf(resultUser.getObject().getId()), investAmount);
			logger.info("ServicesController Invest Id:"+id +" UserId:"+resultUser.getObject().getId()+" InvestAmount:"+investAmount+" Result:"+investResult.success());
			if(investResult.success()){
				return Result.newSuccess();
			}else{
				return Result.newFailure(7, investResult.getMessage());
			}
		}catch(Exception e){
			logger.info("ServicesController Invest Id:"+id +" UserId:"+resultUser.getObject().getId()+" InvestAmount:"+investAmount+" Error.By:"+e.getMessage());
		}
		return Result.newFailure(8, "process has Exception");
		
	}

}
