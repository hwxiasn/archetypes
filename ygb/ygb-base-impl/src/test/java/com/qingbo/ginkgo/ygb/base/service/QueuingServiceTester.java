package com.qingbo.ginkgo.ygb.base.service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.IntHolder;
import org.springframework.beans.factory.annotation.Autowired;

import com.qingbo.ginkgo.ygb.common.result.Result;

public class QueuingServiceTester extends BaseServiceTester {
	@Autowired private QueuingService queuingService;
	
	@Test
	public void queue() {
		Random random = new Random(System.currentTimeMillis());
		String[] src = {"01","02","03","04"};
		Set<Long> nexts = new HashSet<Long>();
		for(int i=0; i<100; i++) {
			String srcRandom = src[random.nextInt(src.length)];
			Result<Long> next = queuingService.next(srcRandom);
			
			Assert.assertTrue(next.success());
			System.out.println(srcRandom+" "+next.getObject());
			
			Assert.assertFalse(nexts.contains(next.getObject()));
			nexts.add(next.getObject());
		}
	}
	
	@Test
	public void log() {
		Result<Long> next = queuingService.next("00");
		Result<Long> next2 = queuingService.next("00");
//		Result<Long> next3 = queuingService.next("00");
//		Result<Long> next4 = queuingService.next("00");
	}
	
	@Test
	public void queues() throws InterruptedException {
		final Set<Long> nexts = new HashSet<Long>();
		final String[] src = {"01","02","03","04"};
		final int times = 80;
		final IntHolder intHolder = new IntHolder(0);
		
		class MyThread extends Thread {
			public void run() {
				Random random = new Random(System.currentTimeMillis());
				for(int i=0; i<times; i++) {
					String srcRandom = src[random.nextInt(src.length)];
					Result<Long> next = queuingService.next(srcRandom);
					
//					Assert.assertTrue(next.success());
					if(next.success()) {
//						System.out.println(srcRandom+" "+next.getObject());
						
	//					Assert.assertFalse(nexts.contains(next.getObject()));
						if(nexts.contains(next.getObject())) {
							System.err.println(srcRandom+" "+next.getObject());
							intHolder.value++;
						}else {
							nexts.add(next.getObject());
						}
					}else {
						System.err.println(srcRandom+" "+next);
					}
				}
			}
		}
		
		long start = System.currentTimeMillis();
		Thread[] threads = new Thread[5];
		for(int i=0; i<threads.length; i++) {
			threads[i] = new MyThread();
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
		System.out.println("test end. "+totalTimes+" times, "+totalMs+" ms, conflicts: "+intHolder.value +","+ timesPMs+"t/ms," + timesPs+" t/s");
	}
}
