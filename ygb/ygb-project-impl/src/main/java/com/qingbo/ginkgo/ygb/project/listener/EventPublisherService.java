package com.qingbo.ginkgo.ygb.project.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;


/**
 * @Resouce	private EventPublisherService eventPublisherService;
 * @author Administrator
 *
 */
@Component
public class EventPublisherService{
	@Autowired
	private ApplicationContext applicationContext;
	
	public void publishEvent(ApplicationEvent event) {
		applicationContext.publishEvent(event);
	}
}
