package com.qingbo.ginkgo.ygb.common.util;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.map.LRUMap;

/**
 * @author hongwei
 * @date 2014-12-26
 * @sample <pre>String key = "orderNo";
 * try{
 * 	OrderNoLock.lock(key);
 * }finally{
 * 	OrderNoLock.unlock(key);
 * }
 * </pre>
 */
public class OrderNoLock {
	public static void lock(String key) {
		getLock(key).lock();
	}
	
	public static void unlock(String key) {
		getLock(key).unlock();
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String, Lock> locks = new LRUMap(1000);
	private static Lock getLock(String key) {
		Lock lock = locks.get(key);
		if(lock==null) {
			synchronized (locks) {
				lock = locks.get(key);
				if(lock==null) {
					lock = new ReentrantLock();
					locks.put(key, lock);
				}
			}
		}
		return lock;
	}
}
