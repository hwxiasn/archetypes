package com.qingbo.ginkgo.ygb.base.service;

import org.springframework.test.context.ContextConfiguration;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepositoryTester;

@ContextConfiguration(locations = { "classpath:base-service.xml","classpath:cache.xml" })
public class BaseServiceTester extends BaseRepositoryTester {
}
