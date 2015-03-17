package com.qingbo.ginkgo.ygb.project.publisher;

import java.util.Iterator;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.AbstractApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

public class AsyncApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

	private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = (taskExecutor != null ? taskExecutor : new SimpleAsyncTaskExecutor());
	}

	protected TaskExecutor getTaskExecutor() {
		return this.taskExecutor;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void multicastEvent(final ApplicationEvent event) {
		try{
			Iterator<ApplicationListener<?>> it = this.getApplicationListeners().iterator();
			while (it.hasNext()) {
				final ApplicationListener listener = it.next();
				getTaskExecutor().execute(new Runnable() {
					public void run() {
						try{
							listener.onApplicationEvent(event);	
						}catch(Exception e){
						}
					}
				});
			}
		}catch(Exception e){
		}
	}
}
