package com.qingbo.gingko.domain.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import com.qingbo.gingko.common.result.PageObject;
import com.qingbo.gingko.common.util.Pager;

public class PagerUtil {
	/**
	 * Pager => Pageable
	 */
	public static Pageable pagable(Pager pager) {
		Pageable pageable = pager.getDirection()==null || pager.getProperties()==null ? 
				new PageRequest(pager.getCurrentPage()-1, pager.getPageSize()) :
					new PageRequest(pager.getCurrentPage()-1, pager.getPageSize(), Direction.valueOf(pager.getDirection()), pager.getProperties().split(","));
		return pageable;
	}
	
	/**
	 * Page => PageObject
	 */
	public static <T> PageObject<T> pageObject(Page<T> page) {
		return new PageObject<T>((int)page.getTotalElements(), page.getContent());
	}
	
	/**
	 * Pager => limit
	 */
	public static String limit(Pager pager) {
		return pager.getCurrentPage() == 1 ? ""+pager.getPageSize() : ""+pager.getStartRow()+","+pager.getPageSize();
	}
}
