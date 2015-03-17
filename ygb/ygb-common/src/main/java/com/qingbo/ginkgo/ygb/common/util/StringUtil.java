package com.qingbo.ginkgo.ygb.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class StringUtil {

	@SuppressWarnings("unchecked")
	public static List<String> linesToList(String lines) {
		if(StringUtils.isBlank(lines)) return Collections.EMPTY_LIST;
		
		List<String> list = new ArrayList<String>();
		for(String line:lines.split("\r\n")) {
			if(StringUtils.isNotBlank(line)) list.add(line.trim());
		}
		return list;
	}
	
	public static String listToLines(List<String> list) {
		if(list == null || list.size() == 0) return StringUtils.EMPTY;
		
		StringBuilder lines = new StringBuilder();
		for(String line:list) {
			lines.append(line);
			lines.append('\n');
		}
		lines.deleteCharAt(lines.length()-1);
		return lines.toString();
	}
}
