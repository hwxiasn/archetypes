package com.qingbo.ginkgo.ygb.common.util;

import java.util.Properties;

public class GinkgoConfig {
	public static String platform = "test";
	public static boolean test = true;
	
	private static final String resource = "ginkgo.properties";
	private static final Properties props = PropertiesUtil.get(resource, "utf-8");
	
	
	public static String getProperty(String name, String defValue) {
		return props.getProperty(name, defValue);
	}
	
	public static String getProperty(String name) {
		return props.getProperty(name);
	}
	
	static {
		String platform_temp = props.getProperty("platform");
		
		if(!"test".equals(platform_temp)) {
			int dash = platform_temp.indexOf('_');
			if(dash>0) {
				platform = platform_temp.substring(0, dash);
			}else {
				platform = platform_temp;
			}
			test = platform_temp.endsWith("test");
			
			String platformResource = "ginkgo_"+platform_temp+".properties";
			Properties load_temp = PropertiesUtil.load(platformResource, "utf-8");
			if(load_temp!=null) {
				//因为props被修改，所以需要重新读取配置内容、合并、计算配置变量
				Properties load = PropertiesUtil.load(resource, "utf-8");
				props.clear();
				props.putAll(load);
				props.putAll(load_temp);
				PropertiesUtil.extractPropertyValues(props);
			}
		}
	}
}
