package com.qingbo.ginkgo.ygb.trade.service;

import org.springframework.test.context.ContextConfiguration;

import com.qingbo.ginkgo.ygb.trade.repository.BaseRepositoryTester;

@ContextConfiguration(locations = { "classpath:consumer.xml", "classpath:trade-service.xml" })
public class BaseServiceTester extends BaseRepositoryTester {
}
