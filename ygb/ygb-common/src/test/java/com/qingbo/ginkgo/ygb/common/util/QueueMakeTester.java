package com.qingbo.ginkgo.ygb.common.util;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class QueueMakeTester {
	private static Set<Long> queues = new HashSet<Long>();
	private static Long limit = 10L;
	
	@Test
	public void queue() {
		for(int i=1; i<=5; i++) {
			TaskUtil.submit(new TestRunner("00"));
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("exit");
	}
	
	static class TestRunner implements Runnable {
		private static int idx = 1;
		private int index;
		private String src;
		public TestRunner(String runner) {
			this.src = runner;
			this.index = idx++;
		}
		public void run() {
			int n = 1;
			int duplicates = 0;
			while(true) {
				Long next = QueuingMake.next(src);
				synchronized (queues) {
					if(queues.contains(next)) {
						if(duplicates%100==0) System.err.println("TestRunner("+index+") "+src+":"+(n++)+" duplicate "+next);
						duplicates++;
						continue;
					}
					queues.add(next);
					if(queues.size() > limit) break;
				}
			}
			if(duplicates>0) System.out.println("TestRunner("+index+") "+src+" duplicates:"+duplicates);
			System.out.println("TestRunner("+index+") "+src+": exit");
		}
	}
}
