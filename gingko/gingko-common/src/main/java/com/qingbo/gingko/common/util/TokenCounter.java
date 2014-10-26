package com.qingbo.gingko.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.IntHolder;

/**
 * 继承TokenCounter，实现getId、update、insert<br/>
 * 配置getExpireCount、getExpireTime、getDate<br/>
 * 初始化new MyTokenCounter().init()<br/>
 * 统计MyTokenCounter.count(token,type)
 * @author hongwei
 */
public class TokenCounter {
	protected static Log log = LogFactory.getLog(TokenCounter.class);
	
	private int expireCount = 20;
	private long expireTime = TimeUnit.SECONDS.toMillis(60);
	private String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd");
	
	private BlockingQueue<String> removeTokens = new LinkedBlockingQueue<String>();
	private BlockingQueue<TokenType> tokenTypes = new LinkedBlockingQueue<TokenType>();
	private Map<String, TokenCount> tokenCounts = new HashMap<String, TokenCount>();
	private Map<String, Integer> idCaches = new ExpireTimeMap<String, Integer>(new HashMap<String, Integer>(), TimeUnit.MINUTES.toMillis(3));
	
	public void init(){
		TaskUtil.submitKeepRunning(new TokenTypesConsumer());
		TaskUtil.submitKeepRunning(new RemoveKeysConsumer());
		TaskUtil.scheduleAtFixedRate(new ExpireTimeChecker(), getExpireTime(), getExpireTime(), TimeUnit.MILLISECONDS);
	}
	
	public void count(String token, String type) {
		tokenTypes.add(new TokenType(token, type));
	}
	
	public Integer getId(TokenCount tokenCount) { return tokenCount.expireTime() ? 1 : null; }
	public void update(TokenCount tokenCount, Integer id) { log.debug("update " + id + ": " + tokenCount.toString()); }
	public void insert(TokenCount tokenCount) { log.debug("insert: " + tokenCount.toString()); }
	
	public int getExpireCount() { return expireCount; }
	public long getExpireTime() { return expireTime; }
	public String getDate() { return DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd"); }
	
	private void merge(TokenCount tokenCount) {
		String cacheKey = tokenCount.token+tokenCount.day;
		Integer cacheId = idCaches.get(cacheKey);
		if(cacheId == null) {
			cacheId = getId(tokenCount);
			if(cacheId != null) idCaches.put(cacheKey, cacheId);
		}
		
		if(cacheId != null) {
			update(tokenCount, cacheId);
		}else {
			insert(tokenCount);
		}
	}
	
	public class TokenCount {
		public long time;
		public String token;
		public String day;
		public Map<String, IntHolder> counts = new HashMap<String, IntHolder>();
		private TokenCount(long time, String token, String date, String type) {
			this.time = time;
			this.token = token;
			this.day = date;
			this.counts.put(type, new IntHolder(1));
		}
		public boolean expireTime() {
			return System.currentTimeMillis() - time >= expireTime;
		}
		public boolean expireCount() {
			int sum = 0;
			for(IntHolder count : counts.values()) {
				sum += count.value;
			}
			return sum >= expireCount;
		}
		public void count(String type) {
			IntHolder count = counts.get(type);
			if(count == null) {
				counts.put(type, new IntHolder(1));
			}else {
				count.value += 1;
			}
		}
		public String toString() {
			return DateFormatUtils.format(time, "yyyy-MM-dd HH:mm:ss") + " " + token + " (" + counts.toString() + ")";
		}
	}
	
	static class TokenType {
		public String token;
		public String type;
		public TokenType(String token, String type) {
			this.token = token;
			this.type = type;
		}
	}
	
	class TokenTypesConsumer implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					TokenType tokenType = tokenTypes.take();
					synchronized (tokenCounts) {
						TokenCount tokenCount = tokenCounts.get(tokenType.token);
						if(tokenCount == null) {
							tokenCount = new TokenCount(System.currentTimeMillis(), tokenType.token, date, tokenType.type);
							tokenCounts.put(tokenType.token, tokenCount);
						}else {
							tokenCount.count(tokenType.type);
						}
						if(tokenCount.expireCount()) {
							removeTokens.add(tokenType.token);
							log.info("expire count: " + tokenCount.toString());
						}
					}
				} catch (InterruptedException e) {
					log.error("TokenCountUtil.ClickTokensConsumer has unexpectedly exception.", e);
					continue;
				}
			}
		}
	}
	
	class RemoveKeysConsumer implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					String key = removeTokens.take();
					TokenCount tokenCount = null;
					synchronized (tokenCounts) {
						tokenCount = tokenCounts.remove(key);
					}
					if(tokenCount != null) {
						log.debug("merge removed: " + tokenCount.toString());
						merge(tokenCount);
					}
				} catch (InterruptedException e) {
					log.error("TokenCountUtil.RemoveKeysConsumer has unexpectedly exception.", e);
					continue;
				}
			}
		}
	}
	
	class ExpireTimeChecker implements Runnable {
		@Override
		public void run() {
			synchronized (tokenCounts) {
				for(TokenCount tokenCount : tokenCounts.values()) {
					if(tokenCount.expireTime()) {
						removeTokens.add(tokenCount.token);
						log.debug("expire time: " + tokenCount.toString());
					}
				}
			}
			date = getDate();
		}
	}
}
