package com.qingbo.ginkgo.ygb.customer.service;

import org.springframework.test.context.ContextConfiguration;

import com.qingbo.ginkgo.ygb.customer.repository.BaseRepositoryTester;

@ContextConfiguration(locations = { "classpath:consumer.xml", "classpath:customer-service.xml" })
public class BaseServiceTester extends BaseRepositoryTester {
}
