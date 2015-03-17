package com.qingbo.ginkgo.ygb.web.biz.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;
import com.qingbo.ginkgo.ygb.web.biz.BizService;

@Service("bizService")
public class BizServiceImpl implements BizService {
	@Autowired TongjiService tongjiService;

	@Override
	public Pager investsPage(Map<String, String> search, Pager pager) {
		boolean byBroker = "broker".equals(search.get("byRole"));
		SqlBuilder sqlBuilder = new SqlBuilder();
		sqlBuilder.from("investment inv left join project p on inv.project_id=p.id left join user on inv.inverstor_id=user.id left join user_profile profile on user.id=profile.user_id left join user_group r on user.id=r.child_user_id left join user broker on r.parent_user_id=broker.id left join user_profile brokerProfile on broker.id=brokerProfile.user_id left join user_group brokerAgent on broker.id=brokerAgent.child_user_id left join user agent on brokerAgent.parent_user_id=agent.id left join user_enterprise_profile agentProfile on agent.id=agentProfile.user_id");
		sqlBuilder.eq("broker.role", "BR");
		sqlBuilder.eq("inv.status", "E");
		sqlBuilder.between("inv.create_at", search.get("timeFrom"), search.get("timeTo"));
		if(search.containsKey("period")) sqlBuilder.eq("p.period", search.get("period"));
		
		if(pager.notInitialized()){
			if(byBroker) {
				sqlBuilder.select("count(distinct broker.id)");
			}else {
				sqlBuilder.select("count(distinct agent.id)");
			}
			long totalRows = tongjiService.count(sqlBuilder).getObject();
			pager.init((int)totalRows);
		}
		
		if(pager.getTotalRows() > 0) {
			sqlBuilder.select("count(distinct user.id) 投资人数,count(inv.id) 投资笔数,round(sum(balance)/10000) 投资额");
			Object[] sums = tongjiService.sums(sqlBuilder).getObject();
			pager.setOthers(Arrays.asList(sums));
			
			if(byBroker) {
				sqlBuilder.select("broker.id 编号, broker.user_name 经纪人,brokerProfile.real_name 姓名,agentProfile.enterprise_name 营销机构,count(distinct user.id) 投资人数,count(inv.id) 投资笔数,round(sum(balance)/10000) 投资额");
				sqlBuilder.groupBy("broker.id");
			}else {
				sqlBuilder.select("agent.id 编号, agent.user_name 营销机构, agentProfile.enterprise_name 企业名,count(distinct user.id) 投资人数,count(inv.id) 投资笔数,round(sum(balance)/10000) 投资额");
				sqlBuilder.groupBy("agent.id");
			}
			sqlBuilder.orderBy("投资额 desc");
			if(pager.getPageSize()>0) sqlBuilder.limit(pager.getStartRow()+","+pager.getPageSize());
			List<?> list = tongjiService.list(sqlBuilder).getObject();
			pager.setElements(list);
		}
		return pager;
	}

}
