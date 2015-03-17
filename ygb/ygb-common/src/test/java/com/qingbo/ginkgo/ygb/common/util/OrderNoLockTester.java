package com.qingbo.ginkgo.ygb.common.util;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.omg.CORBA.IntHolder;

public class OrderNoLockTester {

	@Test
	public void lock() throws InterruptedException {
		final String key = "test";
		final int times = 800;
		final IntHolder intHolder = new IntHolder(0);
		class MyThread extends Thread {
			private String name;
			public MyThread(String name) {
				this.name = name;
			}
			public void run() {
				for(int i=0; i<times; i++) {
//					String key = name+i;
					try {
						OrderNoLock.lock(key);
						System.out.println(name+" times-"+i+" count: "+(++intHolder.value));
						try {
							Thread.sleep(RandomUtils.nextInt(4)+1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}finally {
						OrderNoLock.unlock(key);
					}
					Thread.yield();
				}
			}
		}
		
		long start = System.currentTimeMillis();
		Thread[] threads = new Thread[5];
		for(int i=0; i<threads.length; i++) {
			threads[i] = new MyThread("Thread-"+i);
			threads[i].start();
		}
		for(int i=0; i< threads.length; i++) {
			threads[i].join();
		}
		long end = System.currentTimeMillis();
		int totalTimes = times*threads.length;
		int totalMs = (int)(end-start);
		int timesPMs=totalTimes/totalMs;
		int timesPs = 1000*timesPMs;
		System.out.println("test end. "+totalTimes+" times, "+totalMs+" ms, conflicts: "+(totalTimes-intHolder.value) +","+ timesPMs+"t/ms," + timesPs+" t/s");
	}
}
