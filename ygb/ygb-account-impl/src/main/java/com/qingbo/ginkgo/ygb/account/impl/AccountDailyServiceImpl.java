package com.qingbo.ginkgo.ygb.account.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.account.entity.AccountDaily;
import com.qingbo.ginkgo.ygb.account.entity.AccountLog;
import com.qingbo.ginkgo.ygb.account.entity.SubAccount;
import com.qingbo.ginkgo.ygb.account.enums.AccountLogType;
import com.qingbo.ginkgo.ygb.account.payment.PaymentUtil;
import com.qingbo.ginkgo.ygb.account.repository.AccountDailyRespository;
import com.qingbo.ginkgo.ygb.account.repository.AccountLogRepository;
import com.qingbo.ginkgo.ygb.account.repository.AccountRepository;
import com.qingbo.ginkgo.ygb.account.repository.SubAccountRepository;
import com.qingbo.ginkgo.ygb.account.service.AccountDailyService;
import com.qingbo.ginkgo.ygb.account.service.AccountService;
import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.base.service.TongjiService;
import com.qingbo.ginkgo.ygb.base.util.PagerUtil;
import com.qingbo.ginkgo.ygb.base.util.SpecUtil;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.DateUtil;
import com.qingbo.ginkgo.ygb.common.util.DateUtil.FormatType;
import com.qingbo.ginkgo.ygb.common.util.NumberUtil;
import com.qingbo.ginkgo.ygb.common.util.Pager;
import com.qingbo.ginkgo.ygb.common.util.SqlBuilder;

@Service("accountDailyService")
public class AccountDailyServiceImpl implements AccountDailyService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired AccountRepository accountRepository;
	@Autowired AccountLogRepository accountLogRepository;
	@Autowired SubAccountRepository subAccountRepository;
	@Autowired AccountDailyRespository accountDailyRespository;
	
	@Autowired AccountService accountService;
	@Autowired TongjiService tongjiService;
	@Autowired QueuingService queuingService;

	@Override
	public Result<AccountDaily> findOne(Long userId, String daily) {
		if(userId==null || userId<1 || daily==null || daily.length()!=10) return Result.newFailure(-1, "参数错误");
		
		//查找最近的扎账记录
		SqlBuilder sqlBuilder = new SqlBuilder("id", "account_daily").eq("account_id", String.valueOf(userId)).between("daily", null, daily).orderBy("daily desc").limit("1");
		@SuppressWarnings("rawtypes")
		Result<List> list = tongjiService.list(sqlBuilder);
		Long id = (list.hasObject() && list.getObject().size()==1) ? NumberUtil.parseLong(ObjectUtils.toString(list.getObject().get(0)), null) : null;
		
		if(id!=null) {
			AccountDaily accountDaily = accountDailyRespository.findOne(id);
			accountDaily.setTotalBalance(accountDaily.getBalance().add(accountDaily.getFreezeBalance()));
			Map<Long, SubAccount> subAccounts = subAccounts(accountDaily.getSubBalances());
			Map<Long, SubAccount> dailySubAccounts = subAccounts(accountDaily.getDailySubBalances());
			accountDaily.setSubAccounts(subAccounts);
			accountDaily.setDailySubAccounts(dailySubAccounts);
			return Result.newSuccess(accountDaily);
		}
		
		return Result.newFailure(-1, "没有记录");
	}
	
	private Map<Long, SubAccount> subAccounts(String subBalances) {
		Map<Long, SubAccount> subAccounts = new HashMap<>();
		if(StringUtils.isNotBlank(subBalances)) {
			String[] subBalanceArray = subBalances.split("[;]");
			for(String subBalance : subBalanceArray) {
				String[] balanceArray = subBalance.split("[,]");
				if(balanceArray.length==3) {
					Long subAccountId = NumberUtil.parseLong(balanceArray[0], null);
					BigDecimal balance = NumberUtil.parseBigDecimal(balanceArray[1], null);
					BigDecimal freezeBalance = NumberUtil.parseBigDecimal(balanceArray[2], null);
					if(subAccountId!=null && balance!=null && freezeBalance!=null) {
						SubAccount subAccount = new SubAccount();
						subAccount.setId(subAccountId);
						subAccount.setBalance(balance);
						subAccount.setFreezeBalance(freezeBalance);
						subAccounts.put(subAccountId, subAccount);
					}
				}
			}
		}
		return subAccounts;
	}
	
	private String subBalances(List<SubAccount> subAccounts) {
		if(subAccounts==null || subAccounts.size()==0) return null;
		StringBuilder subBalances = new StringBuilder();
		for(SubAccount subAccount:subAccounts) {
			subBalances.append(subAccount.getId().toString());
			subBalances.append(',');
			subBalances.append(subAccount.getBalance().toPlainString());
			subBalances.append(',');
			subBalances.append(subAccount.getFreezeBalance().toPlainString());
			subBalances.append(';');
		}
		return subBalances.toString();
	}

	@Override
	public Result<Map<String, AccountDaily>> findData(Long userId, String from, String to) {
		if(userId==null || userId<1 || from==null || from.length()!=10 || to==null || to.length()!=10) return Result.newFailure(-1, "参数错误");
		Date fromDay = DateUtil.parse(from), toDay = DateUtil.parse(to), endDay = DateUtil.parse(DateUtil.format(DateUtils.addDays(new Date(), -1), FormatType.DAY)+" 12:00:00");
		if(fromDay.after(toDay) || toDay.after(endDay)) return Result.newFailure(-1, "参数错误");
		
		SqlBuilder sqlBuilder = new SqlBuilder("id", "account_daily").eq("account_id", String.valueOf(userId)).between("daily", from, to).orderBy("daily");
		@SuppressWarnings("rawtypes")
		Result<List> list = tongjiService.list(sqlBuilder);
		
		if(list.hasObject() && list.getObject().size()>0) {//有记录则填充
			Map<String, AccountDaily> accountDailys = new HashMap<>(), accountDailysReturn = new LinkedHashMap<>();
			AccountDaily temp = null;
			for(Object obj : list.getObject()) {
				Long id = NumberUtil.parseLong(ObjectUtils.toString(obj), null);
				if(id==null) continue;
				AccountDaily accountDaily = accountDailyRespository.findOne(id);
				accountDaily.setTotalBalance(accountDaily.getBalance().add(accountDaily.getFreezeBalance()));
				Map<Long, SubAccount> subAccounts = subAccounts(accountDaily.getSubBalances());
				Map<Long, SubAccount> dailySubAccounts = subAccounts(accountDaily.getDailySubBalances());
				accountDaily.setSubAccounts(subAccounts);
				accountDaily.setDailySubAccounts(dailySubAccounts);
				accountDailys.put(accountDaily.getDaily(), accountDaily);
				if(temp==null) temp = accountDaily;
			}
			Date tempDay = fromDay;
			do {
				String daily = DateUtil.format(tempDay, FormatType.DAY);
				AccountDaily accountDaily = accountDailys.get(daily);
				if(accountDaily!=null) temp = accountDaily;
				accountDailysReturn.put(daily, temp);
				tempDay = DateUtils.addDays(tempDay, 1);
			}while(tempDay.before(toDay) || tempDay.equals(toDay));
			return Result.newSuccess(accountDailysReturn);
		}else {//没有记录则再往前查找一条
			Result<AccountDaily> findOne = findOne(userId, from);
			if(findOne.hasObject()) {
				AccountDaily accountDaily = findOne.getObject();
				Map<String, AccountDaily> accountDailysReturn = new LinkedHashMap<>();
				Date tempDay = fromDay;
				do {
					String daily = DateUtil.format(tempDay, FormatType.DAY);
					accountDailysReturn.put(daily, accountDaily);
					tempDay = DateUtils.addDays(tempDay, 1);
				}while(tempDay.before(toDay));
				return Result.newSuccess(accountDailysReturn);
			}
		}
		
		return Result.newFailure(-1, "没有记录");
	}

	@Override
	public Result<Boolean> handleDaily() {
		//找出上次扎账日期
		SqlBuilder sqlBuilder = new SqlBuilder("daily", "account_daily").orderBy("daily desc").limit("1");
		@SuppressWarnings("rawtypes")
		Result<List> list = tongjiService.list(sqlBuilder);
		
		if(list.hasObject() && list.getObject().size()>0) {//从dailyFrom开始扎账
			String dailyLast = ObjectUtils.toString(list.getObject().get(0));
			String dailyNext = DateUtil.format(DateUtils.addDays(new Date(), -1), FormatType.DAY);
			if(dailyLast.equals(dailyNext)) return Result.newSuccess(true);//已扎账至昨天
			
			//查找所有账务日志
			SpecParam<AccountLog> accountLogsSpecs = new SpecParam<>();
			accountLogsSpecs.eq("executed", true);
			accountLogsSpecs.between("createAt", DateUtil.parse(dailyLast+" 23:59:59"), DateUtil.parse(dailyNext+" 23:59:59"));
			List<AccountLog> accountLogs = accountLogRepository.findAll(SpecUtil.spec(accountLogsSpecs));
			Map<Long, List<AccountLog>> accountLogsMap = new HashMap<>();//subAccount -> list(accountLog)
			for(AccountLog accountLog : accountLogs) {
				List<AccountLog> accountLogsList = accountLogsMap.get(accountLog.getSubAccountId());
				if(accountLogsList==null) {
					accountLogsList = new ArrayList<>();
					accountLogsMap.put(accountLog.getSubAccountId(), accountLogsList);
				}
				accountLogsList.add(accountLog);
			}
			
			sqlBuilder = new SqlBuilder("count(id)", "account");
			Integer count = tongjiService.count(sqlBuilder).getObject();
			
			Pager pager = new Pager();
			pager.setPageSize(20);
			pager.init(count);
			sqlBuilder = new SqlBuilder("id", "account").limit(PagerUtil.limit(pager));
			list = tongjiService.list(sqlBuilder);
			while(list.hasObject() && list.getObject().size()>0) {
				List<Long> accountIds = new ArrayList<>();
				for(Object obj : list.getObject()) {
					Long accountId = NumberUtil.parseLong(ObjectUtils.toString(obj), null);
					if(accountId!=null) accountIds.add(accountId);
				}
				
				SpecParam<SubAccount> subAccountsSpecs = new SpecParam<>();
				subAccountsSpecs.in("accountId", accountIds);
				List<SubAccount> findAll = subAccountRepository.findAll(SpecUtil.spec(subAccountsSpecs));
				Map<Long, List<SubAccount>> subAccountsMap = new HashMap<>();
				for(SubAccount subAccount : findAll) {
					List<SubAccount> subAccountsList = subAccountsMap.get(subAccount.getAccountId());
					if(subAccountsList==null) {
						subAccountsList = new ArrayList<>();
						subAccountsMap.put(subAccount.getAccountId(), subAccountsList);
					}
					subAccountsList.add(subAccount);
				}
				
				for(Object obj : list.getObject()) {
					Long accountId = NumberUtil.parseLong(ObjectUtils.toString(obj), null);
					if(accountId!=null) {
						List<SubAccount> subAccountsList = subAccountsMap.get(accountId);
						if(subAccountsList!=null) {
							BigDecimal balance = BigDecimal.ZERO, freezeBalance = BigDecimal.ZERO;
							for(SubAccount subAccount : subAccountsList) {
								balance = balance.add(subAccount.getBalance());
								freezeBalance = freezeBalance.add(subAccount.getFreezeBalance());
							}
							
							Result<AccountDaily> findOne = findOne(accountId, dailyNext);
							if(!findOne.hasObject()) {
								AccountDaily accountDaily = new AccountDaily();
								accountDaily.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
								accountDaily.setAccountId(accountId);
								accountDaily.setDaily(dailyNext);
								accountDaily.setBalance(balance);
								accountDaily.setFreezeBalance(freezeBalance);
								accountDaily.setStatus("Y");
								
								String subBalances = subBalances(subAccountsList);
								accountDaily.setSubBalances(subBalances);
								AccountDaily save = accountDailyRespository.save(accountDaily);
								logger.info("accountDaily saved: "+save.getId()+" for account: "+accountId+", balance="+balance.toPlainString()+", freezeBalance="+freezeBalance.toPlainString());
							}else {
								AccountDaily accountDailyLast = findOne.getObject();
								AccountDaily accountDailyNext = new AccountDaily();
								accountDailyNext.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
								accountDailyNext.setAccountId(accountId);
								accountDailyNext.setDaily(dailyNext);
								accountDailyNext.setBalance(balance);
								accountDailyNext.setFreezeBalance(freezeBalance);
								
								String subBalances = subBalances(subAccountsList);
								accountDailyNext.setSubBalances(subBalances);
								
								//演算所有子账户的账务日志
								for(SubAccount subAccount : subAccountsList) {
									List<AccountLog> accountLogsList = accountLogsMap.get(subAccount.getId());
									if(accountLogsList!=null && accountLogsList.size()>0) {
										SubAccount subAccountLast = accountDailyLast.getSubAccounts().get(subAccount.getId());
										for(AccountLog accountLog : accountLogsList) {
											AccountLogType accountLogType = AccountLogType.getByCode(accountLog.getType());
											if(accountLogType==null) continue;
											switch(accountLogType) {
											case IN:
												subAccountLast.setBalance(subAccountLast.getBalance().add(accountLog.getBalance()));
												break;
											case OUT:
												subAccountLast.setBalance(subAccountLast.getBalance().subtract(accountLog.getBalance()));
												break;
											case FREEZE:
												subAccountLast.setBalance(subAccountLast.getBalance().subtract(accountLog.getBalance()));
												subAccountLast.setFreezeBalance(subAccountLast.getFreezeBalance().add(accountLog.getBalance()));
												break;
											case UNFREEZE:
												subAccountLast.setBalance(subAccountLast.getBalance().add(accountLog.getBalance()));
												subAccountLast.setFreezeBalance(subAccountLast.getFreezeBalance().subtract(accountLog.getBalance()));
												break;
											}
										}
									}
								}
								
								List<SubAccount> dailySubAccountsList = new ArrayList<>();
								for(Long subAccountId : accountDailyLast.getSubAccounts().keySet()) {
									SubAccount dailySubAccount = accountDailyLast.getSubAccounts().get(subAccountId);
									dailySubAccountsList.add(dailySubAccount);
								}
								String dailySubBalances = subBalances(dailySubAccountsList);
								accountDailyNext.setDailySubBalances(dailySubBalances);
								
								balance = BigDecimal.ZERO; freezeBalance = BigDecimal.ZERO;
								for(SubAccount subAccount : dailySubAccountsList) {
									balance = balance.add(subAccount.getBalance());
									freezeBalance = freezeBalance.add(subAccount.getFreezeBalance());
								}
								accountDailyNext.setDailyBalance(balance);
								accountDailyNext.setDailyFreezeBalance(freezeBalance);
								
								//如果余额有变化，或者演算余额不一致则记录
								boolean balanceChanged = !accountDailyNext.getBalance().toPlainString().equals(accountDailyLast.getBalance().toPlainString())
										|| !accountDailyNext.getFreezeBalance().toPlainString().equals(accountDailyLast.getFreezeBalance().toPlainString());
								boolean balanceDifferent = !accountDailyNext.getDailyBalance().toPlainString().equals(accountDailyNext.getBalance().toPlainString())
										|| !accountDailyNext.getDailyFreezeBalance().toPlainString().equals(accountDailyNext.getFreezeBalance().toPlainString());
								if(balanceChanged || balanceDifferent) {
									if(balanceDifferent) accountDailyNext.setStatus("N");//异常的金额差异
									else accountDailyNext.setStatus("Y");//正常的金额变化
									AccountDaily save = accountDailyRespository.save(accountDailyNext);
									logger.info("accountDaily saved: "+save.getId()+" for account: "+accountId+", balance="+balance.toPlainString()+", freezeBalance="+freezeBalance.toPlainString());
								}
							}
						}
					}
				}
				
				int currentPage = pager.getCurrentPage();
				if(currentPage>=pager.getTotalPages()) break;
				pager.page(pager.getCurrentPage()+1);
				if(currentPage>=pager.getCurrentPage()) break;
				
				sqlBuilder.limit(PagerUtil.limit(pager));
				list = tongjiService.list(sqlBuilder);
			}
		}else {//没有记录则全部扎账，期望是凌晨运行
			sqlBuilder = new SqlBuilder("count(id)", "account");
			Integer count = tongjiService.count(sqlBuilder).getObject();
			
			Pager pager = new Pager();
			pager.setPageSize(20);
			pager.init(count);
			sqlBuilder = new SqlBuilder("id", "account").limit(PagerUtil.limit(pager));
			list = tongjiService.list(sqlBuilder);
			String daily = DateUtil.format(DateUtils.addDays(new Date(), -1), FormatType.DAY);
			while(list.hasObject() && list.getObject().size()>0) {
				List<Long> accountIds = new ArrayList<>();
				for(Object obj : list.getObject()) {
					Long accountId = NumberUtil.parseLong(ObjectUtils.toString(obj), null);
					if(accountId!=null) accountIds.add(accountId);
				}
				
				SpecParam<SubAccount> specs = new SpecParam<>();
				specs.in("accountId", accountIds);
				List<SubAccount> findAll = subAccountRepository.findAll(SpecUtil.spec(specs));
				Map<Long, List<SubAccount>> subAccountsMap = new HashMap<>();
				for(SubAccount subAccount : findAll) {
					List<SubAccount> subAccountsList = subAccountsMap.get(subAccount.getAccountId());
					if(subAccountsList==null) {
						subAccountsList = new ArrayList<>();
						subAccountsMap.put(subAccount.getAccountId(), subAccountsList);
					}
					subAccountsList.add(subAccount);
				}
				
				for(Object obj : list.getObject()) {
					Long accountId = NumberUtil.parseLong(ObjectUtils.toString(obj), null);
					if(accountId!=null) {
						List<SubAccount> subAccountsList = subAccountsMap.get(accountId);
						if(subAccountsList!=null) {
							BigDecimal balance = BigDecimal.ZERO, freezeBalance = BigDecimal.ZERO;
							for(SubAccount subAccount : subAccountsList) {
								balance = balance.add(subAccount.getBalance());
								freezeBalance = freezeBalance.add(subAccount.getFreezeBalance());
							}
							
							AccountDaily accountDaily = new AccountDaily();
							accountDaily.setId(queuingService.next(PaymentUtil.ACCOUNT_QUEUING).getObject());
							accountDaily.setAccountId(accountId);
							accountDaily.setDaily(daily);
							accountDaily.setBalance(balance);
							accountDaily.setFreezeBalance(freezeBalance);
							accountDaily.setStatus("Y");
							
							String subBalances = subBalances(subAccountsList);
							accountDaily.setSubBalances(subBalances);
							AccountDaily save = accountDailyRespository.save(accountDaily);
							logger.info("accountDaily saved: "+save.getId()+" for account: "+accountId+", balance="+balance.toPlainString()+", freezeBalance="+freezeBalance.toPlainString());
						}
					}
				}
				
				int currentPage = pager.getCurrentPage();
				if(currentPage>=pager.getTotalPages()) break;
				pager.page(pager.getCurrentPage()+1);
				if(currentPage>=pager.getCurrentPage()) break;
				
				sqlBuilder.limit(PagerUtil.limit(pager));
				list = tongjiService.list(sqlBuilder);
			}
		}
		
		return Result.newSuccess(true);
	}

}
