package com.qingbo.ginkgo.ygb.project.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.ErrorMessage;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.project.entity.BorrowerInfo;
import com.qingbo.ginkgo.ygb.project.enums.BorrowerInfoStatus;
import com.qingbo.ginkgo.ygb.project.repository.BorrowerInfoRepository;
import com.qingbo.ginkgo.ygb.project.service.BorrowerInfoService;
import com.qingbo.ginkgo.ygb.project.enums.*;

@Service("borrowerInfoService")
public class BorrowerInfoServiceImpl implements BorrowerInfoService {
	
	private static Log log = LogFactory.getLog(BorrowerInfoService.class);
	
	@Autowired private BorrowerInfoRepository BorrowerInfoRepository;
	@Resource private QueuingService queuingService;

	private ErrorMessage errorResult = new ErrorMessage("project-errorcode.properties");
	
	
	/**
	 * 分页查询借款人信息
	 * @param spec 查询参数
	 * @param page 分页参数
	 * @return
	 */
	@Override
	public Result<PageObject<BorrowerInfo>> pageBorrowerInfos(SpecParam<BorrowerInfo> specParam, Pager pager) {

		Result<PageObject<BorrowerInfo>> result;
		
		// 参数校验
		if(specParam == null || pager == null){
			return errorResult.newFailure("CMS1801");
		}
		if(pager.getPageSize() <= 0){
			return errorResult.newFailure("CMS1802");
		}
		
		// 准备分页参数
		Pageable pageable = pager.getDirection() == null || pager.getProperties() == null ? 
								new PageRequest(pager.getCurrentPage() - 1, pager.getPageSize()) :
								new PageRequest(pager.getCurrentPage() - 1, pager.getPageSize(), 
												Direction.valueOf(pager.getDirection()), 
												pager.getProperties().split(","));
		
		// 执行查询
		Page<BorrowerInfo> resultSet = null;
		try{			
			resultSet = BorrowerInfoRepository.findAll(SpecUtil.spec(specParam), pageable);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0801");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		return Result.newSuccess(new PageObject<BorrowerInfo>((int)resultSet.getTotalElements(), resultSet.getContent()));
	}
	

	/**
	 * 添加借款人信息
	 * @param info 借款人信息
	 * @return
	 */
	@Override
	public Result<Boolean> addBorrowerInfo(BorrowerInfo info) {
		
		Result<Boolean> result;

		// 准备序列号
		Result<Long> queuing = queuingService.next(ProjectConstants.PROJECT_QUEUING);
	    if(queuing.getError() != null ){
	    	return errorResult.newFailure("PJT0812");
	    }
	    
	    try{
	    	info.setId(queuing.getObject());
	    	info.setCreateTime(new Date());
	    	info.setStatus(BorrowerInfoStatus.NEW.getCode());
	    	BorrowerInfoRepository.save(info);
	    }catch(Exception e){
	    	result = errorResult.newFailure("PJT0811");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
	    }

	    // 返回结果
	 	return Result.newSuccess(true);
	}
	

	/**
	 * 删除借款人信息
	 * @param id 流水号
	 * @return
	 */
	@Override
	public Result<Boolean> deleteBorrowerInfo(Long id) {
		
		Result<Boolean> result;
		
		// 参数校验
		if(id == null){
			return errorResult.newFailure("CMS1841");
		}
		
		// 准备对象
		BorrowerInfo info = BorrowerInfoRepository.findOne(id);
		info.setDeleted(true);
		
		// 持久化操作
		try{
			BorrowerInfoRepository.save(info);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0821");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		return Result.newSuccess(true);
	}
	
	/**
	 * 查找借款人信息
	 * @param id 流水号
	 * @return
	 */
	@Override
	public Result<BorrowerInfo> getBorrowerInfo(Long id) {
		
		Result<BorrowerInfo> result;
		
		// 参数校验
		if(id == null){
			return errorResult.newFailure("PJT1861");
		}
		
		// 准备对象
		BorrowerInfo info;
		
		// 持久化操作
		try{
			info = BorrowerInfoRepository.findOne(id);
		}catch(Exception e){
			result = errorResult.newFailure("CMS0831");
			log.error(SimpleLogFormater.formatException(result.getMessage(), e));
			return result;
		}
		
		// 返回结果
		return Result.newSuccess(info);
	}

}
