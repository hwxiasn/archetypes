package com.qingbo.ginkgo.ygb.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.TreeSet;

import org.junit.Test;

public class GinkgoConfigTester {
	@Test
	public void props() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Field field = GinkgoConfig.class.getDeclaredField("props");
		field.setAccessible(true);
		Properties props = (Properties)field.get(null);
		for(String prop : new TreeSet<String>(props.stringPropertyNames())) {
			System.out.println(prop+"="+props.getProperty(prop));
		}
	}
}
