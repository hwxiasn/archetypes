package com.qingbo.ginkgo.ygb.trade.repository;

import javax.annotation.Resource;

import org.junit.Test;

import com.qingbo.ginkgo.ygb.common.util.QueuingMake;
import com.qingbo.ginkgo.ygb.trade.entity.Deal;
import com.qingbo.ginkgo.ygb.trade.entity.Trade;
import com.qingbo.ginkgo.ygb.trade.entity.TradeLog;
import com.qingbo.ginkgo.ygb.trade.entity.TradeSchedule;

public class TradeRepositoryTester extends BaseRepositoryTester {
	@Resource private TradeRepository trade;
	@Resource private TradeLogRepository tradeLog;
	@Resource private TradeScheduleRepository tradeSchedule;
	@Resource private DealRepository deal;
	
	@Test
	public void fineOne() {
		Trade tr = new Trade();
		tr.setId(QueuingMake.next("01"));
		tr= trade.save(tr);
		System.out.println("Trade "+tr.getBatchId());
		tr = trade.findOne(tr.getId()); 
		tr.setBatchId(QueuingMake.next("01"));
		tr.setSourceTradeId(QueuingMake.next("01"));
		tr.setCreditAccount("TestAcccount");
		trade.save(tr);
		System.out.println("Trade "+tr.getCreditAccount());
		
		TradeLog trLog = new TradeLog();
		trLog.setId(QueuingMake.next("01"));
		trLog.setTradeId(tr.getId());
		trLog = tradeLog.save(trLog);
		System.out.println("TradeLog "+trLog.getCreateAt());
		
		TradeSchedule trSche = new TradeSchedule();
		trSche.setId(QueuingMake.next("01"));
		trSche.setScheduleInfo("test info");
		trSche = tradeSchedule.save(trSche);
		System.out.println("TradeSchedule "+trSche.getCreateAt());
		trSche = tradeSchedule.findOne(trSche.getId());
		System.out.println("TradeSchedule "+trSche.getCreateAt());
		
		Deal d = new Deal();
		d.setId(QueuingMake.next("01"));
		d = deal.save(d);
		System.out.println("Deal "+d.getCreateAt());
		d = deal.findOne(d.getId());
		System.out.println("Deal "+d.getCreateAt());
		
	}

	
}
