package com.qingbo.project.web;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"classpath:repository.xml",
		"classpath:domain.xml",
		"classpath:shiro.xml",
		"classpath:service.xml",
		"classpath:web.xml",
		})
public class BaseTester {

}
