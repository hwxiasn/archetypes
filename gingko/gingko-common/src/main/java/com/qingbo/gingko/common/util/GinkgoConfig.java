package com.qingbo.gingko.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.qingbo.gingko.common.util.PropertiesUtil.PropertiesListener;

public class GinkgoConfig {
	public static String platform = "local";
	
	private static final String resource = "ginkgo.properties";
	private static final Properties props = PropertiesUtil.get(resource, "utf-8");
	
	
	public static String getProperty(String name, String defValue) {
		return props.getProperty(name, defValue);
	}
	
	public static String getProperty(String name) {
		return props.getProperty(name);
	}
	
	private static void updatePlatformConfig() {
		String platform_temp = props.getProperty("platform");
		if(!platform.equals(platform_temp)) {
			platform = platform_temp;
		}
		
		if(!"local".equals(platform)) {
			String platformResource = "ginkgo_"+platform+".properties";
			Properties props_temp = PropertiesUtil.get(platformResource, "utf-8");
			if(props_temp!=null) {
				//因为props被修改，所以需要重新读取配置内容、合并、计算配置变量
				PropertiesUtil.reload(resource, "utf-8");
				props.putAll(props_temp);
				PropertiesUtil.extractPropertyValues(props);
				
				if(!resources.contains(platformResource)) {
					resources.add(platformResource);
					PropertiesUtil.addPropertiesListener(new GinkgoPropertiesListener(platformResource));
				}
			}
		}
	}
	private static final List<String> resources = new ArrayList<String>();
	static {
		updatePlatformConfig();
		PropertiesUtil.addPropertiesListener(new GinkgoPropertiesListener(resource));
	}
	static class GinkgoPropertiesListener implements PropertiesListener {
		private String resource;
		public GinkgoPropertiesListener(String resource) {
			this.resource = resource;
		}
		public String getResource() {
			return resource;
		}
		public void afterReloaded(Properties props) {
			updatePlatformConfig();
		}
	}
}
