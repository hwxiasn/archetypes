package com.qingbo.ginkgo.ygb.common.util;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 执行单次任务或定时任务工具类（用于减少new Thread()和new Timer()的使用）
 * @author hongwei
 */
public class TaskUtil {
	private static Log log = LogFactory.getLog(TaskUtil.class);
	private static ExecutorService cachedExecutor = null;
	private static ScheduledExecutorService scheduledExecutor = null;
	private static Map<Runnable, Future<?>> keepRunningTasks = null;
	private static Map<Future<?>, Callback> callbackdTasks = null;
	static {
		cachedExecutor = Executors.newCachedThreadPool(new TaskUtilThreadFactory("cached"));
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new TaskUtilThreadFactory("scheduled"));
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				TaskUtil.shutdown();
			}
		});
	}
	
	/**
	 * 关闭TaskUtil，通常情况下不必手动调用
	 */
	public static void shutdown() {
		scheduledExecutor.shutdown();
		cachedExecutor.shutdown();
		if(!scheduledExecutor.isTerminated()) scheduledExecutor.shutdownNow();
		if(!cachedExecutor.isTerminated()) scheduledExecutor.shutdownNow();
		log.info("TaskUtil executors shutdown.");
	}

	/**
	 * 立即执行任务
	 */
	public static Future<?> submit(Runnable task) {
		return cachedExecutor.submit(task);
	}
	
	/**
	 * 自动保持任务持续运行，每分钟监视一次
	 */
	public static Future<?> submitKeepRunning(Runnable task){
		Future<?> future = submit(task);
		checkInitCachedTasks();
		synchronized (keepRunningTasks) {
			keepRunningTasks.put(task, future);
		}
		return future;
	}
	
	/**
	 * 延迟执行任务，例如延迟5秒：schedule(task,5,TimeUnit.SECONDS)
	 */
	public static void schedule(Runnable task, long delay, TimeUnit unit) {
		scheduledExecutor.schedule(new ScheduleTask(task), delay, unit);
	}
	
	/**
	 * 定时执行任务一次，比如下午两点：scheduleAt(task, DateUtils.setHours(new Date(), 13))
	 */
	public static void scheduleAt(Runnable task, Date time) {
		long mills = time.getTime() - System.currentTimeMillis();
		schedule(task, mills>0 ? mills : 3, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 定时重复执行任务，比如延迟5秒，每10分钟执行一次：scheduleAtFixRate(task, 5, TimeUnit.MINUTES.toSeconds(10), TimeUnit.SECONDS)
	 */
	public static void scheduleAtFixedRate(Runnable task, long initialDelay, long delay, TimeUnit unit) {
		scheduledExecutor.scheduleWithFixedDelay(new ScheduleTask(task), initialDelay, delay, unit);
	}
	
	/**
	 * 定时重复执行任务，比如下午两点开始，每小时执行一次：scheduleAtFixRate(task, DateUtils.setHours(new Date(), 13), 1, TimeUnit.HOURS)
	 */
	public static void scheduleAtFixtRate(Runnable task, Date time, long delay, TimeUnit unit) {
		long mills = time.getTime() - System.currentTimeMillis();
		scheduleAtFixedRate(task, mills>0 ? mills : 3, unit.toMillis(delay), TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 提交带返回值的任务，支持后续处理
	 */
	public static <T> Future<T> submit(Callable<T> task) {
		return cachedExecutor.submit(task);
	}
	
	/**
	 * 提交带返回值的任务，支持后续处理
	 */
	public static <T> Future<T> submit(Callable<T> task, Callback callback) {
		Future<T> future = submit(task);
		checkInitCachedTasks();
		if(callback != null) {
			synchronized (callbackdTasks) {
				callbackdTasks.put(future, callback);
			}
		}
		return future;
	}
	
	/**
	 * 提交任务，等待返回值
	 */
	public static <T> T wait(Callable<T> task) {
		Future<T> future = cachedExecutor.submit(task);
		try {
			return future.get();
		} catch (Exception e) {
			log.warn(e);
			return null;
		}
	}
	
	private static void checkInitCachedTasks() {
		if(keepRunningTasks != null) return;
		
		keepRunningTasks = new HashMap<Runnable, Future<?>>();
		callbackdTasks = new HashMap<Future<?>, Callback>();
		scheduleAtFixedRate(new CachedTasksMonitor(), 1, 1, TimeUnit.MINUTES);
	}
	
	/**
	 * 监视需要保持运行的任务
	 */
	private static class CachedTasksMonitor implements Runnable {
		public void run() {
			if(keepRunningTasks.size() > 0) {
				synchronized (keepRunningTasks) {
					Map<Runnable, Future<?>> tempTasks = null;
					for(Runnable task : keepRunningTasks.keySet()) {
						Future<?> future = keepRunningTasks.get(task);
						if(future.isDone()) {
							future = submit(task);//恢复运行结束任务
							if(tempTasks == null) tempTasks = new HashMap<Runnable, Future<?>>();
							tempTasks.put(task, future);
						}
					}
					if(tempTasks != null && tempTasks.size() > 0) keepRunningTasks.putAll(tempTasks);
				}
			}
			
			if(callbackdTasks.size() > 0) {
				synchronized (callbackdTasks) {
					List<Future<?>> callbackedFutures = null;
					for(Future<?> future : callbackdTasks.keySet()) {
						final Callback callback = callbackdTasks.get(future);
						if(future.isDone()) {
							try{
								final Object result = future.get(5, TimeUnit.SECONDS);
								submit(new Runnable() {
									public void run() {
										callback.handle(result);
									}
								});
								if(callbackedFutures == null) callbackedFutures = new LinkedList<Future<?>>();
								callbackedFutures.add(future);
							}catch (Exception e) {
								log.warn("TaskUtil callbackedTasks warn: ", e);
							}
						}
					}
					
					if(callbackedFutures != null && callbackedFutures.size() > 0) {
						for(Future<?> future : callbackedFutures) {
							callbackdTasks.remove(future);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 自定义线程名称Task-idx-name-idx2
	 */
	private static class TaskUtilThreadFactory implements ThreadFactory {
		private final static AtomicInteger taskutilThreadNumber = new AtomicInteger(1);
		private final String threadNamePrefix;
		TaskUtilThreadFactory(String threadNamePrefix){
			this.threadNamePrefix = threadNamePrefix;
		}
		
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, String.format("TaskUtil-%d-%s", taskutilThreadNumber.getAndIncrement(), this.threadNamePrefix));
		    t.setDaemon(true);
		    t.setPriority(Thread.MIN_PRIORITY);
			return t;
		}
	}
	
	/**
	 * 封装定时任务，每次调度时使用cached thread运行，基本不占用调度执行时间
	 * @author hongwei
	 * @date 2014-09-05
	 */
	private static class ScheduleTask implements Runnable {
		private Runnable runner;
		public ScheduleTask(Runnable runnable) {
			this.runner = runnable;
		}
		@Override
		public void run() {
			TaskUtil.submit(runner);
		}
	}
	
	/**
	 * 等待结果回调接口
	 */
	public static interface Callback {
		void handle(Object result);
	}
}
