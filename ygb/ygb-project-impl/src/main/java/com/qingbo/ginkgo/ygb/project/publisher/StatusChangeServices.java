package com.qingbo.ginkgo.ygb.project.publisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class StatusChangeServices implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher publisher;
	
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
		publisher.toString();
	}

}
