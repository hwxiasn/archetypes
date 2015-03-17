package com.qingbo.ginkgo.ygb.web.util;

import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import com.qingbo.ginkgo.ygb.common.util.GinkgoConfig;

//@Component
public class VelocityUtil implements InitializingBean {
	private static VelocityEngine velocityEngine;
	
	@Autowired private VelocityConfigurer velocityConfigurer;
	public void afterPropertiesSet() throws Exception {
		velocityEngine = velocityConfigurer.getVelocityEngine();
	}
	
    public static String merge(String vm) {
    	return merge(vm, null);
    }
    
    public static String merge(String vm, Map<String, Object> model) {
    	model.put("front_url", GinkgoConfig.getProperty("front_url"));
    	model.put("upload", GinkgoConfig.getProperty("upload"));
    	return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, vm, "utf-8", model);
    }
}
