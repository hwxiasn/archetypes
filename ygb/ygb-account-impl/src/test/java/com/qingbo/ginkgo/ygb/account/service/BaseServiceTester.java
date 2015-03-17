package com.qingbo.ginkgo.ygb.account.service;

import org.springframework.test.context.ContextConfiguration;

import com.qingbo.ginkgo.ygb.account.repository.BaseRepositoryTester;

@ContextConfiguration(locations = { "classpath:consumer.xml", "classpath:account-service.xml" })
public class BaseServiceTester extends BaseRepositoryTester {
}
