package com.qingbo.project.service;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"classpath:repository.xml",
		"classpath:domain.xml",
		"classpath:service.xml",
		})
public class BaseTester {

}
