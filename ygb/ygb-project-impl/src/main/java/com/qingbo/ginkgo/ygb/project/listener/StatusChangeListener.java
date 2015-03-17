package com.qingbo.ginkgo.ygb.project.listener;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.SimpleLogFormater;
import com.qingbo.ginkgo.ygb.project.entity.Guarantee;
import com.qingbo.ginkgo.ygb.project.entity.Project;
import com.qingbo.ginkgo.ygb.project.enums.ProjectConstants;
import com.qingbo.ginkgo.ygb.project.event.StatusChangeEvent;
import com.qingbo.ginkgo.ygb.project.service.ProjectService;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.service.TradeService;

@SuppressWarnings("unused")
@Component
public class StatusChangeListener implements ApplicationListener<ApplicationEvent> {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource private ProjectService projectService;
	@Resource private TradeService tradeService;
	@SuppressWarnings("unchecked")
	public void onApplicationEvent(ApplicationEvent event) {
		try{
			//加入具体的处理EVENT的处理方法
			if(event instanceof StatusChangeEvent){
				//处理担保函
				if(((StatusChangeEvent) event).getType().equalsIgnoreCase(StatusChangeEvent.GUARANTEE)){
					Guarantee gua = (Guarantee)event.getSource();
					if(gua == null || gua.getStatus() == null || gua.getProjectId() == null){
						logger.info("StatusChangeEvent: GUARANTEE Handle entity is null");
						return ;
					}
					logger.info("StatusChangeEvent: GUARANTEE Handle Status:"+gua.getStatus() +" GuaranteeId:"+gua.getId() +" ProjectId:"+gua.getProjectId());
					//担保函审核中，一级审核员触发
					if(ProjectConstants.GuaranteeStatus.CHECKING.getCode().equalsIgnoreCase(gua.getStatus())){
						Result<Project> result = projectService.buildContract(gua.getProjectId());
						logger.info(SimpleLogFormater.formatResult(result));
						//担保函签发，二级审核员触发，完成支付
					}else if(ProjectConstants.GuaranteeStatus.SIGN.getCode().equalsIgnoreCase(gua.getStatus())){
						Result<Project> result = projectService.buildProject(gua.getProjectId());
						logger.info(SimpleLogFormater.formatResult(result));
					} 
				}else if(((StatusChangeEvent) event).getType().equalsIgnoreCase(StatusChangeEvent.RepayedId)){
					//处理分润
					Long id = (Long)event.getSource();
					if(id == null || id == 0){
						logger.info("StatusChangeEvent: RepayedId Handle Id: 0");
						return ;
					}
					logger.info("StatusChangeEvent: RepayedId Handle Id:"+id);
					Result<Project> repay = projectService.repayProject(id);
					logger.info(SimpleLogFormater.formatResult(repay));
				}else if(((StatusChangeEvent) event).getType().equalsIgnoreCase(StatusChangeEvent.RepayFeeList)){
					//处理分润列表
					List<Trade> trades = (List<Trade>)event.getSource();
					if(trades == null || trades.size() <=0){
						logger.info("StatusChangeEvent: RepayFeeList Handle List Size: 0");
						return;
					}
					logger.info("StatusChangeEvent: RepayFeeList Handle List Size:"+trades.size());
					Result<List<Trade>> resultTrades = tradeService.createTrades(trades);
					logger.info("StatusChangeEvent: RepayFeeList Trade: "+SimpleLogFormater.formatResult(resultTrades));
				}else if(((StatusChangeEvent) event).getType().equalsIgnoreCase(StatusChangeEvent.FundraiseFeeList)){
					//处理分佣列表
					List<Trade> trades = (List<Trade>)event.getSource();
					if(trades == null || trades.size() <=0){
						logger.info("StatusChangeEvent: FundraiseFeeList Handle List Size: 0");
						return;
					}
					logger.info("StatusChangeEvent: FundraiseFeeList Handle List Size:"+trades.size());
					Result<List<Trade>> resultTrades = tradeService.createTrades(trades);
					logger.info(SimpleLogFormater.formatResult(resultTrades));
				}
			}		
		}catch(Exception e){
			logger.info("StatusChangeListener event Error.By:"+e.getMessage());
		}

	}
}
