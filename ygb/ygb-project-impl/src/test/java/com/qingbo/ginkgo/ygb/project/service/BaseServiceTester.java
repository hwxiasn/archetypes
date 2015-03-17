package com.qingbo.ginkgo.ygb.project.service;

import org.springframework.test.context.ContextConfiguration;

import com.qingbo.ginkgo.ygb.project.repository.BaseRepositoryTester;

@ContextConfiguration(locations = { "classpath:consumer.xml", "classpath:project-service.xml" })
public class BaseServiceTester extends BaseRepositoryTester {
}
